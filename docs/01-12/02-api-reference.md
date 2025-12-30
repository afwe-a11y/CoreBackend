# 02-api-reference.md

## 通用说明
- `ApiResponse<T>`: success/message/data 包装（适用于 `@RestController` 的接口）。
- `PageResponse<T>`: items/total/page/size 的分页结构。
- 示例均以当前代码可见 DTO/返回对象为准，未定义的示例标注 `UNKNOWN`。

### GET /api/organizations
1) Purpose
- 获取组织列表，支持关键字检索与分页。
2) AuthN/AuthZ
- UNKNOWN（未发现鉴权配置证据）。
3) Request
- 参数表：
| 参数 | 位置 | 类型 | 必填 | 说明 | Evidence |
| --- | --- | --- | --- | --- | --- |
| keyword | query | String | 否 | 名称或 ID 模糊匹配 | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/OrganizationController.java | listOrganizations | L48-L53 |
| page | query | Integer | 否 | 页码，默认 1 | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/OrganizationApplicationService.java | normalizePage | L287-L292 |
| size | query | Integer | 否 | 页大小，默认 10 | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/OrganizationApplicationService.java | normalizeSize | L294-L299 |
- 示例：UNKNOWN
4) Response
- 字段表（ApiResponse.data = PageResponse<OrganizationListItem>）：
| 字段 | 类型 | 说明 | Evidence |
| --- | --- | --- | --- |
| success | boolean | 成功标识 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/common/ApiResponse.java | ApiResponse | L3-L24 |
| message | String | OK 或错误信息 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/common/ApiResponse.java | ApiResponse | L3-L24 |
| data.items[].id | String | 组织 ID（字符串） | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/OrganizationController.java | toListItem | L142-L151 |
| data.items[].name | String | 组织名称 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/organization/OrganizationListItem.java | OrganizationListItem | L1-L65 |
| data.items[].internalMemberCount | long | 内部成员数 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/organization/OrganizationListItem.java | OrganizationListItem | L1-L65 |
| data.items[].externalMemberCount | long | 外部成员数 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/organization/OrganizationListItem.java | OrganizationListItem | L1-L65 |
| data.items[].primaryAdminDisplay | String | 管理员显示名 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/organization/OrganizationListItem.java | OrganizationListItem | L1-L65 |
| data.items[].status | String | 状态（中文） | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/OrganizationController.java | formatStatus | L154-L163 |
| data.items[].createdDate | String | 创建日期 yyyy-MM-dd | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/OrganizationController.java | toListItem | L142-L151 |
| data.total | long | 总数 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/common/PageResponse.java | PageResponse | L5-L19 |
| data.page | int | 页码 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/common/PageResponse.java | PageResponse | L5-L19 |
| data.size | int | 页大小 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/common/PageResponse.java | PageResponse | L5-L19 |
- 示例：UNKNOWN
5) Errors
| http_status | code | condition | source | evidence |
| --- | --- | --- | --- | --- |
| 500 | UNKNOWN | 未捕获异常 | GlobalExceptionHandler | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/GlobalExceptionHandler.java | handleException | L17-L19 |
6) Side Effects
- 无（只读）。
7) Idempotency
- ASSUMED: 是（只读查询）。
8) Evidence
- iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/OrganizationController.java | listOrganizations | 路由与参数 | L48-L59
- iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/OrganizationApplicationService.java | listOrganizations | 列表查询与排序 | L70-L87

### POST /api/organizations
1) Purpose
- 创建组织并保存组织-应用映射。
2) AuthN/AuthZ
- UNKNOWN（未发现鉴权配置证据）。
3) Request
- 参数表（JSON Body）：
| 参数 | 类型 | 必填 | 说明 | Evidence |
| --- | --- | --- | --- | --- |
| name | String | 是 | 组织名称 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/organization/CreateOrganizationRequest.java | CreateOrganizationRequest | L5-L31 |
| code | String | 是 | 组织编码 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/organization/CreateOrganizationRequest.java | CreateOrganizationRequest | L5-L31 |
| description | String | 否 | 组织描述 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/organization/CreateOrganizationRequest.java | CreateOrganizationRequest | L5-L31 |
| appIds | List<Long> | 否 | 可使用应用 ID 列表 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/organization/CreateOrganizationRequest.java | CreateOrganizationRequest | L5-L40 |
- 示例：UNKNOWN
4) Response
- 字段表（ApiResponse.data = CreateOrganizationResponse）：
| 字段 | 类型 | 说明 | Evidence |
| --- | --- | --- | --- |
| data.id | String | 组织 ID | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/organization/CreateOrganizationResponse.java | CreateOrganizationResponse | L1-L19 |
- 示例：UNKNOWN
5) Errors
| http_status | code | condition | source | evidence |
| --- | --- | --- | --- | --- |
| 400 | UNKNOWN | 组织名称为空/长度超限 | ValidationUtils | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/OrganizationApplicationService.java | createOrganization | L90-L95 |
| 400 | UNKNOWN | 组织编码为空/格式不正确 | ValidationUtils | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/OrganizationApplicationService.java | createOrganization | L93-L95 |
| 400 | UNKNOWN | 组织描述长度超限 | ValidationUtils | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/OrganizationApplicationService.java | createOrganization | L95-L95 |
| 400 | UNKNOWN | 组织名称已被占用（消息要求固定） | BusinessException | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/OrganizationApplicationService.java | createOrganization | L97-L100 |
| 400 | UNKNOWN | 组织编码已被占用 | BusinessException | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/OrganizationApplicationService.java | createOrganization | L101-L104 |
| 500 | UNKNOWN | 未捕获异常 | GlobalExceptionHandler | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/GlobalExceptionHandler.java | handleException | L17-L19 |
6) Side Effects
- 写入组织与组织应用映射。 
7) Idempotency
- ASSUMED: 否（创建会生成新 ID）。
8) Evidence
- iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/OrganizationController.java | createOrganization | DTO -> Command | L62-L70
- iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/OrganizationApplicationService.java | createOrganization | 保存组织与应用映射 | L90-L119

