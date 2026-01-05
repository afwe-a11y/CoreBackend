---
description: API 类型定义 User 权限 组织
---

## 类型定义（后端类型视角｜可直接生成接口）

### 1. Auth

```ts
/** 登录请求 */
export interface LoginRequestDTO {
  account: string; // 登录账号
  password: string; // 密码
  captchaCode: string; // 已确认：登录不提交 captchaId
}

/** 图形验证码返回 */
export interface CaptchaVO {
  captchaId: string; // 用于服务端校验
  captchaImageBase64: string; // 验证码图片Base64
}

/** 登录用户信息 */
export interface LoginUserVO {
  userId: long; // 用户ID
  username: string; // 用户名
  fullName: string; // 姓名
  phone: string; // 手机号
  email: string; // 邮箱
  organizationCode: string; // 组织编码（唯一键）
  organizationName: string; // 组织名称
  userStatus: UserStatusEnum; // 用户状态
  clientType: ClientTypeEnum; // 端侧（管理端/应用端）
}

/** 登录用户角色信息 */
export interface LoginRoleVO {
  roleId: long; // 角色ID
  roleName: string; // 角色名称
  roleCategory: RoleCategoryEnum; // 角色分类（端侧）
  roleStatus: RoleStatusEnum; // 角色状态
}

/** 登录用户权限信息 */
export interface LoginPermissionVO {
  permissionId: long; // 权限ID
  permissionCode: string; // 权限编码
  permissionName: string; // 权限名称
  permissionType: PermissionTypeEnum; // 权限类型（菜单/按钮）
  permissionStatus: PermissionStatusEnum; // 权限状态
}

/** 登录结果返回 */
export interface LoginResultVO {
  accessToken: string; // 访问令牌
  user: LoginUserVO; // 用户信息
  clientType: ClientTypeEnum; // 端侧（便于前端直读）
  roles: LoginRoleVO[]; // 角色列表
  permissions: LoginPermissionVO[]; // 权限列表（明细）
  permissionCodes: string[]; // 权限编码列表（便于鉴权）
  forcePasswordReset?: ForcePasswordResetEnum; // 是否强制重置密码
}

/** 发送重置密码邮箱验证码请求 */
export interface PasswordResetEmailCodeRequestDTO {
  account: string; // 登录账号
  email: string; // 邮箱
}

/** 重置密码请求 */
export interface PasswordResetRequestDTO {
  account: string; // 登录账号
  oldPassword: string; // 旧密码
  newPassword: string; // 新密码
  email: string; // 邮箱
  emailCode: string; // 邮箱验证码
}
```

### 2. Users

```ts
/** 角色分配（按应用维度） */
export interface RoleAssignmentDTO {
  applicationCode: string; // 应用编码（唯一键）
  roleId: long; // 角色ID
}

export interface UserListQueryDTO extends PaginationQueryDTO, KeywordQueryDTO {
  userStatus?: UserStatusEnum;
  clientType?: ClientTypeEnum;
  roleId?: long;
  organizationCode?: string;
}

/** 用户列表项 */
export interface UserListItemVO {
  userId: long; // 用户ID
  username: string; // 用户名
  fullName: string; // 姓名
  phone: string; // 手机号
  email: string; // 邮箱
  organizationName: string; // 组织名称
  roleName: string; // 角色名称
  createdAt: long; // 创建时间
  userStatus: UserStatusEnum; // 用户状态
}

/** 用户详情 */
export interface UserDetailVO {
  userId: long; // 用户ID
  username: string; // 用户名
  organizationCode: string; // 组织编码（唯一键）
  fullName: string; // 姓名
  phone: string; // 手机号
  email: string; // 邮箱
  userStatus: UserStatusEnum; // 用户状态
  clientType: ClientTypeEnum; // 端侧
  roleAssignments: RoleAssignmentDTO[]; // 角色分配列表
  assetScope: JsonObject; // 资产范围
}

/** 创建用户请求 */
export interface CreateUserRequestDTO {
  username: string; // 用户名
  organizationCode: string; // 组织编码（唯一键）
  fullName?: string; // 姓名
  phone?: string; // 手机号
  email: string; // 邮箱
  userStatus?: UserStatusEnum; // 用户状态
  clientType?: ClientTypeEnum; // 端侧
  roleAssignments?: RoleAssignmentDTO[]; // 角色分配列表
  assetScope?: JsonObject; // 资产范围
}

/** 更新用户请求 */
export interface UpdateUserRequestDTO {
  fullName?: string; // 姓名
  phone?: string; // 手机号
  email: string; // 邮箱
  userStatus?: UserStatusEnum; // 用户状态
  roleAssignments?: RoleAssignmentDTO[]; // 角色分配列表
  assetScope?: JsonObject; // 资产范围
}

/** 更新用户状态请求 */
export interface UpdateUserStatusRequestDTO {
  userStatus: UserStatusEnum; // 用户状态
}
```

