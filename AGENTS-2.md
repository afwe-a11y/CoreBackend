# AGENTS — CODE MODE (Backend Implementation / Iteration)

## How to use this file
- Use this file when asking Codex to **generate or modify backend code**.
- If the task is to generate the documentation pack (**01–12**), use **AGENTS.docs.md** instead.

## CODE MODE goals (non-negotiable)
1) Implement backend features according to the business spec in this file (especially **Organization Management v25.01**).
2) Do **not** generate the 01–12 documentation pack in CODE MODE.
3) Do **not** invent missing facts. Use tags:
- `UNKNOWN` if you cannot confirm from code/spec.
- `ASSUMED` only when necessary, and include basis + risk.

## 全局定位：基底服务 / 中台服务（NON-NEGOTIABLE）
### 我们在构建什么
- 当前仓库/服务用于构建整个项目的若干 **基底能力服务**（Foundation / Mid-Platform），面向全项目复用，**不是**直接给前端对接的 BFF/网关服务。
- 未来会有面向前端的微服务（BFF / Web API）来调用这些基底服务（HTTP/Feign、MQ 事件等）。

### 编码与设计要求（MUST）
- **Service-to-service first**：把调用方视为其他服务 / Job / Consumer；避免任何“页面/交互流程”的隐含假设。
- **契约稳定**：以 API DTO + OpenAPI 为准；演进以 **向后兼容** 和 **增量扩展** 为主，避免破坏性变更。
- **杜绝 UI 耦合**：除非业务规格明确要求，否则不要引入 session/cookie、页面专用 DTO、仅为某个页面设计的接口形态等。
- **可扩展优先**：优先以 Ports/Adapters 解耦；允许未来扩展为多实现/多数据源/多租户策略；不写死单一入口形态。
- **可运维性**：统一错误模型、幂等/并发控制（按规格）、审计日志/追踪埋点（按项目约定）与可观测性。

---

## Business Spec Switchboard（可编辑：让 Codex 每次只聚焦一块业务/逻辑）

> 你只需要在这里“开关 + 粘贴业务描述”，Codex 就会**只实现当前激活的业务规格**；其它规格一律不动（除非为了编译/运行做最小适配）。

### 规则（MUST）
- 除非规格明确要求，否则不得引入任何“页面/BFF/前端交互态”耦合（session/cookie、页面专用 DTO 等）。

ACTIVE_SPEC: IAM_SERVICE_V25_01

### 规格库

## 0) Domain Definitions

### Organization (组织)
- “Organization” is a **data boundary**. When created/updated, define which **Applications (可使用应用)** it can use.
- An Organization may have **0..N usable applications** (it can exist structurally even with no apps).

### Member types
- **Internal member (内部成员)**: a user account that belongs to the organization (can be created/edited/deleted here).
- **External member (外部成员)**: a user account that belongs to another organization, but is **associated** to the current organization for access (can be associated/removed here).

### Organization Admin (组织管理员)
- A “primary admin” displayed on organization list as the organization’s “管理员”.
- Admin assignment is done by selecting an **existing platform user** and granting them the org’s “组织管理员” role.

---

## 1) Data Model Requirements (Backend)

### 1.1 Organization fields
Store at minimum:
- `id`: snowflake style **19–21 digits**, unique, system generated
- `name` (组织名称): required, 1–50 chars, **unique globally across all statuses**
- `code` (组织编码): required, **unique globally**, allowed chars: **letters/digits/underscore only**
- `description` (描述): optional, max 200 chars on create; max 400 chars on edit
- `status`: enum {`NORMAL`(正常), `DISABLED`(停用)}; default `NORMAL`
- `created_at`: date/time; list display needs `YYYY-MM-DD`
- `primary_admin_display`: the admin name shown in list (<=20 chars, english letters + digits)
- Contact (联系人) fields (optional, max 1 contact):
    - `contact_name`: optional, allow mixed chars
    - `contact_phone`: optional, must be 11 digits format
    - `contact_email`: optional, must be valid email format

### 1.2 Organization ⇄ Application
- Maintain mapping: Organization has 0..N usable apps (by app IDs).
- Update must support add/remove apps.
- **Important risk rule**: if removing an app that users are already authorized for, backend must handle cleanup (typically remove related role grants for that app).

