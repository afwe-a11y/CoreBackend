# 08-test-plan.md

## 测试范围

- IAM：认证、组织、成员、角色、权限、应用。
- Device：设备模型、产品、网关、设备、导入导出与遥测。

## 建议测试用例

### 鉴权与认证

- 登录验证码校验失败/成功分支。
- 登录失败次数累积与锁定逻辑。
- resetPassword 密码强度校验与邮箱验证码失效。

### 组织与成员

- 组织创建：名称/编码唯一性、描述长度。
- 组织更新：应用删除触发角色授权清理。
- 组织删除：软删除组织并清理成员与外部关联。
- 内部成员创建：用户名格式、邮箱唯一、角色与应用匹配。
- 外部成员关联：不可关联本组织成员/重复关联。

### 角色与权限

- 角色创建：编码与名称冲突。
- 预置角色不可编辑/删除。
- 配置角色权限：权限必须属于应用。
- 权限状态禁用级联子节点。

### 设备模型/产品/网关

- 设备模型继承深度限制与测点标识唯一。
- 测点导入：父模型点不可修改、类型不一致报错。
- 产品创建：生成 key/secret，删除时存在设备阻断。
- 网关更新：stationId 变更同步设备 stationId。

### 设备与导入导出

- 设备创建：deviceKey 唯一、dynamicAttributes 类型兼容。
- 设备更新：网关变更同步 stationId。
- 导入预览/提交：CREATE/UPDATE/INVALID 行识别；失败行返回原因。
- 导出文件名格式与筛选条件影响。

## Evidence

-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/AuthenticationApplicationServiceImpl.java |
login | 登录失败次数与锁定 | L41-L133
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/PasswordResetApplicationServiceImpl.java |
validatePasswordStrength | 密码强度规则 | L101-L116
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/OrganizationApplicationServiceImpl.java |
updateOrganization/deleteOrganization | 应用清理与删除事务 | L151-L216
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/MemberApplicationServiceImpl.java |
createInternalMember/linkExternalMember | 成员校验逻辑 | L108-L342
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/RoleApplicationServiceImpl.java |
updateRole/deleteRole/configureRolePermissions | 角色与权限校验 | L158-L210
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/PermissionApplicationServiceImpl.java |
togglePermissionStatus | 权限级联禁用 | L37-L61
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/DeviceModelApplicationService.java |
createModel/importPoints | 模型与测点校验 | L82-L183
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/ProductApplicationService.java |
createProduct/deleteProduct | 产品生成/删除校验 | L71-L133
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/GatewayApplicationService.java |
updateGateway | stationId 同步 | L126-L170
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/DeviceApplicationService.java |
createDevice/commitImport/exportDevices | 设备创建/导入/导出 | L95-L253

## UNKNOWN/ASSUMED

- ASSUMED：测试执行环境提供可用数据库与 MyBatis 运行配置。