### 3. Roles & Permissions & Members

```ts
/** 角色列表项 */
export interface RoleListItemVO {
  roleId: long; // 角色ID
  roleName: string; // 角色名称
  roleCategory: RoleCategoryEnum; // 角色分类
  roleStatus: RoleStatusEnum; // 角色状态
  isPreset: PresetRoleFlagEnum; // isPreset 字段
}

/** 创建角色请求 */
export interface CreateRoleRequestDTO {
  roleName: string; // 角色名称
  roleCategory: RoleCategoryEnum; // 角色分类
}

/** 更新角色名称请求 */
export interface UpdateRoleNameRequestDTO {
  roleName: string; // 角色名称
}

/** 更新角色状态请求 */
export interface UpdateRoleStatusRequestDTO {
  roleStatus: RoleStatusEnum; // 角色状态
}

/** 角色权限树节点 */
export interface PermissionTreeNodeVO {
  permissionId: long; // 权限ID
  permissionName: string; // 权限名称
  permissionType: PermissionTypeEnum; // 权限类型
  permissionCode: string; // 权限编码
  children: PermissionTreeNodeVO[]; // 子节点
  disabledStatus?: DisabledStatusEnum; // 禁用状态
}

/** 更新角色权限请求 */
export interface UpdateRolePermissionsRequestDTO {
  permissionIds: long[]; // 权限ID列表
}

export interface RoleMemberListQueryDTO extends PaginationQueryDTO, KeywordQueryDTO {}

/** 角色成员列表项 */
export interface RoleMemberListItemVO {
  userId: long; // 用户ID
  username: string; // 用户名
  organizationName: string; // 组织名称
  phone: string; // 手机号
}

/** 添加角色成员请求 */
export interface AddRoleMembersRequestDTO {
  userIds: long[]; // 用户ID列表
}
```

### 4. Applications

```ts
export interface ApplicationListQueryDTO extends PaginationQueryDTO, KeywordQueryDTO {}

/** 应用列表项 */
export interface ApplicationListItemVO {
  applicationCode: string; // 应用编码（唯一键）
  applicationName: string; // 应用名称
  applicationIdentifier: string; // 应用标识
  applicationStatus: ApplicationStatusEnum; // 应用状态
}

/** 创建应用请求 */
export interface CreateApplicationRequestDTO {
  applicationName: string; // 应用名称
  applicationIdentifier: string; // 应用标识
  description?: string; // 描述
  applicationStatus: ApplicationStatusEnum; // 应用状态
}

/** 更新应用请求 */
export interface UpdateApplicationRequestDTO {
  applicationName: string; // 应用名称
  description?: string; // 描述
  applicationStatus: ApplicationStatusEnum; // 应用状态
}
```

### 5. Permission Management（按应用）

```ts
/** 权限树查询参数（按应用） */
export interface PermissionTreeQueryDTO {
  applicationCode: string; // 应用编码（唯一键）
}

/** 权限列表项（树节点） */
export interface PermissionListItemVO {
  permissionId: long; // 权限ID
  permissionName: string; // 权限名称
  permissionType: PermissionTypeEnum; // 权限类型
  permissionCode: string; // 权限编码
  parentPermissionName: string; // parentPermission 名称
  permissionStatus: PermissionStatusEnum; // 权限状态
  children: PermissionListItemVO[]; // 子节点
}

/** 更新权限状态请求 */
export interface UpdatePermissionStatusRequestDTO {
  permissionStatus: PermissionStatusEnum; // 权限状态
}
```