### 1.3 Users & Membership (minimum needed)
User attributes touched by this module:
- `username`: <=20 chars, **english letters + digits only**, immutable after creation
- `name`: optional, <=20 chars, allow mixed chars
- `phone`: optional
- `email`: **required** (globally unique)
- `status`: {NORMAL, DISABLED}
- `account_type`: {管理端, 应用端}
- role bindings (see below)

Membership/association:
- Internal membership: user belongs to organization
- External association: user belongs to source org, but is linked to current org

### 1.4 Role selection payload (“关联角色”)
When creating an internal member:
- Field: `关联组织` = multiple org IDs (required)
- Field: `关联角色` = selected **App-Role combinations** (required)
- Role selector must be loaded using **Organization IDs** and only show roles under apps that those organizations have.

---

## 2) API Behaviors (derive endpoints from the flows)

> You can choose exact paths, but must implement the behaviors below.
> All list APIs must support pagination (page size default 10) where applicable.

### 2.1 Organization List
Behavior:
- Default sort: **created time desc**
- Pagination: **10 items per page**, return total count
- Search: fuzzy match on **name OR id**

Response fields needed by UI:
- `名称` (org name)
- `ID`
- `成员数(I)` internal member count (>=0)
- `成员数(O)` external member count (>=0)
- `管理员` (primary admin display)
- `状态` (正常/停用)
- `创建时间` (`YYYY-MM-DD`)

### 2.2 Create Organization
Input fields:
- `组织名称` (required): 1–50 chars, globally unique across all statuses
    - if duplicate: error message must be exactly: `该组织名称已被占用`
- `组织编码` (required): globally unique, regex `^[A-Za-z0-9_]+$`
- `可使用应用` (optional): list of app IDs, allow empty list
- `描述` (optional): max 200 chars

Behavior:
- generate `id` (snowflake 19–21 digits)
- default `status = NORMAL`
- persist org + org-app mappings

### 2.3 Assign Organization Admin (快捷入口)
Flow:
- Search existing users by keyword (fuzzy): username/email/phone
- Select a user → confirm → backend grants that user the org’s **“组织管理员”** role
- On success: refresh org list, show “分配成功”

### 2.4 Delete Organization
Confirm dialog shows org name + “拥有用户” count (backend must provide these values).
Backend deletion logic:
- Internal users under the org: **soft delete or soft delete** (pick one consistent strategy)
- External users: do **not** delete their accounts; **only unlink association** with the org
- On success: org list refresh + “删除成功”
- Same logic must be reused when deleting from org detail page

### 2.5 Organization Detail — Read & Update
Read returns:
- `名称`, `ID`, `创建时间`, `描述`, `可使用应用` (current apps), `状态`
- contact fields (max 1): `姓名`, `手机号`, `邮箱`

Update validations:
- `名称`: required, 1–50 chars
- `描述`: optional, max 400 chars
- `可使用应用`: optional list; support add/remove
    - removing apps must trigger backend handling of impacted user role grants for those apps (cleanup)
- `状态`: enum NORMAL/DISABLED
- contact:
    - `手机号` optional, must be 11 digits format if present
    - `邮箱` optional, must be valid email format if present

### 2.6 Members — Internal List
Return rows with:
- `用户名`, `手机`, `邮箱`, `角色`, `账号状态` (正常/停用)

Row operations:
- Edit → opens “修改内部成员”
- Disable → requires a second confirmation consistent with “用户管理” module (show at least: username/phone/email/account type/role); backend action = set status DISABLED
- Delete → “删除内部成员确认弹窗” → backend deletes user (physical or soft)

### 2.7 Create Internal Member
Input fields:
- `用户名` (required): <=20 chars, english letters + digits only
- `姓名` (optional): <=20 chars
- `邮箱`: **required** (globally unique)
- `手机号`: optional
- `关联组织` (required): multi-select org IDs
    - if org IDs change, the existing `关联角色` selections must be cleared (backend should reject mismatched roles if client fails to clear)
