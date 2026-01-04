# Kronos-Service 与 IAM-Service 集成说明

## 架构概述

```
┌──────────────┐     ┌─────────────────────────────────────┐     ┌──────────────┐
│   Frontend   │────▶│        kronos-service (BFF)         │────▶│ iam-service  │
│              │     │  - 登录 API                          │     │ (Foundation) │
└──────────────┘     │  - Token 管理 (Redisson/Redis)       │     │ - 凭证验证    │
                     │  - 认证拦截器                         │     │ - 用户状态    │
                     │  - 设备业务编排                       │     │ - 权限查询    │
                     └─────────────────────────────────────┘     └──────────────┘
                               │              │                         │
                               ▼              ▼                         ▼
                     ┌─────────────┐  ┌─────────────────┐      ┌─────────────────┐
                     │    Redis    │  │ device-service  │      │   MySQL (IAM)   │
                     │  (Token)    │  │  (Foundation)   │      └─────────────────┘
                     └─────────────┘  └─────────────────┘
```

### 职责划分

| 服务 | 职责 |
|---|---|
| **kronos-service (BFF)** | 应用层后端，负责登录 API、Token 生成/存储（Redis）、认证拦截、业务编排 |
| **iam-service (基底)** | 用户凭证验证、用户状态检查、权限管理、组织管理，**不管理 Token** |
| **device-service (基底)** | 设备、产品、网关、设备模型等核心业务 |

## 认证流程

### 1. 登录流程

```
Frontend → kronos-service (BFF) → iam-service → MySQL
   │              │                    │           │
   │ POST /api/v1/auth/login           │           │
   │              │                    │           │
   │              │ POST /api/auth/verify-credentials │
   │              │                    │           │
   │              │                    │ 验证用户   │
   │              │                    │ ────────▶ │
   │              │                    │           │ 查询用户表
   │              │                    │ ◀──────── │
   │              │ ◀─────────────────│           │
   │              │ { userId, username, name... } (无 Token)
   │              │                    │           │
   │              │ 生成 Token         │           │
   │              │ ────────▶ Redis    │           │
   │              │ 存储 token→userId  │           │
   │              │ ◀──────── Redis    │           │
   │ ◀────────────│                    │           │
   │ { token, userId, username }       │           │
```

### 2. 请求认证流程

```
Frontend → kronos-service (BFF) → Redis → iam-service
   │              │                  │          │
   │ GET /api/v1/devices             │          │
   │ Header: Authorization           │          │
   │              │                  │          │
   │              │ 1. 验证 Token     │          │
   │              │ ─────────────────▶          │
   │              │                  │ 查询 token│
   │              │ ◀─────────────── │          │
   │              │ userId           │          │
   │              │                  │          │
   │              │ 2. 检查用户状态   │          │
   │              │ GET /api/auth/check-user-status/{userId}
   │              │ ─────────────────────────────▶
   │              │                  │          │ 查询用户表
   │              │ ◀─────────────────────────── │
   │              │ { valid: true }  │          │
   │              │                  │          │
   │              │ 3. 继续处理业务请求           │
   │              │ ────────────▶ device-service │
```

## 技术实现

### iam-service 改造

#### 1. 添加 Redisson 依赖

**文件**: `pom.xml`
```xml
<properties>
    <redisson.version>3.27.2</redisson.version>
</properties>

<dependency>
    <groupId>org.redisson</groupId>
    <artifactId>redisson-spring-boot-starter</artifactId>
    <version>${redisson.version}</version>
</dependency>
```

#### 2. Redis 配置

**文件**: `iam-main/src/main/resources/application.yml`
```yaml
spring:
  data:
    redis:
      redisson:
        config: |
          singleServerConfig:
            address: "redis://${REDIS_HOST:localhost}:${REDIS_PORT:6379}"
            password: ${REDIS_PASSWORD:}
            database: ${REDIS_DATABASE:0}
            connectionPoolSize: 64
            connectionMinimumIdleSize: 10
            timeout: 3000
```

#### 3. RedissonTokenService 实现

**文件**: `iam-infrastructure/src/main/java/.../RedissonTokenService.java`

**特性**:
- ✅ 使用 Redisson 客户端
- ✅ Token 自动过期（24小时）
- ✅ 支持分布式部署
- ✅ 双向映射：token→userId, userId→token
- ✅ 自动续期机制

**存储结构**:
```
Redis Key                    Value       TTL
iam:token:{token}       →   userId      24h
iam:user:token:{userId} →   token       24h
```

### kronos-service 改造

#### 1. IAM Feign 客户端

**文件**: `kronos-infrastructure/src/main/java/.../IamFeignClient.java`

