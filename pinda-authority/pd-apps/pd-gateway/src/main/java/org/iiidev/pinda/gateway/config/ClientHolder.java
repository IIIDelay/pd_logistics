package org.iiidev.pinda.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.function.FailableFunction;
import org.iiidev.pinda.base.Result;
import org.iiidev.pinda.exception.BizException;
import org.iiidev.pinda.gateway.api.ResourceApi;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.Future;

// 这个类的作用，是将Client包装一层，在这里做异步处理
@Slf4j
@Component
public class ClientHolder implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Lazy // 重点：这里必须使用@Lazy
    @Resource
    private ResourceApi resourceApi;

    @Async("taskExecutor") // 重点：这里必须在异步线程中执行，执行结果返回Future
    public Future<Result<List>> list() {
        Result<List> list = resourceApi.list();
        return new AsyncResult<>(list);
    }

    public static <OUT, EX extends Throwable> OUT get(FailableFunction<ClientHolder, ? extends Future<OUT>, EX> function) {
        try {
            ClientHolder clientHolder = applicationContext.getBean(ClientHolder.class);
            return function.apply(clientHolder).get();
        } catch (Throwable throwable) {
            throw new BizException("openFeign error ", throwable);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}