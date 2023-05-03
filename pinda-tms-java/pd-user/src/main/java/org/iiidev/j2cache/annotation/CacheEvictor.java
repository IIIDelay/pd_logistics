package org.iiidev.j2cache.annotation;
import java.lang.annotation.*;

/**
 * 失效缓存
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheEvictor {
    Cache[] value() default {};
}