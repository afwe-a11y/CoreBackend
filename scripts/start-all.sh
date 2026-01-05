#!/bin/bash

# 本地开发环境一键启动脚本
# 用途：启动所有必需的服务（Redis、Nacos、基底服务、BFF、NGINX）

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 配置路径（请根据实际情况修改）
CORE_BACKEND_DIR="/Users/sirgan/Downloads/CoreBackend"
KRONOS_BFF_DIR="/Users/sirgan/Downloads/kronos-bff"
NACOS_DIR="/path/to/nacos"  # 修改为你的 Nacos 安装路径
NGINX_CONF="$CORE_BACKEND_DIR/scripts/nginx.conf"

# 日志目录
LOG_DIR="$CORE_BACKEND_DIR/logs"
mkdir -p "$LOG_DIR"

echo -e "${BLUE}🚀 启动本地开发环境...${NC}"
echo ""

# 检查端口占用
check_port() {
    local port=$1
    local service=$2
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1 ; then
        echo -e "${YELLOW}⚠️  端口 $port 已被占用 ($service)${NC}"
        return 1
    fi
    return 0
}

# 等待服务启动
wait_for_service() {
    local url=$1
    local service=$2
    local max_attempts=30
    local attempt=0
    
    echo -e "${YELLOW}⏳ 等待 $service 启动...${NC}"
    while [ $attempt -lt $max_attempts ]; do
        if curl -s "$url" > /dev/null 2>&1; then
            echo -e "${GREEN}✅ $service 已启动${NC}"
            return 0
        fi
        attempt=$((attempt + 1))
        sleep 2
    done
    
    echo -e "${RED}❌ $service 启动超时${NC}"
    return 1
}

# 1. 启动 Redis
echo -e "${BLUE}📦 启动 Redis...${NC}"
if check_port 6379 "Redis"; then
    redis-server --daemonize yes
    sleep 2
    echo -e "${GREEN}✅ Redis 已启动 (端口 6379)${NC}"
else
    echo -e "${GREEN}✅ Redis 已在运行${NC}"
fi
echo ""

# 2. 启动 Nacos
echo -e "${BLUE}📦 启动 Nacos...${NC}"
if check_port 8848 "Nacos"; then
    if [ -d "$NACOS_DIR" ]; then
        cd "$NACOS_DIR/bin"
        sh startup.sh -m standalone > "$LOG_DIR/nacos.log" 2>&1 &
        wait_for_service "http://localhost:8848/nacos" "Nacos"
    else
        echo -e "${RED}❌ 错误: 找不到 Nacos 目录: $NACOS_DIR${NC}"
        echo -e "${YELLOW}请修改脚本中的 NACOS_DIR 变量${NC}"
        exit 1
    fi
else
    echo -e "${GREEN}✅ Nacos 已在运行${NC}"
fi
echo ""

# 3. 启动 IAM Service
echo -e "${BLUE}🔧 启动 iam-service...${NC}"
if check_port 8080 "iam-service"; then
    cd "$CORE_BACKEND_DIR/iam-service/iam-main"
    nohup mvn spring-boot:run -Dspring-boot.run.profiles=dev \
        > "$LOG_DIR/iam-service.log" 2>&1 &
    echo $! > "$LOG_DIR/iam-service.pid"
    wait_for_service "http://localhost:8080/actuator/health" "iam-service"
else
    echo -e "${GREEN}✅ iam-service 已在运行${NC}"
fi
echo ""

# 4. 启动 Device Service
echo -e "${BLUE}🔧 启动 device-service...${NC}"
if check_port 8081 "device-service"; then
    cd "$CORE_BACKEND_DIR/device-service/device-main"
    nohup mvn spring-boot:run -Dspring-boot.run.profiles=dev \
        > "$LOG_DIR/device-service.log" 2>&1 &
    echo $! > "$LOG_DIR/device-service.pid"
    wait_for_service "http://localhost:8081/actuator/health" "device-service"
else
    echo -e "${GREEN}✅ device-service 已在运行${NC}"
fi
echo ""

# 5. 启动 Kronos BFF
echo -e "${BLUE}🌐 启动 kronos-bff...${NC}"
if [ -d "$KRONOS_BFF_DIR" ]; then
    if check_port 8082 "kronos-bff"; then
        cd "$KRONOS_BFF_DIR/kronos-main"
        nohup mvn spring-boot:run -Dspring-boot.run.profiles=dev \
            > "$LOG_DIR/kronos-bff.log" 2>&1 &
        echo $! > "$LOG_DIR/kronos-bff.pid"
        wait_for_service "http://localhost:8082/actuator/health" "kronos-bff"
    else
        echo -e "${GREEN}✅ kronos-bff 已在运行${NC}"
    fi
else
    echo -e "${YELLOW}⚠️  kronos-bff 目录不存在，跳过启动${NC}"
    echo -e "${YELLOW}请先运行: bash $CORE_BACKEND_DIR/scripts/split-kronos-bff.sh${NC}"
fi
echo ""

# 6. 启动 NGINX
echo -e "${BLUE}🌍 启动 NGINX...${NC}"
if check_port 80 "NGINX"; then
    if [ -f "$NGINX_CONF" ]; then
        # 检查 nginx 是否安装
        if command -v nginx &> /dev/null; then
            nginx -c "$NGINX_CONF"
            sleep 2
            echo -e "${GREEN}✅ NGINX 已启动 (端口 80)${NC}"
        else
            echo -e "${RED}❌ 错误: nginx 未安装${NC}"
            echo -e "${YELLOW}请运行: brew install nginx${NC}"
        fi
    else
        echo -e "${RED}❌ 错误: 找不到 NGINX 配置文件${NC}"
    fi
else
    echo -e "${GREEN}✅ NGINX 已在运行${NC}"
fi
echo ""

# 7. 显示服务状态
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}✅ 所有服务已启动！${NC}"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""
echo -e "${BLUE}📍 服务地址：${NC}"
echo -e "  • 前端入口:        ${GREEN}http://localhost${NC}"
echo -e "  • BFF API:         ${GREEN}http://localhost/api${NC}"
echo -e "  • IAM Service:     ${GREEN}http://localhost:8080${NC}"
echo -e "  • Device Service:  ${GREEN}http://localhost:8081${NC}"
echo -e "  • Kronos BFF:      ${GREEN}http://localhost:8082${NC}"
echo -e "  • Nacos Console:   ${GREEN}http://localhost:8848/nacos${NC} (nacos/nacos)"
echo ""
echo -e "${BLUE}📊 服务状态检查：${NC}"
echo -e "  curl http://localhost/health"
echo -e "  curl http://localhost:8080/actuator/health"
echo -e "  curl http://localhost:8081/actuator/health"
echo -e "  curl http://localhost:8082/actuator/health"
echo ""
echo -e "${BLUE}📝 日志文件：${NC}"
echo -e "  • IAM:     ${LOG_DIR}/iam-service.log"
echo -e "  • Device:  ${LOG_DIR}/device-service.log"
echo -e "  • BFF:     ${LOG_DIR}/kronos-bff.log"
echo -e "  • Nacos:   ${LOG_DIR}/nacos.log"
echo ""
echo -e "${BLUE}🛑 停止服务：${NC}"
echo -e "  bash $CORE_BACKEND_DIR/scripts/stop-all.sh"
echo ""