- `关联角色` (required): chosen via role selector
    - if no `关联组织`, selecting roles must be blocked (backend validation required)
    - selector data source: only apps owned by selected org IDs
- `状态` default = NORMAL

On success:
- return at least: created `用户名` and `手机号` (for success prompt “手机号验证码登录并激活账号”)

### 2.8 Update Internal Member (“修改内部成员”)
Rules:
- `用户名` is read-only (immutable)
- `状态` can be toggled (正常/停用)
- `姓名` optional <=20
- `邮箱` is required (globally unique); `手机号` is optional
- `账号类型` required: {管理端, 应用端}
    - if 管理端: set “管理端角色”
    - if 应用端: set “应用端角色”

### 2.9 External Members — List
Return rows with:
- `用户名`, `归属组织` (source org name), `手机`, `邮箱`

### 2.10 Link External Member (“关联外部成员”)
Two-step flow:
1) Search by username or account ID
2) Confirm and add

Validation rules:
- Can only add users who are **NOT** from the current organization
    - if user belongs to current org: reject with message “不可添加本组织成员”
- If user is already an external member (i.e., already associated as external), adding must be rejected and the message should include the org name they are already an external member of (per PRD text).

### 2.11 Unlink External Member (“移除外部成员”)
- Confirmation includes username/phone/source org
- Backend removes the association; user can no longer access this org
- On success: refresh external member list + “操作成功”

---

## 3) Non-UI Constraints & Consistency

### 3.1 Pagination defaults
- Org list: default page size 10, created time desc, return total count.
- Member lists: pagination behavior may match system standard; implement at least pagination support if list can grow.

### 3.2 Validation & Error handling (minimum)
- Enforce uniqueness at DB level for:
    - Organization `name` (unique across all statuses)
    - Organization `code`
- Enforce format constraints as described in section 1/2.
- For “组织名称” duplication error, message must be exactly: `该组织名称已被占用`.

### 3.3 Transactionality
- Deleting an organization must be transactional across:
    - org record
    - org-app mapping
    - internal users delete/soft delete
    - external associations unlink
- Updating org apps must be transactional with any required role-grant cleanup.

## USER & PERMISSION MANAGEMENT (v25.01) — BACKEND SPEC

> Scope: Backend only. Ignore pure UI layout/styling. Preserve fixed Chinese error messages if specified.

### 3.0 Domain model (IAM)

#### Entities
- **User**: login principal.
- **Organization**: data boundary; a user can be associated with multiple organizations.
- **Application**: has an “included permissions set” chosen by admin.
- **Role**: belongs to an Application; aggregates a set of permissions.
- **Permission**: menu/button permissions; hierarchical (tree); has enabled/disabled status.

#### Core relations (implementation-oriented)
- User ↔ Organization: many-to-many (user can belong to multiple orgs).
- User ↔ Role: many-to-many, with org/app dimensions (recommended mapping key: `user_id, org_id, app_id, role_id`).
- Application ↔ Permission: many-to-many (the included permission subset for the app).
- Role ↔ Permission: many-to-many (must be a subset of the Role’s Application included-permissions).

### 3.1 Authentication / account security

#### 3.1.1 Login
- Login identifier: **username OR email OR phone** + password + captcha.
- Required fields validation: identifier, password, captcha must be present.
- Lock policy: **10 consecutive wrong password attempts** ⇒ lock account for **15 minutes**.
- Initial password enforcement:
    - If user logs in using a system-generated initial password, backend must force **reset password** flow (backend must be able to detect/flag “initial password state”).
- Disabled-account interception (global):
    - If an already-logged-in user becomes disabled by admin, then on any refresh/navigation/API call, backend must return a recognizable error so the client can clear session and redirect to login.

### 3.2 Reset password (email verification)
- Send email code:
    - Email is auto-filled from the user’s bound email; backend must verify it matches the stored email.
    - Code TTL: **5 minutes**.
    - Rate limit: after successful send, enforce **30 seconds cooldown** (anti-spam).
- Confirm reset:
    - Validate old password correct.
    - Validate email code correct and not expired.
    - New password rule: length **8–20**, and must contain at least **two** of: letters / digits / special characters.
    - On success: clear “initial password” flag (if any) and invalidate relevant sessions/tokens.

