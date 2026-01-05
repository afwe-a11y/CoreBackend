#!/bin/bash

# 本地开发环境一键停止脚本
# 用途：停止所有服务

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 配置路径
CORE_BACKEND_DIR="/Users/sirgan/Downloads/CoreBackend"
NACOS_DIR="/path/to/nacos"  # 修改为你的 Nacos 安装路径
LOG_DIR="$CORE_BACKEND_DIR/logs"

echo -e "${BLUE}🛑 停止所有服务...${NC}"
echo ""

# 停止 NGINX
echo -e "${YELLOW}🌍 停止 NGINX...${NC}"
if command -v nginx &> /dev/null; then
    nginx -s stop 2>/dev/null || true
    echo -e "${GREEN}✅ NGINX 已停止${NC}"
else
    echo -e "${YELLOW}⚠️  nginx 未安装，跳过${NC}"
fi

# 停止 Kronos BFF
echo -e "${YELLOW}🌐 停止 kronos-bff...${NC}"
if [ -f "$LOG_DIR/kronos-bff.pid" ]; then
    kill $(cat "$LOG_DIR/kronos-bff.pid") 2>/dev/null || true
    rm "$LOG_DIR/kronos-bff.pid"
fi
pkill -f "kronos-main" 2>/dev/null || true
echo -e "${GREEN}✅ kronos-bff 已停止${NC}"

# 停止 Device Service
echo -e "${YELLOW}🔧 停止 device-service...${NC}"
if [ -f "$LOG_DIR/device-service.pid" ]; then
    kill $(cat "$LOG_DIR/device-service.pid") 2>/dev/null || true
    rm "$LOG_DIR/device-service.pid"
fi
pkill -f "device-main" 2>/dev/null || true
echo -e "${GREEN}✅ device-service 已停止${NC}"

# 停止 IAM Service
echo -e "${YELLOW}🔧 停止 iam-service...${NC}"
if [ -f "$LOG_DIR/iam-service.pid" ]; then
    kill $(cat "$LOG_DIR/iam-service.pid") 2>/dev/null || true
    rm "$LOG_DIR/iam-service.pid"
fi
pkill -f "iam-main" 2>/dev/null || true
echo -e "${GREEN}✅ iam-service 已停止${NC}"

# 停止 Nacos
echo -e "${YELLOW}📦 停止 Nacos...${NC}"
if [ -d "$NACOS_DIR" ]; then
    cd "$NACOS_DIR/bin"
    sh shutdown.sh 2>/dev/null || true
    echo -e "${GREEN}✅ Nacos 已停止${NC}"
else
    pkill -f "nacos" 2>/dev/null || true
    echo -e "${YELLOW}⚠️  Nacos 目录未配置，尝试强制停止${NC}"
fi

# 停止 Redis
echo -e "${YELLOW}📦 停止 Redis...${NC}"
redis-cli shutdown 2>/dev/null || true
echo -e "${GREEN}✅ Redis 已停止${NC}"

echo ""
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}✅ 所有服务已停止！${NC}"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""

# 清理残留进程
echo -e "${BLUE}🧹 清理残留进程...${NC}"
pkill -f "spring-boot:run" 2>/dev/null || true
echo -e "${GREEN}✅ 清理完成${NC}"
echo ""
