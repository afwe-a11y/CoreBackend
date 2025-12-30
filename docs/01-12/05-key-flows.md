# 05-key-flows.md

## 组织列表查询
1. Controller 接收 `keyword/page/size` 参数。
2. ApplicationService 拉取全部组织、按关键字过滤与创建时间倒序排序。
3. Controller 将结果转换为列表 DTO，并格式化状态与日期。
4. 返回分页响应。

## 创建组织
1. Controller 将 DTO 转换为 CreateOrganizationCommand。
2. ApplicationService 校验名称/编码/描述与唯一性。
3. 生成雪花 ID，写入组织与组织-应用映射。
4. 返回组织 ID。

## 更新组织（含应用移除清理）
1. Controller 将 DTO 转换为 UpdateOrganizationCommand。
2. ApplicationService 校验名称/描述/联系人/状态。
3. 在事务中更新组织信息并替换组织应用映射。
4. 对被移除应用的角色授权执行软删除。

## 删除组织
1. Controller 触发 deleteOrganization。
2. ApplicationService 在事务中：软删除组织、清空应用映射、软删除内部用户/成员关系/角色授权、软删除外部成员关联。

## 分配组织管理员
1. Controller 接收 userId，转换为 AssignAdminCommand。
2. ApplicationService 校验用户存在与显示名格式。
3. 写入 ORG_ADMIN 授权并更新组织管理员显示名。

## 内部成员管理
- 列表：读取组织成员关系 → 查询用户与角色授权 → 分页返回。
- 创建：校验用户名/手机号邮箱/关联组织/角色 → 创建用户 → 写入成员关系与角色授权。
- 更新：校验成员存在 → 更新用户信息与角色分类。
- 停用：校验成员存在 → 更新用户状态为 DISABLED。
- 删除：软删除用户与成员关系、角色授权。

## 外部成员管理
- 列表：读取外部成员关系 → 查询用户与来源组织 → 分页返回。
- 关联：校验用户存在、非本组织成员、未重复关联、存在归属组织 → 写入外部成员关系。
- 移除：软删除外部成员关系。

## Evidence
- iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/OrganizationController.java | OrganizationController | 组织相关入口 | L36-L139
- iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/MemberController.java | MemberController | 成员相关入口 | L36-L149
- iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/OrganizationApplicationService.java | OrganizationApplicationService | 组织用例编排 | L70-L239
- iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/MemberApplicationService.java | MemberApplicationService | 成员用例编排 | L72-L309
- iam-service/iam-infrastructure/src/main/java/com/tenghe/corebackend/infrastructure/snowflake/SnowflakeIdGenerator.java | SnowflakeIdGenerator | ID 生成 | L6-L52
- iam-service/iam-infrastructure/src/main/java/com/tenghe/corebackend/infrastructure/inmemory/LocalTransactionManager.java | LocalTransactionManager | 事务边界 | L6-L15

## UNKNOWN/ASSUMED
- 鉴权、审计、日志、限流等横切能力 UNKNOWN：未见相关实现。
- 外部服务调用/消息机制 UNKNOWN：未见相关端口或适配器。
