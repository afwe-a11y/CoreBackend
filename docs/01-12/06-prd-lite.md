# 06-prd-lite.md

## 背景与目标
- 本服务聚焦“组织与成员管理”的基础能力：组织增删改查、管理员分配、成员管理与外部成员关联。
- 其他业务背景/多端需求为 UNKNOWN（未在代码中体现）。

## 目标用户与角色
- 组织管理用户（管理端）ASSUMED：由 `AccountType.MANAGEMENT` 角色分类推断。
- 应用端用户 ASSUMED：由 `AccountType.APPLICATION` 推断。

## 功能范围（以代码为准）
### 组织管理
- 组织列表：支持关键字过滤、按创建时间倒序、分页（默认 10）。
- 创建组织：校验名称/编码格式与唯一性，写入组织与组织-应用映射。
- 组织详情：返回基础信息、状态与联系人信息。
- 更新组织：支持修改名称/描述/联系人/状态/应用映射，并清理被移除应用授权。
- 删除组织：软删除组织，清理成员关系、外部关系与授权。
- 管理员分配：为指定用户授予 ORG_ADMIN 并更新管理员显示名。

### 成员管理
- 内部成员：列表、创建、更新、停用、删除。
- 外部成员：列表、搜索、关联、移除。

### Demo 接口
- /hello、/user、/save_user、/html 等为示例接口，非业务主流程。

## 关键规则
- 用户名为字母数字组合且唯一；手机号/邮箱至少填写一项。
- 组织编码格式为字母/数字/下划线组合。
- 组织状态与用户状态由枚举解析。
- 外部成员关联需排除本组织成员，并禁止重复关联。

## 非功能
- 鉴权/审计/限流/日志策略 UNKNOWN。
- 持久化与数据库方案 UNKNOWN（当前为内存仓库）。

## Evidence
- iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/OrganizationController.java | OrganizationController | 组织功能入口 | L36-L139
- iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/MemberController.java | MemberController | 成员功能入口 | L36-L149
- iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/OrganizationApplicationService.java | OrganizationApplicationService | 组织规则与行为 | L70-L239
- iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/MemberApplicationService.java | MemberApplicationService | 成员规则与行为 | L72-L309
- iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/validation/ValidationUtils.java | ValidationUtils | 关键校验规则 | L7-L72

## UNKNOWN/ASSUMED
- 业务场景描述与多端需求 UNKNOWN：未见需求文档或 README。
- 角色体系与权限模型 UNKNOWN：仅见 `RoleGrant` 数据结构与 ORG_ADMIN 授权写入。
- 管理端/应用端用户的具体权限 ASSUMED：仅可从枚举名称推断。
