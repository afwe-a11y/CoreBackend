# iam-service 后端技术规格说明（v25.02）

> 来源：`1111.md`（用户与权限 v25.02） + `222.md`（组织管理 v25.02） + `Dev-PRD_Refinement.md`（整体对齐与约束）  
> 目的：用于生成 **iam-service** 的可落地后端业务代码（Auth / Organization / User / Role / App / Permission）

---

## 0. 范围（Scope）

### 0.1 覆盖范围（In Scope）

- 认证与登录（Auth）
    - 带图形验证码登录
    - 连续失败锁定策略
    - 使用初始密码登录后强制引导修改密码
    - 通过邮箱验证码 + 旧密码进行重置密码
    - **账号被禁用后，所有 API 必须统一拦截**
- 组织（Organization）
    - 组织列表 / 新建 / 编辑 / 状态切换 / 删除
    - 快捷分配组织管理员（搜索 + 确认）
    - 组织可用应用（Organization Apps）
    - 组织成员（内部成员 / 外部成员）
- 用户（User）
    - 用户列表 / 新建 / 编辑 / 启停用 / 删除
    - 用户 ↔ 组织（多组织关联）
    - 用户 ↔ 角色授权（受组织已关联应用约束）
- 应用（Application）
    - 应用列表 / 注册 / 编辑 / 启停用 / 删除（删除需被角色关联阻断）
    - 应用包含权限（Included Permissions：全局权限树的子集）
- 角色（Role）
    - 角色按应用查看
    - 新建 / 编辑（编码不可编辑）/ 删除（若存在关联用户则阻断）
    - 角色权限配置（必须为应用包含权限的子集）
    - 角色成员管理（添加/移除）
- 权限（Permission）
    - 权限树查询（结构只读）
    - 权限启停开关（状态管理）

### 0.2 不在范围（Out of Scope）

- 纯 UI 布局/样式（仅描述后端行为）
- SSO / OAuth2 第三方登录（文档未要求）
- 权限码与每个接口的细粒度映射（可后续补齐）
- 除邮箱验证码之外的多因子认证（文档未要求）

---

## 1. 全局约定（Global Conventions）

### 1.1 ID 与时间字段

- 所有主键使用 **雪花 ID**（19–21 位）
- 所有持久化记录必须包含：
    - `created_at`, `updated_at`
    - `deleted_at`（可空）
- **禁止物理删除**：所有删除均为 **软删/逻辑删除**

### 1.2 状态字段

- 用户：`NORMAL` / `DISABLED`
- 组织：`NORMAL` / `DISABLED`
- 应用：`ENABLED` / `DISABLED`
- 权限：`ENABLED` / `DISABLED`
- 角色：`ENABLED` / `DISABLED`
- 备注：UI 侧可能显示“正常/停用/禁用”，后端可用枚举存储。

### 1.3 分页

- 请求：`pageNo`（1-based）, `pageSize`
- 响应：`{ total, items, pageNo, pageSize }`

### 1.4 统一响应与错误体

- 成功：`{ data, traceId? }`
- 失败：`{ errorCode, message, details?, traceId? }`
- **文档中出现的固定中文文案必须原样返回**（详见 §8）。

### 1.5 鉴权

- 除 Auth 相关接口外，其他接口必须携带 `Authorization: Bearer <token>`
- Token 至少包含 `userId`，如有“当前组织上下文”概念，可：
    - 通过 Header `X-Org-Id` 传递，或
    - 通过参数 `orgId` 传递（见接口建议）。

---

## 2. 领域模型（Domain Model）

### 2.1 实体（Entities）

- **User（用户）**
    - 登录主体
    - 唯一性：`username`、`email`（平台全局唯一）
    - 状态：`NORMAL/DISABLED`
    - 初始密码标识：`must_change_password`（初始密码登录必须提示修改）
    - “内部归属组织（home org）” + “可关联多个组织（包含外部关联）”
- **Organization（组织）**
    - 数据边界（近似租户）
    - `org_name`：在非删除数据内，**NORMAL/DISABLED 全状态唯一**
    - `org_code`：全局唯一
    - 状态：`NORMAL/DISABLED`
    - 联系人：最多 1 个（name/phone/email）
    - 组织可用应用集合