### 3.3 User management

#### 3.3.1 List/query (recommended)
Support filtering by:
- username or ID (username/real-name fuzzy; ID exact)
- status (all/normal/disabled)
- organizations (multi-select)
- roles

List fields at minimum:
- id, username, name, phone, email, associated organizations, roles, created_at, status.

#### 3.3.2 Create user
Business rules:
- `username`: required, ≤20, **globally unique**, letters+digits only (if already constrained by project).
- `email`: **required**, valid format, **globally unique**.
- `phone`: optional, valid format.
- `organizations`: required, multi-select.
- `roles`: required; role selector depends on selected organizations:
    - must select organizations first; roles must belong to apps that the selected organizations can use.
- `status`: default NORMAL.
- On success: system must deliver **initial password** to the user’s email.

#### 3.3.3 Update user
- `username` is immutable.
- Allow changing `email` (if allowed by your policy) but must remain globally unique.
- Organization changes:
    - If an organization is removed from the user, backend must automatically remove all role assignments under that organization (consistency guarantee).
- Role changes:
    - Saved role assignments must be a subset of the roles available under current organizations/apps.

#### 3.3.4 Enable/disable user
- Toggle user status.
- Disabled users must be globally blocked (see 3.1.1).

#### 3.3.5 Delete user
- MUST be **soft delete** only (logical deletion).

### 3.4 Role management

#### 3.4.1 Role constraints
- Roles are grouped by their owning application.
- Preset roles (e.g., “超级管理员”, “组织管理员”):
    - role name not editable.
    - status cannot be toggled.

#### 3.4.2 Create/update role
- `app_id`: required on create; typically immutable on update.
- `role_name`: required; **unique within the same application**.
- `role_code`: required; **globally unique**.
- `description`: optional.

#### 3.4.3 Configure role permissions
- Permission tree data source is NOT the global permissions set:
    - It must be the **included permissions subset** for the role’s application (from Application Management).
- Persist role-permission mappings on save.

#### 3.4.4 Delete role
- If the role is assigned to any user, deletion must be **blocked** (require removing members first).
- Otherwise allow deletion as **soft delete**.

#### 3.4.5 Role members (user-role binding)
- Support batch add/remove members for a role.
- “Visible range” / candidate users must be constrained by the operator’s permission scope (backend must enforce).

### 3.5 Application management

#### 3.5.1 Create/update application
- `app_name`: required.
- `app_code`: required; **globally unique**.
- `included_permissions`: required; must select at least 1 permission from the global permission set.
- `status`: enable/disable.
- Persist app-permission mappings.

Editing risk (removing permissions):
- If a permission being removed is already assigned to any role under this application, backend should **block** the removal and return a clear message (or implement a well-defined cleanup strategy if your policy allows).

#### 3.5.2 Delete application
- If the application has any associated roles, deletion must be **blocked** (must remove roles first).
- Deletion must be **soft delete**.

### 3.6 Permission management

#### 3.6.1 Read + status toggle
- Permission tree is readable; only provide enable/disable toggle.

#### 3.6.2 Enable/disable permission
- Toggling updates permission status.
- If disabling a **parent menu**, backend must cascade disable to all child menus/buttons (transactional).

---

## Output format (CODE MODE)
- Prefer a **git-style patch** or a **file-by-file change list**.
- For each file: show path, then a code block with the full changed content or a minimal diff.
- If database changes are required, output a **migration SQL file** separately (do not guess schema; follow the constraints below).

---

## Global business invariants (MUST)
- **Soft delete only**: All delete/removal operations across this project MUST be **logical deletion** (soft delete). Never physically remove records.
- **User email uniqueness**: Email is a supported login identifier; therefore **email MUST be globally unique** across all users.
- **User email required on creation**: When creating a user account, `email` is required (and unique). Phone is optional.

---

## 启用条件
- ✅ 启用条件：任何 Java 后端相关任务（设计 / 编码 / 重构 / 文档）
- ⛔ 不启用：非 Java 任务

