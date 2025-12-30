# **AGENTS — CODE MODE (Backend Implementation / Iteration)**

## **How to use this file**

* Use this file when asking Codex to **generate or modify backend code**.
* If the task is to generate the documentation pack (**01–12**), use **AGENTS.docs.md** instead.

## **CODE MODE goals (non-negotiable)**

1. Implement backend features according to the business spec in this file (especially **Device Access & Management v25.01**).
2. Do **not** generate the 01–12 documentation pack in CODE MODE.
3. Do **not** invent missing facts. Use tags:
    * UNKNOWN if you cannot confirm from code/spec.
    * ASSUMED only when necessary, and include basis \+ risk.

## **Output format (CODE MODE)**

* Prefer a **git-style patch** or a **file-by-file change list**.
* For each file: show path, then a code block with the full changed content or a minimal diff.
* If database changes are required, output a **migration SQL file** separately (do not guess schema; follow the constraints below).

# **项目结构（强制要求 / 按层拆分为多模块）**

## **目标结构**

* 项目必须是 **构建层面的多模块工程（multi-module）**，按“分层”拆成多个子模块。
* 只能有 **一个** Spring Boot 启动类（@SpringBootApplication），且只能放在启动模块（例如 main/bootstrap）。

## **模块清单（必须是子模块，不是 package）**

以下每一项都是一个独立子模块目录（例如 /\<module\>/pom.xml \+ /\<module\>/src/main/java）：

* xxx-model：领域模型/实体/值对象/枚举/DTO（按你定义）
* xxx-api：对外接口契约（Request/Response/Feign 客户端/开放接口声明等）
* xxx-controller：Web/HTTP 层（Controller、Request mapping、参数校验、适配）
* xxx-application：应用层（用例编排、事务边界、调用领域/基础设施）
* xxx-interfaces：接口层/适配层抽象（例如 Repository 接口、外部系统 Gateway 接口）
* xxx-infrastructure：基础设施实现（DB/Redis/MQ/第三方调用的实现）
* xxx-main（或 xxx-bootstrap）：唯一启动模块，包含启动类与 Spring Boot 运行配置

## **依赖方向（必须严格遵守）**

* xxx-main 允许依赖所有模块（用于组装运行）
* xxx-controller \-\> xxx-application \-\> (xxx-interfaces \+ xxx-model)
* xxx-infrastructure \-\> xxx-interfaces \+ xxx-model
* 禁止反向依赖：例如 xxx-application 不得依赖 xxx-controller；xxx-model 不得依赖任何其它模块

## **禁止事项**

* 禁止把所有代码放在单模块里，仅通过 package（包目录）分层来冒充“模块化”。
* 禁止出现第二个启动类（除 xxx-main 外其他模块不允许 @SpringBootApplication）。

## **验收标准（不满足即错误）**

* 根目录必须存在父构建文件：
    * Maven：根 pom.xml 必须是 \<packaging\>pom\</packaging\> 且包含 \<modules\> 子模块列表
    * 或 Gradle：settings.gradle(.kts) 必须 include(...) 所有子模块
* 每个模块目录必须有自己的构建文件（pom.xml/build.gradle）和 src/main/java。
* 除启动模块外，不得出现 @SpringBootApplication。

## **默认假设 (Assumptions)**

1. ASSUMED：可读取完整代码仓库（后端源码、配置、README、脚本、依赖清单、PO/Entity 源码）。
2. ASSUMED：存在可枚举的 HTTP 入口（Controller/Route/Handler），可提取 METHOD、PATH、处理函数符号。
3. ASSUMED：PO（Persistence Object / Entity）是生成 DDL 的唯一数据来源；PO 字段具备可用于生成 DDL 的类型与注释来源信息。
4. ASSUMED：PO 可能存在继承（extends/superclass），需要读取父类并展开字段参与 DDL 生成。
5. UNKNOWN：语言/框架/注解体系（JPA/MyBatis/MyBatis-Plus/Lombok/自定义注解等）；必须从代码识别，无法确认则标注 UNKNOWN。
6. UNKNOWN：数据库方言；本提示词仅输出 MySQL DDL；若 PO 标注“非MYSQL表”则排除生成。

