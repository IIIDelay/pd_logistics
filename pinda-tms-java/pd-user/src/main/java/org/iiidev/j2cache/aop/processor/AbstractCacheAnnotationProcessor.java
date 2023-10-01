package org.iiidev.j2cache.aop.processor;
import org.iiidev.j2cache.annotation.Cache;
import org.iiidev.j2cache.annotation.CacheEvictor;
import org.iiidev.j2cache.model.AnnotationInfo;
import org.iiidev.j2cache.utils.CacheKeyBuilder;
import org.iiidev.j2cache.utils.SpringApplicationContextUtils;
import net.oschina.j2cache.CacheChannel;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;
import java.lang.reflect.Method;

/**
 * 抽象注解处理器
 */
public abstract class AbstractCacheAnnotationProcessor {
    protected CacheChannel cacheChannel;

    /**
     * 初始化缓存注解处理器
     *
     * @param proceedingJoinPoint 切点
     */
    public AbstractCacheAnnotationProcessor() {
        ApplicationContext applicationContext =
                SpringApplicationContextUtils.getApplicationContext();
        cacheChannel = applicationContext.getBean(CacheChannel.class);
    }

    /**
     * 转换为注解信息
     *
     * @param cache 缓存注解
     * @return 注解信息
     */
    protected AnnotationInfo<Cache> getAnnotationInfo(ProceedingJoinPoint proceedingJoinPoint, Cache cache) {
        AnnotationInfo<Cache> annotationInfo = new AnnotationInfo<>();
        annotationInfo.setAnnotation(cache);
        annotationInfo.setRegion(cache.region());
        try {
            annotationInfo.setKey(generateKey(proceedingJoinPoint, annotationInfo.getAnnotation()));
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("生成键出错: ", e);
        }
        return annotationInfo;
    }

    /**
     * 生成key字符串
     *
     * @param cache 缓存注解
     * @return key字符串
     */
    protected String generateKey(ProceedingJoinPoint proceedingJoinPoint, Cache cache) throws IllegalAccessException {
        String key = cache.key();
        if (!StringUtils.hasText(key)) {
            String className = proceedingJoinPoint.getTarget().getClass().getSimpleName();
            MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
            Method method = methodSignature.getMethod();
            key = className + ":" + method.getName();
        }
        key = CacheKeyBuilder.generate(key, cache.params(), proceedingJoinPoint.getArgs());
        return key;
    }

    /**
     * 处理
     *
     * @param proceedingJoinPoint 切点
     * @return 处理结果
     */
    public abstract Object process(ProceedingJoinPoint proceedingJoinPoint) throws Throwable;

    /**
     * 获取注解处理器
     *
     * @param proceedingJoinPoint 切点
     * @param cache               注解
     * @return 注解处理器
     */
    public static AbstractCacheAnnotationProcessor getProcessor(ProceedingJoinPoint proceedingJoinPoint, Cache cache) {
        return new CachesAnnotationProcessor(proceedingJoinPoint, cache);
    }

    /**
     * 获取注解处理器
     *
     * @param proceedingJoinPoint 切点
     * @param cacheEvictor        注解
     * @return 注解处理器
     */
    public static AbstractCacheAnnotationProcessor getProcessor(ProceedingJoinPoint proceedingJoinPoint, CacheEvictor cacheEvictor) {
        return new CacheEvictorAnnotationProcessor(proceedingJoinPoint, cacheEvictor);
    }
}