### GET /api/organizations/{organizationId}
1) Purpose
- 获取组织详情。
2) AuthN/AuthZ
- UNKNOWN。
3) Request
- 参数表：
| 参数 | 位置 | 类型 | 必填 | 说明 | Evidence |
| --- | --- | --- | --- | --- | --- |
| organizationId | path | Long | 是 | 组织 ID | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/OrganizationController.java | getOrganizationDetail | L73-L86 |
- 示例：UNKNOWN
4) Response
- 字段表（ApiResponse.data = OrganizationDetailResponse）：
| 字段 | 类型 | 说明 | Evidence |
| --- | --- | --- | --- |
| data.id | String | 组织 ID | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/organization/OrganizationDetailResponse.java | OrganizationDetailResponse | L5-L87 |
| data.name | String | 组织名称 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/organization/OrganizationDetailResponse.java | OrganizationDetailResponse | L5-L87 |
| data.createdDate | String | 创建日期 yyyy-MM-dd | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/OrganizationController.java | getOrganizationDetail | L73-L85 |
| data.description | String | 描述 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/organization/OrganizationDetailResponse.java | OrganizationDetailResponse | L5-L87 |
| data.appIds | List<Long> | 可使用应用 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/organization/OrganizationDetailResponse.java | OrganizationDetailResponse | L5-L87 |
| data.status | String | 状态（NORMAL/DISABLED） | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/OrganizationApplicationService.java | getOrganizationDetail | L122-L137 |
| data.contactName | String | 联系人姓名 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/organization/OrganizationDetailResponse.java | OrganizationDetailResponse | L5-L87 |
| data.contactPhone | String | 联系人手机 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/organization/OrganizationDetailResponse.java | OrganizationDetailResponse | L5-L87 |
| data.contactEmail | String | 联系人邮箱 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/organization/OrganizationDetailResponse.java | OrganizationDetailResponse | L5-L87 |
- 示例：UNKNOWN
5) Errors
| http_status | code | condition | source | evidence |
| --- | --- | --- | --- | --- |
| 400 | UNKNOWN | 组织不存在 | BusinessException | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/OrganizationApplicationService.java | requireOrganization | L254-L261 |
| 500 | UNKNOWN | 未捕获异常 | GlobalExceptionHandler | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/GlobalExceptionHandler.java | handleException | L17-L19 |
6) Side Effects
- 无（只读）。
7) Idempotency
- ASSUMED: 是（只读查询）。
8) Evidence
- iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/OrganizationController.java | getOrganizationDetail | 路由与返回 DTO | L73-L86
- iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/OrganizationApplicationService.java | getOrganizationDetail | 读取组织与应用 | L122-L137

### PUT /api/organizations/{organizationId}
1) Purpose
- 更新组织基础信息、状态与应用关联，并清理移除应用的角色授权。
2) AuthN/AuthZ
- UNKNOWN。
3) Request
- 参数表（path + JSON Body）：
| 参数 | 位置 | 类型 | 必填 | 说明 | Evidence |
| --- | --- | --- | --- | --- | --- |
| organizationId | path | Long | 是 | 组织 ID | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/OrganizationController.java | updateOrganization | L89-L103 |
| name | body | String | 是 | 组织名称 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/organization/UpdateOrganizationRequest.java | UpdateOrganizationRequest | L5-L66 |
| description | body | String | 否 | 描述 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/organization/UpdateOrganizationRequest.java | UpdateOrganizationRequest | L5-L66 |
| appIds | body | List<Long> | 否 | 应用 ID 列表 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/organization/UpdateOrganizationRequest.java | UpdateOrganizationRequest | L5-L66 |
| status | body | String | 是 | NORMAL/DISABLED 或 中文值 | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/OrganizationApplicationService.java | updateOrganization | L155-L157 |
| contactName | body | String | 否 | 联系人姓名 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/organization/UpdateOrganizationRequest.java | UpdateOrganizationRequest | L5-L66 |
| contactPhone | body | String | 否 | 联系人手机 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/organization/UpdateOrganizationRequest.java | UpdateOrganizationRequest | L5-L66 |
| contactEmail | body | String | 否 | 联系人邮箱 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/organization/UpdateOrganizationRequest.java | UpdateOrganizationRequest | L5-L66 |
- 示例：UNKNOWN
4) Response
- ApiResponse<Void>，无 data。
5) Errors
| http_status | code | condition | source | evidence |
| --- | --- | --- | --- | --- |
| 400 | UNKNOWN | 组织不存在 | BusinessException | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/OrganizationApplicationService.java | requireOrganization | L254-L261 |
| 400 | UNKNOWN | 组织名称为空/长度超限 | ValidationUtils | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/OrganizationApplicationService.java | updateOrganization | L140-L144 |
| 400 | UNKNOWN | 组织描述长度超限 | ValidationUtils | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/OrganizationApplicationService.java | updateOrganization | L144-L145 |
| 400 | UNKNOWN | 联系人手机号/邮箱格式不正确 | ValidationUtils | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/OrganizationApplicationService.java | updateOrganization | L145-L146 |
| 400 | UNKNOWN | 组织名称已被占用 | BusinessException | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/OrganizationApplicationService.java | updateOrganization | L148-L152 |
| 400 | UNKNOWN | 组织状态不能为空 | BusinessException | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/OrganizationApplicationService.java | updateOrganization | L155-L157 |
| 500 | UNKNOWN | 未捕获异常 | GlobalExceptionHandler | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/GlobalExceptionHandler.java | handleException | L17-L19 |
6) Side Effects
- 更新组织记录、更新组织应用映射、清理已移除应用的角色授权。
7) Idempotency
- ASSUMED: 是（重复提交相同数据结果一致）。
8) Evidence
- iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/OrganizationController.java | updateOrganization | 路由与 DTO 转换 | L89-L103
- iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/OrganizationApplicationService.java | updateOrganization | 更新与角色清理 | L140-L182