- **Application（应用）**
    - `app_code` 全局唯一、创建后不可编辑
    - `included_permissions`：全局权限树的子集（注册时至少 1 个）
- **Permission（权限点）**
    - 树结构，类型 `MENU` / `BUTTON`
    - 状态：`ENABLED/DISABLED`
- **Role（角色）**
    - 隶属某应用
    - 唯一性：同应用内 `role_name` 唯一
    - `role_code` 全局唯一、创建后不可编辑
    - 状态：`ENABLED/DISABLED`
    - 权限集合：必须为应用包含权限的子集
    - 存在“预设角色”（例如组织管理员），预设角色**不允许切换状态**
- **Membership / Association（组织成员关系）**
    - User ↔ Organization：标识用户是否可访问该组织（内部/外部）
- **Role Grant（角色授权关系）**
    - User ↔ Role，并携带组织/应用维度
    - 推荐唯一键：`(user_id, org_id, app_id, role_id)`（支持多组织）

### 2.2 核心约束（必须强制校验）

- 组织：
    - 组织名称：长度 1–50；非删除范围内 NORMAL/DISABLED 全状态唯一；重复提示固定文案 **“该组织名称已被占用”**
    - 描述：<= 400
    - 组织编码：仅允许 `[A-Za-z0-9_]+`，全局唯一
- 用户：
    - `username`：<= 20；建议仅字母数字（在组织成员创建文档中为强约束）
    - `real_name`：<= 20
    - 手机：11 位格式校验
    - 邮箱：格式校验；在用户管理创建中为必填且全局唯一
    - 创建用户：系统生成 **初始密码** 并设置 `must_change_password=true`
- 角色：
    - `role_name`：同应用内唯一
    - `role_code`：全局唯一；创建后不可编辑
    - 预设角色：不允许修改状态；提示文案 **“该角色不能更新其状态”**
- 应用：
    - `app_code`：全局唯一；创建后不可编辑
    - `included_permissions`：至少选择 1 个
    - 移除 included permissions：若移除的权限点已被任何角色分配，则必须阻断：
        - 固定文案：**“权限点 [X, Y] 已被分配给角色，无法移除”**
- 删除限制：
    - 删除应用：若该应用下存在角色，必须阻断：
        - 固定文案：**“无法删除，请先移除该应用关联角色”**
    - 删除角色：若存在关联用户，必须阻断：
        - 固定文案：**“该角色存在关联用户 [用户1]、[用户2]...，请先在“成员” Tab 页清空关联用户后再来删除角色”**

---

## 3. 业务规则（Business Rules）

### 3.1 登录（Login）

- 登录标识支持：`username` 或 `email` 或 `phone`
- 登录需要图形验证码（可刷新）
- 空值校验固定文案：
    - 用户名为空 → “请输入用户名”
    - 密码为空 → “请输入密码”
    - 验证码为空 → “请输入验证码”
- 锁定策略：
    - 密码连续错误 **10 次** → 锁定 **15 分钟**
- 初始密码强制修改：
    - 若使用初始密码登录（`must_change_password=true`）：
        - 登录成功返回 `forceResetPassword=true`
        - 并返回固定提示：
            - **“检测到您使用了初始密码登录，为了保障您的账号安全，请立即修改一次密码。”**

### 3.2 用户禁用拦截（全局）

- 若用户在登录后被禁用：
    - 任意页面刷新/路由进入/任意 API 调用，后端必须返回统一错误（建议 403）
    - message 固定为：
        - **`账号 xxx（邮箱号）已被禁用，请联系管理员`**
    - 前端应清理登录态并跳转登录页
- 后端实现必须在所有鉴权请求统一校验用户状态。

### 3.3 重置密码（安全要求高）

页面字段：username（只读）、old password、new password、email（只读/校验）、email code。

- 新密码规则：
    - 长度 **8–20**
    - 至少包含两种：字母 / 数字 / 特殊字符