## **输入 (Inputs)**

1. MUST：代码仓库（本地工作区或已加载上下文）。
2. MUST：后端源码：Controller/Route/Handler、Service/UseCase、Repository/DAO、Domain、DTO、Middleware/Filter、异常处理。
3. MUST：配置：路由注册、依赖注入、鉴权、日志、异常处理、事务、限流、缓存等。
4. MUST：PO 源码（包含继承父类、字段定义、字段注释/注解、表名映射信息若有）。
5. SHOULD：README/部署脚本/环境变量模板（用于 runbook）。

## **约束 (Constraints)**

### **1\) 证据与事实 (Evidence & Factuality)**

1. MUST：每条关键结论必须附 Evidence，格式固定：path | symbol | summary | line\_range\_or\_equivalent\_locator。
2. MUST NOT：编造不存在的接口、字段、表、索引、外键、错误码、外部服务、配置项或业务规则。
3. MUST：无法从代码确认的信息标注 UNKNOWN。
4. MUST：基于推断的信息标注 ASSUMED，并在同一条目写明推断依据与风险。

### **2\) 覆盖与追踪 (Coverage & Tracing)**

1. MUST：覆盖 100% 可枚举的 Controller/Route 入口（以代码枚举结果为准）。
2. MUST：每个入口追踪调用链：入参解析/校验 → 鉴权 → 业务逻辑 → 数据访问/外部调用/消息/缓存 → 响应/异常。
3. MUST：标注副作用（Side Effects）：写库、发消息、外部服务调用、缓存更新、文件/对象存储等。
4. SHOULD：识别横切关注点：鉴权/鉴别、校验、审计、日志、异常处理、事务、幂等、限流、缓存一致性。

### **3\) DDL 仅来源于 PO (PO-only DDL Rules)**

1. MUST：DDL 生成的唯一来源为 PO（含继承父类）；MUST NOT 使用 migrations/schema/ORM 脚本/DTO/命名推断任何字段或约束。
2. MUST：若 PO 类级/表级注释或注解文本包含 非MYSQL表（大小写不敏感；允许标点/空格变体），则 MUST 排除该 PO 对应表的 DDL 生成，并在自证报告中列为 PASS/FAIL 检查项。
3. MUST：表名必须来自 PO 的显式声明（如 @Table(name=)、@TableName、常量、或项目约定注解且可证据定位）；无法获取表名则 MUST NOT 生成该表 DDL，并在自证报告中记录为 FAIL（DDL 覆盖项不计入分母，按“不可生成”单列）。
4. MUST：字段 SQL 类型必须来自 PO 中显式可得信息（例如：明确列类型注解/显式 SQL 类型字符串/仓库内明示映射规则且可给 Evidence）。
5. MUST NOT：基于语言类型（Long/String/Integer）自动映射 SQL 类型，除非存在“明示映射规则”且给出 Evidence；否则该字段 MUST NOT 进入 DDL，并在自证报告中记录为 FAIL（字段缺失/类型未知）。
6. MUST：每个字段行必须包含注释来源，格式固定：
    * \<column\_name\> \<sql\_type\> /\* \<comment\> 来源\<poFieldName\_or\_annotationKey\> \*/
    * 注释来源 MUST 为 PO 中真实字段名（camelCase）或明确注解 key；MUST NOT 写推断来源。
7. MUST：继承处理
    * MUST：递归读取父类字段至继承链顶端；合并顺序：父类 → 子类；子类同名字段覆盖父类。
    * MUST：在 Evidence 与自证报告中记录继承链与字段合并规则执行结果。
8. MUST：禁止 infer 任何长度/精度/默认值/NOT NULL/索引/外键/字符集/引擎；仅当 PO 明示声明且有 Evidence 才可输出对应约束；否则 MUST NOT 输出。

### **4\) 交互限制 (No Questions)**

1. MUST NOT：向用户反问索取信息。
2. MUST：信息不足时继续产出文档；DDL 部分不满足规则即不生成，并在自证报告中 FAIL。