### DELETE /api/organizations/{organizationId}
1) Purpose
- 删除组织（软删除）并清理成员、关联与授权。
2) AuthN/AuthZ
- UNKNOWN。
3) Request
- 参数表：
| 参数 | 位置 | 类型 | 必填 | 说明 | Evidence |
| --- | --- | --- | --- | --- | --- |
| organizationId | path | Long | 是 | 组织 ID | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/OrganizationController.java | deleteOrganization | L106-L109 |
- 示例：UNKNOWN
4) Response
- ApiResponse<Void>，无 data。
5) Errors
| http_status | code | condition | source | evidence |
| --- | --- | --- | --- | --- |
| 400 | UNKNOWN | 组织不存在 | BusinessException | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/OrganizationApplicationService.java | requireOrganization | L254-L261 |
| 500 | UNKNOWN | 未捕获异常 | GlobalExceptionHandler | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/GlobalExceptionHandler.java | handleException | L17-L19 |
6) Side Effects
- 软删除组织、清空组织应用映射、软删除内部成员及其授权、移除外部成员关联。
7) Idempotency
- ASSUMED: 是（多次删除不会改变最终删除状态）。
8) Evidence
- iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/OrganizationController.java | deleteOrganization | 路由 | L106-L109
- iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/OrganizationApplicationService.java | deleteOrganization | 删除与清理逻辑 | L185-L201

### GET /api/organizations/{organizationId}/delete-info
1) Purpose
- 获取删除确认信息（组织名称与成员数量）。
2) AuthN/AuthZ
- UNKNOWN。
3) Request
- 参数表：
| 参数 | 位置 | 类型 | 必填 | 说明 | Evidence |
| --- | --- | --- | --- | --- | --- |
| organizationId | path | Long | 是 | 组织 ID | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/OrganizationController.java | getDeleteInfo | L112-L119 |
- 示例：UNKNOWN
4) Response
- 字段表（ApiResponse.data = DeleteOrganizationInfoResponse）：
| 字段 | 类型 | 说明 | Evidence |
| --- | --- | --- | --- |
| data.name | String | 组织名称 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/organization/DeleteOrganizationInfoResponse.java | DeleteOrganizationInfoResponse | L1-L20 |
| data.userCount | long | 内部成员数 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/organization/DeleteOrganizationInfoResponse.java | DeleteOrganizationInfoResponse | L1-L20 |
- 示例：UNKNOWN
5) Errors
| http_status | code | condition | source | evidence |
| --- | --- | --- | --- | --- |
| 400 | UNKNOWN | 组织不存在 | BusinessException | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/OrganizationApplicationService.java | requireOrganization | L254-L261 |
| 500 | UNKNOWN | 未捕获异常 | GlobalExceptionHandler | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/GlobalExceptionHandler.java | handleException | L17-L19 |
6) Side Effects
- 无（只读）。
7) Idempotency
- ASSUMED: 是（只读查询）。
8) Evidence
- iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/OrganizationController.java | getDeleteInfo | 路由与响应 | L112-L119
- iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/OrganizationApplicationService.java | getDeleteInfo | 查询成员数 | L204-L209

### GET /api/organizations/{organizationId}/admin/search
1) Purpose
- 搜索组织管理员候选用户（按关键字）。
2) AuthN/AuthZ
- UNKNOWN。
3) Request
- 参数表：
| 参数 | 位置 | 类型 | 必填 | 说明 | Evidence |
| --- | --- | --- | --- | --- | --- |
| organizationId | path | Long | 是 | 组织 ID | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/OrganizationController.java | searchAdminCandidates | L122-L128 |
| keyword | query | String | 否 | 关键字（用户名/邮箱/手机） | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/OrganizationApplicationService.java | searchAdminCandidates | L212-L217 |
- 示例：UNKNOWN
4) Response
- 字段表（ApiResponse.data = List<UserSummary>）：
| 字段 | 类型 | 说明 | Evidence |
| --- | --- | --- | --- |
| data[].id | String | 用户 ID | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/user/UserSummary.java | UserSummary | L1-L46 |
| data[].username | String | 用户名 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/user/UserSummary.java | UserSummary | L1-L46 |
| data[].name | String | 姓名 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/user/UserSummary.java | UserSummary | L1-L46 |
| data[].phone | String | 手机 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/user/UserSummary.java | UserSummary | L1-L46 |
| data[].email | String | 邮箱 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/user/UserSummary.java | UserSummary | L1-L46 |
- 示例：UNKNOWN
5) Errors
| http_status | code | condition | source | evidence |
| --- | --- | --- | --- | --- |
| 400 | UNKNOWN | 组织不存在 | BusinessException | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/OrganizationApplicationService.java | requireOrganization | L254-L261 |
| 500 | UNKNOWN | 未捕获异常 | GlobalExceptionHandler | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/GlobalExceptionHandler.java | handleException | L17-L19 |
6) Side Effects
- 无（只读）。
7) Idempotency
- ASSUMED: 是（只读查询）。
8) Evidence
- iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/OrganizationController.java | searchAdminCandidates | 路由与响应 | L122-L128
- iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/OrganizationApplicationService.java | searchAdminCandidates | 调用 userRepository | L212-L217