- 邮箱验证码：
    - 发送前必须校验 email 与账号绑定邮箱一致
    - TTL：**5 分钟**
- 重置时校验：
    - 旧密码正确
    - email 匹配
    - code 正确且未过期
    - 新密码符合规则
- 成功：
    - 固定文案 **“重置成功”**
    - 跳转登录

### 3.4 组织管理（Organization）

#### 3.4.1 组织列表

- 列：组织名称、ID、内部成员数、外部成员数、组织管理员、状态、创建时间
- 行操作：快捷分配管理员 / 编辑（进入详情）/ 删除（弹窗确认）

#### 3.4.2 新建组织

- 必填：org_name、org_code
- 可选：desc、apps（组织可用应用）
- apps 选择使用公共选择器组件
- 必须严格校验唯一性与编码正则（见 §2.2）

#### 3.4.3 编辑组织（详情 → 基本信息）

- 支持查看/编辑模式
- 可编辑字段：
    - name、desc、apps、status、contact（name/phone/email）
- 风险：移除 apps
    - 规则：组织移除某 app 时，后端必须撤销该组织内该 app 相关的所有角色授权
    - 建议响应返回摘要：`revokedRoleGrantsCount` 便于审计

#### 3.4.4 快捷分配组织管理员

- 流程：按 username/email/phone 搜索用户 → 选择 → 确认
- 确认文案固定：
    - **“确认将 [用户名称] 设置为 [组织名称] 的组织管理员?”**
- 后端行为：
    - 确保用户与该组织存在 membership
    - 给该用户在该组织下授予组织管理员所需预设角色（预设角色需 seed/config 存在）

#### 3.4.5 删除组织

- 确认弹窗固定文案：
    - **“确认删除以下组织？删除后组织内的用户将一并被删除。”**
- 详情页风险提示（如需对齐 UI）：
    - “删除组织将导致组织的所有内部用户账号被删除，请谨慎操作”
- 删除效果（全为软删）：
    - 组织：软删
    - 内部用户（home org 为该组织）：软删用户账号，并级联软删其 membership 与 role grants
    - 外部用户：仅移除与该组织的关联（软删 membership）并撤销该组织下的角色授权，账号保持不变
- 建议返回统计：
    - `internalUsersDeleted`, `externalMembershipsRemoved`, `roleGrantsRevoked`

### 3.5 组织成员（详情 → 成员）

#### 3.5.1 内部成员

- 新建内部成员：在该组织下创建账号并授权初始角色
- 字段：
    - username（<=20，仅字母数字）
    - name（<=20）
    - phone（11 位）/ email（邮箱格式）——二选一必填
    - orgIds（可多选）
    - roles（通过角色选择器；必须受 org 已关联 app 约束）
    - status（默认 NORMAL）
- 初始密码投递：
    - 有 email：发送初始密码到邮箱
    - 无 email：接口响应一次性返回 `initialPassword`（不落库明文、不打印日志）
- 编辑内部成员：
    - username 只读
    - 可切换启停用
    - 可调整联系方式、组织关联与角色授权
    - 移除某 org 时，必须撤销该 org 内全部角色授权

#### 3.5.2 外部成员

- 关联外部成员：
    - 通过 username/email/phone 搜索并选择
    - 不允许添加已是本组织内部成员的用户：
        - 固定文案 **“不可添加本组织成员”**
    - 成功后创建 EXTERNAL membership；角色授权可通过统一用户编辑能力完成
- 移除外部成员：
    - 弹窗：
        - 标题：**“确认移除外部用户”**
        - 内容：**“移除后该用户将不能访问本组织”**
    - 成功 toast：**“操作成功”**
    - 效果：软删 membership，并撤销该用户在该组织内全部角色授权

---

## 4. 接口规范（建议版）

> 注意：最终 base path 可按网关规范调整。以下示例使用 `/iam/v1`。  
> 如果需要组织上下文，建议统一通过 `X-Org-Id` 或 `orgId` 参数传递。

### 4.1 Auth

#### 4.1.1 获取验证码

