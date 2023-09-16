package org.iiidev.pinda.gateway.api.client;

import cn.hutool.core.lang.Assert;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.function.FailableFunction;
import org.iiidev.pinda.authority.dto.auth.ResourceQueryDTO;
import org.iiidev.pinda.authority.entity.auth.Resource;
import org.iiidev.pinda.base.Result;
import org.iiidev.pinda.exception.BizException;
import org.iiidev.pinda.gateway.api.ResourceApi;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Future;
import java.util.function.Function;

// 这个类的作用，是将Client包装一层，在这里做异步处理
@Slf4j
@Component
@Getter
public class ClientHolder implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Lazy // 重点：这里必须使用@Lazy
    @Autowired
    private ResourceApi resourceApi;

    @Async("taskExecutor") // 重点：这里必须在异步线程中执行，执行结果返回Future
    public Future<Result<List<Resource>>> visible(ResourceQueryDTO resource) {
        Result<List<Resource>> list = resourceApi.visible(resource);
        return new AsyncResult<>(list);
    }

    @Async("taskExecutor") // 重点：这里必须在异步线程中执行，执行结果返回Future
    public <T, IN, EX extends Throwable>Future<T> returnGet(Function<ClientHolder,IN> apiFunc, FailableFunction<IN, T, EX> callFunc) {
        Assert.notNull(apiFunc, () -> BizException.wrap("input apiFunc must have value."));
        Assert.notNull(callFunc, () -> BizException.wrap("input callSupplier must have value."));
        try {
            log.info("returnGet invoke ...");
            IN apiIn = apiFunc.apply(this);
            return new AsyncResult<>(callFunc.apply(apiIn));
        } catch (Throwable e) {
            throw BizException.wrap("returnGet invoke error.");
        }
    }

    public static <IN,T, EX extends Throwable> T get(Function<ClientHolder,IN> apiFunc, FailableFunction<IN, T, EX> callFunc) {
        try {
            ClientHolder clientHolder = applicationContext.getBean(ClientHolder.class);
            return clientHolder.returnGet(apiFunc, callFunc).get();
        } catch (Throwable throwable) {
            throw new BizException("openFeign error ", throwable);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}