### POST /api/organizations/{organizationId}/admin/assign
1) Purpose
- 为组织分配管理员（授予 ORG_ADMIN 角色并更新显示名）。
2) AuthN/AuthZ
- UNKNOWN。
3) Request
- 参数表：
| 参数 | 位置 | 类型 | 必填 | 说明 | Evidence |
| --- | --- | --- | --- | --- | --- |
| organizationId | path | Long | 是 | 组织 ID | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/OrganizationController.java | assignAdmin | L131-L139 |
| userId | body | Long | 是 | 用户 ID | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/organization/AssignAdminRequest.java | AssignAdminRequest | L1-L11 |
- 示例：UNKNOWN
4) Response
- ApiResponse<Void>，无 data。
5) Errors
| http_status | code | condition | source | evidence |
| --- | --- | --- | --- | --- |
| 400 | UNKNOWN | 组织不存在 | BusinessException | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/OrganizationApplicationService.java | requireOrganization | L254-L261 |
| 400 | UNKNOWN | 用户不存在 | BusinessException | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/OrganizationApplicationService.java | assignAdmin | L220-L225 |
| 400 | UNKNOWN | 管理员名称不合法 | ValidationUtils | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/OrganizationApplicationService.java | assignAdmin | L226-L227 |
| 500 | UNKNOWN | 未捕获异常 | GlobalExceptionHandler | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/GlobalExceptionHandler.java | handleException | L17-L19 |
6) Side Effects
- 新增角色授权（ORG_ADMIN），更新组织管理员显示名。
7) Idempotency
- ASSUMED: 否（重复请求可能新增多条授权记录）。
8) Evidence
- iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/OrganizationController.java | assignAdmin | 路由与 DTO 转换 | L131-L139
- iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/OrganizationApplicationService.java | assignAdmin | 角色授权与更新 | L220-L239

### GET /api/organizations/{organizationId}/members/internal
1) Purpose
- 获取组织内部成员列表。
2) AuthN/AuthZ
- UNKNOWN。
3) Request
- 参数表：
| 参数 | 位置 | 类型 | 必填 | 说明 | Evidence |
| --- | --- | --- | --- | --- | --- |
| organizationId | path | Long | 是 | 组织 ID | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/MemberController.java | listInternalMembers | L45-L56 |
| page | query | Integer | 否 | 页码，默认 1 | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/MemberApplicationService.java | normalizePage | L338-L343 |
| size | query | Integer | 否 | 页大小，默认 10 | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/MemberApplicationService.java | normalizeSize | L345-L349 |
- 示例：UNKNOWN
4) Response
- 字段表（ApiResponse.data = PageResponse<InternalMemberListItem>）：
| 字段 | 类型 | 说明 | Evidence |
| --- | --- | --- | --- |
| data.items[].username | String | 用户名 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/member/InternalMemberListItem.java | InternalMemberListItem | L1-L50 |
| data.items[].phone | String | 手机 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/member/InternalMemberListItem.java | InternalMemberListItem | L1-L50 |
| data.items[].email | String | 邮箱 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/member/InternalMemberListItem.java | InternalMemberListItem | L1-L50 |
| data.items[].roles | List<String> | 角色编码列表 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/member/InternalMemberListItem.java | InternalMemberListItem | L1-L50 |
| data.items[].status | String | 状态（中文） | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/MemberController.java | formatUserStatus | L195-L205 |
- 示例：UNKNOWN
5) Errors
| http_status | code | condition | source | evidence |
| --- | --- | --- | --- | --- |
| 400 | UNKNOWN | 组织不存在 | BusinessException | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/MemberApplicationService.java | requireOrganization | L311-L318 |
| 500 | UNKNOWN | 未捕获异常 | GlobalExceptionHandler | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/GlobalExceptionHandler.java | handleException | L17-L19 |
6) Side Effects
- 无（只读）。
7) Idempotency
- ASSUMED: 是（只读查询）。
8) Evidence
- iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/MemberController.java | listInternalMembers | 路由与响应 | L45-L56
- iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/MemberApplicationService.java | listInternalMembers | 查询成员与角色 | L72-L96

### POST /api/organizations/{organizationId}/members/internal
1) Purpose
- 创建内部成员并绑定组织与角色授权。
2) AuthN/AuthZ
- UNKNOWN。
3) Request
- 参数表（path + JSON Body）：
| 参数 | 位置 | 类型 | 必填 | 说明 | Evidence |
| --- | --- | --- | --- | --- | --- |
| organizationId | path | Long | 是 | 当前组织 ID | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/MemberController.java | createInternalMember | L59-L75 |
| username | body | String | 是 | 用户名 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/member/CreateInternalMemberRequest.java | CreateInternalMemberRequest | L5-L77 |
| name | body | String | 否 | 姓名 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/member/CreateInternalMemberRequest.java | CreateInternalMemberRequest | L5-L77 |
| phone | body | String | 否 | 手机 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/member/CreateInternalMemberRequest.java | CreateInternalMemberRequest | L5-L77 |
| email | body | String | 否 | 邮箱 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/member/CreateInternalMemberRequest.java | CreateInternalMemberRequest | L5-L77 |
| organizationIds | body | List<Long> | 是 | 关联组织列表 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/member/CreateInternalMemberRequest.java | CreateInternalMemberRequest | L5-L77 |
| roleSelections | body | List<RoleSelectionDto> | 是 | 角色选择 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/member/CreateInternalMemberRequest.java | CreateInternalMemberRequest | L5-L77 |
| status | body | String | 否 | 状态 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/member/CreateInternalMemberRequest.java | CreateInternalMemberRequest | L5-L77 |
| accountType | body | String | 否 | 账号类型 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/member/CreateInternalMemberRequest.java | CreateInternalMemberRequest | L5-L77 |
- 示例：UNKNOWN
4) Response
- 字段表（ApiResponse.data = CreateInternalMemberResponse）：
| 字段 | 类型 | 说明 | Evidence |
| --- | --- | --- | --- |
| data.username | String | 用户名 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/member/CreateInternalMemberResponse.java | CreateInternalMemberResponse | L1-L29 |
| data.phone | String | 手机 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/member/CreateInternalMemberResponse.java | CreateInternalMemberResponse | L1-L29 |
- 示例：UNKNOWN
5) Errors
| http_status | code | condition | source | evidence |
| --- | --- | --- | --- | --- |
| 400 | UNKNOWN | 组织不存在 | BusinessException | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/MemberApplicationService.java | requireOrganization | L311-L318 |
| 400 | UNKNOWN | 用户名为空/格式不正确 | ValidationUtils | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/MemberApplicationService.java | createInternalMember | L99-L104 |
| 400 | UNKNOWN | 姓名长度超限 | ValidationUtils | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/MemberApplicationService.java | createInternalMember | L104-L105 |
| 400 | UNKNOWN | 手机或邮箱至少填写一项/格式不正确 | ValidationUtils | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/MemberApplicationService.java | createInternalMember | L105-L107 |
| 400 | UNKNOWN | 关联组织不能为空 | ValidationUtils | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/MemberApplicationService.java | createInternalMember | L108-L109 |
| 400 | UNKNOWN | 关联角色不能为空/不完整/不匹配组织应用 | BusinessException | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/MemberApplicationService.java | createInternalMember | L109-L129 |
| 400 | UNKNOWN | 关联组织必须包含当前组织 | BusinessException | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/MemberApplicationService.java | createInternalMember | L111-L113 |
| 400 | UNKNOWN | 用户名已存在 | BusinessException | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/MemberApplicationService.java | createInternalMember | L115-L117 |
| 500 | UNKNOWN | 未捕获异常 | GlobalExceptionHandler | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/GlobalExceptionHandler.java | handleException | L17-L19 |
6) Side Effects
- 创建用户、写入组织成员关系、写入角色授权。
7) Idempotency
- ASSUMED: 否（创建会生成新用户 ID）。
8) Evidence
- iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/MemberController.java | createInternalMember | DTO -> Command | L59-L75
- iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/MemberApplicationService.java | createInternalMember | 用户/成员/授权创建 | L99-L173

