#!/bin/bash

# 完整调用链路测试脚本
# 用途：验证 Frontend → NGINX → BFF → 基底服务 的完整链路

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🧪 开始测试完整调用链路...${NC}"
echo ""

# 测试计数
PASSED=0
FAILED=0

# 测试函数
test_endpoint() {
    local name=$1
    local url=$2
    local expected_code=$3
    
    echo -e "${YELLOW}测试: $name${NC}"
    echo -e "  URL: $url"
    
    response=$(curl -s -w "\n%{http_code}" "$url" 2>/dev/null || echo "000")
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | sed '$d')
    
    if [ "$http_code" = "$expected_code" ]; then
        echo -e "  ${GREEN}✅ 通过 (HTTP $http_code)${NC}"
        PASSED=$((PASSED + 1))
    else
        echo -e "  ${RED}❌ 失败 (期望 $expected_code, 实际 $http_code)${NC}"
        FAILED=$((FAILED + 1))
    fi
    echo ""
}

test_endpoint_with_auth() {
    local name=$1
    local url=$2
    local token=$3
    local expected_code=$4
    
    echo -e "${YELLOW}测试: $name${NC}"
    echo -e "  URL: $url"
    
    response=$(curl -s -w "\n%{http_code}" -H "Authorization: Bearer $token" "$url" 2>/dev/null || echo "000")
    http_code=$(echo "$response" | tail -n1)
    
    if [ "$http_code" = "$expected_code" ]; then
        echo -e "  ${GREEN}✅ 通过 (HTTP $http_code)${NC}"
        PASSED=$((PASSED + 1))
    else
        echo -e "  ${RED}❌ 失败 (期望 $expected_code, 实际 $http_code)${NC}"
        FAILED=$((FAILED + 1))
    fi
    echo ""
}

echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${BLUE}1️⃣  测试基础设施${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""

# 测试 Redis
echo -e "${YELLOW}测试: Redis${NC}"
if redis-cli ping > /dev/null 2>&1; then
    echo -e "  ${GREEN}✅ Redis 运行正常${NC}"
    PASSED=$((PASSED + 1))
else
    echo -e "  ${RED}❌ Redis 未运行${NC}"
    FAILED=$((FAILED + 1))
fi
echo ""

# 测试 Nacos
test_endpoint "Nacos Console" "http://localhost:8848/nacos" "200"

echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${BLUE}2️⃣  测试基底服务（直连）${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""

test_endpoint "IAM Service Health" "http://localhost:8080/actuator/health" "200"
test_endpoint "Device Service Health" "http://localhost:8081/actuator/health" "200"

echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${BLUE}3️⃣  测试 BFF 服务（直连）${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""

test_endpoint "Kronos BFF Health" "http://localhost:8082/actuator/health" "200"

echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${BLUE}4️⃣  测试 NGINX 代理${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""

test_endpoint "NGINX Health" "http://localhost/health" "200"
test_endpoint "NGINX → BFF (验证码接口)" "http://localhost/api/v1/auth/captcha" "200"

echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${BLUE}5️⃣  测试完整认证链路${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""

echo -e "${YELLOW}测试: 获取验证码${NC}"
captcha_response=$(curl -s "http://localhost/api/v1/auth/captcha")
echo -e "  响应: ${captcha_response:0:100}..."

if echo "$captcha_response" | grep -q "captchaId"; then
    echo -e "  ${GREEN}✅ 验证码获取成功${NC}"
    PASSED=$((PASSED + 1))
    
    # 提取 captchaId（简化处理）
    captcha_id=$(echo "$captcha_response" | grep -o '"captchaId":"[^"]*"' | cut -d'"' -f4)
    echo -e "  CaptchaId: $captcha_id"
else
    echo -e "  ${RED}❌ 验证码获取失败${NC}"
    FAILED=$((FAILED + 1))
fi
echo ""

echo -e "${YELLOW}测试: 登录接口（预期失败 - 用户不存在）${NC}"
login_response=$(curl -s -X POST "http://localhost/api/v1/auth/login" \
    -H "Content-Type: application/json" \
    -d '{
        "identifier": "test_user",
        "password": "test123456",
        "captcha": "1234",
        "captchaKey": "test"
    }')
echo -e "  响应: ${login_response:0:100}..."

# 登录失败是预期的（因为用户不存在），只要接口能响应就算通过
if [ -n "$login_response" ]; then
    echo -e "  ${GREEN}✅ 登录接口可访问${NC}"
    PASSED=$((PASSED + 1))
else
    echo -e "  ${RED}❌ 登录接口无响应${NC}"
    FAILED=$((FAILED + 1))
fi
echo ""

echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${BLUE}6️⃣  测试服务注册（Nacos）${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""

echo -e "${YELLOW}测试: 查询已注册服务${NC}"
services=$(curl -s "http://localhost:8848/nacos/v1/ns/service/list?pageNo=1&pageSize=10")

if echo "$services" | grep -q "iam-service"; then
    echo -e "  ${GREEN}✅ iam-service 已注册${NC}"
    PASSED=$((PASSED + 1))
else
    echo -e "  ${RED}❌ iam-service 未注册${NC}"
    FAILED=$((FAILED + 1))
fi

if echo "$services" | grep -q "device-service"; then
    echo -e "  ${GREEN}✅ device-service 已注册${NC}"
    PASSED=$((PASSED + 1))
else
    echo -e "  ${RED}❌ device-service 未注册${NC}"
    FAILED=$((FAILED + 1))
fi

if echo "$services" | grep -q "kronos-bff"; then
    echo -e "  ${GREEN}✅ kronos-bff 已注册${NC}"
    PASSED=$((PASSED + 1))
else
    echo -e "  ${YELLOW}⚠️  kronos-bff 未注册（如果未拆分则正常）${NC}"
fi
echo ""

echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${BLUE}7️⃣  测试调用链路追踪${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""

echo -e "${YELLOW}调用链路示意：${NC}"
echo -e "  Frontend (Browser)"
echo -e "    ↓ HTTP"
echo -e "  NGINX (localhost:80)"
echo -e "    ↓ proxy_pass"
echo -e "  Kronos-BFF (localhost:8082)"
echo -e "    ├─→ IAM Service (localhost:8080) [Feign]"
echo -e "    └─→ Device Service (localhost:8081) [Feign]"
echo ""

# 总结
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${BLUE}📊 测试结果总结${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""
echo -e "  ${GREEN}通过: $PASSED${NC}"
echo -e "  ${RED}失败: $FAILED${NC}"
echo ""

if [ $FAILED -eq 0 ]; then
    echo -e "${GREEN}✅ 所有测试通过！完整链路运行正常${NC}"
    exit 0
else
    echo -e "${RED}❌ 部分测试失败，请检查日志${NC}"
    echo ""
    echo -e "${YELLOW}调试建议：${NC}"
    echo -e "  1. 检查服务日志: tail -f ~/Downloads/CoreBackend/logs/*.log"
    echo -e "  2. 检查端口占用: lsof -i :8080 -i :8081 -i :8082 -i :80"
    echo -e "  3. 检查 Nacos 控制台: http://localhost:8848/nacos"
    echo -e "  4. 检查 NGINX 错误日志: tail -f /usr/local/var/log/nginx/error.log"
    exit 1
fi
