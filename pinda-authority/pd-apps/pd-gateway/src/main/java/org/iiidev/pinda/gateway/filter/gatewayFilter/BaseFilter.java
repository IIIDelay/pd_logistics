package org.iiidev.pinda.gateway.filter.gatewayFilter;

import cn.hutool.core.text.StrPool;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.iiidev.pinda.base.Result;
import org.iiidev.pinda.common.adapter.IgnoreTokenConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * BaseFilter
 *
 * @Author IIIDelay
 * @Date 2023/8/26 16:01
 **/
public abstract class BaseFilter implements GlobalFilter {

    /**
     * 前缀: /api
     */
    @Value("${server.servlet.context-path}")
    protected String zuulPrefix;

    // 判断当前请求uri是否需要忽略 (直接放行)
    protected boolean isIgnoreToken(ServerHttpRequest request) {
        // 动态获取当前请求的uri
        String uri = request.getURI().getPath();
        uri = StringUtils.substring(uri, zuulPrefix.length());
        uri = StringUtils.substring(uri, uri.indexOf(StrPool.C_SLASH, 1));
        boolean ignoreToken = IgnoreTokenConfig.isIgnoreToken(uri);
        return ignoreToken;
    }

    // 网关抛异常，不再进行路由，而是直接返回到前端
    protected Mono<Void> errorResponse(ServerHttpResponse response, String errMsg, int errCode, int httpStatusCode) {
        Result<Void> build = Result.fail(errCode, errMsg);

        byte[] bytes = JSONObject.toJSONString(build).getBytes(StandardCharsets.UTF_8);

        // 获取DataBuffer
        DataBuffer wrap = response.bufferFactory().wrap(bytes);
        // 中文乱码处理
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");

        return response.writeWith(Mono.just(wrap));
    }
}