### PUT /api/organizations/{organizationId}/members/internal/{userId}
1) Purpose
- 更新内部成员信息与账号类型/状态。
2) AuthN/AuthZ
- UNKNOWN。
3) Request
- 参数表（path + JSON Body）：
| 参数 | 位置 | 类型 | 必填 | 说明 | Evidence |
| --- | --- | --- | --- | --- | --- |
| organizationId | path | Long | 是 | 组织 ID | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/MemberController.java | updateInternalMember | L77-L91 |
| userId | path | Long | 是 | 用户 ID | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/MemberController.java | updateInternalMember | L77-L91 |
| name | body | String | 否 | 姓名 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/member/UpdateInternalMemberRequest.java | UpdateInternalMemberRequest | L1-L48 |
| phone | body | String | 否 | 手机 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/member/UpdateInternalMemberRequest.java | UpdateInternalMemberRequest | L1-L48 |
| email | body | String | 否 | 邮箱 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/member/UpdateInternalMemberRequest.java | UpdateInternalMemberRequest | L1-L48 |
| status | body | String | 否 | 状态 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/member/UpdateInternalMemberRequest.java | UpdateInternalMemberRequest | L1-L48 |
| accountType | body | String | 是 | 账号类型 | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/MemberApplicationService.java | updateInternalMember | L195-L197 |
- 示例：UNKNOWN
4) Response
- ApiResponse<Void>，无 data。
5) Errors
| http_status | code | condition | source | evidence |
| --- | --- | --- | --- | --- |
| 400 | UNKNOWN | 组织不存在 | BusinessException | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/MemberApplicationService.java | requireOrganization | L311-L318 |
| 400 | UNKNOWN | 成员不存在 | BusinessException | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/MemberApplicationService.java | updateInternalMember | L184-L189 |
| 400 | UNKNOWN | 姓名长度超限 | ValidationUtils | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/MemberApplicationService.java | updateInternalMember | L191-L194 |
| 400 | UNKNOWN | 手机或邮箱至少填写一项/格式不正确 | ValidationUtils | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/MemberApplicationService.java | updateInternalMember | L192-L194 |
| 400 | UNKNOWN | 账号类型不能为空 | BusinessException | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/MemberApplicationService.java | updateInternalMember | L195-L197 |
| 400 | UNKNOWN | 账号状态不能为空 | BusinessException | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/MemberApplicationService.java | updateInternalMember | L199-L201 |
| 500 | UNKNOWN | 未捕获异常 | GlobalExceptionHandler | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/GlobalExceptionHandler.java | handleException | L17-L19 |
6) Side Effects
- 更新用户信息与角色分类。
7) Idempotency
- ASSUMED: 是（重复提交相同数据结果一致）。
8) Evidence
- iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/MemberController.java | updateInternalMember | 路由与 DTO 转换 | L77-L91
- iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/MemberApplicationService.java | updateInternalMember | 更新用户与角色分类 | L181-L209

### POST /api/organizations/{organizationId}/members/internal/{userId}/disable
1) Purpose
- 停用内部成员账号。
2) AuthN/AuthZ
- UNKNOWN。
3) Request
- 参数表：
| 参数 | 位置 | 类型 | 必填 | 说明 | Evidence |
| --- | --- | --- | --- | --- | --- |
| organizationId | path | Long | 是 | 组织 ID | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/MemberController.java | disableInternalMember | L94-L99 |
| userId | path | Long | 是 | 用户 ID | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/MemberController.java | disableInternalMember | L94-L99 |
- 示例：UNKNOWN
4) Response
- ApiResponse<Void>，无 data。
5) Errors
| http_status | code | condition | source | evidence |
| --- | --- | --- | --- | --- |
| 400 | UNKNOWN | 组织不存在 | BusinessException | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/MemberApplicationService.java | requireOrganization | L311-L318 |
| 400 | UNKNOWN | 成员不存在 | BusinessException | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/MemberApplicationService.java | disableInternalMember | L212-L219 |
| 500 | UNKNOWN | 未捕获异常 | GlobalExceptionHandler | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/GlobalExceptionHandler.java | handleException | L17-L19 |
6) Side Effects
- 将用户状态更新为 DISABLED。
7) Idempotency
- ASSUMED: 是（多次停用结果一致）。
8) Evidence
- iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/MemberController.java | disableInternalMember | 路由 | L94-L99
- iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/MemberApplicationService.java | disableInternalMember | 更新状态 | L212-L222

