package org.iiidev.pinda.gateway.filter;

import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.iiidev.pinda.authority.dto.auth.ResourceQueryDTO;
import org.iiidev.pinda.authority.entity.auth.Resource;
import org.iiidev.pinda.base.Result;
import org.iiidev.pinda.common.constant.CacheKey;
import org.iiidev.pinda.common.utils.RedisHelper;
import org.iiidev.pinda.constant.MatchType;
import org.iiidev.pinda.context.BaseContextConstants;
import org.iiidev.pinda.exception.code.ExceptionCode;
import org.iiidev.pinda.gateway.api.ResourceApi;
import org.iiidev.pinda.gateway.config.ClientHolder;
import org.iiidev.pinda.utils.CollectionHelper;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 鉴权处理过滤器
 *
 * @Author IIIDelay
 * @Date 2023/8/26 16:38
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class AccessFilter extends BaseFilter {
    private final ClientHolder clientHolder;

    /**
     * filter 鉴权处理逻辑
     *
     * @param exchange exchange
     * @param chain    chain
     * @return Mono<Void>
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        log.info("验证信息过滤器接受url: {}", request.getURI().getPath());

        // 第1步：判断当前请求uri是否需要忽略
        if (isIgnoreToken(request)) {
            // 当前请求需要忽略，直接放行
            return chain.filter(exchange);
        }
        // 第2步：获取当前请求的请求方式和uri，拼接成GET/user/page这种形式，称为权限标识符
        String method = request.getMethod().name();
        String uri = request.getURI().getPath();
        uri = StringUtils.substring(uri, zuulPrefix.length());
        uri = StringUtils.substring(uri, uri.indexOf("/", 1));
        String permission = method + uri;// GET/user/page

        // 第3步：从缓存中获取所有需要进行鉴权的资源(同样是由资源表的method字段值+url字段值拼接成)，如果没有获取到则通过Feign调用权限服务获取并放入缓存中
        String value = RedisHelper.getValue(CacheKey.RESOURCE, CacheKey.RESOURCE_NEED_TO_CHECK);
        List<String> uriList = JSONObject.parseArray(value, String.class);
        if (CollectionUtils.isEmpty(uriList)) {
            // 缓存中没有相应数据
            // Result<List> result = ClientHolder.get(clientHolder -> clientHolder.list());
            Result<List> result = ClientHolder.get(client -> client.getResourceApi(), ResourceApi::list);
            uriList = Optional.ofNullable(result).map(Result::getData).orElse(null);

            if (CollectionUtils.isNotEmpty(uriList)) {
                RedisHelper.save(uriList, Duration.ofDays(1), CacheKey.RESOURCE, CacheKey.RESOURCE_NEED_TO_CHECK);
            }
        }

        // 第4步：判断这些资源是否包含当前请求的权限标识符，如果不包含当前请求的权限标识符，则返回未经授权错误提示
        boolean bool = CollectionHelper.matchAny(uriList, permission, MatchType.PREFIX);
        if (!bool) {
            // 当前请求是一个未知请求，直接返回未授权异常信息
            return errorResponse(response, ExceptionCode.UNAUTHORIZED.getMsg(), ExceptionCode.UNAUTHORIZED.getCode(), HttpStatus.OK);
        }

        // 第5步：如果包含当前的权限标识符，则从gateway header中取出用户id，根据用户id取出缓存中的用户拥有的权限，如果没有取到则通过Feign
        // 调用权限服务获取并放入缓存，判断用户拥有的权限是否包含当前请求的权限标识符
        log.info("网关获取用户请求头信息: {}", JSONObject.toJSONString(request.getHeaders()));
        String userId = request.getHeaders().get(BaseContextConstants.JWT_KEY_USER_ID).stream().findFirst().orElse("");

        List<String> visibleResource = JSONObject.parseArray(RedisHelper.getValue(CacheKey.USER_RESOURCE, userId), String.class);
        if (visibleResource == null) {
            // 缓存中不存在，需要通过接口远程调用权限服务来获取
            ResourceQueryDTO resourceQueryDTO = ResourceQueryDTO.builder().userId(new Long(userId)).build();
            List<Resource> resourceList = ClientHolder.get(clientHolder1 -> clientHolder1.visible(resourceQueryDTO)).getData();
            if (resourceList != null && resourceList.size() > 0) {
                visibleResource = resourceList.stream()
                    .map((resource -> resource.getMethod() + resource.getUrl()))
                    .collect(Collectors.toList());
                // 将当前用户拥有的权限载入缓存
                RedisHelper.save(visibleResource, CacheKey.USER_RESOURCE, userId);
            }
        }


        // 第6步：如果用户拥有的权限包含当前请求的权限标识符则说明当前用户拥有权限，直接放行
        long count = visibleResource.stream()
            .filter(permission::startsWith)
            .count();
        if (count <= 0) {
            // 第7步：如果用户拥有的权限不包含当前请求的权限标识符则说明当前用户没有权限，返回未经授权错误提示
            return errorResponse(response,ExceptionCode.UNAUTHORIZED.getMsg(), ExceptionCode.UNAUTHORIZED.getCode(), HttpStatus.OK);
        }

        // 放行
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