# **1\) PROJECT CONTEXT (HIGH LEVEL)**

你是一名资深的企业级软件架构师，精通领域驱动设计（DDD）、微服务架构、事件驱动、CQRS、BFF 模式、权限治理（RBAC/ABAC）、时序数据处理等技术实践。你正在为一个复杂的企业级 SaaS 系统进行架构设计。

【项目背景】  
（在这里填写项目简要背景，例如：新能源电站监控与能源管理 SaaS 平台，支持多租户、数百个电站、千万级测点时序数据、实时告警、收益指标计算等。）  
【核心目标】

* 高可扩展性、可维护性、安全性
* 清晰的限界上下文与服务边界
* 严格遵守“单一职责”和“边界铁律”
* 数据一致性与权限控制有明确保障机制
* 支持多端（Web 管理后台、移动 App、未来小程序）

【已知约束与原则】

1. 资产树是资源层级的唯一真相，权限、告警、导航、报表均引用 asset nodeId。
2. 告警计算纯流式，不在管理面做计算。
3. 时序数据只存事实，不直接修改历史数据，通过 mappingVersion 或 indicatorVersion 实现可追溯。
4. 聚合根设计需明确说明一致性边界、实体、值对象。

【架构要求】

采用 **DDD-lite \+ 六边形架构 (Hexagonal Architecture)** 的工程化分层方案。

* **目标：** 模块职责清晰、边界稳定；业务类型统一定义；依赖方向合理，避免循环依赖。
* **明确区分层次：** 业务类型 (model)、应用服务 (application)、出站接口 (interfaces)、入站接口 (controller)、基础设施实现 (infrastructure)。

### **核心模块与职责 (Modules and Responsibilities)**

服务由以下 7 个模块组成：

| 模块名称 | 职责定位 | 核心功能 |
| :---- | :---- | :---- |
| model | **业务类型中心** | 存放业务枚举、业务常量、值对象 (VO) 和未来的实体 (Entity)。 |
| api | **对外契约层** | 存放 API 请求/响应 DTO、Feign 接口定义、OpenAPI 定义。 |
| controller | **入站入口层** | 统一容器，包含 **Web (HTTP)**、**Consumer (Kafka/MQ)**、**Schedule (Job)** 等系统入口。 |
| application | **用例服务层 (Use Cases)** | 应用服务、用例编排、校验、事务控制。负责调用出站 Ports。 |
| interfaces | **出站接口抽象层 (Ports)** | 定义抽象接口，如 Repository Port 和外部服务调用 Port。定义 PortData DTO。 |
| infrastructure | **出站适配器层** | 实现 interfaces 中定义的 Ports。包含 DB 访问 (persistence)、MQ Producer (messaging)、HTTP Client (gateway)。 |
| main | **启动层** | Spring Boot 入口和模块装配。 |

### **3\. 关键依赖约束 (Dependency Rules)**

AI 必须严格遵循以下依赖流向，以确保依赖方向干净，避免循环和污染：

1. **核心依赖链：** controller → application → interfaces → infrastructure。
2. **Model 依赖：** api 依赖 model；application 依赖 model；interfaces 依赖 model。
3. **适配器实现：** infrastructure 必须实现 interfaces (ports)。
4. **Application 依赖：** application 仅依赖 model 和 interfaces。

#### **❌ 强制禁止的依赖关系 (Forbidden Dependencies)**

* infrastructure **不得** 依赖 api, controller, main, 或 application。
* controller **不得** 依赖 infrastructure, main 或 interfaces (应通过 application 间接调用)。
* application **不得** 依赖 infrastructure, main。
* model **不得** 依赖 api, controller, main, 或 application。

### **4\. 模块职责细节与限制 (Specific Constraints)**

#### **A. controller 层限制**

* **禁止写业务逻辑**。
* 职责仅限于：接收输入 (HTTP 请求/Kafka 事件)；输入转换 (DTO → AppCommand)；调用 ApplicationService；返回 DTO。
* controller 必须使用 api DTO，并将其转换为 application command。  
  根据源文件（《中台服务框架结构.docx》）的定义，以下是关于 analytics-service-controller 模块的详细规范：

