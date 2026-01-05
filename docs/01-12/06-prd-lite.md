# 06-prd-lite.md

## 范围与角色

- IAM：组织、用户、角色、权限、应用管理，含登录与密码重置。
- Device：设备模型、产品、网关、设备与导入导出、遥测查询。

## 关键需求（按实现）

### 组织管理

- 组织列表支持关键字搜索与分页，默认按创建时间倒序。
- 组织创建校验名称/编码唯一性，生成雪花 ID。
- 组织详情支持联系人字段。
- 更新组织可调整可用应用并清理相关角色授权。
- 删除组织采用软删除并清理成员关系/外部关联。
- 组织管理员分配：授予“组织管理员”角色并设置展示名。

### 成员管理

- 内部成员创建：要求用户名、邮箱、关联组织与角色，校验角色与应用匹配。
- 内部成员更新：可修改姓名、手机号、邮箱、状态、账号类型。
- 外部成员：支持搜索、关联、解除关联。

### 用户与权限

- 用户创建发送初始密码邮件。
- 权限树支持树形查询与状态级联禁用。

### 设备管理

- 设备模型支持 NEW/INHERIT，限制继承深度（单层）。
- 设备模型测点支持导入/更新，校验标识唯一与类型一致。
- 产品创建生成 productKey/productSecret，并保存协议映射。
- 网关支持启用/停用；更新时同步子设备 stationId。
- 设备导入以行列表提交（非文件上传）；导出返回文件名与行列表。
- 设备遥测查询返回最新值列表。

## 重要差异/缺口（从代码观测）

- 内部成员与用户创建均强制邮箱必填；“手机号/邮箱至少一个”未在创建流程体现。ASSUMED（可能由上层补充）
- 验证码响应字段为 captchaImage，但实际返回内容为验证码字符串而非图片。ASSUMED（可能为占位）。
- 设备导入未实现 Excel 解析，仅接受 rows 列表。UNKNOWN（是否由上游处理 Excel）。
- 网关禁用“停止数据处理”未见实现，enabled 字段仅保存开关状态。UNKNOWN。

## Evidence

-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/OrganizationApplicationServiceImpl.java |
listOrganizations | 排序与分页 | L77-L95
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/OrganizationApplicationServiceImpl.java |
createOrganization | 唯一性校验与生成 ID | L98-L128
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/OrganizationController.java |
getOrganizationDetail | 联系人字段返回 | L53-L65
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/OrganizationApplicationServiceImpl.java |
updateOrganization | 应用变更清理授权 | L171-L191
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/OrganizationApplicationServiceImpl.java |
deleteOrganization | 软删除与清理 | L196-L216
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/OrganizationApplicationServiceImpl.java |
assignAdmin | 授权组织管理员 | L238-L263
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/MemberApplicationServiceImpl.java |
createInternalMember | 邮箱必填与角色校验 | L108-L197
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/UserApplicationServiceImpl.java |
createUser | 邮箱必填与发送初始密码 | L132-L213
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/AuthController.java |
getCaptcha | captchaImage 字段承载验证码内容 | L33-L37
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/DeviceModelApplicationService.java |
createModel/importPoints | 模型与测点校验 | L82-L183
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/ProductApplicationService.java |
createProduct | 生成 key/secret | L71-L106
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/GatewayApplicationService.java |
updateGateway/toggleGateway | 网关更新与启用/停用 | L126-L186
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/DeviceApplicationService.java |
previewImport/commitImport | 导入流程 | L149-L221
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/DeviceApplicationService.java |
exportDevices | 导出文件名格式 | L223-L249

## UNKNOWN/ASSUMED

- ASSUMED：上层系统可能在 BFF/前端限制手机/邮箱至少一个，但本服务创建流程未体现。
- UNKNOWN：验证码图像化/短信通道等外部能力未见实现。
