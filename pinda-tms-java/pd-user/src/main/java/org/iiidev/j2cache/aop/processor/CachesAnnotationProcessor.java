package org.iiidev.j2cache.aop.processor;
import org.iiidev.j2cache.annotation.Cache;
import org.iiidev.j2cache.model.AnnotationInfo;
import org.iiidev.j2cache.model.CacheHolder;
import net.oschina.j2cache.CacheObject;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.annotation.Annotation;

/**
 * Cache注解处理器
 */
public class CachesAnnotationProcessor extends AbstractCacheAnnotationProcessor {
    private static final Logger logger = LoggerFactory.getLogger(CachesAnnotationProcessor.class);
    private AnnotationInfo<Cache> annotationInfo;
    /**
     * 初始化缓存注解处理器
     *
     * @param proceedingJoinPoint 切点
     * @param annotation         注解
     */
    public CachesAnnotationProcessor(ProceedingJoinPoint proceedingJoinPoint, Annotation annotation) {
        super();
        annotationInfo = getAnnotationInfo(proceedingJoinPoint, (Cache) annotation);
    }

    /**
     * 处理
     *
     * @param proceedingJoinPoint 切点
     * @return 处理结果
     */
    public Object process(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object result = null;
        boolean readCache = false;
        //获取缓存数据
        CacheHolder cacheHolder = getCache(annotationInfo);
        if (cacheHolder.isExistsCache()) {
            result = cacheHolder.getValue();
            readCache = true;
        }
        if (!readCache) {
            //调用目标方法
            result = doInvoke(proceedingJoinPoint);
            //设置缓存数据
            setCache(result);
        }
        return result;
    }

    /**
     * 尝试获取值
     *
     * @param proceedingJoinPoint 切点
     * @return 结果
     * @throws Throwable 异常时抛出
     */
    private Object doInvoke(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object result = proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
        return result;
    }

    /**
     * 设置缓存
     *
     * @param result 数据
     * @throws Throwable 异常时抛出
     */
    private void setCache(Object result) throws Throwable {
        try {
            String key = annotationInfo.getKey();
            String region = annotationInfo.getRegion();
            cacheChannel.set(region, key, result);
        } catch (Throwable throwable) {
            logger.error("设置缓存时出错");
        }
    }

    /**
     * 读取缓存
     *
     * @param proceedingJoinPoint 切点
     * @param annotationInfo 缓存信息
     * @return 缓存
     */
    private CacheHolder getCache(AnnotationInfo<Cache> annotationInfo) {
        String region = annotationInfo.getRegion();
        String key = annotationInfo.getKey();
        Object value = null;
        boolean exists = cacheChannel.exists(region, key);
        if(exists){
            CacheObject cacheObject = cacheChannel.get(region, key);
            value = cacheObject.getValue();
            return CacheHolder.newResult(value,true);
        }
        return CacheHolder.newResult(value,false);
    }
}