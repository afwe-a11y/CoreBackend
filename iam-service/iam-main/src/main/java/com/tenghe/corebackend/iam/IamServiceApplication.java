package com.tenghe.corebackend.iam;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * IAM 服务启动入口。
 */
@SpringBootApplication
@MapperScan("com.tenghe.corebackend.iam.infrastructure.persistence.mapper")
public class IamServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IamServiceApplication.class, args);
    }

}
