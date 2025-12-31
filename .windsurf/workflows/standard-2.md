---
description: 通用规范-2 数据库依赖版本
---

# [M-HTTP] Http接口规范

## 启用条件
- ✅ 启用条件：涉及对外 HTTP API 设计/实现/联调
- ⛔ 不启用：纯内部离线脚本且不对外提供接口

## 规范内容
### RESTful 风格
遵循 RESTful 风格设计接口路径与 HTTP 方法。

### 返回结果统一
无论是否异常，均统一转换为以下结构返回（T 为泛型）：

```json
{
  "message": "",
  "code": "",
  "data": "T"
}
```

- 翻页结果统一放到 `data` 中返回：

```json
{
  "pageSize": 10,
  "page": 1,
  "total": 1,
  "data": "T"
}
```

### 空数据返回规则
- 查询类接口（列表/分页/详情）当查询结果为空时，不视为错误
- HTTP 状态码返回 200
- code 返回成功码（与非空成功保持一致）
- data 按类型返回空值：
  - 列表：`[]`
  - 分页：`{ pageSize, page, total: 0, data: [] }`
  - 详情：`null`（或 `{}`，二选一并固定）
- message 返回空


---

# [M-QUALITY] 测试与质量门禁

## 启用条件
- ✅ 启用条件：任何实质性代码变更（尤其是落库、接口、事务、并发、安全相关）
- ⛔ 不启用：纯文档改动且不涉及代码

## 重要说明（给 AI 的约束）
- 本模块用于约束 AI 在输出实现时：同时输出 **测试代码 / 校验清单 / 可执行命令**。
- AI 默认无法在你的真实仓库/CI 环境里自动执行测试；需要你提供本地/CI 的测试输出或日志，AI 才能基于结果迭代修复。

## 最低测试要求（建议）
- 关键业务（创建/修改/状态流转/资金相关）至少覆盖：
  - 正常路径
  - 关键边界条件（空值、越界、非法状态）
  - 幂等（重复请求）
  - 并发冲突（乐观锁 / 唯一键冲突 / 分布式锁相关）
- 对外 API：至少覆盖 Controller 入参校验与统一异常映射的行为。

## Maven 质量门禁（建议）
### 依赖收敛（推荐 Maven Enforcer）
- 建议在 root pom 启用：
  - `dependencyConvergence`
  - `requireUpperBoundDeps`
  - `requireJavaVersion`（>=17）

### 规范自动化（按团队选型）
- Spotless / Checkstyle / PMD / SpotBugs 至少选一套，确保规范自动化执行。
- PR/CI 阶段必须跑门禁，避免仅靠人肉 review 发现问题。

### 回归检查清单（建议输出给联调/验收）
- API.md 已更新（若接口变更）
- upgrade.log 已追加（若有实质变更）
- 错误码与 message 行为不破坏兼容性
- 日志不泄露敏感信息，traceId 可定位
- 事务边界与幂等策略在代码/文档中可追溯


---

# [M-SEC] 安全与合规

## 启用条件
- ✅ 启用条件：任何对外 API、写操作、或涉及用户态请求/敏感字段
- ⛔ 不启用：纯离线脚本且不输出对外接口/日志

## 规范内容
### 鉴权与权限
- 建议明确：哪些接口需要登录、需要哪些角色/权限、以及鉴权失败的返回规范。
- 建议在 API.md 中标注接口的权限要求。

### 敏感信息处理
- 日志与返回体避免包含：密码、token、证件号、手机号、地址等敏感信息。
- 建议补充脱敏规则（例如手机号、证件号的掩码策略）。


---

# [M-DATA] 数据、事务与一致性

## 启用条件
- ✅ 启用条件：涉及写操作、事务、并发、幂等、或 MQ/第三方调用
- ⛔ 不启用：纯读接口且无并发/事务要求（如仍涉及索引/分页，可仅启用 [M-STORAGE]）

## 规范内容
### 事务边界建议
- 建议明确：哪些 Service 方法需要 `@Transactional`，传播行为与回滚规则。
- 写操作与外部依赖调用（MQ/第三方）结合时，建议明确最终一致性的实现方式。

### 幂等与并发控制
- 对“创建/支付/扣减”等关键操作，建议提供幂等键（业务唯一键、请求号等）。
- 对并发写冲突，建议结合乐观锁 version / 唯一索引 / 分布式锁等策略。

### 数据库索引与字段建议
- 建议明确：查询条件必建索引、避免全表扫描、联合索引顺序规则等。
- 建议明确：create_time/update_time 的更新策略（应用写入 or DB 默认/触发器）。


---

# [T-MiddlePlatform] 中台服务框架

## 核心架构理念

中台采用 **DDD-lite + 六边形架构 (Hexagonal Architecture)** 的工程化分层方案。

