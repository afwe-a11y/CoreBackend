# 09-runbook.md

## 运行前提

- JDK 17
- MySQL（IAM 与 Device 使用不同数据库名）

## 环境变量

- IAM：IAM_DB_URL / IAM_DB_USERNAME / IAM_DB_PASSWORD
- Device：DEVICE_DB_URL / DEVICE_DB_USERNAME / DEVICE_DB_PASSWORD

## 启动方式

- IAM 服务：运行 `com.tenghe.corebackend.iam.IamServiceApplication`
- Device 服务：运行 `com.tenghe.corebackend.device.DeviceServiceApplication`

## MyBatis 配置

- mapper-locations：classpath*:mapper/**/*.xml
- map-underscore-to-camel-case: true

## 运维注意事项

- 软删除：删除类操作只更新 deleted 标记。
- Token/Captcha/Email/EmailCode/Telemetry 使用内存实现，重启后数据丢失。
- 设备遥测查询依赖内存仓库，未持久化。

## Evidence

- /Users/sirgan/Downloads/CoreBackend/iam-service/iam-main/src/main/resources/application.yml | datasource | IAM
  数据源环境变量 | L1-L6
- /Users/sirgan/Downloads/CoreBackend/device-service/device-main/src/main/resources/application.yml | datasource |
  Device 数据源环境变量 | L1-L6
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-main/src/main/java/com/tenghe/corebackend/iam/IamServiceApplication.java |
IamServiceApplication | IAM 启动入口 | L8-L16
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-main/src/main/java/com/tenghe/corebackend/device/DeviceServiceApplication.java |
DeviceServiceApplication | Device 启动入口 | L7-L10
- /Users/sirgan/Downloads/CoreBackend/iam-service/iam-main/src/main/resources/application.yml | mybatis | IAM MyBatis
  配置 | L8-L11
- /Users/sirgan/Downloads/CoreBackend/device-service/device-main/src/main/resources/application.yml | mybatis | Device
  MyBatis 配置 | L8-L11
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-infrastructure/src/main/java/com/tenghe/corebackend/iam/infrastructure/inmemory/InMemoryTokenService.java |
InMemoryTokenService | Token 内存实现 | L9-L54
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-infrastructure/src/main/java/com/tenghe/corebackend/device/infrastructure/inmemory/InMemoryTelemetryRepository.java |
InMemoryTelemetryRepository | Telemetry 内存实现 | L11-L31

## UNKNOWN/ASSUMED

- UNKNOWN：生产环境日志/监控/告警体系未在仓库中体现。