```java
@FeignClient(name = "iam-service", path = "/api/auth")
public interface IamFeignClient {
    @PostMapping("/login")
    IamApiResponse<LoginResponse> login(@RequestBody LoginRequest request);
    
    @GetMapping("/validate")
    IamApiResponse<Boolean> validateToken(@RequestHeader("Authorization") String auth);
    
    @PostMapping("/logout")
    IamApiResponse<Void> logout(@RequestHeader("Authorization") String auth);
}
```

#### 2. 认证拦截器

**文件**: `kronos-controller/src/main/java/.../AuthenticationInterceptor.java`

**拦截规则**:
- ✅ 拦截所有 `/api/**` 请求
- ❌ 排除 `/api/v1/auth/**` (登录接口)
- ❌ 排除 `/api/v1/health` (健康检查)

**验证流程**:
1. 提取 `Authorization` header
2. 调用 iam-service 验证 token
3. 验证失败返回 401 Unauthorized
4. 验证成功继续处理请求

#### 3. Auth Controller

**文件**: `kronos-controller/src/main/java/.../AuthController.java`

**接口**:
- `POST /api/v1/auth/login` - 转发登录请求到 iam-service
- `POST /api/v1/auth/logout` - 转发登出请求到 iam-service

## 部署配置

### 环境变量

#### iam-service
```bash
# 数据库
IAM_DB_URL=jdbc:mysql://localhost:3306/iam
IAM_DB_USERNAME=root
IAM_DB_PASSWORD=root

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=
REDIS_DATABASE=0

# Nacos
NACOS_SERVER_ADDR=localhost:8848
```

#### kronos-service
```bash
# Nacos
NACOS_SERVER_ADDR=localhost:8848
```

### 启动顺序

1. **启动 Redis**
   ```bash
   redis-server
   ```

2. **启动 Nacos**
   ```bash
   sh startup.sh -m standalone
   ```

3. **启动 iam-service** (端口 8080)
   ```bash
   cd iam-service/iam-main
   mvn spring-boot:run
   ```

4. **启动 device-service** (端口 8081)
   ```bash
   cd device-service/device-main
   mvn spring-boot:run
   ```

5. **启动 kronos-service** (端口 8082)
   ```bash
   cd kronos-service/kronos-main
   mvn spring-boot:run
   ```

## API 使用示例

### 1. 登录

**请求**:
```bash
curl -X POST http://localhost:8082/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "identifier": "admin",
    "password": "password123",
    "captcha": "1234",
    "captchaKey": "xxx"
  }'
```

**响应**:
```json
{
  "code": "200",
  "message": "success",
  "data": {
    "userId": "1",
    "username": "admin",
    "token": "a1b2c3d4e5f6g7h8i9j0",
    "requirePasswordReset": false
  }
}
```

### 2. 访问受保护接口

**请求**:
```bash
curl -X GET http://localhost:8082/api/v1/devices \
  -H "Authorization: Bearer a1b2c3d4e5f6g7h8i9j0"
```

### 3. 登出

**请求**:
```bash
curl -X POST http://localhost:8082/api/v1/auth/logout \
  -H "Authorization: Bearer a1b2c3d4e5f6g7h8i9j0"
```

## 错误处理

### 未登录
```json
{
  "code": "UNAUTHORIZED",
  "message": "未登录或登录已过期"
}
```

### Token 无效
```json
{
  "code": "INVALID_TOKEN",
  "message": "Token 无效或已过期"
}
```

### 认证服务异常
```json
{
  "code": "INTERNAL_ERROR",
  "message": "认证服务异常"
}
```

## 监控与日志

### 关键日志

**iam-service**:
```
生成 Token: userId=1, username=admin
Token 生成成功: userId=1, token=xxx
Token 验证成功: token=xxx, userId=1
注销 Token: token=xxx
```

**kronos-service**:
```
CALL iam-service login identifier=admin
CALL iam-service login costMs=123 success=true
认证拦截: method=GET uri=/api/v1/devices
Token 验证成功: uri=/api/v1/devices
```

### Redis 监控

查看 Token 存储:
```bash
redis-cli
> KEYS iam:token:*
> KEYS iam:user:token:*
> TTL iam:token:xxx
```

## 注意事项

1. **Token 过期时间**: 默认 24 小时，可在 `RedissonTokenService` 中修改
2. **自动续期**: 每次验证成功会自动续期 24 小时
3. **单点登录**: 同一用户登录会使旧 token 失效
4. **分布式支持**: 使用 Redis 存储，支持多实例部署
5. **服务发现**: 通过 Nacos 进行服务注册与发现

## 故障排查

### 问题 1: Token 验证失败
- 检查 Redis 是否正常运行
- 检查 iam-service 是否启动
- 检查 Nacos 服务发现是否正常

### 问题 2: 无法登录
- 检查数据库连接
- 检查用户密码是否正确
- 查看 iam-service 日志

### 问题 3: 拦截器不生效
- 检查 `WebMvcConfig` 是否正确注册
- 检查拦截路径配置
- 查看 kronos-service 日志
