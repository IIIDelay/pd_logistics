package org.iiidev.j2cache.aop;
import org.iiidev.j2cache.annotation.Cache;
import org.iiidev.j2cache.annotation.CacheEvictor;
import org.iiidev.j2cache.aop.processor.AbstractCacheAnnotationProcessor;
import org.iiidev.j2cache.utils.SpringApplicationContextUtils;
import org.aopalliance.intercept.Interceptor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

/**
 * 缓存拦截器
 */
@Aspect
@Component
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Import(SpringApplicationContextUtils.class)
public class CacheMethodInterceptor implements Interceptor {
    /**
     * 拦截单个Cache注解的方法以便实现缓存
     *
     * @param proceedingJoinPoint 切点
     * @return 结果
     * @throws Throwable 异常
     */
    @Around("@annotation(org.iiidev.j2cache.annotation.Cache)")
    public Object invokeCacheAllMethod(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        Cache cache = AnnotationUtils.findAnnotation(methodSignature.getMethod(), Cache.class);
        if (cache != null) {
            AbstractCacheAnnotationProcessor processor = AbstractCacheAnnotationProcessor.getProcessor(proceedingJoinPoint, cache);
            return processor.process(proceedingJoinPoint);
        }
        return proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
    }

    /**
     * 拦截CacheEvictor注解的方法以便实现失效指定key的缓存
     *
     * @param proceedingJoinPoint 切点
     * @return 结果
     * @throws Throwable 异常
     */
    @Around("@annotation(org.iiidev.j2cache.annotation.CacheEvictor)")
    public Object invokeCacheEvictorAllMethod(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        CacheEvictor cacheEvictor = AnnotationUtils.findAnnotation(methodSignature.getMethod(), CacheEvictor.class);
        if (cacheEvictor != null) {
            AbstractCacheAnnotationProcessor processor = AbstractCacheAnnotationProcessor.getProcessor(proceedingJoinPoint, cacheEvictor);
            return processor.process(proceedingJoinPoint);
        }
        return proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
    }
}