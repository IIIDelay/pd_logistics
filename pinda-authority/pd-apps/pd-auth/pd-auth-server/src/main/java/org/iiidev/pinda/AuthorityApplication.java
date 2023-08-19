package org.iiidev.pinda;

import lombok.extern.slf4j.Slf4j;
import org.iiidev.pinda.auth.server.EnableAuthServer;
import org.iiidev.pinda.user.annotation.EnableLoginArgResolver;
import org.iiidev.pinda.validator.config.EnableFormValidator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
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
@EnableDiscoveryClient
@EnableAuthServer
@EnableFeignClients(value = {
    "org.iiidev.pinda",
})
@Slf4j
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@EnableLoginArgResolver
@EnableFormValidator
public class AuthorityApplication {
    public static void main(String[] args) throws UnknownHostException {
        ConfigurableApplicationContext context = SpringApplication.run(AuthorityApplication.class, args);
        ConfigurableEnvironment environment = context.getEnvironment();
        String appName = environment.getProperty("spring.application.name");
        String port = environment.getProperty("server.port");
        String hostAddress = InetAddress
            .getLocalHost()
            .getHostAddress();
        // 启动完成后在控制台提示项目启动成功，并且输出当前服务对应的swagger接口文档访问地址
        // http://localhost:8080/doc.html
        log.info("应用{}启动成功!swagger地址：http://{}:{}/doc.html", appName, hostAddress, port);
    }
}