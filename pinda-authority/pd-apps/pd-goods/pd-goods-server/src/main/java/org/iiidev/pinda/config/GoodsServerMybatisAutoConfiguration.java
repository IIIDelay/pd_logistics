package org.iiidev.pinda.config;

import lombok.extern.slf4j.Slf4j;
import org.iiidev.pinda.database.datasource.BaseMybatisConfiguration;
import org.iiidev.pinda.database.properties.DatabaseProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 配置一些拦截器
 */
@Configuration
@Slf4j

public class GoodsServerMybatisAutoConfiguration extends BaseMybatisConfiguration {
    public GoodsServerMybatisAutoConfiguration(DatabaseProperties databaseProperties) {
        super(databaseProperties);
    }
}