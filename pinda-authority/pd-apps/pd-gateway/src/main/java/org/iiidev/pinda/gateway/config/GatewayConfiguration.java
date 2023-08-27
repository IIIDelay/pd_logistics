package org.iiidev.pinda.gateway.config;

import org.iiidev.pinda.auth.client.properties.AuthClientProperties;
import org.iiidev.pinda.common.config.BaseConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

/**
 * 解决跨域问题的配置类
 */
@Configuration
@EnableConfigurationProperties(AuthClientProperties.class)
public class GatewayConfiguration extends BaseConfig {
    @Bean
    public CorsWebFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 允许cookies跨域
        config.setAllowCredentials(true);
        // #允许向该服务器提交请求的URI，*表示全部允许
        // config.addAllowedOrigin("*");
        // #允许访问的头信息,*表示全部
        config.addAllowedHeader("*");
        // 预检请求的缓存时间（秒），即在这个时间段里，对于相同的跨域请求不会再预检了
        config.setMaxAge(18000L);
        // 允许提交请求的方法，*表示全部允许
        config.addAllowedMethod("*");

        config.addAllowedOriginPattern("*");

        // 配置源对象
        UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        configurationSource.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(configurationSource);
    }
}