- `GET /iam/v1/auth/captcha`
- Auth: No
- Response:

```json
{
  "data": {
    "captchaId": "string",
    "imageBase64": "string",
    "expiresInSec": 120
  }
}
```

#### 4.1.2 登录

- `POST /iam/v1/auth/login`
- Auth: No
- Request:

```json
{
  "login": "username_or_email_or_phone",
  "password": "string",
  "captchaId": "string",
  "captchaCode": "string"
}
```

- Response:

```json
{
  "data": {
    "accessToken": "string",
    "refreshToken": "string",
    "user": {
      "id": "snowflake",
      "username": "string",
      "email": "string",
      "status": "NORMAL|DISABLED"
    },
    "forceResetPassword": false,
    "lockout": {
      "isLocked": false,
      "lockedUntil": "2026-01-04T00:00:00Z"
    }
  }
}
```

#### 4.1.3 退出登录

- `POST /iam/v1/auth/logout`
- Auth: Yes

#### 4.1.4 发送重置密码邮箱验证码

- `POST /iam/v1/auth/password/reset/code`
- Auth: 可选（既支持强制改密，也支持主动改密）
- Request:

```json
{
  "username": "string",
  "email": "string"
}
```

- 行为：
    - 必须校验 email 与账号绑定邮箱一致
    - TTL 5 分钟

#### 4.1.5 重置密码

- `POST /iam/v1/auth/password/reset`
- Auth: 可选（如强制改密流程也可允许 Auth=Yes）
- Request:

```json
{
  "username": "string",
  "oldPassword": "string",
  "newPassword": "string",
  "email": "string",
  "code": "string"
}
```

- Response:

```json
{ "data": { "success": true, "message": "重置成功" } }
```

---

### 4.2 Organization

#### 4.2.1 组织列表

- `GET /iam/v1/orgs?pageNo=1&pageSize=20&keyword=...&status=...`
- Auth: Yes

#### 4.2.2 新建组织

- `POST /iam/v1/orgs`
- Auth: Yes
- Request:

```json
{
  "name": "string",
  "code": "string",
  "description": "string",
  "appIds": ["snowflake"]
}
```

#### 4.2.3 组织详情

- `GET /iam/v1/orgs/{orgId}`
- Auth: Yes

#### 4.2.4 编辑组织

- `PUT /iam/v1/orgs/{orgId}`
- Auth: Yes
- Request:

```json
{
  "name": "string",
  "description": "string",
  "appIds": ["snowflake"],
  "status": "NORMAL|DISABLED",
  "contact": { "name": "string", "phone": "string", "email": "string" }
}
```

#### 4.2.5 分配组织管理员

- `POST /iam/v1/orgs/{orgId}/admin`
- Auth: Yes
- Request:

```json
{ "userId": "snowflake" }
```

#### 4.2.6 删除组织

- `DELETE /iam/v1/orgs/{orgId}`
- Auth: Yes
- Response（建议）:

```json
{
  "data": {
    "deleted": true,
    "internalUsersDeleted": 0,
    "externalMembershipsRemoved": 0,
    "roleGrantsRevoked": 0
  }
}
```

---

### 4.3 Organization Members

#### 4.3.1 内部成员列表

- `GET /iam/v1/orgs/{orgId}/members/internal?pageNo=1&pageSize=20&keyword=...&status=...`
- Auth: Yes

#### 4.3.2 新建内部成员

- `POST /iam/v1/orgs/{orgId}/members/internal`
- Auth: Yes
- Request:

```json
{
  "username": "string",
  "name": "string",
  "phone": "string",
  "email": "string",
  "orgIds": ["snowflake"],
  "roleGrants": [
    { "orgId": "snowflake", "appId": "snowflake", "roleIds": ["snowflake"] }
  ],
  "status": "NORMAL|DISABLED"
}
```

- Response（无邮箱时可一次性返回初始密码）:

```json
{
  "data": {
    "userId": "snowflake",
    "initialPassword": "string_or_null"
  }
}
```

#### 4.3.3 编辑内部成员

- `PUT /iam/v1/orgs/{orgId}/members/internal/{userId}`
- Auth: Yes

