package com.ihrm.gate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * 网关启动类
 */
@SpringBootApplication(scanBasePackages = "com.ihrm" , exclude = {DataSourceAutoConfiguration.class})
@EnableZuulProxy
//开启服务发现功能
@EnableDiscoveryClient
public class GateApplication {
    public static void main(String[] args) {
        SpringApplication.run(GateApplication.class);
    }
}