##### **1\. 核心职责与严格限制**

| 职责类型 | 描述 | 来源 |
| :---- | :---- | :---- |
| **核心职责** | 输入转换（DTO → AppCommand）；调用 application service；返回 DTO (针对 Web 接口)。 |  |
| **禁止事项** | **禁止写业务逻辑**；不访问 DB。 |  |

##### **2\. 模块内容与结构**

该模块统一包含所有驱动应用执行的入口代码，推荐的目录结构和内容如下：

| 目录/子模块 | 职责定位 | 示例 | 来源 |
| :---- | :---- | :---- | :---- |
| **web/** | **HTTP Controller** 的实现。 | DeviceController |  |
| **consumer/** | **Kafka/MQ 消费者** 的实现。负责将事件转为命令，调用 ApplicationService。 | DeviceStatusConsumer |  |
| **schedule/** | **XXL-Job / Cron / Quartz** 等调度入口。内部逻辑同 consumer：使用 ApplicationService 执行业务。 | DeviceOfflineJob |  |
| **listener/** | Spring / Domain Listener（可选）。 | DomainEventListener |  |

#### **2\. application 层限制**

* **禁止访问基础设施：** 不允许进行 DB 访问 (MyBatis/JPA)、HTTP 调用、Kafka 生产。
* **仅调用抽象：** 只能调用 interfaces 中定义的 ports。

#### **3\. 数据类型规范**

* **业务枚举/常量：** 所有具有“业务语义”的枚举和常量必须放在 model 中。例如：DeviceStatusEnum。
* **技术常量：** Kafka Topic、Redis Key 等技术配置必须放在 infrastructure 中，**不得** 放入 model。
* **DTO 使用：** controller 和 api 使用 DTO；infrastructure **不使用** API DTO，只使用 Model 类型或 PortData 类型。

## **0\) Domain Definitions**

### **Device Model (设备模型)**

* **Defines capabilities**: The blueprint for devices, defining standard attributes and telemetry points.
* **Inheritance**: Supports single-layer inheritance (Parent Model \-\> Child Model).
* **Identifier**: Globally unique, immutable after creation.

### **Product (产品)**

* **Communication Contract**: Defines how devices connect (Protocol, Access Mode).
* **Security Boundary**: Generates ProductKey and ProductSecret for authentication.
* **Immutability**: Key configurations are locked upon creation to prevent data parsing errors.

### **Gateway (网关)**

* **Physical Node**: Represents a physical gateway device (Edge Gateway) or Virtual Gateway.
* **Association**: Must belong to a Station and a Gateway Product.
* **Control**: Can be enabled/disabled; disabling stops data processing for all sub-devices.

### **Device (设备)**

* **Asset Instance**: The actual equipment generating data.
* **Dependencies**: Must define Product (what is it?) and Gateway (where is it connected?).
* **Keys**: Identified by DeviceKey (unique within product or globally).

## **1\) Data Model Requirements (Backend)**

### **1.1 Device Model Management**

Fields:

* identifier: 2-8 chars, english+digits, **globally unique**, immutable.
* name: max 50 chars, required.
* source: Enum {NEW, INHERIT}.
* parent\_model\_id: required if source=INHERIT. **Max inheritance depth \= 1**.
* points: List of Attribute/Telemetry definitions.
    * type: Attribute vs Telemetry.
    * data\_type: int, float, double, string, enum, bool, datetime.
    * identifier: Unique within model hierarchy.

### **1.2 Product Management**

Fields:

* name: max 50 chars.
* product\_key: Globally unique, system generated (or manual), **immutable**.
* product\_secret: max 30 chars, english/digits, **immutable**.
* device\_model\_id: **immutable** link to Device Model.
* access\_mode: Enum (e.g., General\_MQTT), **immutable**.
* protocol\_mapping: JSON/Map, maps Model Point ID \<-\> Physical Collection Name.

### **1.3 Gateway Management**

Fields:

* name: max 50 chars.
* type: Enum {EDGE, VIRTUAL}, **immutable**.
* sn: max 20 chars, **unique**.
* product\_id: Link to Gateway Product.
* station\_id: Link to Station (Power Plant).
* status: Enum {ONLINE, OFFLINE}.
* enabled: Boolean (Enable/Disable).

### **1.4 Device Management**

Fields:

* name: max 50 chars.
* product\_id: **immutable**.
* device\_key: max 30 chars, **immutable**.
* device\_secret: System generated.
* gateway\_id: Mutable link to Gateway.
* station\_id: Read-only, syncs with Gateway's station.
* dynamic\_attributes: JSON, stores values for Model-defined attributes.

## **2\) API Behaviors (derive endpoints from the flows)**

### **2.1 Device Model APIs**

* **Create**:
    * Validations: Identifier regex & uniqueness; Parent existence (if inherit).
    * Logic: Allow single-level inheritance only.
* **Delete**:
    * **Blocking Rule A**: Fail if used by any Product/Device.
    * **Blocking Rule B**: Fail if used as Parent by another Model.
    * Action: Physical delete if checks pass.
* **Point Management**:
    * Add/Edit Points: Validate data type compatibility; Enum requires items.
    * Import Points: Transactional "All-or-Nothing". Match by identifier (Update existing, Create new).

### **2.2 Product APIs**

* **Create**:
    * Generate unique ProductKey & Secret.
    * Persist immutable bindings (Model, Access Mode).
* **Delete**:
    * **Blocking Rule**: Fail if Count(Devices) \> 0\.
* **Update**:
    * Only allow modifying Name, Description.
    * **Protocol Mapping**: Allow editing mapping rules (collection names) for data parsing.

### **2.3 Gateway APIs**

* **List**: Support search by Name/SN.
* **Create/Edit**:
    * Validate SN uniqueness.
    * Type is immutable.
* **Delete**:
    * **Blocking Rule**: Fail if Gateway has ANY sub-devices (regardless of status).
* **Toggle Status (Enable/Disable)**:
    * If Disabled: Backend MUST stop processing upstream data from this gateway.

### **2.4 Device APIs**

* **List**: Filter by Product, Search by Name/ID. Show Status (Online/Offline).
* **Create**:
    * Auto-fill Station from Gateway.
    * Validate Dynamic Attributes against Device Model definitions.
* **Import Wizard**:
    * Step 1: Parse Excel.
    * Step 2: Preview & Duplicate Check (Logic: ID empty \-\> Create; ID exists \-\> Update).
    * Step 3: Write only valid records. Return success/fail counts.
* **Export**:
    * File name format: {Product}\_{Description}\_{Timestamp}.xlsx.
    * Scope: Respect current filters (not just current page).
* **Real-time Data**:
    * Query latest telemetry for device. Sort by update time desc.

## **3\) Non-UI Constraints & Consistency**

### **3.1 Immutability Rules**

* **Critical Fields**: Identifier (Model), ProductKey, DeviceKey, AccessMode, DeviceModel (in Product) CANNOT be changed after creation.
* **Reasoning**: Changing these breaks data parsing, historical data queries, and device connectivity.

### **3.2 Transactionality**

* **Import Operations**: Point import must be atomic (All-or-Nothing).
* **Cascading Logic**: Deleting entities must strictly follow Blocking Rules (No orphan records).

### **3.3 Data consistency**

* **Station Sync**: Device station\_id must always match gateway.station\_id.
* **Status Logic**: Gateway Online/Offline is inferred from heartbeat/SN; not manually set (except Enable/Disable toggle).

### **3.4 Pagination defaults**

* Lists: Default page size 10, support keyword fuzzy search.

## **4\) Coverage Checklist (must be fully implemented)**

* \[P1\] Device Model: List, Create (Unique ID), Delete (Blocking), Detail (Point Mgmt).
* \[P1\] Product: List, Create (Key Gen), Edit (Restricted), Delete (Blocking), Mapping.
* \[P1\] Gateway: List, Create, Edit, Delete (Blocking), Enable/Disable.
* \[P1\] Device: List, Add (Attr Validation), Edit, Import (Wizard), Export, Real-time Data.