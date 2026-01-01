package com.tenghe.corebackend.kronos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Kronos BFF 服务启动入口。
 * 设备管理前端网关服务，负责设备模型、产品、网关、设备的管理与编排。
 * BFF 层不直接访问数据库，通过 Feign 调用 device-service 实现功能。
 */
@SpringBootApplication
@EnableFeignClients(basePackages = "com.tenghe.corebackend.kronos.infrastructure.feign")
public class KronosServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(KronosServiceApplication.class, args);
  }
}