*   **目标：** 模块职责清晰、边界稳定；业务类型统一定义；依赖方向合理，避免循环依赖。
*   **明确区分层次：** 业务类型 (model)、应用服务 (application)、出站接口 (interfaces)、入站接口 (controller)、基础设施实现 (infrastructure)。

## 核心模块与职责 (Modules and Responsibilities)

服务由以下 7 个模块组成：

| 模块名称 | 职责定位 | 核心功能 |
| :--- | :--- | :--- |
| `model` | **业务类型中心** | 存放业务枚举、业务常量、值对象 (VO) 和未来的实体 (Entity)。 |
| `api` | **对外契约层** | 存放 API 请求/响应 DTO、Feign 接口定义、OpenAPI 定义。 |
| `controller` | **入站入口层** | 统一容器，包含 **Web (HTTP)**、**Consumer (Kafka/MQ)**、**Schedule (Job)** 等系统入口,。 |
| `application` | **用例服务层 (Use Cases)** | 应用服务、用例编排、校验、事务控制。负责调用出站 Ports。 |
| `interfaces` | **出站接口抽象层 (Ports)** | 定义抽象接口，如 Repository Port 和外部服务调用 Port。定义 PortData DTO。 |
| `infrastructure` | **出站适配器层** | 实现 `interfaces` 中定义的 Ports。包含 DB 访问 (persistence)、MQ Producer (messaging)、HTTP Client (gateway)。 |
| `main` | **启动层** | Spring Boot 入口和模块装配。 |

## 3. 关键依赖约束 (Dependency Rules)

AI 必须严格遵循以下依赖流向，以确保依赖方向干净，避免循环和污染：

1.  **核心依赖链：** `controller` → `application` → `interfaces` → `infrastructure`。
2.  **Model 依赖：** `api` 依赖 `model`；`application` 依赖 `model`；`interfaces` 依赖 `model`。
3.  **适配器实现：** `infrastructure` 必须实现 `interfaces` (ports),。
4.  **Application 依赖：** `application` 仅依赖 `model` 和 `interfaces`。

### ❌ 强制禁止的依赖关系 (Forbidden Dependencies)

*   `infrastructure` **不得** 依赖 `api`, `controller`, `main`, 或 `application`。
*   `controller` **不得** 依赖 `infrastructure`, `main` 或 `interfaces` (应通过 `application` 间接调用),。
*   `application` **不得** 依赖 `infrastructure`, `main`。
*   `model` **不得** 依赖 `api`, `controller`, `main`, 或 `application`。

## 4. 模块职责细节与限制 (Specific Constraints)

### A. controller 层限制

*   **禁止写业务逻辑**,。
*   职责仅限于：接收输入 (HTTP 请求/Kafka 事件)；输入转换 (DTO → AppCommand)；调用 `ApplicationService`；返回 DTO,。
*   `controller` 必须使用 `api` DTO，并将其转换为 `application command`。
根据源文件（《中台服务框架结构.docx》）的定义，以下是关于 `analytics-service-controller` 模块的详细规范：

#### 1. 核心职责与严格限制

| 职责类型 | 描述 | 来源 |
| :--- | :--- | :--- |
| **核心职责** | 输入转换（DTO → AppCommand）,；调用 `application service`；返回 DTO (针对 Web 接口)。|, |
| **禁止事项** | **禁止写业务逻辑**,；不访问 DB。|, |

#### 2. 模块内容与结构

该模块统一包含所有驱动应用执行的入口代码，推荐的目录结构和内容如下：

