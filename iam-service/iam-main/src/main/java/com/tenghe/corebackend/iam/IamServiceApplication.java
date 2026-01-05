package com.tenghe.corebackend.iam;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * IAM 服务启动入口。
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.tenghe.corebackend.iam.infrastructure.persistence.mapper")
public class IamServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(IamServiceApplication.class, args);
  }

}