### DELETE /api/organizations/{organizationId}/members/internal/{userId}
1) Purpose
- 删除内部成员（软删除用户与关联）。
2) AuthN/AuthZ
- UNKNOWN。
3) Request
- 参数表：
| 参数 | 位置 | 类型 | 必填 | 说明 | Evidence |
| --- | --- | --- | --- | --- | --- |
| organizationId | path | Long | 是 | 组织 ID | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/MemberController.java | deleteInternalMember | L102-L107 |
| userId | path | Long | 是 | 用户 ID | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/MemberController.java | deleteInternalMember | L102-L107 |
- 示例：UNKNOWN
4) Response
- ApiResponse<Void>，无 data。
5) Errors
| http_status | code | condition | source | evidence |
| --- | --- | --- | --- | --- |
| 400 | UNKNOWN | 组织不存在 | BusinessException | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/MemberApplicationService.java | requireOrganization | L311-L318 |
| 400 | UNKNOWN | 成员不存在 | BusinessException | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/MemberApplicationService.java | deleteInternalMember | L226-L229 |
| 500 | UNKNOWN | 未捕获异常 | GlobalExceptionHandler | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/GlobalExceptionHandler.java | handleException | L17-L19 |
6) Side Effects
- 软删除用户、成员关系与角色授权。
7) Idempotency
- ASSUMED: 是（多次删除结果一致）。
8) Evidence
- iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/MemberController.java | deleteInternalMember | 路由 | L102-L107
- iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/MemberApplicationService.java | deleteInternalMember | 删除逻辑 | L225-L234

### GET /api/organizations/{organizationId}/members/external
1) Purpose
- 获取组织外部成员列表。
2) AuthN/AuthZ
- UNKNOWN。
3) Request
- 参数表：
| 参数 | 位置 | 类型 | 必填 | 说明 | Evidence |
| --- | --- | --- | --- | --- | --- |
| organizationId | path | Long | 是 | 组织 ID | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/MemberController.java | listExternalMembers | L110-L121 |
| page | query | Integer | 否 | 页码，默认 1 | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/MemberApplicationService.java | normalizePage | L338-L343 |
| size | query | Integer | 否 | 页大小，默认 10 | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/MemberApplicationService.java | normalizeSize | L345-L349 |
- 示例：UNKNOWN
4) Response
- 字段表（ApiResponse.data = PageResponse<ExternalMemberListItem>）：
| 字段 | 类型 | 说明 | Evidence |
| --- | --- | --- | --- |
| data.items[].username | String | 用户名 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/member/ExternalMemberListItem.java | ExternalMemberListItem | L1-L39 |
| data.items[].sourceOrganizationName | String | 归属组织 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/member/ExternalMemberListItem.java | ExternalMemberListItem | L1-L39 |
| data.items[].phone | String | 手机 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/member/ExternalMemberListItem.java | ExternalMemberListItem | L1-L39 |
| data.items[].email | String | 邮箱 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/member/ExternalMemberListItem.java | ExternalMemberListItem | L1-L39 |
- 示例：UNKNOWN
5) Errors
| http_status | code | condition | source | evidence |
| --- | --- | --- | --- | --- |
| 400 | UNKNOWN | 组织不存在 | BusinessException | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/MemberApplicationService.java | requireOrganization | L311-L318 |
| 500 | UNKNOWN | 未捕获异常 | GlobalExceptionHandler | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/GlobalExceptionHandler.java | handleException | L17-L19 |
6) Side Effects
- 无（只读）。
7) Idempotency
- ASSUMED: 是（只读查询）。
8) Evidence
- iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/MemberController.java | listExternalMembers | 路由与响应 | L110-L121
- iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/MemberApplicationService.java | listExternalMembers | 查询外部成员 | L237-L262

### GET /api/organizations/{organizationId}/members/external/search
1) Purpose
- 搜索外部成员候选用户。
2) AuthN/AuthZ
- UNKNOWN。
3) Request
- 参数表：
| 参数 | 位置 | 类型 | 必填 | 说明 | Evidence |
| --- | --- | --- | --- | --- | --- |
| organizationId | path | Long | 是 | 组织 ID | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/MemberController.java | searchExternalMembers | L124-L130 |
| keyword | query | String | 否 | 关键字 | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/MemberController.java | searchExternalMembers | L124-L129 |
- 示例：UNKNOWN
4) Response
- 字段表（ApiResponse.data = List<UserSummary>）：
| 字段 | 类型 | 说明 | Evidence |
| --- | --- | --- | --- |
| data[].id | String | 用户 ID | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/user/UserSummary.java | UserSummary | L1-L46 |
| data[].username | String | 用户名 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/user/UserSummary.java | UserSummary | L1-L46 |
| data[].name | String | 姓名 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/user/UserSummary.java | UserSummary | L1-L46 |
| data[].phone | String | 手机 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/user/UserSummary.java | UserSummary | L1-L46 |
| data[].email | String | 邮箱 | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/user/UserSummary.java | UserSummary | L1-L46 |
- 示例：UNKNOWN
5) Errors
| http_status | code | condition | source | evidence |
| --- | --- | --- | --- | --- |
| 400 | UNKNOWN | 组织不存在 | BusinessException | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/MemberApplicationService.java | requireOrganization | L311-L318 |
| 500 | UNKNOWN | 未捕获异常 | GlobalExceptionHandler | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/GlobalExceptionHandler.java | handleException | L17-L19 |
6) Side Effects
- 无（只读）。
7) Idempotency
- ASSUMED: 是（只读查询）。
8) Evidence
- iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/MemberController.java | searchExternalMembers | 路由与响应 | L124-L130
- iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/MemberApplicationService.java | searchExternalCandidates | 搜索用户 | L265-L277

