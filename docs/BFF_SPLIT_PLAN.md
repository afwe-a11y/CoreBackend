# Kronos-BFF 拆分与本地部署方案

## 📋 目标

将 `kronos-service` 从 CoreBackend 单体项目中拆分为独立的 BFF 项目，并在本地搭建完整的前后端调用链路。

---

## 🏗️ 架构设计

### 拆分后的项目结构

```
┌─────────────────────────────────────────────────────────────────┐
│                         本地开发环境                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  ┌──────────┐    HTTP     ┌──────────────┐                      │
│  │ Frontend │ ──────────▶ │    NGINX     │                      │
│  │  Vue/    │             │  (Port 80)   │                      │
│  │  React   │             └──────┬───────┘                      │
│  └──────────┘                    │                               │
│                                   │                               │
│                    ┌──────────────┴──────────────┐              │
│                    │                               │              │
│                    ▼                               ▼              │
│         ┌─────────────────────┐       ┌──────────────────────┐ │
│         │   kronos-bff        │       │   Static Files       │ │
│         │   (Port 8082)       │       │   /dist              │ │
│         │   - Auth API        │       └──────────────────────┘ │
│         │   - Device API      │                                 │
│         │   - Token管理       │                                 │
│         └──────────┬──────────┘                                 │
│                    │ Feign/HTTP                                  │
│                    │                                             │
│         ┌──────────┴──────────────────────────┐                │
│         │                                       │                │
│         ▼                                       ▼                │
│  ┌─────────────────┐                  ┌─────────────────┐      │
│  │  iam-service    │                  │ device-service  │      │
│  │  (Port 8080)    │                  │  (Port 8081)    │      │
│  │  - 用户管理      │                  │  - 设备管理      │      │
│  │  - 组织管理      │                  │  - 产品管理      │      │
│  │  - 权限管理      │                  │  - 网关管理      │      │
│  └─────────┬───────┘                  └─────────┬───────┘      │
│            │                                     │               │
│            ▼                                     ▼               │
│     ┌────────────┐                       ┌────────────┐         │
│     │  MySQL     │                       │  MySQL     │         │
│     │  (IAM DB)  │                       │ (Device DB)│         │
│     └────────────┘                       └────────────┘         │
│                                                                   │
│            ┌────────────────────────────────┐                   │
│            │         Nacos (8848)           │                   │
│            │      服务注册与发现              │                   │
│            └────────────────────────────────┘                   │
│                                                                   │
│            ┌────────────────────────────────┐                   │
│            │      Redis (6379)              │                   │
│            │      Token存储                  │                   │
│            └────────────────────────────────┘                   │
└─────────────────────────────────────────────────────────────────┘
```

---

## 📁 拆分步骤

### Step 1: 创建独立的 kronos-bff 项目

在 CoreBackend 同级目录创建新项目：

```bash
cd /Users/sirgan/Downloads
mkdir kronos-bff
cd kronos-bff
```

### Step 2: 初始化项目结构

```
kronos-bff/
├── pom.xml                          # 独立的父POM
├── .gitignore
├── README.md
├── docs/
│   └── API.md
├── kronos-model/                    # 领域模型
├── kronos-api/                      # API DTO
├── kronos-interfaces/               # 接口定义（Feign客户端）
├── kronos-application/              # 应用服务层
├── kronos-infrastructure/           # 基础设施（Feign实现、Redis）
├── kronos-controller/               # REST控制器
└── kronos-main/                     # 启动模块
    ├── src/main/java/.../KronosBffApplication.java
    └── src/main/resources/
        ├── application.yml
        ├── application-dev.yml
        └── application-prod.yml
```

### Step 3: 迁移代码

从 `CoreBackend/kronos-service/` 复制所有模块到 `kronos-bff/`：

```bash
cp -r /Users/sirgan/Downloads/CoreBackend/kronos-service/* /Users/sirgan/Downloads/kronos-bff/
```