#### 4.3.4 外部成员列表

- `GET /iam/v1/orgs/{orgId}/members/external?pageNo=1&pageSize=20&keyword=...`
- Auth: Yes

#### 4.3.5 关联外部成员

- `POST /iam/v1/orgs/{orgId}/members/external`
- Auth: Yes
- Request:

```json
{ "search": "username_or_email_or_phone" }
```

- Response:

```json
{ "data": { "associated": true, "userId": "snowflake" } }
```

- Error：
    - 若已是本组织内部成员 → message “不可添加本组织成员”

#### 4.3.6 移除外部成员

- `DELETE /iam/v1/orgs/{orgId}/members/external/{userId}`
- Auth: Yes
- Response:

```json
{ "data": { "success": true, "message": "操作成功" } }
```

---

### 4.4 Users（用户管理页）

#### 4.4.1 用户列表

- `GET /iam/v1/users?pageNo=1&pageSize=20&keyword=...&orgIds=...&status=...&roleIds=...`
- Auth: Yes
- 说明：
    - “组织”列可能为多值（用户可关联多个组织）

#### 4.4.2 新建用户

- `POST /iam/v1/users`
- Auth: Yes
- Request:

```json
{
  "username": "string",
  "name": "string",
  "phone": "string",
  "email": "string",
  "orgIds": ["snowflake"],
  "roleGrants": [
    { "orgId": "snowflake", "appId": "snowflake", "roleIds": ["snowflake"] }
  ],
  "status": "NORMAL|DISABLED"
}
```

- 行为：
    - 生成初始密码；发送到邮箱；设置 `must_change_password=true`

#### 4.4.3 编辑用户

- `PUT /iam/v1/users/{userId}`
- Auth: Yes
- 说明：
    - username 不可编辑
    - 移除某 org → 必须撤销该 org 内全部角色授权

#### 4.4.4 启停用用户

- `PATCH /iam/v1/users/{userId}/status`
- Auth: Yes
- Request:

```json
{ "status": "NORMAL|DISABLED" }
```

- 禁用成功 toast（UI）： “禁用成功”

#### 4.4.5 删除用户

- `DELETE /iam/v1/users/{userId}`
- Auth: Yes

---

### 4.5 Applications（应用）

#### 4.5.1 应用列表

- `GET /iam/v1/apps?pageNo=1&pageSize=20&keyword=...`
- Auth: Yes

#### 4.5.2 注册应用

- `POST /iam/v1/apps`
- Auth: Yes
- Request:

```json
{
  "name": "string",
  "code": "string",
  "icon": "string",
  "includedPermissionIds": ["snowflake"],
  "status": "ENABLED|DISABLED"
}
```

- 校验：
    - 至少选择 1 个 included permission

#### 4.5.3 编辑应用

- `PUT /iam/v1/apps/{appId}`
- Auth: Yes
- 说明：
    - code 不可编辑
    - 移除 included permissions 若被角色引用必须阻断（固定文案见 §8）

#### 4.5.4 删除应用

- `DELETE /iam/v1/apps/{appId}`
- Auth: Yes
- 阻断：
    - 该应用下存在角色 → “无法删除，请先移除该应用关联角色”

---

### 4.6 Roles（角色）

#### 4.6.1 角色列表

- `GET /iam/v1/roles?appId=...`
- Auth: Yes

#### 4.6.2 新建角色

- `POST /iam/v1/roles`
- Auth: Yes
- Request:

```json
{
  "appId": "snowflake",
  "name": "string",
  "code": "string",
  "description": "string",
  "status": "ENABLED|DISABLED"
}
```

#### 4.6.3 编辑角色

- `PUT /iam/v1/roles/{roleId}`
- Auth: Yes
- 说明：
    - role code 不可编辑

#### 4.6.4 修改角色状态

- `PATCH /iam/v1/roles/{roleId}/status`
- Auth: Yes
- 约束：
    - 预设角色禁止切换（固定文案 “该角色不能更新其状态”）

#### 4.6.5 删除角色