### POST /api/organizations/{organizationId}/members/external
1) Purpose
- 关联外部成员到当前组织。
2) AuthN/AuthZ
- UNKNOWN。
3) Request
- 参数表（path + JSON Body）：
| 参数 | 位置 | 类型 | 必填 | 说明 | Evidence |
| --- | --- | --- | --- | --- | --- |
| organizationId | path | Long | 是 | 组织 ID | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/MemberController.java | linkExternalMember | L133-L141 |
| userId | body | Long | 是 | 用户 ID | iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/member/LinkExternalMemberRequest.java | LinkExternalMemberRequest | L1-L11 |
- 示例：UNKNOWN
4) Response
- ApiResponse<Void>，无 data。
5) Errors
| http_status | code | condition | source | evidence |
| --- | --- | --- | --- | --- |
| 400 | UNKNOWN | 组织不存在 | BusinessException | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/MemberApplicationService.java | requireOrganization | L311-L318 |
| 400 | UNKNOWN | 用户不存在 | BusinessException | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/MemberApplicationService.java | linkExternalMember | L281-L287 |
| 400 | UNKNOWN | 不可添加本组织成员 | BusinessException | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/MemberApplicationService.java | linkExternalMember | L288-L290 |
| 400 | UNKNOWN | 该用户已关联外部成员 | BusinessException | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/MemberApplicationService.java | linkExternalMember | L291-L293 |
| 400 | UNKNOWN | 该用户已是外部成员，所属组织：{orgName} | BusinessException | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/MemberApplicationService.java | linkExternalMember | L294-L299 |
| 400 | UNKNOWN | 用户归属组织未知 | BusinessException | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/MemberApplicationService.java | linkExternalMember | L300-L302 |
| 500 | UNKNOWN | 未捕获异常 | GlobalExceptionHandler | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/GlobalExceptionHandler.java | handleException | L17-L19 |
6) Side Effects
- 写入外部成员关联记录。
7) Idempotency
- ASSUMED: 否（重复请求可能被拒绝或更新关联）。
8) Evidence
- iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/MemberController.java | linkExternalMember | 路由与 DTO 转换 | L133-L141
- iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/MemberApplicationService.java | linkExternalMember | 关联校验与写入 | L281-L304

### DELETE /api/organizations/{organizationId}/members/external/{userId}
1) Purpose
- 移除外部成员关联。
2) AuthN/AuthZ
- UNKNOWN。
3) Request
- 参数表：
| 参数 | 位置 | 类型 | 必填 | 说明 | Evidence |
| --- | --- | --- | --- | --- | --- |
| organizationId | path | Long | 是 | 组织 ID | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/MemberController.java | unlinkExternalMember | L144-L149 |
| userId | path | Long | 是 | 用户 ID | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/MemberController.java | unlinkExternalMember | L144-L149 |
- 示例：UNKNOWN
4) Response
- ApiResponse<Void>，无 data。
5) Errors
| http_status | code | condition | source | evidence |
| --- | --- | --- | --- | --- |
| 400 | UNKNOWN | 组织不存在 | BusinessException | iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/MemberApplicationService.java | requireOrganization | L311-L318 |
| 500 | UNKNOWN | 未捕获异常 | GlobalExceptionHandler | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/GlobalExceptionHandler.java | handleException | L17-L19 |
6) Side Effects
- 软删除外部成员关联。
7) Idempotency
- ASSUMED: 是（多次删除结果一致）。
8) Evidence
- iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/MemberController.java | unlinkExternalMember | 路由 | L144-L149
- iam-service/iam-application/src/main/java/com/tenghe/corebackend/application/service/MemberApplicationService.java | unlinkExternalMember | 删除关联 | L306-L309

### ANY /hello
1) Purpose
- Demo：返回问候字符串。
2) AuthN/AuthZ
- UNKNOWN。
3) Request
- 参数表：
| 参数 | 位置 | 类型 | 必填 | 说明 | Evidence |
| --- | --- | --- | --- | --- | --- |
| name | query | String | 否 | 默认 unknown user | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/demos/web/BasicController.java | hello | L31-L35 |
- 示例：UNKNOWN
4) Response
- `text/plain` 字符串（非 ApiResponse）。
5) Errors
| http_status | code | condition | source | evidence |
| --- | --- | --- | --- | --- |
| 500 | UNKNOWN | 未捕获异常 | GlobalExceptionHandler | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/GlobalExceptionHandler.java | handleException | L17-L19 |
6) Side Effects
- 无。
7) Idempotency
- ASSUMED: 是（只读）。
8) Evidence
- iam-service/iam-controller/src/main/java/com/tenghe/corebackend/demos/web/BasicController.java | hello | Demo 接口 | L31-L35

### ANY /user
1) Purpose
- Demo：返回固定用户对象。
2) AuthN/AuthZ
- UNKNOWN。
3) Request
- 无。
4) Response
- JSON 对象（非 ApiResponse），字段：name/age。
5) Errors
| http_status | code | condition | source | evidence |
| --- | --- | --- | --- | --- |
| 500 | UNKNOWN | 未捕获异常 | GlobalExceptionHandler | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/GlobalExceptionHandler.java | handleException | L17-L19 |
6) Side Effects
- 无。
7) Idempotency
- ASSUMED: 是（只读）。
8) Evidence
- iam-service/iam-controller/src/main/java/com/tenghe/corebackend/demos/web/BasicController.java | user | Demo 接口 | L38-L45
- iam-service/iam-controller/src/main/java/com/tenghe/corebackend/demos/web/User.java | User | Demo 返回对象 | L22-L40

