# Kronos-BFF 拆分与本地部署 - 快速开始

## 🎯 目标

将 `kronos-service` 拆分为独立的 BFF 项目，并在本地搭建完整的调用链路：

```
Frontend → NGINX (80) → Kronos-BFF (8082) → [IAM (8080), Device (8081)]
```

---

## 📋 前置条件检查

在开始之前，请确保已安装以下软件：

```bash
# 检查 Java
java -version  # 需要 JDK 17+

# 检查 Maven
mvn -version   # 需要 Maven 3.8+

# 检查 MySQL
mysql --version  # 需要 MySQL 8.0+

# 检查 Redis
redis-cli --version  # 需要 Redis 6.0+

# 检查 NGINX（如果未安装）
nginx -v

# macOS 安装 NGINX
brew install nginx
```

---

## 🚀 快速开始（5步完成）

### Step 1: 拆分 Kronos-BFF 项目

```bash
cd /Users/sirgan/Downloads/CoreBackend
bash scripts/split-kronos-bff.sh
```

**预期输出**：

```
🚀 开始拆分 Kronos-BFF 项目...
📁 创建目标目录: /Users/sirgan/Downloads/kronos-bff
📦 复制模块...
📝 创建独立的 pom.xml...
🔧 更新子模块 POM...
✅ 拆分完成！
```

### Step 2: 构建 Kronos-BFF 项目

```bash
cd /Users/sirgan/Downloads/kronos-bff
mvn clean install -DskipTests
```

**预期输出**：

```
[INFO] BUILD SUCCESS
[INFO] Total time: XX s
```

### Step 3: 配置 Nacos 和 Redis

确保 Nacos 和 Redis 已启动：

```bash
# 启动 Redis（后台运行）
redis-server --daemonize yes

# 启动 Nacos（需要先下载 Nacos）
# 下载地址: https://github.com/alibaba/nacos/releases
cd /path/to/nacos/bin
sh startup.sh -m standalone
```

**验证 Nacos**：

- 访问 http://localhost:8848/nacos
- 用户名/密码: `nacos/nacos`

### Step 4: 配置 NGINX

```bash
# 复制 NGINX 配置
sudo cp /Users/sirgan/Downloads/CoreBackend/scripts/nginx.conf /usr/local/etc/nginx/nginx.conf

# 或者在 Homebrew 安装的路径
sudo cp /Users/sirgan/Downloads/CoreBackend/scripts/nginx.conf /opt/homebrew/etc/nginx/nginx.conf

# 测试配置
nginx -t

# 启动 NGINX
sudo nginx
```

**注意**：修改 `nginx.conf` 中的前端静态资源路径：

```nginx
location / {
    root   /Users/sirgan/Downloads/frontend/dist;  # 修改为你的前端路径
    ...
}
```

### Step 5: 启动所有服务

```bash
cd /Users/sirgan/Downloads/CoreBackend

# 方式1: 使用一键启动脚本（推荐）
bash scripts/start-all.sh

# 方式2: 手动启动各服务
# Terminal 1 - IAM Service
cd iam-service/iam-main
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Terminal 2 - Device Service
cd device-service/device-main
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Terminal 3 - Kronos BFF
cd /Users/sirgan/Downloads/kronos-bff/kronos-main
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

---

## ✅ 验证部署

### 1. 检查服务状态

```bash
# 运行测试脚本
bash /Users/sirgan/Downloads/CoreBackend/scripts/test-chain.sh
```

### 2. 手动验证

```bash
# 检查 Redis
redis-cli ping
# 预期输出: PONG

# 检查 Nacos
curl http://localhost:8848/nacos
# 预期: 返回 Nacos 控制台页面

# 检查 IAM Service
curl http://localhost:8080/actuator/health
# 预期: {"status":"UP"}

# 检查 Device Service
curl http://localhost:8081/actuator/health
# 预期: {"status":"UP"}

# 检查 Kronos BFF
curl http://localhost:8082/actuator/health
# 预期: {"status":"UP"}

# 检查 NGINX
curl http://localhost/health
# 预期: healthy

# 测试完整链路（获取验证码）
curl http://localhost/api/v1/auth/captcha
# 预期: 返回 JSON 包含 captchaId 和 imageBase64
```

### 3. 检查服务注册

访问 Nacos 控制台：http://localhost:8848/nacos

应该看到以下服务已注册：

- ✅ `iam-service`
- ✅ `device-service`
- ✅ `kronos-bff`

---

## 🌐 访问地址

| 服务                 | 地址                          | 说明          |
|--------------------|-----------------------------|-------------|
| **前端应用**           | http://localhost            | 通过 NGINX 访问 |
| **BFF API**        | http://localhost/api        | 前端统一调用入口    |
| **IAM Service**    | http://localhost:8080       | 基底服务（直连）    |
| **Device Service** | http://localhost:8081       | 基底服务（直连）    |
| **Kronos BFF**     | http://localhost:8082       | BFF 服务（直连）  |
| **Nacos Console**  | http://localhost:8848/nacos | 服务注册中心      |

---

## 🧪 测试完整链路

### 场景1: 用户登录

```bash
# 1. 获取验证码
curl http://localhost/api/v1/auth/captcha

# 2. 登录（使用实际的验证码）
curl -X POST http://localhost/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "identifier": "admin",
    "password": "your_password",
    "captcha": "1234",
    "captchaKey": "captcha_key_from_step1"
  }'
```

**调用链路**：

```
curl → NGINX (80) → Kronos-BFF (8082) → IAM Service (8080) → MySQL
```

### 场景2: 查询设备列表

```bash
# 使用登录返回的 token
curl -X GET "http://localhost/api/v1/devices?page=1&size=10" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

**调用链路**：

