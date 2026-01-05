package com.tenghe.corebackend.device;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.tenghe.corebackend.device.infrastructure.persistence.mapper")
public class DeviceServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(DeviceServiceApplication.class, args);
  }
}