### ANY /save_user
1) Purpose
- Demo：回显用户参数。
2) AuthN/AuthZ
- UNKNOWN。
3) Request
- 参数表（query/form）：
| 参数 | 位置 | 类型 | 必填 | 说明 | Evidence |
| --- | --- | --- | --- | --- | --- |
| name | query/form | String | 否 | 用户名 | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/demos/web/BasicController.java | saveUser | L48-L52 |
| age | query/form | Integer | 否 | 年龄 | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/demos/web/BasicController.java | saveUser | L48-L52 |
- 示例：UNKNOWN
4) Response
- `text/plain` 字符串（非 ApiResponse）。
5) Errors
| http_status | code | condition | source | evidence |
| --- | --- | --- | --- | --- |
| 500 | UNKNOWN | 未捕获异常 | GlobalExceptionHandler | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/GlobalExceptionHandler.java | handleException | L17-L19 |
6) Side Effects
- 无。
7) Idempotency
- ASSUMED: 是（只读）。
8) Evidence
- iam-service/iam-controller/src/main/java/com/tenghe/corebackend/demos/web/BasicController.java | saveUser | Demo 接口 | L48-L52

### ANY /html
1) Purpose
- Demo：返回 view 名称 `index.html`。
2) AuthN/AuthZ
- UNKNOWN。
3) Request
- 无。
4) Response
- View 名称（非 ApiResponse）。
5) Errors
| http_status | code | condition | source | evidence |
| --- | --- | --- | --- | --- |
| 500 | UNKNOWN | 未捕获异常 | GlobalExceptionHandler | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/GlobalExceptionHandler.java | handleException | L17-L19 |
6) Side Effects
- 无。
7) Idempotency
- ASSUMED: 是（只读）。
8) Evidence
- iam-service/iam-controller/src/main/java/com/tenghe/corebackend/demos/web/BasicController.java | html | Demo 接口 | L55-L58

### GET /user/{userId}/roles/{roleId}
1) Purpose
- Demo：回显路径变量。
2) AuthN/AuthZ
- UNKNOWN。
3) Request
- 参数表：
| 参数 | 位置 | 类型 | 必填 | 说明 | Evidence |
| --- | --- | --- | --- | --- | --- |
| userId | path | String | 是 | 用户 ID | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/demos/web/PathVariableController.java | getLogin | L31-L35 |
| roleId | path | String | 是 | 角色 ID | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/demos/web/PathVariableController.java | getLogin | L31-L35 |
- 示例：UNKNOWN
4) Response
- `text/plain` 字符串（非 ApiResponse）。
5) Errors
| http_status | code | condition | source | evidence |
| --- | --- | --- | --- | --- |
| 500 | UNKNOWN | 未捕获异常 | GlobalExceptionHandler | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/GlobalExceptionHandler.java | handleException | L17-L19 |
6) Side Effects
- 无。
7) Idempotency
- ASSUMED: 是（只读）。
8) Evidence
- iam-service/iam-controller/src/main/java/com/tenghe/corebackend/demos/web/PathVariableController.java | getLogin | Demo 接口 | L31-L35

### GET /javabeat/{regexp1:[a-z-]+}
1) Purpose
- Demo：回显正则路径变量。
2) AuthN/AuthZ
- UNKNOWN。
3) Request
- 参数表：
| 参数 | 位置 | 类型 | 必填 | 说明 | Evidence |
| --- | --- | --- | --- | --- | --- |
| regexp1 | path | String | 是 | 匹配 a-z- 的片段 | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/demos/web/PathVariableController.java | getRegExp | L38-L42 |
- 示例：UNKNOWN
4) Response
- `text/plain` 字符串（非 ApiResponse）。
5) Errors
| http_status | code | condition | source | evidence |
| --- | --- | --- | --- | --- |
| 500 | UNKNOWN | 未捕获异常 | GlobalExceptionHandler | iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/GlobalExceptionHandler.java | handleException | L17-L19 |
6) Side Effects
- 无。
7) Idempotency
- ASSUMED: 是（只读）。
8) Evidence
- iam-service/iam-controller/src/main/java/com/tenghe/corebackend/demos/web/PathVariableController.java | getRegExp | Demo 接口 | L38-L42

## Evidence
- iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/common/ApiResponse.java | ApiResponse | 通用响应结构 | L3-L24
- iam-service/iam-api/src/main/java/com/tenghe/corebackend/api/dto/common/PageResponse.java | PageResponse | 分页结构 | L5-L19
- iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/OrganizationController.java | OrganizationController | 组织相关路由 | L36-L139
- iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/MemberController.java | MemberController | 成员相关路由 | L36-L149
- iam-service/iam-controller/src/main/java/com/tenghe/corebackend/demos/web/BasicController.java | BasicController | Demo 路由 | L28-L66
- iam-service/iam-controller/src/main/java/com/tenghe/corebackend/demos/web/PathVariableController.java | PathVariableController | Demo 路由 | L28-L42
- iam-service/iam-controller/src/main/java/com/tenghe/corebackend/controller/web/GlobalExceptionHandler.java | GlobalExceptionHandler | 异常处理 | L10-L20

## UNKNOWN/ASSUMED
- AuthN/AuthZ UNKNOWN：未发现安全配置或过滤器证据。
- Demo 接口方法未显式限定 HTTP Method，按 Spring 默认处理，METHOD 取 ANY（ASSUMED）。
- 幂等性判断缺乏显式幂等控制逻辑，按语义标注（ASSUMED）。
- 响应示例 UNKNOWN：代码未给出明确示例。