- `DELETE /iam/v1/roles/{roleId}`
- Auth: Yes
- 阻断：
    - 若存在关联用户 → 固定文案见 §8

#### 4.6.6 角色权限

- `GET /iam/v1/roles/{roleId}/permissions`
- `PUT /iam/v1/roles/{roleId}/permissions`
- Auth: Yes
- PUT request:

```json
{ "permissionIds": ["snowflake"] }
```

- 校验：
    - `permissionIds` 必须是该角色所属应用的 `includedPermissionIds` 子集
    - 不得包含已禁用权限（或按你的策略过滤）

#### 4.6.7 角色成员

- 列表：
    - `GET /iam/v1/roles/{roleId}/members?pageNo=1&pageSize=20&keyword=...`
- 添加：
    - `POST /iam/v1/roles/{roleId}/members`
    - 建议显式携带 org 维度：

```json
{
  "assignments": [
    { "userId": "snowflake", "orgId": "snowflake" }
  ]
}
```

- 若 `orgId` 缺失且无法推断，返回错误要求明确 orgId
- 移除：
    - `DELETE /iam/v1/roles/{roleId}/members/{userId}?orgId=...`
    - UI 确认文案：**“确认要移除 [用户名] 吗？”**

---

### 4.7 Permissions（权限）

#### 4.7.1 权限树

- `GET /iam/v1/permissions/tree?appId=...`（可选：appId 用于标记 included）
- Auth: Yes

#### 4.7.2 修改权限状态

- `PATCH /iam/v1/permissions/{permissionId}/status`
- Auth: Yes
- Request:

```json
{ "status": "ENABLED|DISABLED" }
```

- 成功 toast： “状态更新成功”
- 失败：前端需回滚开关状态

---

## 5. 数据模型（表结构大纲）

> 表名仅为建议；请与你项目命名规范对齐。所有表必须支持软删。

### 5.1 `iam_user`

- `id`（snowflake, PK）
- `username`（unique, not null）
- `name`
- `phone`
- `email`（unique；建议不为空以支持重置密码）
- `password_hash`
- `status`（NORMAL/DISABLED）
- `must_change_password`（bool）
- `failed_login_count`（int）
- `locked_until`（timestamp nullable）
- `home_org_id`（FK → org.id）
- 审计字段 + `deleted_at`

### 5.2 `iam_org`

- `id`
- `name`（non-deleted 范围唯一）
- `code`（non-deleted 范围唯一）
- `description`
- `status`
- 审计字段 + `deleted_at`

### 5.3 `iam_org_contact`

- `id`
- `org_id`（unique：最多 1 个联系人）
- `name`, `phone`, `email`
- 审计字段 + `deleted_at`

### 5.4 `iam_app`

- `id`
- `name`
- `code`（unique）
- `icon`
- `status`
- 审计字段 + `deleted_at`

### 5.5 `iam_permission`

- `id`
- `name`
- `type`（MENU/BUTTON）
- `key`（唯一的权限标识）
- `parent_id`
- `status`
- `sort`（可选）
- 审计字段 + `deleted_at`

### 5.6 `iam_app_permission`

- `id`
- `app_id`
- `permission_id`
- unique `(app_id, permission_id)`
- 审计字段 + `deleted_at`

### 5.7 `iam_role`

- `id`
- `app_id`
- `name`（同 app 唯一）
- `code`（全局唯一）
- `description`
- `status`
- `is_preset`（bool）
- 审计字段 + `deleted_at`

### 5.8 `iam_role_permission`

- `id`
- `role_id`
- `permission_id`
- unique `(role_id, permission_id)`
- 审计字段 + `deleted_at`

### 5.9 `iam_user_org`（组织成员关系）

- `id`
- `user_id`
- `org_id`
- `membership_type`（INTERNAL/EXTERNAL）
- unique `(user_id, org_id)`
- 审计字段 + `deleted_at`

### 5.10 `iam_user_org_role`（角色授权）

- `id`
- `user_id`
- `org_id`
- `app_id`
- `role_id`
- unique `(user_id, org_id, app_id, role_id)`
- 审计字段 + `deleted_at`

