package org.iiidev.pinda.user.feign.fallback;

import lombok.extern.slf4j.Slf4j;
import org.iiidev.pinda.base.Result;
import org.iiidev.pinda.user.feign.UserResolveApi;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 用户API熔断
 *
 */
@Component
@Slf4j
public class UserResolveApiFallback implements FallbackFactory<UserResolveApi> {
    
    @Override
    public UserResolveApi create(Throwable throwable) {
        return (id, userQuery) -> {
            log.error("通过用户名查询用户异常:{}", id, throwable);
            return Result.timeout();
        };
    }
}
