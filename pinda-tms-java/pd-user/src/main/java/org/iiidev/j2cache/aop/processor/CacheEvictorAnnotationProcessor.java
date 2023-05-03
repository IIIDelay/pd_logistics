package org.iiidev.j2cache.aop.processor;
import org.iiidev.j2cache.annotation.Cache;
import org.iiidev.j2cache.annotation.CacheEvictor;
import org.iiidev.j2cache.model.AnnotationInfo;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * 失效缓存注解处理器
 */
public class CacheEvictorAnnotationProcessor extends AbstractCacheAnnotationProcessor {
    private static final Logger logger = LoggerFactory.getLogger(CacheEvictorAnnotationProcessor.class);
    private List<AnnotationInfo<Cache>> cacheList = new ArrayList<>();

    /**
     * 初始化缓存注解处理器
     *
     * @param proceedingJoinPoint 切点
     * @param annotation         注解
     */
    public CacheEvictorAnnotationProcessor(ProceedingJoinPoint proceedingJoinPoint, Annotation annotation) {
        super();
        CacheEvictor cacheEvictor = (CacheEvictor) annotation;
        for (Cache cache : cacheEvictor.value()) {
            AnnotationInfo<Cache> annotationInfo = getAnnotationInfo(proceedingJoinPoint, cache);
            cacheList.add(annotationInfo);
        }
    }

    /**
     * 处理
     *
     * @param proceedingJoinPoint 切点
     * @return 处理结果
     */
    public Object process(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        for (AnnotationInfo<Cache> item : cacheList) {
            try {
                cacheChannel.evict(item.getRegion(), item.getKey());
            } catch (Throwable throwable) {
                logger.error("失效缓存时出错");
            }
        }
        return proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
    }
}