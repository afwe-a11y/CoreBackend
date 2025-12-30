# 08-test-plan.md

## 测试范围
- 应用服务层单元测试：组织与成员用例、参数校验与异常分支。
- 接口层集成测试：HTTP 路由、请求参数绑定与响应结构。
- 基础设施适配器测试：内存仓库与事务管理的基本行为。

## 关键用例
### 组织相关
- 创建组织：
  - name/code 为空、格式错误、长度超限。
  - name/code 重复。
  - 正常创建返回 ID。
- 更新组织：
  - 状态非法、联系人手机号/邮箱格式非法。
  - 应用映射移除触发角色授权清理。
- 删除组织：
  - 组织不存在。
  - 删除后组织、成员关系、外部关系、授权均被软删除。
- 分配管理员：
  - 用户不存在；管理员显示名不合法。

### 成员相关
- 创建内部成员：
  - 用户名格式与唯一性；手机号/邮箱至少一项。
  - 关联组织为空或不包含当前组织。
  - 角色选择不完整或不匹配组织应用。
- 更新内部成员：
  - 成员不存在、账号类型/状态非法。
- 停用/删除内部成员：
  - 成员不存在。
- 关联外部成员：
  - 不可关联本组织成员。
  - 已关联外部成员与跨组织重复关联。
  - 用户无归属组织。

### Demo 接口
- /hello、/user、/save_user、/html 基础回归，确保返回格式稳定。

## 测试数据与准备
- 使用 InMemory 仓库作为默认实现（可直接构造并注入）。
- 通过 `IdGeneratorPort` 生成稳定 ID（可替换为固定值实现）。

## Evidence
- iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/OrganizationApplicationService.java | OrganizationApplicationService | 组织流程与校验 | L70-L239
- iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/MemberApplicationService.java | MemberApplicationService | 成员流程与校验 | L72-L309
- iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/validation/ValidationUtils.java | ValidationUtils | 校验规则 | L7-L72
- iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/OrganizationController.java | OrganizationController | HTTP 路由 | L36-L139
- iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/MemberController.java | MemberController | HTTP 路由 | L36-L149
- iam-service/iam-infrastructure/src/main/java/com/tenghe/corebackend/infrastructure/inmemory/LocalTransactionManager.java | LocalTransactionManager | 事务边界 | L6-L15
- iam-service/iam-main/src/test/java/com/tenghe/corebackend/CoreBackendApplicationTests.java | CoreBackendApplicationTests | 现有测试样例 | L1-L12

## UNKNOWN/ASSUMED
- 测试环境与数据装载方式 UNKNOWN：未见测试配置或 README。
- 接口鉴权测试 ASSUMED：目前代码未实现鉴权逻辑。
