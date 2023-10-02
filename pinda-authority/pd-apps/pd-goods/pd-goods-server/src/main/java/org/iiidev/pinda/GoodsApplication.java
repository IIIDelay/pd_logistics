package org.iiidev.pinda;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.ConfigurableEnvironment;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 权限服务启动类
 */
@SpringBootApplication
@EnableFeignClients(value = {
    "org.iiidev.pinda.*",
})
@Slf4j
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class GoodsApplication {
    public static void main(String[] args) throws UnknownHostException {
        ConfigurableApplicationContext context = SpringApplication.run(GoodsApplication.class, args);
        ConfigurableEnvironment environment = context.getEnvironment();
        String appName = environment.getProperty("spring.application.name");
        String port = environment.getProperty("server.port");
        String hostAddress = InetAddress
            .getLocalHost()
            .getHostAddress();
        log.info("应用{}启动成功!knife4j地址: http://{}:{}/doc.html", appName, hostAddress, port);
        log.info("应用{}启动成功!swagger2地址: http://{}:{}/swagger-ui.html", appName, hostAddress, port);
    }
}