### Step 4: 修改 POM 依赖

**新的 kronos-bff/pom.xml**：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.4</version>
        <relativePath/>
    </parent>

    <groupId>com.tenghe</groupId>
    <artifactId>kronos-bff</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>pom</packaging>
    <name>Kronos BFF</name>
    <description>Kronos Backend For Frontend Service</description>

    <properties>
        <java.version>17</java.version>
        <spring-cloud.version>2023.0.1</spring-cloud.version>
        <redisson.version>3.27.2</redisson.version>
    </properties>

    <modules>
        <module>kronos-model</module>
        <module>kronos-api</module>
        <module>kronos-interfaces</module>
        <module>kronos-application</module>
        <module>kronos-infrastructure</module>
        <module>kronos-controller</module>
        <module>kronos-main</module>
    </modules>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <dependency>
                <groupId>org.redisson</groupId>
                <artifactId>redisson-spring-boot-starter</artifactId>
                <version>${redisson.version}</version>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <pluginManagement>
            <plugins>
                <plugin>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-maven-plugin</artifactId>
                </plugin>
            </plugins>
        </pluginManagement>
    </build>
</project>
```

### Step 5: 更新 CoreBackend/pom.xml

从 CoreBackend 父POM中移除 kronos-service 模块：

```xml
<modules>
    <module>iam-service</module>
    <module>device-service</module>
    <!-- <module>kronos-service</module> 移除 -->
</modules>
```

---

## 🔧 配置服务间调用

### kronos-bff 配置

**kronos-main/src/main/resources/application-dev.yml**：

```yaml
server:
  port: 8082

spring:
  application:
    name: kronos-bff
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        namespace: dev
        group: DEFAULT_GROUP
  data:
    redis:
      host: localhost
      port: 6379
      database: 0
      password: 
      timeout: 3000ms

# Feign 配置
feign:
  client:
    config:
      default:
        connectTimeout: 5000
        readTimeout: 10000
        loggerLevel: FULL
  
# 基底服务地址（通过Nacos服务发现）
downstream:
  services:
    iam-service:
      name: iam-service
      path: /api
    device-service:
      name: device-service
      path: /api

logging:
  level:
    com.tenghe.corebackend.kronos: DEBUG
    com.tenghe.corebackend.kronos.infrastructure.feign: DEBUG
```

### iam-service 配置

**iam-main/src/main/resources/application-dev.yml**：

```yaml
server:
  port: 8080

spring:
  application:
    name: iam-service
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        namespace: dev
        group: DEFAULT_GROUP
```

### device-service 配置

**device-main/src/main/resources/application-dev.yml**：

```yaml
server:
  port: 8081

spring:
  application:
    name: device-service
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        namespace: dev
        group: DEFAULT_GROUP
