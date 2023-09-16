package org.iiidev.pinda.common.config;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * TheadPollConfig
 *
 * @Author IIIDelay
 * @Date 2023/9/12 23:19
 **/
@Configuration
public class TheadPoolConfig {
    @Bean("queryThreadPool")
    public Executor queryThreadPool(){
        int core = Runtime.getRuntime().availableProcessors();
        int maxCore = core * 2 + 1;
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(core, maxCore, 10, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(100000),
            new BasicThreadFactory.Builder().namingPattern("query-thread-pool-%s").build(),
            new ThreadPoolExecutor.CallerRunsPolicy());
        return poolExecutor;
    }
}
