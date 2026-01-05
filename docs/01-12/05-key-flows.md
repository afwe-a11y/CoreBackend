# 05-key-flows.md

## 登录与会话

1) GET /api/auth/captcha 生成验证码 key 与验证码内容。
2) POST /api/auth/login 校验验证码与密码，生成 token，更新失败次数/锁定时间。
3) POST /api/auth/logout 失效化 token。
4) GET /api/auth/validate 校验 token 有效性，不合法则失效化。

## 组织创建与应用绑定

1) 校验组织名称/编码/描述。
2) 生成 ID、保存组织记录。
3) 通过 OrganizationAppRepository 替换组织可用应用集合。

## 组织更新（含应用变更清理）

1) 校验组织名称、描述与联系人信息。
2) 事务内更新组织信息。
3) 若应用集合变更，替换组织应用并对被移除应用的角色授权做软删除清理。

## 组织删除

1) 事务内软删除组织记录。
2) 清理组织应用映射。
3) 对内部成员：若仅属于当前组织则软删除用户；清理该组织下角色授权与成员关系。
4) 对外部成员：软删除外部关联。

## 组织管理员分配

1) 校验用户存在与管理员展示名合法性。
2) 查询“组织管理员”角色并生成 RoleGrant。
3) 更新组织 primaryAdminDisplay。

## 内部成员创建

1) 校验用户名/邮箱/角色选择与组织应用匹配。
2) 生成用户并保存。
3) 建立组织成员关系与角色授权。

## 外部成员关联

1) 校验用户存在且不属于当前组织。
2) 校验外部成员未重复关联，并检查已有外部成员归属。
3) 新增外部成员关系。

## 设备模型点导入

1) 校验模型存在与继承层级。
2) 校验测点标识唯一、类型与枚举项。
3) 合并并更新测点集合（事务内）。

## 网关更新（电站同步）

1) 校验 SN 唯一性与网关产品类型。
2) 更新网关信息。
3) 若电站变更，同步更新所有子设备 stationId。

## 设备导入（提交）

1) 对每行判定 CREATE/UPDATE/INVALID。
2) 对 CREATE 行创建设备；对 UPDATE 行更新设备。
3) 统计成功/失败并返回行级错误信息。

## Evidence

-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/AuthController.java |
login/logout/validateSession | 登录/注销/校验入口 | L43-L81
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/AuthenticationApplicationServiceImpl.java |
login/validateSession | 生成 token 与校验逻辑 | L41-L99
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/OrganizationApplicationServiceImpl.java |
createOrganization | 生成 ID 与保存组织 | L98-L128
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/OrganizationApplicationServiceImpl.java |
updateOrganization | 事务更新与清理授权 | L151-L192
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/OrganizationApplicationServiceImpl.java |
deleteOrganization | 软删除与清理关系 | L196-L216
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/OrganizationApplicationServiceImpl.java |
assignAdmin | 保存 RoleGrant 并更新组织 | L238-L263
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/MemberApplicationServiceImpl.java |
createInternalMember | 保存用户与角色授权 | L108-L197
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/MemberApplicationServiceImpl.java |
linkExternalMember | 外部成员关联逻辑 | L318-L342
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/DeviceModelApplicationService.java |
importPoints | 导入测点合并 | L163-L183
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/GatewayApplicationService.java |
updateGateway | 电站变更同步设备 | L126-L170
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/DeviceApplicationService.java |
commitImport | 导入提交流程 | L182-L221

## UNKNOWN/ASSUMED

- ASSUMED：流程中的事务边界以 TransactionManagerPort 实现为准，具体事务传播策略需结合配置确认。
- UNKNOWN：登录 token 的过期策略与存储持久化策略未在代码中体现。