## 规范内容
### 角色定位
你是硬核 Java 后端与架构负责人：熟练掌控 **Java 17 / Spring Boot** 体系；具备扎实的 **MySQL、Redis、MQ** 实战经验；在 **分布式架构与 DDD** 方面经验丰富；既能落地高质量代码，也能输出可长期迭代的架构方案。

### 核心交付目标
- 输出满足 **JDK 17** 基线、可直接编译运行的实现代码与必要的架构落地方案。
- 对外提供可供**其他服务/微服务**调用的 **API.md**（Service-to-service contract；见 [M-OUTPUT] / API.md 规范）。
- 当发生实质性变更时，追加更新 **upgrade.log**（见 [M-OUTPUT] / upgrade.log 规则）。

---

## 启用条件
- ✅ 启用条件：任何需求实现/迭代（尤其是跨模块、影响范围较大改动）
- ⛔ 不启用：纯问答且不产出代码/文档

## 规范内容
### 需求澄清与假设记录
- 对不明确的需求，先列出“已知/未知/假设/风险点”，并在实现中显式记录（例如写到 PR 描述或升级日志的 Why 中）。
- 关键的业务约束（幂等、并发、权限、时区、精度）在设计阶段先确认，再落地到代码与文档。

### 变更策略（小步提交）
- 先保证最小可用闭环：接口契约 -> 参数校验 -> 核心业务 -> 持久化/缓存 -> 返回体 -> API.md -> upgrade.log。
- 对影响范围大的改动，优先做“兼容式演进”，避免破坏性变更。

### 交付物清单（建议）
- 代码：Controller/Service/Repository + 必要的 DTO/VO/PO/Converter
- 配置：application.yml（如涉及）
- 文档：API.md（必需）+ 关键设计说明（可选）
- 日志：upgrade.log（仅在发生实质变更时）


---

## 默认假设（Assumptions）
1. ASSUMED：可读取完整代码仓库（后端源码、配置、README、脚本、依赖清单、PO/Entity 源码）。
2. ASSUMED：存在可枚举的 HTTP 入口（Controller/Route/Handler），可提取 METHOD、PATH、处理函数符号。
3. ASSUMED：PO（Persistence Object / Entity）是生成 DDL 的唯一数据来源；PO 字段具备可用于生成 DDL 的类型与注释来源信息。
4. ASSUMED：PO 可能存在继承（extends/superclass），需要读取父类并展开字段参与 DDL 生成。
5. UNKNOWN：语言/框架/注解体系（JPA/MyBatis/MyBatis-Plus/Lombok/自定义注解等）；必须从代码识别，无法确认则标注 UNKNOWN。
6. UNKNOWN：数据库方言；本提示词仅输出 MySQL DDL；若 PO 标注“非MYSQL表”则排除生成。

## 输入（Inputs）
1. MUST：代码仓库（本地工作区或已加载上下文）。
2. MUST：后端源码：Controller/Route/Handler、Service/UseCase、Repository/DAO、Domain、DTO、Middleware/Filter、异常处理。
3. MUST：配置：路由注册、依赖注入、鉴权、日志、异常处理、事务、限流、缓存等。
4. MUST：PO 源码（包含继承父类、字段定义、字段注释/注解、表名映射信息若有）。
5. SHOULD：README/部署脚本/环境变量模板（用于 runbook）。

## 约束（Constraints）
### 1) 证据与事实（Evidence & Factuality）
1. MUST：每条关键结论必须附 Evidence，格式固定：`path | symbol | summary | line_range_or_equivalent_locator`。
2. MUST NOT：编造不存在的接口、字段、表、索引、外键、错误码、外部服务、配置项或业务规则。
3. MUST：无法从代码确认的信息标注 `UNKNOWN`。
4. MUST：基于推断的信息标注 `ASSUMED`，并在同一条目写明推断依据与风险。

### 2) 覆盖与追踪（Coverage & Tracing）
1. MUST：覆盖 100% 可枚举的 Controller/Route 入口（以代码枚举结果为准）。
2. MUST：每个入口追踪调用链：入参解析/校验 → 鉴权 → 业务逻辑 → 数据访问/外部调用/消息/缓存 → 响应/异常。
3. MUST：标注副作用（Side Effects）：写库、发消息、外部服务调用、缓存更新、文件/对象存储等。
4. SHOULD：识别横切关注点：鉴权/鉴别、校验、审计、日志、异常处理、事务、幂等、限流、缓存一致性。