| 目录/子模块 | 职责定位 | 示例 | 来源 |
| :--- | :--- | :--- | :--- |
| **web/** | **HTTP Controller** 的实现,。 | `CustomerController` |, |
| **consumer/** | **Kafka/MQ 消费者** 的实现。负责将事件转为命令，调用 ApplicationService,。 | `ElectricityAccountChangedConsumer` |, |
| **schedule/** | **XXL-Job / Cron / Quartz** 等调度入口。内部逻辑同 consumer：使用 ApplicationService 执行业务。 | `ForecastRecalcJob` |, |
| **listener/** | Spring / Domain Listener（可选）。 | `DomainEventListener` | |

### 2. application 层限制

*   **禁止访问基础设施：** 不允许进行 DB 访问 (MyBatis/JPA)、HTTP 调用、Kafka 生产。
*   **仅调用抽象：** 只能调用 `interfaces` 中定义的 ports。

### 3. 数据类型规范

*   **业务枚举/常量：** 所有具有“业务语义”的枚举和常量必须放在 `analytics-service-model` 中。例如：`ContractStatusEnum`,。
*   **技术常量：** Kafka Topic、Redis Key 等技术配置必须放在 `analytics-service-infrastructure` 中，**不得** 放入 `model`,。
*   **DTO 使用：** `controller` 和 `api` 使用 DTO；`infrastructure` **不使用** API DTO，只使用 Model 类型或 PortData 类型。


---

# [M-POM] 通用pom，支持多模块Maven pom生成

## 启用条件
- ✅ 需要新建/重构多模块 Maven 工程，或用户明确要求“生成/对齐 POM”
- ⛔ 不启用：仅改业务代码且不涉及构建/依赖/版本治理

## 总体原则
- **示例即占位**：文中出现的 `analytics-service`、`vault-service`、`helios-*`、私服地址均为示例；生成新项目时必须用用户提供的 `{serviceName}`/`{groupId}`/`{bomGAV}`/`{repoUrl}` 替换，不得照搬。
- **单一事实源**：所有版本号集中在根 POM `<properties>` + `<dependencyManagement>`；子模块依赖（含外部 API）一律不写 `<version>`.
- **版本可演进**：版本 = `baseRevision + version.suffix`；`suffix` 由 profile 控制（dev/uat/prod），禁止手工改版本。
- **聚合不打包**：根 POM 只做聚合和版本收敛，可执行打包仅在 `main` 模块完成。

## 根 POM（聚合）必备内容
- **坐标与打包**：`groupId={groupId}`；`artifactId={serviceName}-service`（或团队约定）；`packaging=pom`。
- **Parent/BOM**：继承平台 BOM（示例）：
  ```xml
  <parent>
    <groupId>{bom.groupId}</groupId>
    <artifactId>{bom.artifactId}</artifactId>
    <version>{bom.version}</version>
  </parent>
  ```
- **Modules（推荐顺序）**：`{serviceName}-model → -api → -interfaces → -application → -infrastructure → -controller → -main`（可按团队既有顺序，但需保证依赖方向与 reactor 构建正常）。
- **私服仓库（可选）**：仅当用户提供时添加：
  ```xml
  <repositories>
    <repository>
      <id>company_public</id>
      <name>companyMaven</name>
      <url>{repoUrl}</url>
    </repository>
  </repositories>
  ```

## 版本与属性
- **编译基线**：`maven.compiler.source/target=17`，`project.build.sourceEncoding=UTF-8`。
- **版本拆分**：
  ```xml
  <properties>
    <version.suffix>-DEV-SNAPSHOT</version.suffix>
    <service-base-revision>2025.11.0</service-base-revision>
    <service-version>${service-base-revision}${version.suffix}</service-version>
    <!-- 需要对齐的外部服务同样使用 base-revision + version.suffix 组合 -->
    <maven.deploy.skip>true</maven.deploy.skip> <!-- 根 POM 不部署 -->
  </properties>
  ```
- **Profiles（控制 suffix）**：
  ```xml
  <profiles>
    <profile>
      <id>dev</id>
      <activation><activeByDefault>true</activeByDefault></activation>
      <properties><version.suffix>-DEV-SNAPSHOT</version.suffix></properties>
    </profile>
    <profile>
      <id>uat</id>
      <properties><version.suffix>-UAT-SNAPSHOT</version.suffix></properties>
    </profile>
    <profile>
      <id>prod</id>
      <properties><version.suffix></version.suffix></properties>
    </profile>
  </profiles>
  ```

## 依赖收敛（dependencyManagement）
- **内部子模块统一版本**（全部使用 `${service-version}`）：在根 POM 声明 `model/api/interfaces/application/infrastructure/controller`。
- **外部服务 API 统一版本**：在根 POM 声明 `${other-service-version}`，子模块引用时不写版本。
- 子模块依赖示例（无 version）：
  ```xml
  <dependency>
    <groupId>${project.groupId}</groupId>
    <artifactId>{serviceName}-model</artifactId>
  </dependency>
  ```

## 构建插件
- **flatten-maven-plugin**（推荐）：`mode=oss`，阶段：process-resources / package / clean；`updatePomFile=true`，确保发布时使用展开后的 POM。
- **main 模块打包插件**：仅在 `{serviceName}-main/pom.xml` 配置 Spring Boot repackage，必须包含以下内容
（`mainClass` 为 Spring Boot Application 启动类）示例：
  ```xml
  <finalName>app</finalName>
  <plugins>
    <plugin>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-maven-plugin</artifactId>
      <configuration>
        <mainClass></mainClass>
      </configuration>
      <executions>
        <execution>
          <goals><goal>repackage</goal></goals>
        </execution>
      </executions>
    </plugin>
  </plugins>
  ```

## 子模块 POM 规范
- **parent**：指向根 POM，`artifactId={serviceName}-service`，`version=${service-version}`。
- **部署策略**：子模块默认 `maven.deploy.skip=false`。
- **依赖方向**（示例约束，可按团队 DDD 分层）：
  - api → model
  - interfaces → model
  - application → model + interfaces + api
  - infrastructure → model + interfaces + application + api + 基础设施 starters
  - controller → api + application + model
  - main → 聚合所有内层模块 + web/actuator/jpa/redis 等 starters；可执行打包仅此模块
- **禁止事项**：子模块手写 `<version>`；根 POM 配可执行打包；携带示例命名/地址进入新项目。

## AI 生成步骤清单
1) 采集变量：`{serviceName}`、`{groupId}`、`{bomGAV}`、`{repoUrl?}`、`{baseRevision}`、需要对齐版本的外部服务清单。  
2) 生成根 POM：坐标/parent/modules/repositories/properties/profiles/dependencyManagement/build（flatten）。  
3) 生成子模块 POM：统一 parent + 编译属性；按依赖方向写 dependencies（无 version）。  
4) main 模块补充 Spring Boot repackage 配置，`finalName=app`，`mainClass` 填用户给定启动类。  
5) 校验：无子模块 version，版本收敛正确，profile 后缀符合环境，聚合 POM 不打包。


---

# [M-GITIGNORE] 通用-gitignore

## 启用条件
- ✅ 启用条件：工程初始化、或用户明确要求生成/更新 `.gitignore`
- ⛔ 不启用：不涉及仓库工程化配置

## 规范内容
### 创建/更新 .gitignore
- 若项目根目录不存在 `.gitignore` 文件，则创建一份适用于 Java 项目且兼容 IntelliJ IDEA 的 `.gitignore`


---

# [M-STORAGE] 通用-数据存储规范

## 启用条件
- ✅ 启用条件：涉及 MySQL 表设计、PO/Mapper/Repository、或查询性能要求
- ⛔ 不启用：不落库/不涉及持久化

## 规范内容
### MySQL 8.0 必有字段
以下所有字段均非业务字段，只是记录此行的时间和状态，以 BaseEntityPO 类存储，其余表继承此基类。

| 字段名 | 类型 | 是否必填 | 描述 |
|---|---|---|---|
| id | bigint(20) | 是 | 雪花ID |
| created_by | varchar(20) | 是 | 创建人ID |
| updated_by | varchar(20) | 是 | 创建时有值，修改人ID |
| create_time | datetime(3) | 是 | 仅创建时有值，无法更新 |
| update_time | datetime(3) | 是 | 创建时有值，每次随更新 |
| deleted | tiny(1) | 是 | 是否软删除，默认0 |
| version | int | 是 | 乐观锁版本号 |

### 数据库字段命名
- 所有关联字段结尾需要用 `id` 或 `code`，例如：`architectureTemplateCode`


---

# [M-MIDDLEWARE] 通用-中间件和依赖版本约束

## 启用条件
- ✅ 启用条件：初始化工程、调整依赖版本、或任务明确涉及依赖/中间件
- ⛔ 不启用：只改业务逻辑且不触达依赖/版本（仍可作为背景约束保留）

## 规范内容
## 中间件
- JAVA 17
- Spring Boot 3.2.4
- Spring Cloud 2023.0.1
- Spring Cloud Alibaba 2022.0.0.0
- Nacos 2.5.1
- MySQL 8.0
- Redis 7.0
- Jackson 2.15.4
- Lombok 1.18.30
- mybatis.plus.boot.starter 3.5.5
- mybatis.spring 3.0.3
- lombok.mapstruct.binding 0.2.0
- jjwt 0.9.1
- alibaba.ttl 2.14.2
- google.guava 30.1-jre
- logstash-logback-encoder 7.4
- aliyun-sdk-oss 3.16.1
- aliyun-java-sdk-core 4.6.3
- maven 3.5

## 依赖版本约束
- 所有依赖版本号必须统一收敛到 **根目录 POM**，并以 `<properties>` 形式记录。
- MySQL 必须使用以下依赖版本：

```xml
<dependency>
  <groupId>mysql</groupId>
  <artifactId>mysql-connector-java</artifactId>
  <version>8.0.33</version>
</dependency>
```

- MYBATIS 必须使用以下依赖版本：

```xml
<dependency>
  <groupId>com.baomidou</groupId>
  <artifactId>mybatis-plus-boot-starter</artifactId>
  <version>${mybatis.plus.version}</version>
  <exclusions>
    <exclusion>
      <groupId>org.mybatis</groupId>
      <artifactId>mybatis-spring</artifactId>
    </exclusion>
  </exclusions>
</dependency>

<dependency>
  <groupId>org.mybatis</groupId>
  <artifactId>mybatis-spring</artifactId>
  <version>${mybatis.spring.version}</version>
</dependency>
```