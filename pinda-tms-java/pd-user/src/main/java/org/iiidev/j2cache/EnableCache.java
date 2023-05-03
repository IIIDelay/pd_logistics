package org.iiidev.j2cache;

import org.iiidev.j2cache.aop.CacheMethodInterceptor;
import org.springframework.context.annotation.Import;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import(CacheMethodInterceptor.class)
public @interface EnableCache {
}