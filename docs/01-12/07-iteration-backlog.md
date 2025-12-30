# 07-iteration-backlog.md

## 迭代清单（基于代码缺口与 UNKNOWN/ASSUMED）
### P0
- 补齐持久化存储（ASSUMED）：当前仅有 InMemory 仓库实现，生产持久化未实现。
- 补齐鉴权/鉴别（UNKNOWN）：未见安全配置或拦截器实现。
- 明确错误码体系（UNKNOWN）：`ApiResponse` 仅含 message，无标准 code。

### P1
- 增加组织/用户唯一性持久化约束（ASSUMED）：代码侧校验存在，但缺少数据库唯一约束证据。
- 完善外部成员重复关联提示信息格式（ASSUMED）：代码中错误消息已包含组织名，但未见统一规范。
- 补齐 App、Role 领域对象（UNKNOWN）：仅见 appId/roleCode 字段。

### P2
- 增补审计日志/操作记录（UNKNOWN）。
- 增补运行监控与健康检查（UNKNOWN）。
- 增补组织/成员的批量操作能力（ASSUMED）。

## Evidence
- iam-service/iam-infrastructure/src/main/java/com/tenghe/corebackend/infrastructure/inmemory/InMemoryOrganizationRepository.java | InMemoryOrganizationRepository | 内存仓库实现 | L11-L67
- iam-service/iam-infrastructure/src/main/java/com/tenghe/corebackend/infrastructure/inmemory/InMemoryUserRepository.java | InMemoryUserRepository | 内存仓库实现 | L12-L95
- iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/GlobalExceptionHandler.java | GlobalExceptionHandler | 错误响应仅包含 message | L10-L20
- iam-service/iam-model/src/main/java/com/tenghe/corebackend/model/OrganizationApp.java | OrganizationApp | 仅 appId 字段 | L8-L11
- iam-service/iam-model/src/main/java/com/tenghe/corebackend/model/RoleGrant.java | RoleGrant | 仅 roleCode 字段 | L9-L17

## UNKNOWN/ASSUMED
- 鉴权与权限中间件 UNKNOWN：未发现相关实现类或配置。
- 批量操作需求 ASSUMED：基于业务常见需求推断，代码无证据。