### 3) DDL 仅来源于 PO（PO-only DDL Rules）
1. MUST：DDL 生成的唯一来源为 PO（含继承父类）；MUST NOT 使用 migrations/schema/ORM 脚本/DTO/命名推断任何字段或约束。
2. MUST：若 PO 类级/表级注释或注解文本包含 `非MYSQL表`（大小写不敏感；允许标点/空格变体），则 MUST 排除该 PO 对应表的 DDL 生成，并在自证报告中列为 PASS/FAIL 检查项。
3. MUST：表名必须来自 PO 的显式声明（如 @Table(name=)、@TableName、常量、或项目约定注解且可证据定位）；无法获取表名则 MUST NOT 生成该表 DDL，并在自证报告中记录为 FAIL（DDL 覆盖项不计入分母，按“不可生成”单列）。
4. MUST：字段 SQL 类型必须来自 PO 中显式可得信息（例如：明确列类型注解/显式 SQL 类型字符串/仓库内明示映射规则且可给 Evidence）。
5. MUST NOT：基于语言类型（Long/String/Integer）自动映射 SQL 类型，除非存在“明示映射规则”且给出 Evidence；否则该字段 MUST NOT 进入 DDL，并在自证报告中记录为 FAIL（字段缺失/类型未知）。
6. MUST：每个字段行必须包含注释来源，格式固定：
- `<column_name> <sql_type> /* <comment> 来源<poFieldName_or_annotationKey> */`
- 注释来源 MUST 为 PO 中真实字段名（camelCase）或明确注解 key；MUST NOT 写推断来源。
7. MUST：继承处理
- MUST：递归读取父类字段至继承链顶端；合并顺序：父类 → 子类；子类同名字段覆盖父类。
- MUST：在 Evidence 与自证报告中记录继承链与字段合并规则执行结果。
8. MUST：禁止 infer 任何长度/精度/默认值/NOT NULL/索引/外键/字符集/引擎；仅当 PO 明示声明且有 Evidence 才可输出对应约束；否则 MUST NOT 输出。

### 4) 交互限制（No Questions）
1. MUST NOT：向用户反问索取信息。
2. MUST：信息不足时继续产出文档；DDL 部分不满足规则即不生成，并在自证报告中 FAIL。

---

# 1) PROJECT CONTEXT (HIGH LEVEL)

你是一名资深的企业级软件架构师，精通领域驱动设计（DDD）、微服务架构、事件驱动、CQRS、BFF 模式、权限治理（RBAC/ABAC）、时序数据处理等技术实践。你正在为一个复杂的企业级 SaaS 系统进行架构设计。

【项目背景】
（在这里填写项目简要背景，例如：新能源电站监控与能源管理 SaaS 平台，支持多租户、数百个电站、千万级测点时序数据、实时告警、收益指标计算等。）

【核心目标】
- 高可扩展性、可维护性、安全性
- 清晰的限界上下文与服务边界
- 严格遵守“单一职责”和“边界铁律”
- 数据一致性与权限控制有明确保障机制
- 支持多端（Web 管理后台、移动 App、未来小程序）

【已知约束与原则】
1. 资产树是资源层级的唯一真相，权限、告警、导航、报表均引用 asset nodeId。
2. 告警计算纯流式，不在管理面做计算。
3. 时序数据只存事实，不直接修改历史数据，通过 mappingVersion 或 indicatorVersion 实现可追溯。
4. 聚合根设计需明确说明一致性边界、实体、值对象。

【架构要求】

采用 **DDD-lite + 六边形架构 (Hexagonal Architecture)** 的工程化分层方案。

*   **目标：** 模块职责清晰、边界稳定；业务类型统一定义；依赖方向合理，避免循环依赖。
*   **明确区分层次：** 业务类型 (model)、应用服务 (application)、出站接口 (interfaces)、入站接口 (controller)、基础设施实现 (infrastructure)。

### 核心模块与职责 (Modules and Responsibilities)

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

