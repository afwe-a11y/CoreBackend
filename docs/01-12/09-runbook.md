# 09-runbook.md

## 启动前提
- JDK 1.8（由 Maven 配置推断）。
- Spring Boot 2.6.13（由依赖管理版本推断）。

## 启动方式
- 方式一：直接运行 `CoreBackendApplication`（Spring Boot 主类）。
- 方式二：使用 Maven 运行（ASSUMED：未提供 README/脚本）。

## 运行参数
- HTTP 端口：`8080`。

## 健康检查
- UNKNOWN：未发现 Actuator 或健康检查端点配置。

## 常见问题
- 组织/成员数据丢失：当前为内存存储，进程重启后数据丢失。
- 端口冲突：请检查 `server.port` 配置。

## Evidence
- pom.xml | properties | JDK 与 Spring Boot 版本 | L14-L19
- iam-main/src/main/java/com/tenghe/corebackend/CoreBackendApplication.java | CoreBackendApplication | 启动入口 | L6-L11
- iam-main/src/main/resources/application.properties | server.port | 端口配置 | L1-L2
- iam-infrastructure/src/main/java/com/tenghe/corebackend/infrastructure/inmemory/InMemoryOrganizationRepository.java | InMemoryOrganizationRepository | 内存存储实现 | L11-L67

## UNKNOWN/ASSUMED
- Maven 启动命令 ASSUMED：代码中无脚本与 README。
- 日志级别/日志文件路径 UNKNOWN：未见日志配置。
- 生产部署方式 UNKNOWN：未见部署文档。
