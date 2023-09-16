package org.iiidev.pinda.gateway.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.iiidev.pinda.auth.client.properties.AuthClientProperties;
import org.iiidev.pinda.auth.client.utils.JwtTokenClientUtils;
import org.iiidev.pinda.auth.utils.JwtUserInfo;
import org.iiidev.pinda.base.Result;
import org.iiidev.pinda.context.BaseContextConstants;
import org.iiidev.pinda.exception.BizException;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 当前过滤器负责解析请求头中的jwt令牌并且将解析出的用户信息放入zuul的header中
 *
 * @Author IIIDelay
 * @Date 2023/8/26 18:27
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class TokenContextFilter extends BaseFilter {
    private final AuthClientProperties authClientProperties;

    private final JwtTokenClientUtils jwtTokenClientUtils;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        if (isIgnoreToken(request)) {
            // 直接放行
            return chain.filter(exchange);
        }

        // 从请求头中获取前端提交的jwt令牌
        String userToken = request.getHeaders()
            .get(authClientProperties.getUser().getHeaderName())
            .stream()
            .findFirst()
            .orElse("");

        JwtUserInfo userInfo = null;
        // 解析jwt令牌
        try {
            userInfo = jwtTokenClientUtils.getUserInfo(userToken);
        } catch (BizException e) {
            return errorResponse(response, e.getMessage(), e.getCode(), HttpStatus.OK);
        } catch (Exception e) {
            return errorResponse(response, "解析jwt令牌出错", Result.FAIL_CODE, HttpStatus.OK);
        }

        // 将信息放入header
        ServerHttpRequest.Builder mutate = request.mutate();

        HttpHeaders headers = request.getHeaders();
        if (userInfo != null && headers != null) {
            request.mutate()
                .header(BaseContextConstants.JWT_KEY_ACCOUNT, userInfo.getAccount())
                .header(BaseContextConstants.JWT_KEY_USER_ID, String.valueOf(userInfo.getUserId()))
                .header(BaseContextConstants.JWT_KEY_NAME, userInfo.getName())
                .header(BaseContextConstants.JWT_KEY_ORG_ID, String.valueOf(userInfo.getOrgId()))
                .header(BaseContextConstants.JWT_KEY_STATION_ID, String.valueOf(userInfo.getStationId()));
        }
        return chain.filter(exchange);

    }

    @Override
    public int getOrder() {
        return 0;
    }
}