### 3. 关键依赖约束 (Dependency Rules)

AI 必须严格遵循以下依赖流向，以确保依赖方向干净，避免循环和污染：

1.  **核心依赖链：** `controller` → `application` → `interfaces` → `infrastructure`。
2.  **Model 依赖：** `api` 依赖 `model`；`application` 依赖 `model`；`interfaces` 依赖 `model`。
3.  **适配器实现：** `infrastructure` 必须实现 `interfaces` (ports),。
4.  **Application 依赖：** `application` 仅依赖 `model` 和 `interfaces`。

#### ❌ 强制禁止的依赖关系 (Forbidden Dependencies)

*   `infrastructure` **不得** 依赖 `api`, `controller`, `main`, 或 `application`。
*   `controller` **不得** 依赖 `infrastructure`, `main` 或 `interfaces` (应通过 `application` 间接调用),。
*   `application` **不得** 依赖 `infrastructure`, `main`。
*   `model` **不得** 依赖 `api`, `controller`, `main`, 或 `application`。

### 4. 模块职责细节与限制 (Specific Constraints)

#### A. controller 层限制

*   **禁止写业务逻辑**,。
*   职责仅限于：接收输入 (HTTP 请求/Kafka 事件)；输入转换 (DTO → AppCommand)；调用 `ApplicationService`；返回 DTO,。
*   `controller` 必须使用 `api` DTO，并将其转换为 `application command`。
*   **接口定位**：HTTP Controller 提供的是“服务能力 API”，主要供其他微服务/BFF 调用；除非规格明确，否则不要引入任何前端交互态/页面态概念（如 session、页面状态机）。
    根据源文件（《中台服务框架结构.docx》）的定义，以下是关于 `analytics-service-controller` 模块的详细规范：

##### 1. 核心职责与严格限制

| 职责类型 | 描述 | 来源 |
| :--- | :--- | :--- |
| **核心职责** | 输入转换（DTO → AppCommand）,；调用 `application service`；返回 DTO (针对 Web 接口)。|, |
| **禁止事项** | **禁止写业务逻辑**,；不访问 DB。|, |

##### 2. 模块内容与结构

该模块统一包含所有驱动应用执行的入口代码，推荐的目录结构和内容如下：

| 目录/子模块 | 职责定位 | 示例 | 来源 |
| :--- | :--- | :--- | :--- |
| **web/** | **HTTP Controller** 的实现,。 | `CustomerController` |, |
| **consumer/** | **Kafka/MQ 消费者** 的实现。负责将事件转为命令，调用 ApplicationService,。 | `ElectricityAccountChangedConsumer` |, |
| **schedule/** | **XXL-Job / Cron / Quartz** 等调度入口。内部逻辑同 consumer：使用 ApplicationService 执行业务。 | `ForecastRecalcJob` |, |
| **listener/** | Spring / Domain Listener（可选）。 | `DomainEventListener` | |

#### 2. application 层限制

*   **禁止访问基础设施：** 不允许进行 DB 访问 (MyBatis/JPA)、HTTP 调用、Kafka 生产。
*   **仅调用抽象：** 只能调用 `interfaces` 中定义的 ports。

#### 3. 数据类型规范

*   **业务枚举/常量：** 所有具有“业务语义”的枚举和常量必须放在 `analytics-service-model` 中。例如：`ContractStatusEnum`,。
*   **技术常量：** Kafka Topic、Redis Key 等技术配置必须放在 `analytics-service-infrastructure` 中，**不得** 放入 `model`,。
*   **DTO 使用：** `controller` 和 `api` 使用 DTO；`infrastructure` **不使用** API DTO，只使用 Model 类型或 PortData 类型。

---

## Coverage Checklist (must be fully implemented)
- [P1] Org list: fields, search, sort, pagination, navigation data needs
- [M] Create org: validations + app mapping
- [M] Assign admin: user search + grant “组织管理员”
- [M] Delete org: internal delete/soft delete + external unlink
- [P2] Org detail basic info: read/update + contact + apps update cleanup
- [P2] Members: internal list + create/update/disable/delete
- [P2] Members: external list + link/unlink external member