### 5.11 `iam_email_code`（重置密码邮箱验证码）

- `id`
- `username`
- `email`
- `code_hash`（只存 hash，不存明文）
- `expires_at`
- `used_at`
- 审计字段 + `deleted_at`

---

## 6. 事务与一致性（Transactions & Integrity）

- 用户新建/编辑必须事务：
    - 写 user + memberships + role grants
- 删除组织必须事务：
    - 软删 org
    - 软删内部用户及其级联数据
    - 软删外部关联并撤销该组织内授权
- 编辑应用 included permissions 必须事务：
    - 移除权限前校验角色引用；通过后再落库
- 删除角色：
    - 若存在未删除的 role grants，必须阻断并返回固定文案

---

## 7. 邮件/短信集成（Integration）

### 7.1 初始密码投递

- 默认：创建用户时发送初始密码到邮箱（用户管理创建文档要求）
- 如允许无邮箱：
    - 接口一次性返回 `initialPassword`
    - 不落库明文、严禁日志打印
    - （短信可扩展，但文档未要求）

### 7.2 重置密码邮箱验证码

- 按请求发送验证码
- TTL：5 分钟
- 建议存 hash，使用后即作废

---

## 8. 固定文案（必须原样返回，禁止改动）

- 登录校验：
    - “请输入用户名”
    - “请输入密码”
    - “请输入验证码”
- 初始密码提示：
    - “检测到您使用了初始密码登录，为了保障您的账号安全，请立即修改一次密码。”
- 禁用拦截：
    - `账号 xxx（邮箱号）已被禁用，请联系管理员`
- 组织名称占用：
    - “该组织名称已被占用”
- 分配管理员确认：
    - “确认将 [用户名称] 设置为 [组织名称] 的组织管理员?”
- 删除组织确认：
    - “确认删除以下组织？删除后组织内的用户将一并被删除。”
- 外部成员：
    - “不可添加本组织成员”
    - “确认移除外部用户”
    - “移除后该用户将不能访问本组织”
    - “操作成功”
- 应用删除阻断：
    - “无法删除，请先移除该应用关联角色”
- 权限移除阻断：
    - “权限点 [X, Y] 已被分配给角色，无法移除”
- 角色删除阻断：
    - “该角色存在关联用户 [用户1]、[用户2]...，请先在“成员” Tab 页清空关联用户后再来删除角色”
- 角色状态限制提示：
    - “该角色不能更新其状态”
- 密码重置成功：
    - “重置成功”
- 权限状态更新成功：
    - “状态更新成功”
- 用户禁用成功：
    - “禁用成功”

---

## 9. 验收测试清单（Backend Checklist）

- 登录
    - 空字段返回固定文案
    - 连续错误计数正确；第 10 次锁定 15 分钟
    - 禁用用户无法登录
    - 初始密码登录返回 `forceResetPassword=true` 与固定提示文案
- 重置密码
    - email 必须与绑定邮箱一致
    - code 5 分钟 TTL；错误/过期阻断
    - 新密码规则严格校验
- 组织
    - 名称全状态唯一；编码正则校验
    - 删除组织按内部/外部规则软删与撤销授权
    - 分配管理员会授予该组织下的预设组织管理员角色
- 用户
    - username/email 唯一
    - 变更组织关联会撤销被移除组织下所有授权
    - 禁用后所有鉴权 API 统一拦截并返回固定 message
- 应用
    - 存在角色时删除阻断
    - included permissions 移除被角色引用时阻断
- 角色
    - 存在关联用户时删除阻断（固定文案）
    - 预设角色禁止切换状态
- 权限
    - 状态切换可保存并返回“状态更新成功”

---

## 10. 待确认项（TBD）

- token 形态（JWT/Session）与 refresh 策略
- 组织上下文的传递方式（header vs 参数）
- 组织被停用时，是否需要统一阻断该组织内所有访问
- 无邮箱用户的初始密码投递策略是否允许（邮箱是否强制）
- 权限启停是否需要父子级联策略（父停用是否必停用子节点等）