### 6. Organizations（organizationCode）

```ts
export interface OrganizationListQueryDTO extends PaginationQueryDTO, KeywordQueryDTO {}

/** 组织列表项 */
export interface OrganizationListItemVO {
  organizationCode: string; // 组织编码（唯一键）
  name: string; // 名称
  internalMemberCount: int; // 内部成员数
  externalMemberCount: int; // 外部成员数
  adminName: string; // 管理员名称
  organizationStatus: OrganizationStatusEnum; // 组织状态
  createdAt: long; // 创建时间
}

/** 组织详情 */
export interface OrganizationDetailVO {
  organizationCode: string; // 组织编码（唯一键）
  name: string; // 名称
  description: string; // 描述
  organizationStatus: OrganizationStatusEnum; // 组织状态
  createdAt: long; // 创建时间
  contact: { // 联系人信息
    fullName?: string; // 姓名
    phone?: string; // 手机号
    email?: string; // 邮箱
  };
  adminRoleScopeIds: long[]; // adminRoleScopeIds 字段
  appRoleScopeIds: long[]; // appRoleScopeIds 字段
}

/** 创建组织请求 */
export interface CreateOrganizationRequestDTO {
  name: string; // 名称
  description?: string; // 描述
  adminRoleScopeIds?: long[]; // adminRoleScopeIds 字段
  appRoleScopeIds?: long[]; // appRoleScopeIds 字段
  organizationStatus: OrganizationStatusEnum; // 组织状态
}

/** 更新组织请求 */
export interface UpdateOrganizationRequestDTO {
  name?: string; // 名称
  description?: string; // 描述
  adminRoleScopeIds?: long[]; // adminRoleScopeIds 字段
  appRoleScopeIds?: long[]; // appRoleScopeIds 字段
  organizationStatus?: OrganizationStatusEnum; // 组织状态
  contact?: { // 联系人信息
    fullName?: string; // 姓名
    phone?: string; // 手机号
    email?: string; // 邮箱
  };
}

/** 分配组织管理员请求 */
export interface AssignOrganizationAdminRequestDTO {
  userId: long; // 用户ID
}

/** 组织删除预检统计返回 */
export interface OrganizationDeletePreviewVO {
  assetCount: int; // 资产数量
  userCount: int; // 用户数量
}

export interface OrganizationInternalMemberListQueryDTO extends PaginationQueryDTO, KeywordQueryDTO {}

/** 组织内部成员列表项 */
export interface OrganizationInternalMemberItemVO {
  userId: long; // 用户ID
  username: string; // 用户名
  fullName: string; // 姓名
  phone: string; // 手机号
  email: string; // 邮箱
  accountStatus: AccountStatusEnum; // 账号状态
  assetScope: JsonObject; // 资产范围
}

/** 创建组织内部成员请求 */
export interface CreateOrganizationInternalMemberRequestDTO {
  username: string; // 用户名
  password?: string; // 密码
  fullName?: string; // 姓名
  phone?: string; // 手机号
  email?: string; // 邮箱
  accountStatus?: AccountStatusEnum; // 账号状态
  roleAssignments?: RoleAssignmentDTO[]; // 角色分配列表
  assetScope?: JsonObject; // 资产范围
}

/** 更新组织内部成员请求 */
export interface UpdateOrganizationInternalMemberRequestDTO {
  fullName?: string; // 姓名
  phone?: string; // 手机号
  email?: string; // 邮箱
  accountStatus?: AccountStatusEnum; // 账号状态
  roleAssignments?: RoleAssignmentDTO[]; // 角色分配列表
  assetScope?: JsonObject; // 资产范围
}

export interface OrganizationExternalMemberSearchQueryDTO extends PaginationQueryDTO, KeywordQueryDTO {}

/** 组织外部成员列表项 */
export interface OrganizationExternalMemberItemVO {
  userId: long; // 用户ID
  username: string; // 用户名
  organizationName: string; // 组织名称
}

/** 添加组织外部成员请求 */
export interface AddOrganizationExternalMemberRequestDTO {
  userId: long; // 用户ID
  roleAssignments?: RoleAssignmentDTO[]; // 角色分配列表
  assetScope?: JsonObject; // 资产范围
}
```