```
curl → NGINX (80) → Kronos-BFF (8082)
    ├─→ IAM Service (8080) [验证 Token]
    └─→ Device Service (8081) [查询设备]
```

---

## 🛑 停止服务

```bash
# 使用一键停止脚本
bash /Users/sirgan/Downloads/CoreBackend/scripts/stop-all.sh

# 或手动停止
nginx -s stop
pkill -f "iam-main"
pkill -f "device-main"
pkill -f "kronos-main"
cd /path/to/nacos/bin && sh shutdown.sh
redis-cli shutdown
```

---

## 📂 项目结构（拆分后）

```
/Users/sirgan/Downloads/
├── CoreBackend/                    # 中台基底服务
│   ├── iam-service/               # IAM 服务（端口 8080）
│   ├── device-service/            # 设备服务（端口 8081）
│   ├── scripts/                   # 部署脚本
│   │   ├── split-kronos-bff.sh   # 拆分脚本
│   │   ├── start-all.sh          # 一键启动
│   │   ├── stop-all.sh           # 一键停止
│   │   ├── test-chain.sh         # 链路测试
│   │   └── nginx.conf            # NGINX 配置
│   └── docs/                      # 文档
│       ├── BFF_SPLIT_PLAN.md     # 详细拆分方案
│       ├── QUICK_START.md        # 本文档
│       └── FRONTEND_CONFIG.md    # 前端配置指南
│
└── kronos-bff/                    # BFF 独立项目（端口 8082）
    ├── pom.xml                    # 独立的父 POM
    ├── kronos-model/
    ├── kronos-api/
    ├── kronos-interfaces/
    ├── kronos-application/
    ├── kronos-infrastructure/
    ├── kronos-controller/
    └── kronos-main/
        └── src/main/resources/
            └── application-dev.yml  # 需要配置 Nacos 地址
```

---

## ⚙️ 配置说明

### Kronos-BFF 配置

编辑 `/Users/sirgan/Downloads/kronos-bff/kronos-main/src/main/resources/application-dev.yml`：

```yaml
server:
  port: 8082

spring:
  application:
    name: kronos-bff
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848  # Nacos 地址
        namespace: dev
        group: DEFAULT_GROUP

# Feign 配置
feign:
  client:
    config:
      default:
        connectTimeout: 5000
        readTimeout: 10000
```

### IAM Service 配置

确保 `CoreBackend/iam-service/iam-main/src/main/resources/application-dev.yml` 包含：

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
```

### Device Service 配置

确保 `CoreBackend/device-service/device-main/src/main/resources/application-dev.yml` 包含：

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
```

---

## 🔧 常见问题

### Q1: 端口被占用

```bash
# 查看端口占用
lsof -i :8080
lsof -i :8081
lsof -i :8082
lsof -i :80

# 杀死进程
kill -9 <PID>
```

### Q2: Nacos 连接失败

**检查**：

1. Nacos 是否启动：`curl http://localhost:8848/nacos`
2. 配置文件中的 Nacos 地址是否正确
3. 防火墙是否阻止连接

### Q3: NGINX 启动失败

```bash
# 检查配置
nginx -t

# 查看错误日志
tail -f /usr/local/var/log/nginx/error.log

# 常见问题：端口 80 被占用
sudo lsof -i :80
```

### Q4: 服务无法注册到 Nacos

**检查**：

1. `spring.cloud.nacos.discovery.server-addr` 配置是否正确
2. Nacos 是否正常运行
3. 查看服务日志：`tail -f logs/iam-service.log`

### Q5: 前端请求 404

**检查**：

1. NGINX 配置中的 `proxy_pass` 是否正确
2. BFF 服务是否正常运行
3. 前端请求路径是否以 `/api/` 开头

---

## 📊 监控与日志

### 查看服务日志

```bash
# 实时查看所有服务日志
tail -f /Users/sirgan/Downloads/CoreBackend/logs/*.log

# 查看特定服务
tail -f /Users/sirgan/Downloads/CoreBackend/logs/iam-service.log
tail -f /Users/sirgan/Downloads/CoreBackend/logs/device-service.log
tail -f /Users/sirgan/Downloads/CoreBackend/logs/kronos-bff.log
```

### 查看 NGINX 日志

```bash
# 访问日志
tail -f /usr/local/var/log/nginx/access.log

# 错误日志
tail -f /usr/local/var/log/nginx/error.log
```

### 查看 Redis 状态

```bash
redis-cli info
redis-cli monitor  # 实时监控命令
```

---

## 🎓 下一步

1. **前端配置**：参考 [FRONTEND_CONFIG.md](./FRONTEND_CONFIG.md)
2. **详细方案**：查看 [BFF_SPLIT_PLAN.md](./BFF_SPLIT_PLAN.md)
3. **业务开发**：基于 BFF 架构开发新功能
4. **生产部署**：配置 HTTPS、负载均衡、监控告警

---

## 📞 获取帮助

如果遇到问题：

1. 查看日志文件
2. 运行测试脚本：`bash scripts/test-chain.sh`
3. 检查 Nacos 控制台服务注册情况
4. 参考详细文档：`docs/BFF_SPLIT_PLAN.md`

---

## ✅ 检查清单

拆分完成后，请确认以下项目：

- [ ] kronos-bff 项目已创建并构建成功
- [ ] 所有服务已注册到 Nacos
- [ ] NGINX 配置正确并启动
- [ ] 可以通过 `http://localhost/api` 访问 BFF
- [ ] 登录接口测试通过
- [ ] 设备查询接口测试通过
- [ ] 前端可以正常调用后端 API
- [ ] Token 验证流程正常
- [ ] 日志输出正常

全部完成后，你的本地开发环境就搭建完成了！🎉
