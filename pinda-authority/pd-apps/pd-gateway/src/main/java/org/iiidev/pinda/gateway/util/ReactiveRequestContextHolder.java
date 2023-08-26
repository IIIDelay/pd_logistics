package org.iiidev.pinda.gateway.util;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

/**
 * ReactiveRequestContextHolder
 */
public class ReactiveRequestContextHolder {
    /**
     * getRequest: 获取Request对象
     *
     * @return Mono<ServerHttpRequest>
     */
    public static Mono<ServerHttpRequest> getRequest() {
        return Mono.deferContextual(contextView -> Mono.just(contextView.get(ServerHttpRequest.class)));
    }

    /**
     * getRequest: 获取Response对象
     *
     * @return Mono<ServerHttpRequest>
     */
    public static Mono<ServerHttpResponse> getResponse() {
        return Mono.deferContextual(contextView -> Mono.just(contextView.get(ServerHttpResponse.class)));
    }
}