```

---

## 🌐 NGINX 配置

### 安装 NGINX (macOS)

```bash
brew install nginx
```

### 配置文件

**/usr/local/etc/nginx/nginx.conf** 或 **/opt/homebrew/etc/nginx/nginx.conf**：

```nginx
http {
    include       mime.types;
    default_type  application/octet-stream;

    # 日志格式
    log_format main '$remote_addr - $remote_user [$time_local] "$request" '
                    '$status $body_bytes_sent "$http_referer" '
                    '"$http_user_agent" "$http_x_forwarded_for"';

    access_log  /usr/local/var/log/nginx/access.log  main;
    error_log   /usr/local/var/log/nginx/error.log   warn;

    sendfile        on;
    keepalive_timeout  65;

    # 上游服务定义
    upstream kronos_bff {
        server 127.0.0.1:8082 max_fails=3 fail_timeout=30s;
    }

    upstream iam_service {
        server 127.0.0.1:8080 max_fails=3 fail_timeout=30s;
    }

    upstream device_service {
        server 127.0.0.1:8081 max_fails=3 fail_timeout=30s;
    }

    server {
        listen       80;
        server_name  localhost;

        # 前端静态资源
        location / {
            root   /Users/sirgan/Downloads/frontend/dist;
            index  index.html;
            try_files $uri $uri/ /index.html;
        }

        # BFF API 路由（推荐：前端统一调用此路径）
        location /api/ {
            proxy_pass http://kronos_bff/api/;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
            
            # 超时配置
            proxy_connect_timeout 10s;
            proxy_send_timeout 30s;
            proxy_read_timeout 30s;
            
            # CORS（如果需要）
            add_header 'Access-Control-Allow-Origin' '*' always;
            add_header 'Access-Control-Allow-Methods' 'GET, POST, PUT, DELETE, OPTIONS' always;
            add_header 'Access-Control-Allow-Headers' 'Authorization, Content-Type' always;
            
            if ($request_method = 'OPTIONS') {
                return 204;
            }
        }

        # 可选：直接暴露基底服务（用于调试）
        location /iam-api/ {
            proxy_pass http://iam_service/api/;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        }

        location /device-api/ {
            proxy_pass http://device_service/api/;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        }

        # 健康检查
        location /health {
            access_log off;
            return 200 "healthy\n";
            add_header Content-Type text/plain;
        }
    }
}
```

---

## 🚀 本地启动流程

### 1. 启动基础设施

```bash
# 启动 MySQL
mysql.server start

# 启动 Redis
redis-server

# 启动 Nacos
cd /path/to/nacos/bin
sh startup.sh -m standalone
```

### 2. 启动基底服务

**Terminal 1 - IAM Service**：

```bash
cd /Users/sirgan/Downloads/CoreBackend/iam-service/iam-main
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

**Terminal 2 - Device Service**：

```bash
cd /Users/sirgan/Downloads/CoreBackend/device-service/device-main
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 3. 启动 BFF 服务

**Terminal 3 - Kronos BFF**：

```bash
cd /Users/sirgan/Downloads/kronos-bff/kronos-main
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 4. 启动 NGINX

```bash
# 测试配置
nginx -t

# 启动
nginx

# 或重新加载配置
nginx -s reload

# 停止
nginx -s stop
```

### 5. 验证服务

```bash
# 检查 Nacos 服务注册
open http://localhost:8848/nacos
# 用户名/密码: nacos/nacos

# 检查各服务健康状态
curl http://localhost:8080/actuator/health  # iam-service
curl http://localhost:8081/actuator/health  # device-service
curl http://localhost:8082/actuator/health  # kronos-bff

# 通过 NGINX 访问
curl http://localhost/api/v1/auth/captcha
```

---

## 🔍 完整调用链路测试

### 场景1: 用户登录

```bash
# 1. 获取验证码
curl http://localhost/api/v1/auth/captcha

# 2. 登录
curl -X POST http://localhost/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "identifier": "admin",
    "password": "password123",
    "captcha": "1234",
    "captchaKey": "xxx"
  }'

# 响应:
# {
#   "code": "200",
#   "data": {
#     "token": "xxx",
#     "userId": "1",
#     "username": "admin"
#   }
# }
```

**调用链路**：

```
Frontend → NGINX (80) → kronos-bff (8082) → iam-service (8080) → MySQL
```

### 场景2: 查询设备列表

```bash
curl -X GET "http://localhost/api/v1/devices?page=1&size=10" \
  -H "Authorization: Bearer {token}"
```

**调用链路**：

```
Frontend → NGINX (80) → kronos-bff (8082) 
    ├─→ iam-service (8080) [验证Token]
    └─→ device-service (8081) [查询设备]
```

---

## 📊 端口规划总结

| 服务             | 端口   | 说明        |
|----------------|------|-----------|
| NGINX          | 80   | 前端入口，统一网关 |
| iam-service    | 8080 | IAM基底服务   |
| device-service | 8081 | 设备基底服务    |
| kronos-bff     | 8082 | BFF编排层    |
| Nacos          | 8848 | 服务注册中心    |
| Redis          | 6379 | Token存储   |
| MySQL          | 3306 | 数据库       |

---

## 🎯 前端配置

### 开发环境 (.env.development)

```bash
# 统一通过 NGINX 访问
VITE_API_BASE_URL=http://localhost/api
```

### 生产环境 (.env.production)

```bash
VITE_API_BASE_URL=https://your-domain.com/api
```

### Axios 配置

```javascript
import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000,
});

// 请求拦截器
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// 响应拦截器
api.interceptors.response.use(
  response => response.data,
  error => {
    if (error.response?.status === 401) {
      // 跳转登录
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;
```

---

## 🛠️ 开发脚本

### start-all.sh

```bash
#!/bin/bash

echo "🚀 启动本地开发环境..."

# 启动基础设施
echo "📦 启动 Redis..."
redis-server &

echo "📦 启动 Nacos..."
cd /path/to/nacos/bin && sh startup.sh -m standalone

# 等待 Nacos 启动
sleep 10

# 启动基底服务
echo "🔧 启动 iam-service..."
cd /Users/sirgan/Downloads/CoreBackend/iam-service/iam-main
mvn spring-boot:run -Dspring-boot.run.profiles=dev &

echo "🔧 启动 device-service..."
cd /Users/sirgan/Downloads/CoreBackend/device-service/device-main
mvn spring-boot:run -Dspring-boot.run.profiles=dev &

# 等待基底服务启动
sleep 15

# 启动 BFF
echo "🌐 启动 kronos-bff..."
cd /Users/sirgan/Downloads/kronos-bff/kronos-main
mvn spring-boot:run -Dspring-boot.run.profiles=dev &

# 启动 NGINX
echo "🌍 启动 NGINX..."
nginx

echo "✅ 所有服务已启动！"
echo "📍 访问地址: http://localhost"
```

### stop-all.sh

```bash
#!/bin/bash

echo "🛑 停止所有服务..."

# 停止 NGINX
nginx -s stop

# 停止 Java 进程
pkill -f "iam-main"
pkill -f "device-main"
pkill -f "kronos-main"

# 停止 Nacos
cd /path/to/nacos/bin && sh shutdown.sh

# 停止 Redis
redis-cli shutdown

echo "✅ 所有服务已停止！"
```

---

## 📝 注意事项

1. **服务发现**：所有服务必须注册到 Nacos，BFF 通过服务名调用基底服务
2. **Token 管理**：Token 由 kronos-bff 生成并存储在 Redis，基底服务不感知 Token
3. **CORS**：如果前端独立部署（非 NGINX 代理），需要在 BFF 配置 CORS
4. **日志**：建议统一日志格式，便于链路追踪
5. **监控**：生产环境建议接入 Prometheus + Grafana
6. **安全**：生产环境 NGINX 需配置 HTTPS、限流、防火墙规则

---

## 🔄 迁移检查清单

- [ ] 复制 kronos-service 代码到独立项目
- [ ] 修改 POM 依赖，移除对 CoreBackend 父POM的依赖
- [ ] 更新 application.yml 配置（Nacos、Redis）
- [ ] 配置 Feign 客户端指向基底服务
- [ ] 安装并配置 NGINX
- [ ] 测试服务注册到 Nacos
- [ ] 测试 BFF → IAM 调用链路
- [ ] 测试 BFF → Device 调用链路
- [ ] 测试前端 → NGINX → BFF 完整链路
- [ ] 编写启动/停止脚本
- [ ] 更新项目文档

---

## 📚 参考文档

- [Spring Cloud OpenFeign](https://spring.io/projects/spring-cloud-openfeign)
- [Nacos 服务发现](https://nacos.io/zh-cn/docs/quick-start-spring-cloud.html)
- [NGINX 反向代理](https://nginx.org/en/docs/http/ngx_http_proxy_module.html)
- [Redisson 分布式锁](https://github.com/redisson/redisson/wiki)
