#!/bin/bash

# Kronos-BFF 拆分脚本
# 用途：将 kronos-service 从 CoreBackend 拆分为独立项目

set -e

echo "🚀 开始拆分 Kronos-BFF 项目..."

# 定义路径
CORE_BACKEND_DIR="/Users/sirgan/Downloads/CoreBackend"
KRONOS_BFF_DIR="/Users/sirgan/Downloads/kronos-bff"
KRONOS_SOURCE_DIR="$CORE_BACKEND_DIR/kronos-service"

# 检查源目录是否存在
if [ ! -d "$KRONOS_SOURCE_DIR" ]; then
    echo "❌ 错误: 找不到 kronos-service 目录"
    exit 1
fi

# 创建目标目录
echo "📁 创建目标目录: $KRONOS_BFF_DIR"
mkdir -p "$KRONOS_BFF_DIR"

# 复制所有模块
echo "📦 复制模块..."
cp -r "$KRONOS_SOURCE_DIR/kronos-model" "$KRONOS_BFF_DIR/"
cp -r "$KRONOS_SOURCE_DIR/kronos-api" "$KRONOS_BFF_DIR/"
cp -r "$KRONOS_SOURCE_DIR/kronos-interfaces" "$KRONOS_BFF_DIR/"
cp -r "$KRONOS_SOURCE_DIR/kronos-application" "$KRONOS_BFF_DIR/"
cp -r "$KRONOS_SOURCE_DIR/kronos-infrastructure" "$KRONOS_BFF_DIR/"
cp -r "$KRONOS_SOURCE_DIR/kronos-controller" "$KRONOS_BFF_DIR/"
cp -r "$KRONOS_SOURCE_DIR/kronos-main" "$KRONOS_BFF_DIR/"

# 复制文档
if [ -f "$KRONOS_SOURCE_DIR/API.md" ]; then
    mkdir -p "$KRONOS_BFF_DIR/docs"
    cp "$KRONOS_SOURCE_DIR/API.md" "$KRONOS_BFF_DIR/docs/"
fi

if [ -f "$KRONOS_SOURCE_DIR/INTEGRATION.md" ]; then
    cp "$KRONOS_SOURCE_DIR/INTEGRATION.md" "$KRONOS_BFF_DIR/docs/"
fi

# 创建新的父 POM
echo "📝 创建独立的 pom.xml..."
cat > "$KRONOS_BFF_DIR/pom.xml" << 'EOF'
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
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
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
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <version>1.18.30</version>
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
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-compiler-plugin</artifactId>
                    <version>3.11.0</version>
                    <configuration>
                        <source>17</source>
                        <target>17</target>
                        <encoding>UTF-8</encoding>
                    </configuration>
                </plugin>
            </plugins>
        </pluginManagement>
    </build>
</project>
EOF

# 更新子模块 POM（移除对 CoreBackend 的依赖）
echo "🔧 更新子模块 POM..."
for module in kronos-model kronos-api kronos-interfaces kronos-application kronos-infrastructure kronos-controller kronos-main; do
    if [ -f "$KRONOS_BFF_DIR/$module/pom.xml" ]; then
        # 使用 sed 替换 parent 引用
        sed -i.bak 's/<artifactId>kronos-service</<artifactId>kronos-bff</g' "$KRONOS_BFF_DIR/$module/pom.xml"
        sed -i.bak 's/<artifactId>CoreBackend</<artifactId>kronos-bff</g' "$KRONOS_BFF_DIR/$module/pom.xml"
        rm "$KRONOS_BFF_DIR/$module/pom.xml.bak"
    fi
done

# 创建 .gitignore
echo "📝 创建 .gitignore..."
cat > "$KRONOS_BFF_DIR/.gitignore" << 'EOF'
# Maven
target/
pom.xml.tag
pom.xml.releaseBackup
pom.xml.versionsBackup
pom.xml.next
release.properties
dependency-reduced-pom.xml
buildNumber.properties
.mvn/timing.properties
.mvn/wrapper/maven-wrapper.jar

# IDE
.idea/
*.iml
*.ipr
*.iws
.vscode/
.DS_Store

# Logs
logs/
*.log

# Application
application-local.yml
application-*.yml.bak
EOF

# 创建 README
echo "📝 创建 README.md..."
cat > "$KRONOS_BFF_DIR/README.md" << 'EOF'
# Kronos BFF

Backend For Frontend service for Kronos platform.

## Architecture

```
Frontend → NGINX → Kronos-BFF → [iam-service, device-service]
```

## Quick Start

### Prerequisites

- JDK 17+
- Maven 3.8+
- Redis 6.0+
- Nacos 2.x
- MySQL 8.0+

### Run

```bash
cd kronos-main
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Build

```bash
mvn clean package -DskipTests
```

## Documentation

- [API Documentation](docs/API.md)
- [Integration Guide](docs/INTEGRATION.md)
- [Deployment Guide](../CoreBackend/docs/BFF_SPLIT_PLAN.md)

## Port

- Development: 8082
- Production: 8082
EOF

echo "✅ 拆分完成！"
echo ""
echo "📍 新项目位置: $KRONOS_BFF_DIR"
echo ""
echo "🔄 下一步操作："
echo "1. cd $KRONOS_BFF_DIR"
echo "2. mvn clean install"
echo "3. 配置 application-dev.yml"
echo "4. 启动服务: cd kronos-main && mvn spring-boot:run"
echo ""
echo "📖 详细部署方案请查看: $CORE_BACKEND_DIR/docs/BFF_SPLIT_PLAN.md"
