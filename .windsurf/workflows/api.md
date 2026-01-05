---
description: API 单独规范
---

# API 设计规范（Clean 版｜可直接给后端实现/生成接口）

版本：v1.8
日期：2025-12-25
BasePath：`/api/v1`

> 本文件面向后端：已采用默认枚举命名与取值集合，可直接用于后端生成接口/实现。，已采用一套默认的枚举命名与取值集合，以及统一的对象占位结构（如
`map<string, any>`）。如需调整，请直接修改“枚举定义/占位对象定义”即可。

---

## 1) 全局约定（实现固定）

### 1.1 统一响应 Envelope

- 成功：HTTP 200 + `code="200000"`；`message=""`
- 失败：HTTP 按标准返回（400/401/403/404/409/429/5xx）；`code=bizCode(6位字符串)`；`message` 为错误文案
- `data` 永不为 `null`：
    - 无对象：`{}`
    - 数组：`[]`
    - 分页：`{page,pageSize,total,data}`

```ts
// 后端视角（可映射为 Java/Go/TS 任意实现）
type long = number; // 仅用于表达“int64”
type int = number;
type JsonObject = Record<string, any>;

export interface Envelope<T> {
  message: string;
  code: string;      // bizCode
  data: T;           // never null
  requestId: string;
}

export interface Page<T> {
  page: int;
  pageSize: int;
  total: long;
  data: T[];
}

/** 分页查询参数 */
export interface PaginationQueryDTO {
  page: int; // 页码
  pageSize: int; // 每页数量
}

/** 关键字查询参数 */
export interface KeywordQueryDTO {
  keyword?: string; // 关键字
}
```

### 1.2 bizCode（默认实现）

- `6位字符串 = HTTP状态码(3位) + 序号(3位)`
- 默认：
    - 成功：`200000`
    - 参数错误：`400001`
    - 未认证：`401001`
    - 无权限：`403001`
    - 账号禁用：`403002`（固定：`message="Account is disabled"`）
    - 冲突：`409001`
    - 频控：`429001`
    - 服务端错误：`500001`

### 1.3 认证

- Bearer Token：`Authorization: Bearer <accessToken>`
- 无需认证：
    - `POST /auth/login`
    - `GET /auth/captcha`
    - `POST /auth/password-reset/email-code`
    - `POST /auth/password-reset`

### 1.4 时间字段

- `createdAt/updatedAt/lastOnlineAt/...`：`long` 时间戳（毫秒）

### 1.5 boolean 禁止（已落到类型系统）

- 所有“开关/启停/状态/禁用/是否预设/是否必填/是否有效”等：全部使用 **字符串枚举**（见第 2 章 enums）

### 1.6 唯一键（固定）

- 组织：`organizationCode: string`
- 设备：`deviceCode: string`
- 应用：`applicationCode: string`

### 1.7 删除统一规则（固定）

- 所有 `DELETE`：软删（逻辑删除）
- 删除成功：`Envelope<{}>`（`data={}`）
- 不返回关联列表

---

## 2) 枚举定义（默认值集，可直接落库）

```ts
// 通用启停
export enum EnableStatusEnum {
  ENABLED = "ENABLED",
  DISABLED = "DISABLED",
}

// 在线状态
export enum OnlineStatusEnum {
  ONLINE = "ONLINE",
  OFFLINE = "OFFLINE",
}

// 用户状态
export enum UserStatusEnum {
  NORMAL = "NORMAL",
  DISABLED = "DISABLED",
}

// 账号状态（组织成员等）
export enum AccountStatusEnum {
  NORMAL = "NORMAL",
  DISABLED = "DISABLED",
}

// 端侧
export enum ClientTypeEnum {
  ADMIN = "ADMIN",
  APPLICATION = "APPLICATION",
}

// 角色分类
export enum RoleCategoryEnum {
  ADMIN = "ADMIN",
  APPLICATION = "APPLICATION",
}

// 角色状态
export enum RoleStatusEnum {
  ENABLED = "ENABLED",
  DISABLED = "DISABLED",
}

// 预设标识（替代 boolean）
export enum PresetRoleFlagEnum {
  PRESET = "PRESET",
  CUSTOM = "CUSTOM",
}

// 权限类型
export enum PermissionTypeEnum {
  MENU = "MENU",
  BUTTON = "BUTTON",
}

// 权限状态
export enum PermissionStatusEnum {
  ENABLED = "ENABLED",
  DISABLED = "DISABLED",
}

// 应用状态
export enum ApplicationStatusEnum {
  ENABLED = "ENABLED",
  DISABLED = "DISABLED",
}

// 可用性/禁用态（替代 boolean disabled）
export enum DisabledStatusEnum {
  AVAILABLE = "AVAILABLE",
  DISABLED = "DISABLED",
}

// 设备模型来源类型
export enum DeviceModelSourceTypeEnum {
  SOURCE_TYPE_1 = "SOURCE_TYPE_1",
  SOURCE_TYPE_2 = "SOURCE_TYPE_2",
}

// 设备类型
export enum DeviceTypeEnum {
  DEVICE_TYPE_1 = "DEVICE_TYPE_1",
  DEVICE_TYPE_2 = "DEVICE_TYPE_2",
}

// 点位类型
export enum PointTypeEnum {
  ATTRIBUTE = "ATTRIBUTE",
  MEASUREMENT = "MEASUREMENT",
}

// 数据格式
export enum DataFormatEnum {
  NUMBER = "NUMBER",
  STRING = "STRING",
  ENUM = "ENUM",
  OBJECT = "OBJECT",
}

// 单位（字典/枚举）
export enum UnitCodeEnum {
  UNIT_1 = "UNIT_1",
  UNIT_2 = "UNIT_2",
}

// 产品接入方式
export enum AccessMethodEnum {
  METHOD_1 = "METHOD_1",
  METHOD_2 = "METHOD_2",
}

// 测点映射接入状态
export enum MappingAccessStatusEnum {
  ENABLED = "ENABLED",
  DISABLED = "DISABLED",
}

// 网关类型
export enum GatewayTypeEnum {
  GATEWAY_TYPE_1 = "GATEWAY_TYPE_1",
  GATEWAY_TYPE_2 = "GATEWAY_TYPE_2",
}

// 组织状态
export enum OrganizationStatusEnum {
  ENABLED = "ENABLED",
  DISABLED = "DISABLED",
}

// 点位必填状态（替代 boolean）
export enum RequiredStatusEnum {
  REQUIRED = "REQUIRED",
  OPTIONAL = "OPTIONAL",
}

// 登录结果：强制重置密码（替代 boolean）
export enum ForcePasswordResetEnum {
  REQUIRED = "REQUIRED",
  NOT_REQUIRED = "NOT_REQUIRED",
}

// 导入：行校验状态（替代 boolean valid）
export enum ImportValidateStatusEnum {
  VALID = "VALID",
  INVALID = "INVALID",
}

// 导入：确认模式
export enum ImportConfirmModeEnum {
  ALL = "ALL",
  VALID_ONLY = "VALID_ONLY",
  MODE_3 = "MODE_3",
}
```
