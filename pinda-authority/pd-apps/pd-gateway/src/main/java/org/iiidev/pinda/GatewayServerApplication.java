package org.iiidev.pinda;

import org.iiidev.pinda.auth.client.EnableAuthClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients({"org.iiidev.pinda"})
@EnableAuthClient//开启授权客户端，开启后就可以使用pd-tools-jwt提供的工具类进行jwt token解析了
public class GatewayServerApplication {
    public static void main(String[] args) {
        try {
            SpringApplication.run(GatewayServerApplication.class,args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
