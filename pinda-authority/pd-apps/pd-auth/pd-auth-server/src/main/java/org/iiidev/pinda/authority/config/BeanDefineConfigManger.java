package org.iiidev.pinda.authority.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * BeanDefineConfigManger
 *
 * @Author IIIDelay
 * @Date 2023/8/20 12:35
 **/
@Configuration
public class BeanDefineConfigManger {
    /**
     * 配置feign日志
     *
     * @return Logger.Level
     */
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
