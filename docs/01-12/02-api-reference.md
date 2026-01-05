# 02-api-reference.md

## IAM 通用响应

| 字段      | 类型     | 说明                |
|---------|--------|-------------------|
| code    | string | 成功为 "0"，错误为 "500" |
| message | string | 错误信息或空字符串         |
| data    | object | 业务返回体（可能为 null）   |

## Device 通用响应

| 字段      | 类型      | 说明              |
|---------|---------|-----------------|
| success | boolean | 成功为 true        |
| message | string  | 错误信息或 "OK"      |
| data    | object  | 业务返回体（可能为 null） |

## IAM APIs

### GET /api/auth/captcha

1) Purpose

- 获取验证码 Key 与验证码内容。

2) AuthN/AuthZ

- UNKNOWN（未见鉴权过滤器/注解证据）。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | --- | --- | --- | --- | --- |
   | query | (无) | - | - | 无请求参数 |
   示例：UNKNOWN
4) Response
   Data 字段：
   | 字段 | 类型 | 说明 |
   | captchaKey | string | 验证码 key |
   | captchaImage | string | 由服务生成的验证码内容 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 500 | 500 | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/GlobalExceptionHandler.java#L23 |
6) Side Effects

- 生成验证码并存入内存存储。

7) Idempotency

- NOT IDEMPOTENT（每次生成新的 captchaKey）。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/AuthController.java |
getCaptcha | GET /api/auth/captcha | L33-L38
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-api/src/main/java/com/tenghe/corebackend/iam/api/dto/auth/CaptchaResponse.java |
CaptchaResponse | captchaKey/captchaImage 字段 | L6-L15
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/AuthenticationApplicationServiceImpl.java |
generateCaptcha | 调用 captchaService 生成验证码 | L101-L104
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-infrastructure/src/main/java/com/tenghe/corebackend/iam/infrastructure/inmemory/InMemoryCaptchaService.java |
generateCaptcha | 内存存储验证码 | L14-L19

### POST /api/auth/login

1) Purpose

- 登录并生成访问令牌。

2) AuthN/AuthZ

- UNKNOWN（未见鉴权过滤器/注解证据）。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | --- | --- | --- | --- | --- |
   | body | identifier | string | 是 | 登录标识（用户名/邮箱/手机号） |
   | body | password | string | 是 | 密码 |
   | body | captcha | string | 是 | 验证码 |
   | body | captchaKey | string | 是 | 验证码 key |
   示例：UNKNOWN
4) Response
   Data 字段：
   | 字段 | 类型 | 说明 |
   | userId | string | 用户 ID |
   | username | string | 用户名 |
   | token | string | 访问令牌 |
   | requirePasswordReset | boolean | 是否需要重置密码 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | 500 | 业务校验/认证失败 | AuthenticationApplicationServiceImpl |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/AuthenticationApplicationServiceImpl.java#L41
   | 500 | 500 | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/GlobalExceptionHandler.java#L23 |
6) Side Effects

- 校验验证码、生成 token、更新用户登录失败次数与锁定状态。

7) Idempotency

- NOT IDEMPOTENT（每次成功登录生成新 token）。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/AuthController.java |
login | POST /api/auth/login | L43-L57
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-api/src/main/java/com/tenghe/corebackend/iam/api/dto/auth/LoginRequest.java |
LoginRequest | 登录入参字段 | L6-L12
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-api/src/main/java/com/tenghe/corebackend/iam/api/dto/auth/LoginResponse.java |
LoginResponse | 登录响应字段 | L6-L13
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/AuthenticationApplicationServiceImpl.java |
login | 生成 token/更新登录状态 | L41-L79
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-infrastructure/src/main/java/com/tenghe/corebackend/iam/infrastructure/inmemory/InMemoryTokenService.java |
generateToken | 生成并存储 token | L15-L24

### POST /api/auth/logout

1) Purpose

- 注销当前令牌。

2) AuthN/AuthZ

- UNKNOWN（仅从 Header 读取 Authorization）。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | header | Authorization | string | 否 | Bearer token |
   示例：UNKNOWN
4) Response
   Data 字段：
   | 字段 | 类型 | 说明 |
   | (null) | - | 无业务返回 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 500 | 500 | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/GlobalExceptionHandler.java#L23 |
6) Side Effects

- 失效化 token。

7) Idempotency

- ASSUMED IDEMPOTENT（重复调用对已失效 token 无额外影响）。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/AuthController.java |
logout | POST /api/auth/logout | L63-L69
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/AuthenticationApplicationServiceImpl.java |
logout | 调用 tokenService.invalidateToken | L83-L85
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-infrastructure/src/main/java/com/tenghe/corebackend/iam/infrastructure/inmemory/InMemoryTokenService.java |
invalidateToken | 删除 token 映射 | L34-L43

### GET /api/auth/validate

1) Purpose

- 校验当前令牌有效性。

2) AuthN/AuthZ

- UNKNOWN（仅从 Header 读取 Authorization）。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | header | Authorization | string | 否 | Bearer token |
   示例：UNKNOWN
4) Response
   Data 字段：
   | 字段 | 类型 | 说明 |
   | (root) | boolean | 是否有效 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 500 | 500 | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/GlobalExceptionHandler.java#L23 |
6) Side Effects

- 若用户无效/禁用，失效化 token。

7) Idempotency

- ASSUMED IDEMPOTENT（同一 token 重复校验结果一致，除非状态变更）。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/AuthController.java |
validateSession | GET /api/auth/validate | L75-L81
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/AuthenticationApplicationServiceImpl.java |
validateSession | 失效化无效 token | L87-L98

### POST /api/password/send-code

1) Purpose

- 发送邮箱验证码。

2) AuthN/AuthZ

- UNKNOWN（通过 Header 读取 X-User-Id）。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | header | X-User-Id | long | 否 | 用户 ID |
   | body | email | string | 否 | 目标邮箱 |
   示例：UNKNOWN
4) Response
   Data 字段：
   | 字段 | 类型 | 说明 |
   | (null) | - | 无业务返回 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | 500 | 邮箱校验/发送频率限制 | PasswordResetApplicationServiceImpl |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/PasswordResetApplicationServiceImpl.java#L45
   | 500 | 500 | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/GlobalExceptionHandler.java#L23 |
6) Side Effects

- 生成邮箱验证码并发送邮件。

7) Idempotency

- NOT IDEMPOTENT（受发送频率限制，可能返回不同验证码）。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/PasswordController.java |
sendEmailCode | POST /api/password/send-code | L30-L38
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-api/src/main/java/com/tenghe/corebackend/iam/api/dto/auth/SendEmailCodeRequest.java |
SendEmailCodeRequest | email 字段 | L6-L10
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/PasswordResetApplicationServiceImpl.java |
sendEmailCode | 生成并发送验证码 | L45-L63
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-infrastructure/src/main/java/com/tenghe/corebackend/iam/infrastructure/inmemory/InMemoryEmailCodeService.java |
generateCode/canSendCode | 生成验证码与频率控制 | L21-L55
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-infrastructure/src/main/java/com/tenghe/corebackend/iam/infrastructure/inmemory/InMemoryEmailService.java |
sendVerificationCode | 邮件发送实现 | L9-L12

### POST /api/password/reset

1) Purpose

- 重置密码。

2) AuthN/AuthZ

- UNKNOWN（通过 Header 读取 X-User-Id）。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | header | X-User-Id | long | 否 | 用户 ID |
   | body | oldPassword | string | 是 | 原密码 |
   | body | newPassword | string | 是 | 新密码 |
   | body | emailCode | string | 是 | 邮箱验证码 |
   示例：UNKNOWN
4) Response
   Data 字段：
   | 字段 | 类型 | 说明 |
   | (null) | - | 无业务返回 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | 500 | 原密码/验证码/密码强度校验失败 | PasswordResetApplicationServiceImpl |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/PasswordResetApplicationServiceImpl.java#L66
   | 500 | 500 | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/GlobalExceptionHandler.java#L23 |
6) Side Effects

- 更新用户密码并失效该用户所有 token。

7) Idempotency

- NOT IDEMPOTENT（密码与验证码一次性使用）。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/PasswordController.java |
resetPassword | POST /api/password/reset | L44-L55
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-api/src/main/java/com/tenghe/corebackend/iam/api/dto/auth/ResetPasswordRequest.java |
ResetPasswordRequest | 重置密码字段 | L6-L12
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/PasswordResetApplicationServiceImpl.java |
resetPassword | 更新密码与失效 token | L66-L88

### GET /api/users

1) Purpose

- 查询用户列表（分页/筛选）。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | query | keyword | string | 否 | 关键字 |
   | query | status | string | 否 | 用户状态 |
   | query | organizationIds | list<long> | 否 | 组织过滤 |
   | query | roleCodes | list<string> | 否 | 角色过滤 |
   | query | page | int | 否 | 页码 |
   | query | size | int | 否 | 页大小 |
   示例：UNKNOWN
4) Response
   Data 字段（PageResponse）：
   | 字段 | 类型 | 说明 |
   | pageSize | int | 页大小 |
   | page | int | 页码 |
   | total | long | 总数 |
   | data | list<UserListItem> | 列表 |
   UserListItem 字段：
   | 字段 | 类型 | 说明 |
   | id | string | 用户 ID |
   | username | string | 用户名 |
   | name | string | 姓名 |
   | phone | string | 手机号 |
   | email | string | 邮箱 |
   | status | string | 状态 |
   | organizationNames | list<string> | 组织名列表 |
   | roleNames | list<string> | 角色名列表 |
   | createdDate | string | 创建日期 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 500 | 500 | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/GlobalExceptionHandler.java#L23 |
6) Side Effects

- 仅查询（无写入）。

7) Idempotency

- ASSUMED IDEMPOTENT（只读查询）。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/UserController.java |
listUsers | GET /api/users | L52-L72
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-api/src/main/java/com/tenghe/corebackend/iam/api/dto/common/PageResponse.java |
PageResponse | 分页字段 | L5-L19
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-api/src/main/java/com/tenghe/corebackend/iam/api/dto/user/UserListItem.java |
UserListItem | 列表字段 | L9-L18
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/UserApplicationServiceImpl.java |
listUsers | 查询与分页 | L80-L129

### POST /api/users

1) Purpose

- 创建用户。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | body | username | string | 是 | 用户名 |
   | body | name | string | 否 | 姓名 |
   | body | phone | string | 否 | 手机 |
   | body | email | string | 是 | 邮箱 |
   | body | organizationIds | list<long> | 是 | 关联组织 |
   | body | roleSelections | list<RoleSelectionDto> | 是 | 关联角色 |
   示例：UNKNOWN
4) Response
   Data 字段：
   | 字段 | 类型 | 说明 |
   | (root) | string | 新用户 ID |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | 500 | 校验失败/用户名或邮箱冲突 | UserApplicationServiceImpl |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/UserApplicationServiceImpl.java#L132
   | 500 | 500 | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/GlobalExceptionHandler.java#L23 |
6) Side Effects

- 保存用户、组织成员关系、角色授权，并发送初始密码邮件。

7) Idempotency

- NOT IDEMPOTENT（每次生成新用户 ID 与初始密码）。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/UserController.java |
createUser | POST /api/users | L78-L88
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-api/src/main/java/com/tenghe/corebackend/iam/api/dto/user/CreateUserRequest.java |
CreateUserRequest | 创建用户字段 | L8-L16
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/UserApplicationServiceImpl.java |
createUser | 保存用户/授权/发送邮件 | L132-L213
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-infrastructure/src/main/java/com/tenghe/corebackend/iam/infrastructure/inmemory/InMemoryEmailService.java |
sendInitialPassword | 发送初始密码 | L9-L13

### GET /api/users/{userId}

1) Purpose

- 获取用户详情。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | path | userId | long | 是 | 用户 ID |
   示例：UNKNOWN
4) Response
   Data 字段（UserDetailResponse）：
   | 字段 | 类型 | 说明 |
   | id | string | 用户 ID |
   | username | string | 用户名 |
   | name | string | 姓名 |
   | phone | string | 手机 |
   | email | string | 邮箱 |
   | status | string | 状态 |
   | createdDate | string | 创建日期 |
   | organizationIds | list<long> | 组织列表 |
   | roleSelections | list<RoleSelectionDto> | 角色选择 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | 500 | 用户不存在 | UserApplicationServiceImpl |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/UserApplicationServiceImpl.java#L355
   | 500 | 500 | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/GlobalExceptionHandler.java#L23 |
6) Side Effects

- 仅查询。

7) Idempotency

- ASSUMED IDEMPOTENT。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/UserController.java |
getUserDetail | GET /api/users/{userId} | L94-L107
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-api/src/main/java/com/tenghe/corebackend/iam/api/dto/user/UserDetailResponse.java |
UserDetailResponse | 用户详情字段 | L8-L19
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/UserApplicationServiceImpl.java |
getUserDetail | 组装详情 | L216-L244

### PUT /api/users/{userId}

1) Purpose

- 更新用户信息。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | path | userId | long | 是 | 用户 ID |
   | body | name | string | 否 | 姓名 |
   | body | phone | string | 否 | 手机 |
   | body | email | string | 是 | 邮箱 |
   | body | organizationIds | list<long> | 否 | 关联组织 |
   | body | roleSelections | list<RoleSelectionDto> | 否 | 角色选择 |
   示例：UNKNOWN
4) Response
   Data 字段：
   | 字段 | 类型 | 说明 |
   | (null) | - | 无业务返回 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | 500 | 校验失败/邮箱冲突/角色不匹配 | UserApplicationServiceImpl |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/UserApplicationServiceImpl.java#L247
   | 500 | 500 | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/GlobalExceptionHandler.java#L23 |
6) Side Effects

- 更新用户、组织成员关系、角色授权。

7) Idempotency

- ASSUMED IDEMPOTENT（相同输入下更新同一状态）。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/UserController.java |
updateUser | PUT /api/users/{userId} | L113-L125
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-api/src/main/java/com/tenghe/corebackend/iam/api/dto/user/UpdateUserRequest.java |
UpdateUserRequest | 更新字段 | L8-L15
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/UserApplicationServiceImpl.java |
updateUser | 更新组织/授权 | L247-L327

### PUT /api/users/{userId}/status

1) Purpose

- 切换用户状态。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | path | userId | long | 是 | 用户 ID |
   | body | status | string | 是 | 状态值 |
   示例：UNKNOWN
4) Response
   Data 字段：
   | 字段 | 类型 | 说明 |
   | (null) | - | 无业务返回 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | 500 | 状态值无效/用户不存在 | UserApplicationServiceImpl |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/UserApplicationServiceImpl.java#L330
   | 500 | 500 | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/GlobalExceptionHandler.java#L23 |
6) Side Effects

- 更新用户状态。

7) Idempotency

- ASSUMED IDEMPOTENT（同一状态重复设置）。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/UserController.java |
toggleUserStatus | PUT /api/users/{userId}/status | L131-L136
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-api/src/main/java/com/tenghe/corebackend/iam/api/dto/user/ToggleUserStatusRequest.java |
ToggleUserStatusRequest | status 字段 | L6-L10
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/UserApplicationServiceImpl.java |
toggleUserStatus | 更新状态 | L330-L339

### DELETE /api/users/{userId}

1) Purpose

- 删除用户（软删除）。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | path | userId | long | 是 | 用户 ID |
   示例：UNKNOWN
4) Response
   Data 字段：
   | 字段 | 类型 | 说明 |
   | (null) | - | 无业务返回 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | 500 | 用户不存在 | UserApplicationServiceImpl |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/UserApplicationServiceImpl.java#L341
   | 500 | 500 | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/GlobalExceptionHandler.java#L23 |
6) Side Effects

- 软删除用户并清理组织关系与角色授权。

7) Idempotency

- NOT IDEMPOTENT（重复删除可能触发“用户不存在”）。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/UserController.java |
deleteUser | DELETE /api/users/{userId} | L142-L145
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/UserApplicationServiceImpl.java |
deleteUser | 软删除与清理关系 | L341-L352

### GET /api/organizations

1) Purpose

- 查询组织列表（分页、模糊搜索）。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | query | keyword | string | 否 | 名称或 ID 模糊匹配 |
   | query | page | int | 否 | 页码 |
   | query | size | int | 否 | 页大小 |
   示例：UNKNOWN
4) Response
   Data 字段（PageResponse）：
   | 字段 | 类型 | 说明 |
   | pageSize | int | 页大小 |
   | page | int | 页码 |
   | total | long | 总数 |
   | data | list<OrganizationListItem> | 列表 |
   OrganizationListItem 字段：
   | 字段 | 类型 | 说明 |
   | id | string | 组织 ID |
   | name | string | 组织名称 |
   | internalMemberCount | long | 内部成员数 |
   | externalMemberCount | long | 外部成员数 |
   | primaryAdminDisplay | string | 管理员展示名 |
   | status | string | 状态 |
   | createdDate | string | 创建日期 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 500 | 500 | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/GlobalExceptionHandler.java#L23 |
6) Side Effects

- 仅查询。

7) Idempotency

- ASSUMED IDEMPOTENT。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/OrganizationController.java |
listOrganizations | GET /api/organizations | L42-L50
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-api/src/main/java/com/tenghe/corebackend/iam/api/dto/organization/OrganizationListItem.java |
OrganizationListItem | 列表字段 | L3-L66
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/OrganizationApplicationServiceImpl.java |
listOrganizations | 列表/排序/分页 | L77-L95

### POST /api/organizations

1) Purpose

- 创建组织。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | body | name | string | 是 | 组织名称 |
   | body | code | string | 是 | 组织编码 |
   | body | description | string | 否 | 描述 |
   | body | appIds | list<long> | 否 | 可用应用 |
   示例：UNKNOWN
4) Response
   Data 字段：
   | 字段 | 类型 | 说明 |
   | id | string | 新组织 ID |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | 500 | 名称/编码重复或校验失败 | OrganizationApplicationServiceImpl |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/OrganizationApplicationServiceImpl.java#L98
   | 500 | 500 | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/GlobalExceptionHandler.java#L23 |
6) Side Effects

- 保存组织并写入组织-应用关系。

7) Idempotency

- NOT IDEMPOTENT（生成新组织 ID）。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/OrganizationController.java |
createOrganization | POST /api/organizations | L59-L68
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-api/src/main/java/com/tenghe/corebackend/iam/api/dto/organization/CreateOrganizationRequest.java |
CreateOrganizationRequest | 创建字段 | L5-L40
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-api/src/main/java/com/tenghe/corebackend/iam/api/dto/organization/CreateOrganizationResponse.java |
CreateOrganizationResponse | 返回 ID | L3-L18
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/OrganizationApplicationServiceImpl.java |
createOrganization | 保存组织/应用映射 | L98-L128

### GET /api/organizations/{organizationId}

1) Purpose

- 获取组织详情。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | path | organizationId | long | 是 | 组织 ID |
   示例：UNKNOWN
4) Response
   Data 字段（OrganizationDetailResponse）：
   | 字段 | 类型 | 说明 |
   | id | string | 组织 ID |
   | name | string | 组织名称 |
   | createdDate | string | 创建日期 |
   | description | string | 描述 |
   | appIds | list<long> | 应用 ID 列表 |
   | status | string | 状态 |
   | contactName | string | 联系人姓名 |
   | contactPhone | string | 联系电话 |
   | contactEmail | string | 联系邮箱 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | 500 | 组织不存在 | OrganizationApplicationServiceImpl |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/OrganizationApplicationServiceImpl.java#L277
   | 500 | 500 | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/GlobalExceptionHandler.java#L23 |
6) Side Effects

- 仅查询。

7) Idempotency

- ASSUMED IDEMPOTENT。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/OrganizationController.java |
getOrganizationDetail | GET /api/organizations/{organizationId} | L53-L65
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-api/src/main/java/com/tenghe/corebackend/iam/api/dto/organization/OrganizationDetailResponse.java |
OrganizationDetailResponse | 详情字段 | L5-L86
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/OrganizationApplicationServiceImpl.java |
getOrganizationDetail | 组装详情 | L131-L147

### PUT /api/organizations/{organizationId}

1) Purpose

- 更新组织信息。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | path | organizationId | long | 是 | 组织 ID |
   | body | name | string | 是 | 组织名称 |
   | body | description | string | 否 | 描述 |
   | body | appIds | list<long> | 否 | 应用列表 |
   | body | status | string | 是 | 状态 |
   | body | contactName | string | 否 | 联系人姓名 |
   | body | contactPhone | string | 否 | 联系电话 |
   | body | contactEmail | string | 否 | 联系邮箱 |
   示例：UNKNOWN
4) Response
   Data 字段：
   | 字段 | 类型 | 说明 |
   | (null) | - | 无业务返回 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | 500 | 校验失败/名称冲突/状态为空 | OrganizationApplicationServiceImpl |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/OrganizationApplicationServiceImpl.java#L151
   | 500 | 500 | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/GlobalExceptionHandler.java#L23 |
6) Side Effects

- 更新组织并按需清理组织应用对应的角色授权。

7) Idempotency

- ASSUMED IDEMPOTENT（相同输入下结果一致）。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/OrganizationController.java |
updateOrganization | PUT /api/organizations/{organizationId} | L88-L101
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-api/src/main/java/com/tenghe/corebackend/iam/api/dto/organization/UpdateOrganizationRequest.java |
UpdateOrganizationRequest | 更新字段 | L5-L68
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/OrganizationApplicationServiceImpl.java |
updateOrganization | 更新与清理授权 | L151-L192

### DELETE /api/organizations/{organizationId}

1) Purpose

- 删除组织（软删除）。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | path | organizationId | long | 是 | 组织 ID |
   示例：UNKNOWN
4) Response
   Data 字段：
   | 字段 | 类型 | 说明 |
   | (null) | - | 无业务返回 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | 500 | 组织不存在 | OrganizationApplicationServiceImpl |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/OrganizationApplicationServiceImpl.java#L196
   | 500 | 500 | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/GlobalExceptionHandler.java#L23 |
6) Side Effects

- 软删除组织、清理组织应用、成员关系、外部成员关联与角色授权。

7) Idempotency

- NOT IDEMPOTENT（重复删除可能返回“组织不存在”）。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/OrganizationController.java |
deleteOrganization | DELETE /api/organizations/{organizationId} | L106-L110
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/OrganizationApplicationServiceImpl.java |
deleteOrganization | 软删除与清理关系 | L196-L216

### GET /api/organizations/{organizationId}/delete-info

1) Purpose

- 获取删除组织提示信息。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | path | organizationId | long | 是 | 组织 ID |
   示例：UNKNOWN
4) Response
   Data 字段：
   | 字段 | 类型 | 说明 |
   | name | string | 组织名称 |
   | userCount | long | 组织成员数 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | 500 | 组织不存在 | OrganizationApplicationServiceImpl |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/OrganizationApplicationServiceImpl.java#L220
   | 500 | 500 | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/GlobalExceptionHandler.java#L23 |
6) Side Effects

- 仅查询。

7) Idempotency

- ASSUMED IDEMPOTENT。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/OrganizationController.java |
getDeleteInfo | GET /api/organizations/{organizationId}/delete-info | L114-L121
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-api/src/main/java/com/tenghe/corebackend/iam/api/dto/organization/DeleteOrganizationInfoResponse.java |
DeleteOrganizationInfoResponse | name/userCount 字段 | L3-L19
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/OrganizationApplicationServiceImpl.java |
getDeleteInfo | 读取组织与成员数 | L220-L225

### GET /api/organizations/{organizationId}/admin/search

1) Purpose

- 查询组织管理员候选人。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | path | organizationId | long | 是 | 组织 ID |
   | query | keyword | string | 否 | 关键字 |
   示例：UNKNOWN
4) Response
   Data 字段：
   | 字段 | 类型 | 说明 |
   | (root) | list<UserSummary> | 候选用户列表 |
   UserSummary 字段：
   | 字段 | 类型 | 说明 |
   | id | string | 用户 ID |
   | username | string | 用户名 |
   | name | string | 姓名 |
   | phone | string | 手机 |
   | email | string | 邮箱 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | 500 | 组织不存在 | OrganizationApplicationServiceImpl |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/OrganizationApplicationServiceImpl.java#L229
   | 500 | 500 | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/GlobalExceptionHandler.java#L23 |
6) Side Effects

- 仅查询。

7) Idempotency

- ASSUMED IDEMPOTENT。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/OrganizationController.java |
searchAdminCandidates | GET /api/organizations/{organizationId}/admin/search | L128-L135
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-api/src/main/java/com/tenghe/corebackend/iam/api/dto/user/UserSummary.java |
UserSummary | 字段定义 | L3-L48
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/OrganizationApplicationServiceImpl.java |
searchAdminCandidates | 搜索用户 | L228-L235

### POST /api/organizations/{organizationId}/admin/assign

1) Purpose

- 指派组织管理员。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | path | organizationId | long | 是 | 组织 ID |
   | body | userId | long | 是 | 用户 ID |
   示例：UNKNOWN
4) Response
   Data 字段：
   | 字段 | 类型 | 说明 |
   | (null) | - | 无业务返回 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | 500 | 用户不存在/角色不存在/名称不合法 | OrganizationApplicationServiceImpl |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/OrganizationApplicationServiceImpl.java#L238
   | 500 | 500 | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/GlobalExceptionHandler.java#L23 |
6) Side Effects

- 授权“组织管理员”角色并更新组织管理员展示名。

7) Idempotency

- NOT IDEMPOTENT（重复授予将生成新的授权记录）。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/OrganizationController.java |
assignAdmin | POST /api/organizations/{organizationId}/admin/assign | L140-L148
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-api/src/main/java/com/tenghe/corebackend/iam/api/dto/organization/AssignAdminRequest.java |
AssignAdminRequest | userId 字段 | L3-L10
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/OrganizationApplicationServiceImpl.java |
assignAdmin | 保存 RoleGrant 与更新组织 | L237-L263

### GET /api/organizations/{organizationId}/members/internal

1) Purpose

- 查询组织内部成员列表。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | path | organizationId | long | 是 | 组织 ID |
   | query | page | int | 否 | 页码 |
   | query | size | int | 否 | 页大小 |
   示例：UNKNOWN
4) Response
   Data 字段（PageResponse）：
   | 字段 | 类型 | 说明 |
   | pageSize | int | 页大小 |
   | page | int | 页码 |
   | total | long | 总数 |
   | data | list<InternalMemberListItem> | 列表 |
   InternalMemberListItem 字段：
   | 字段 | 类型 | 说明 |
   | username | string | 用户名 |
   | phone | string | 手机 |
   | email | string | 邮箱 |
   | roles | list<string> | 角色编码列表 |
   | status | string | 状态 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | 500 | 组织不存在 | MemberApplicationServiceImpl |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/MemberApplicationServiceImpl.java#L80
   | 500 | 500 | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/GlobalExceptionHandler.java#L23 |
6) Side Effects

- 仅查询。

7) Idempotency

- ASSUMED IDEMPOTENT。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/MemberController.java |
listInternalMembers | GET /api/organizations/{organizationId}/members/internal | L50-L61
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-api/src/main/java/com/tenghe/corebackend/iam/api/dto/member/InternalMemberListItem.java |
InternalMemberListItem | 列表字段 | L5-L50
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/MemberApplicationServiceImpl.java |
listInternalMembers | 读取成员与角色 | L80-L106

### POST /api/organizations/{organizationId}/members/internal

1) Purpose

- 创建组织内部成员。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | path | organizationId | long | 是 | 组织 ID |
   | body | username | string | 是 | 用户名 |
   | body | name | string | 否 | 姓名 |
   | body | phone | string | 否 | 手机 |
   | body | email | string | 是 | 邮箱 |
   | body | organizationIds | list<long> | 是 | 关联组织 |
   | body | roleSelections | list<RoleSelectionDto> | 是 | 关联角色 |
   | body | status | string | 否 | 状态 |
   | body | accountType | string | 否 | 账号类型 |
   示例：UNKNOWN
4) Response
   Data 字段：
   | 字段 | 类型 | 说明 |
   | username | string | 用户名 |
   | phone | string | 手机 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | 500 | 校验失败/用户名或邮箱冲突/角色不匹配 | MemberApplicationServiceImpl |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/MemberApplicationServiceImpl.java#L108
   | 500 | 500 | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/GlobalExceptionHandler.java#L23 |
6) Side Effects

- 保存用户、成员关系与角色授权。

7) Idempotency

- NOT IDEMPOTENT（生成新用户 ID）。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/MemberController.java |
createInternalMember | POST /api/organizations/{organizationId}/members/internal | L68-L79
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-api/src/main/java/com/tenghe/corebackend/iam/api/dto/member/CreateInternalMemberRequest.java |
CreateInternalMemberRequest | 创建字段 | L5-L77
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-api/src/main/java/com/tenghe/corebackend/iam/api/dto/member/CreateInternalMemberResponse.java |
CreateInternalMemberResponse | 返回字段 | L3-L29
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/MemberApplicationServiceImpl.java |
createInternalMember | 保存用户/授权 | L108-L203

### PUT /api/organizations/{organizationId}/members/internal/{userId}

1) Purpose

- 更新组织内部成员。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | path | organizationId | long | 是 | 组织 ID |
   | path | userId | long | 是 | 用户 ID |
   | body | name | string | 否 | 姓名 |
   | body | phone | string | 否 | 手机 |
   | body | email | string | 是 | 邮箱 |
   | body | status | string | 否 | 状态 |
   | body | accountType | string | 是 | 账号类型 |
   示例：UNKNOWN
4) Response
   Data 字段：
   | 字段 | 类型 | 说明 |
   | (null) | - | 无业务返回 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | 500 | 成员不存在/校验失败 | MemberApplicationServiceImpl |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/MemberApplicationServiceImpl.java#L206
   | 500 | 500 | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/GlobalExceptionHandler.java#L23 |
6) Side Effects

- 更新用户信息与角色分类。

7) Idempotency

- ASSUMED IDEMPOTENT。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/MemberController.java |
updateInternalMember | PUT /api/organizations/{organizationId}/members/internal/{userId} | L86-L97
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-api/src/main/java/com/tenghe/corebackend/iam/api/dto/member/UpdateInternalMemberRequest.java |
UpdateInternalMemberRequest | 更新字段 | L3-L48
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/MemberApplicationServiceImpl.java |
updateInternalMember | 更新成员 | L206-L239

### POST /api/organizations/{organizationId}/members/internal/{userId}/disable

1) Purpose

- 停用组织内部成员。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | path | organizationId | long | 是 | 组织 ID |
   | path | userId | long | 是 | 用户 ID |
   示例：UNKNOWN
4) Response
   Data 字段：
   | 字段 | 类型 | 说明 |
   | (null) | - | 无业务返回 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | 500 | 成员不存在 | MemberApplicationServiceImpl |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/MemberApplicationServiceImpl.java#L242
   | 500 | 500 | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/GlobalExceptionHandler.java#L23 |
6) Side Effects

- 更新用户状态为 DISABLED。

7) Idempotency

- ASSUMED IDEMPOTENT。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/MemberController.java |
disableInternalMember | POST /api/organizations/{organizationId}/members/internal/{userId}/disable | L101-L106
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/MemberApplicationServiceImpl.java |
disableInternalMember | 更新状态 | L242-L252

### DELETE /api/organizations/{organizationId}/members/internal/{userId}

1) Purpose

- 删除组织内部成员（软删除）。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | path | organizationId | long | 是 | 组织 ID |
   | path | userId | long | 是 | 用户 ID |
   示例：UNKNOWN
4) Response
   Data 字段：
   | 字段 | 类型 | 说明 |
   | (null) | - | 无业务返回 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | 500 | 成员不存在 | MemberApplicationServiceImpl |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/MemberApplicationServiceImpl.java#L255
   | 500 | 500 | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/GlobalExceptionHandler.java#L23 |
6) Side Effects

- 软删除用户或成员关系并清理授权。

7) Idempotency

- NOT IDEMPOTENT。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/MemberController.java |
deleteInternalMember | DELETE /api/organizations/{organizationId}/members/internal/{userId} | L110-L115
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/MemberApplicationServiceImpl.java |
deleteInternalMember | 删除成员与授权 | L255-L269

### GET /api/organizations/{organizationId}/members/external

1) Purpose

- 查询外部成员列表。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | path | organizationId | long | 是 | 组织 ID |
   | query | page | int | 否 | 页码 |
   | query | size | int | 否 | 页大小 |
   示例：UNKNOWN
4) Response
   Data 字段（PageResponse）：
   | 字段 | 类型 | 说明 |
   | pageSize | int | 页大小 |
   | page | int | 页码 |
   | total | long | 总数 |
   | data | list<ExternalMemberListItem> | 列表 |
   ExternalMemberListItem 字段：
   | 字段 | 类型 | 说明 |
   | username | string | 用户名 |
   | sourceOrganizationName | string | 归属组织 |
   | phone | string | 手机 |
   | email | string | 邮箱 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | 500 | 组织不存在 | MemberApplicationServiceImpl |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/MemberApplicationServiceImpl.java#L272
   | 500 | 500 | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/GlobalExceptionHandler.java#L23 |
6) Side Effects

- 仅查询。

7) Idempotency

- ASSUMED IDEMPOTENT。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/MemberController.java |
listExternalMembers | GET /api/organizations/{organizationId}/members/external | L118-L129
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-api/src/main/java/com/tenghe/corebackend/iam/api/dto/member/ExternalMemberListItem.java |
ExternalMemberListItem | 列表字段 | L3-L39
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/MemberApplicationServiceImpl.java |
listExternalMembers | 查询外部成员 | L272-L299

### GET /api/organizations/{organizationId}/members/external/search

1) Purpose

- 搜索可关联的外部成员。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | path | organizationId | long | 是 | 组织 ID |
   | query | keyword | string | 否 | 关键字 |
   示例：UNKNOWN
4) Response
   Data 字段：
   | 字段 | 类型 | 说明 |
   | (root) | list<UserSummary> | 用户列表 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | 500 | 组织不存在 | MemberApplicationServiceImpl |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/MemberApplicationServiceImpl.java#L301
   | 500 | 500 | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/GlobalExceptionHandler.java#L23 |
6) Side Effects

- 仅查询。

7) Idempotency

- ASSUMED IDEMPOTENT。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/MemberController.java |
searchExternalMembers | GET /api/organizations/{organizationId}/members/external/search | L137-L144
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-api/src/main/java/com/tenghe/corebackend/iam/api/dto/user/UserSummary.java |
UserSummary | 字段定义 | L3-L48
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/MemberApplicationServiceImpl.java |
searchExternalCandidates | 搜索用户 | L301-L315

### POST /api/organizations/{organizationId}/members/external

1) Purpose

- 关联外部成员。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | path | organizationId | long | 是 | 组织 ID |
   | body | userId | long | 是 | 用户 ID |
   示例：UNKNOWN
4) Response
   Data 字段：
   | 字段 | 类型 | 说明 |
   | (null) | - | 无业务返回 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | 500 | 用户不存在/不可添加本组织成员/已关联 | MemberApplicationServiceImpl |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/MemberApplicationServiceImpl.java#L318
   | 500 | 500 | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/GlobalExceptionHandler.java#L23 |
6) Side Effects

- 新增外部成员关联。

7) Idempotency

- NOT IDEMPOTENT（重复关联会返回业务错误）。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/MemberController.java |
linkExternalMember | POST /api/organizations/{organizationId}/members/external | L146-L153
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-api/src/main/java/com/tenghe/corebackend/iam/api/dto/member/LinkExternalMemberRequest.java |
LinkExternalMemberRequest | userId 字段 | L3-L10
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/MemberApplicationServiceImpl.java |
linkExternalMember | 关联逻辑与校验 | L318-L342

### DELETE /api/organizations/{organizationId}/members/external/{userId}

1) Purpose

- 解除外部成员关联。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | path | organizationId | long | 是 | 组织 ID |
   | path | userId | long | 是 | 用户 ID |
   示例：UNKNOWN
4) Response
   Data 字段：
   | 字段 | 类型 | 说明 |
   | (null) | - | 无业务返回 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | 500 | 组织不存在 | MemberApplicationServiceImpl |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/MemberApplicationServiceImpl.java#L345
   | 500 | 500 | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/GlobalExceptionHandler.java#L23 |
6) Side Effects

- 软删除外部成员关联。

7) Idempotency

- ASSUMED IDEMPOTENT（重复删除不报错取决于底层实现）。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/MemberController.java |
unlinkExternalMember | DELETE /api/organizations/{organizationId}/members/external/{userId} | L156-L162
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/MemberApplicationServiceImpl.java |
unlinkExternalMember | 软删除关联 | L344-L348

### GET /api/roles

1) Purpose

- 查询角色列表（分页/搜索）。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | query | appId | long | 否 | 应用 ID |
   | query | keyword | string | 否 | 关键字 |
   | query | page | int | 否 | 页码 |
   | query | size | int | 否 | 页大小 |
   示例：UNKNOWN
4) Response
   Data 字段（PageResponse）：
   | 字段 | 类型 | 说明 |
   | pageSize | int | 页大小 |
   | page | int | 页码 |
   | total | long | 总数 |
   | data | list<RoleListItem> | 列表 |
   RoleListItem 字段：
   | 字段 | 类型 | 说明 |
   | id | string | 角色 ID |
   | appId | string | 应用 ID |
   | appName | string | 应用名 |
   | roleName | string | 角色名 |
   | roleCode | string | 角色编码 |
   | description | string | 描述 |
   | status | string | 状态 |
   | preset | boolean | 是否预置 |
   | memberCount | int | 成员数 |
   | createdDate | string | 创建日期 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 500 | 500 | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/GlobalExceptionHandler.java#L23 |
6) Side Effects

- 仅查询。

7) Idempotency

- ASSUMED IDEMPOTENT。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/RoleController.java |
listRoles | GET /api/roles | L52-L63
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-api/src/main/java/com/tenghe/corebackend/iam/api/dto/role/RoleListItem.java |
RoleListItem | 列表字段 | L8-L18
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/RoleApplicationServiceImpl.java |
listRoles | 查询与分页 | L78-L101

### POST /api/roles

1) Purpose

- 创建角色。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | body | appId | long | 是 | 所属应用 |
   | body | roleName | string | 是 | 角色名称 |
   | body | roleCode | string | 是 | 角色编码 |
   | body | description | string | 否 | 描述 |
   示例：UNKNOWN
4) Response
   Data 字段：
   | 字段 | 类型 | 说明 |
   | (root) | string | 新角色 ID |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | 500 | 应用不存在/名称或编码冲突 | RoleApplicationServiceImpl |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/RoleApplicationServiceImpl.java#L104
   | 500 | 500 | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/GlobalExceptionHandler.java#L23 |
6) Side Effects

- 保存角色记录。

7) Idempotency

- NOT IDEMPOTENT。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/RoleController.java |
createRole | POST /api/roles | L69-L77
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-api/src/main/java/com/tenghe/corebackend/iam/api/dto/role/CreateRoleRequest.java |
CreateRoleRequest | 创建字段 | L6-L13
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/RoleApplicationServiceImpl.java |
createRole | 保存角色 | L104-L136

### GET /api/roles/{roleId}

1) Purpose

- 获取角色详情。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | path | roleId | long | 是 | 角色 ID |
   示例：UNKNOWN
4) Response
   Data 字段（RoleDetailResponse）：
   | 字段 | 类型 | 说明 |
   | id | string | 角色 ID |
   | appId | string | 应用 ID |
   | appName | string | 应用名 |
   | roleName | string | 角色名 |
   | roleCode | string | 角色编码 |
   | description | string | 描述 |
   | status | string | 状态 |
   | preset | boolean | 是否预置 |
   | createdDate | string | 创建日期 |
   | permissionIds | list<long> | 权限 ID 列表 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | 500 | 角色不存在 | RoleApplicationServiceImpl |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/RoleApplicationServiceImpl.java#L139
   | 500 | 500 | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/GlobalExceptionHandler.java#L23 |
6) Side Effects

- 仅查询。

7) Idempotency

- ASSUMED IDEMPOTENT。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/RoleController.java |
getRoleDetail | GET /api/roles/{roleId} | L83-L97
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-api/src/main/java/com/tenghe/corebackend/iam/api/dto/role/RoleDetailResponse.java |
RoleDetailResponse | 字段定义 | L8-L19
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/RoleApplicationServiceImpl.java |
getRoleDetail | 组装详情 | L139-L155

### PUT /api/roles/{roleId}

1) Purpose

- 更新角色信息。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | path | roleId | long | 是 | 角色 ID |
   | body | roleName | string | 是 | 角色名称 |
   | body | description | string | 否 | 描述 |
   | body | status | string | 是 | 状态 |
   示例：UNKNOWN
4) Response
   Data 字段：
   | 字段 | 类型 | 说明 |
   | (null) | - | 无业务返回 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | 500 | 预置角色不可编辑/名称冲突 | RoleApplicationServiceImpl |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/RoleApplicationServiceImpl.java#L158
   | 500 | 500 | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/GlobalExceptionHandler.java#L23 |
6) Side Effects

- 更新角色。

7) Idempotency

- ASSUMED IDEMPOTENT。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/RoleController.java |
updateRole | PUT /api/roles/{roleId} | L103-L113
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-api/src/main/java/com/tenghe/corebackend/iam/api/dto/role/UpdateRoleRequest.java |
UpdateRoleRequest | 更新字段 | L6-L12
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/RoleApplicationServiceImpl.java |
updateRole | 更新逻辑 | L158-L180

### PUT /api/roles/{roleId}/permissions

1) Purpose

- 配置角色权限。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | path | roleId | long | 是 | 角色 ID |
   | body | permissionIds | list<long> | 否 | 权限 ID 列表 |
   示例：UNKNOWN
4) Response
   Data 字段：
   | 字段 | 类型 | 说明 |
   | (null) | - | 无业务返回 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | 500 | 权限不在应用范围 | RoleApplicationServiceImpl |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/RoleApplicationServiceImpl.java#L182
   | 500 | 500 | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/GlobalExceptionHandler.java#L23 |
6) Side Effects

- 替换角色权限关联。

7) Idempotency

- ASSUMED IDEMPOTENT（相同权限集合重复配置）。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/RoleController.java |
configureRolePermissions | PUT /api/roles/{roleId}/permissions | L119-L127
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-api/src/main/java/com/tenghe/corebackend/iam/api/dto/role/ConfigureRolePermissionsRequest.java |
ConfigureRolePermissionsRequest | permissionIds 字段 | L6-L10
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/RoleApplicationServiceImpl.java |
configureRolePermissions | 替换权限 | L182-L195

### DELETE /api/roles/{roleId}

1) Purpose

- 删除角色（软删除）。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | path | roleId | long | 是 | 角色 ID |
   示例：UNKNOWN
4) Response
   Data 字段：
   | 字段 | 类型 | 说明 |
   | (null) | - | 无业务返回 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | 500 | 预置角色不可删除/存在成员 | RoleApplicationServiceImpl |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/RoleApplicationServiceImpl.java#L197
   | 500 | 500 | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/GlobalExceptionHandler.java#L23 |
6) Side Effects

- 软删除角色与角色权限。

7) Idempotency

- NOT IDEMPOTENT。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/RoleController.java |
deleteRole | DELETE /api/roles/{roleId} | L133-L136
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/RoleApplicationServiceImpl.java |
deleteRole | 软删除与清理权限 | L197-L210

### GET /api/roles/{roleId}/members

1) Purpose

- 查询角色成员列表。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | path | roleId | long | 是 | 角色 ID |
   | query | organizationId | long | 否 | 组织 ID |
   | query | page | int | 否 | 页码 |
   | query | size | int | 否 | 页大小 |
   示例：UNKNOWN
4) Response
   Data 字段（PageResponse）：
   | 字段 | 类型 | 说明 |
   | pageSize | int | 页大小 |
   | page | int | 页码 |
   | total | long | 总数 |
   | data | list<RoleMember> | 列表 |
   RoleMember 字段：
   | 字段 | 类型 | 说明 |
   | userId | string | 用户 ID |
   | username | string | 用户名 |
   | name | string | 姓名 |
   | phone | string | 手机 |
   | email | string | 邮箱 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | 500 | 角色不存在 | RoleApplicationServiceImpl |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/RoleApplicationServiceImpl.java#L213
   | 500 | 500 | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/GlobalExceptionHandler.java#L23 |
6) Side Effects

- 仅查询。

7) Idempotency

- ASSUMED IDEMPOTENT。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/RoleController.java |
listRoleMembers | GET /api/roles/{roleId}/members | L142-L153
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-api/src/main/java/com/tenghe/corebackend/iam/api/dto/role/RoleMember.java |
RoleMember | 字段定义 | L6-L13
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/RoleApplicationServiceImpl.java |
listRoleMembers | 读取成员 | L213-L237

### POST /api/roles/{roleId}/members

1) Purpose

- 批量添加角色成员。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | path | roleId | long | 是 | 角色 ID |
   | body | organizationId | long | 是 | 组织 ID |
   | body | userIds | list<long> | 是 | 用户 ID 列表 |
   示例：UNKNOWN
4) Response
   Data 字段：
   | 字段 | 类型 | 说明 |
   | (null) | - | 无业务返回 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | 500 | 组织未开通应用/成员不属于组织 | RoleApplicationServiceImpl |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/RoleApplicationServiceImpl.java#L240
   | 500 | 500 | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/GlobalExceptionHandler.java#L23 |
6) Side Effects

- 批量新增角色授权。

7) Idempotency

- NOT IDEMPOTENT（新增授权）。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/RoleController.java |
batchAddMembers | POST /api/roles/{roleId}/members | L159-L168
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-api/src/main/java/com/tenghe/corebackend/iam/api/dto/role/BatchRoleMemberRequest.java |
BatchRoleMemberRequest | organizationId/userIds | L6-L11
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/RoleApplicationServiceImpl.java |
batchAddMembers | 授权逻辑 | L240-L281

### DELETE /api/roles/{roleId}/members

1) Purpose

- 批量移除角色成员。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | path | roleId | long | 是 | 角色 ID |
   | body | organizationId | long | 是 | 组织 ID |
   | body | userIds | list<long> | 是 | 用户 ID 列表 |
   示例：UNKNOWN
4) Response
   Data 字段：
   | 字段 | 类型 | 说明 |
   | (null) | - | 无业务返回 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 500 | 500 | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/GlobalExceptionHandler.java#L23 |
6) Side Effects

- 软删除角色授权。

7) Idempotency

- ASSUMED IDEMPOTENT（重复删除同一授权）。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/RoleController.java |
batchRemoveMembers | DELETE /api/roles/{roleId}/members | L174-L183
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-api/src/main/java/com/tenghe/corebackend/iam/api/dto/role/BatchRoleMemberRequest.java |
BatchRoleMemberRequest | organizationId/userIds | L6-L11
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/RoleApplicationServiceImpl.java |
batchRemoveMembers | 删除授权 | L284-L295

### GET /api/permissions/tree

1) Purpose

- 获取权限树。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | query | (无) | - | - | 无请求参数 |
   示例：UNKNOWN
4) Response
   Data 字段：
   | 字段 | 类型 | 说明 |
   | (root) | list<PermissionTreeNode> | 权限树 |
   PermissionTreeNode 字段：
   | 字段 | 类型 | 说明 |
   | id | string | 权限 ID |
   | permissionCode | string | 权限编码 |
   | permissionName | string | 权限名称 |
   | permissionType | string | 类型 |
   | status | string | 状态 |
   | sortOrder | int | 排序 |
   | children | list<PermissionTreeNode> | 子节点 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 500 | 500 | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/GlobalExceptionHandler.java#L23 |
6) Side Effects

- 仅查询。

7) Idempotency

- ASSUMED IDEMPOTENT。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/PermissionController.java |
getPermissionTree | GET /api/permissions/tree | L31-L37
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-api/src/main/java/com/tenghe/corebackend/iam/api/dto/permission/PermissionTreeNode.java |
PermissionTreeNode | 树节点字段 | L9-L16
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/PermissionApplicationServiceImpl.java |
getPermissionTree | 构建树 | L31-L35

### PUT /api/permissions/{permissionId}/status

1) Purpose

- 切换权限状态。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | path | permissionId | long | 是 | 权限 ID |
   | body | status | string | 是 | 状态 |
   示例：UNKNOWN
4) Response
   Data 字段：
   | 字段 | 类型 | 说明 |
   | (null) | - | 无业务返回 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | 500 | 权限不存在/状态无效 | PermissionApplicationServiceImpl |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/PermissionApplicationServiceImpl.java#L37
   | 500 | 500 | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/GlobalExceptionHandler.java#L23 |
6) Side Effects

- 更新权限状态并级联禁用子权限。

7) Idempotency

- ASSUMED IDEMPOTENT。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/PermissionController.java |
togglePermissionStatus | PUT /api/permissions/{permissionId}/status | L43-L48
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-api/src/main/java/com/tenghe/corebackend/iam/api/dto/permission/TogglePermissionStatusRequest.java |
TogglePermissionStatusRequest | status 字段 | L6-L10
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/PermissionApplicationServiceImpl.java |
togglePermissionStatus | 更新状态与级联 | L37-L61

### GET /api/applications

1) Purpose

- 查询应用列表（分页/搜索）。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | query | keyword | string | 否 | 关键字 |
   | query | page | int | 否 | 页码 |
   | query | size | int | 否 | 页大小 |
   示例：UNKNOWN
4) Response
   Data 字段（PageResponse）：
   | 字段 | 类型 | 说明 |
   | pageSize | int | 页大小 |
   | page | int | 页码 |
   | total | long | 总数 |
   | data | list<ApplicationListItem> | 列表 |
   ApplicationListItem 字段：
   | 字段 | 类型 | 说明 |
   | id | string | 应用 ID |
   | appName | string | 应用名称 |
   | appCode | string | 应用编码 |
   | description | string | 描述 |
   | status | string | 状态 |
   | createdDate | string | 创建日期 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 500 | 500 | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/GlobalExceptionHandler.java#L23 |
6) Side Effects

- 仅查询。

7) Idempotency

- ASSUMED IDEMPOTENT。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/ApplicationController.java |
listApplications | GET /api/applications | L46-L57
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-api/src/main/java/com/tenghe/corebackend/iam/api/dto/application/ApplicationListItem.java |
ApplicationListItem | 列表字段 | L8-L14
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/ApplicationApplicationServiceImpl.java |
listApplications | 查询与分页 | L57-L76

### POST /api/applications

1) Purpose

- 创建应用。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | body | appName | string | 是 | 应用名称 |
   | body | appCode | string | 是 | 应用编码 |
   | body | description | string | 否 | 描述 |
   | body | permissionIds | list<long> | 是 | 权限集合 |
   示例：UNKNOWN
4) Response
   Data 字段：
   | 字段 | 类型 | 说明 |
   | (root) | string | 新应用 ID |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | 500 | 编码重复/权限为空 | ApplicationApplicationServiceImpl |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/ApplicationApplicationServiceImpl.java#L78
   | 500 | 500 | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/GlobalExceptionHandler.java#L23 |
6) Side Effects

- 保存应用并写入应用权限关系。

7) Idempotency

- NOT IDEMPOTENT。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/ApplicationController.java |
createApplication | POST /api/applications | L62-L70
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-api/src/main/java/com/tenghe/corebackend/iam/api/dto/application/CreateApplicationRequest.java |
CreateApplicationRequest | 创建字段 | L8-L13
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/ApplicationApplicationServiceImpl.java |
createApplication | 保存应用与权限 | L78-L103

### GET /api/applications/{appId}

1) Purpose

- 获取应用详情。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | path | appId | long | 是 | 应用 ID |
   示例：UNKNOWN
4) Response
   Data 字段（ApplicationDetailResponse）：
   | 字段 | 类型 | 说明 |
   | id | string | 应用 ID |
   | appName | string | 应用名称 |
   | appCode | string | 应用编码 |
   | description | string | 描述 |
   | status | string | 状态 |
   | createdDate | string | 创建日期 |
   | permissionIds | list<long> | 权限集合 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | 500 | 应用不存在 | ApplicationApplicationServiceImpl |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/ApplicationApplicationServiceImpl.java#L106
   | 500 | 500 | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/GlobalExceptionHandler.java#L23 |
6) Side Effects

- 仅查询。

7) Idempotency

- ASSUMED IDEMPOTENT。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/ApplicationController.java |
getApplicationDetail | GET /api/applications/{appId} | L76-L87
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-api/src/main/java/com/tenghe/corebackend/iam/api/dto/application/ApplicationDetailResponse.java |
ApplicationDetailResponse | 详情字段 | L8-L16
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/ApplicationApplicationServiceImpl.java |
getApplicationDetail | 组装详情 | L106-L119

### PUT /api/applications/{appId}

1) Purpose

- 更新应用信息。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | path | appId | long | 是 | 应用 ID |
   | body | appName | string | 是 | 应用名称 |
   | body | description | string | 否 | 描述 |
   | body | status | string | 是 | 状态 |
   | body | permissionIds | list<long> | 否 | 权限集合 |
   示例：UNKNOWN
4) Response
   Data 字段：
   | 字段 | 类型 | 说明 |
   | (null) | - | 无业务返回 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | 500 | 应用不存在/权限已被角色使用 | ApplicationApplicationServiceImpl |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/ApplicationApplicationServiceImpl.java#L121
   | 500 | 500 | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/GlobalExceptionHandler.java#L23 |
6) Side Effects

- 更新应用及权限关系。

7) Idempotency

- ASSUMED IDEMPOTENT。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/ApplicationController.java |
updateApplication | PUT /api/applications/{appId} | L93-L104
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-api/src/main/java/com/tenghe/corebackend/iam/api/dto/application/UpdateApplicationRequest.java |
UpdateApplicationRequest | 更新字段 | L8-L13
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/ApplicationApplicationServiceImpl.java |
updateApplication | 更新与校验 | L121-L157

### DELETE /api/applications/{appId}

1) Purpose

- 删除应用（软删除）。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | path | appId | long | 是 | 应用 ID |
   示例：UNKNOWN
4) Response
   Data 字段：
   | 字段 | 类型 | 说明 |
   | (null) | - | 无业务返回 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | 500 | 应用下存在角色 | ApplicationApplicationServiceImpl |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/ApplicationApplicationServiceImpl.java#L160
   | 500 | 500 | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/GlobalExceptionHandler.java#L23 |
6) Side Effects

- 软删除应用并清理应用权限关系。

7) Idempotency

- NOT IDEMPOTENT。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-controller/src/main/java/com/tenghe/corebackend/iam/controller/web/ApplicationController.java |
deleteApplication | DELETE /api/applications/{appId} | L110-L113
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-application/src/main/java/com/tenghe/corebackend/iam/application/service/impl/ApplicationApplicationServiceImpl.java |
deleteApplication | 软删除与清理 | L160-L170

## Device APIs

### GET /api/device-models

1) Purpose

- 查询设备模型列表（分页）。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | query | page | int | 否 | 页码 |
   | query | size | int | 否 | 页大小 |
   示例：UNKNOWN
4) Response
   Data 字段（PageResponse）：
   | 字段 | 类型 | 说明 |
   | items | list<DeviceModelListItem> | 列表 |
   | total | long | 总数 |
   | page | int | 页码 |
   | size | int | 页大小 |
   DeviceModelListItem 字段：
   | 字段 | 类型 | 说明 |
   | id | long | 模型 ID |
   | identifier | string | 标识符 |
   | name | string | 名称 |
   | source | string | 来源 |
   | parentModelId | long | 父模型 ID |
   | pointCount | int | 测点数 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 500 | (N/A) | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/device-service/device-controller/src/main/java/com/tenghe/corebackend/device/controller/web/GlobalExceptionHandler.java#L17 |
6) Side Effects

- 仅查询。

7) Idempotency

- ASSUMED IDEMPOTENT。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/device-service/device-controller/src/main/java/com/tenghe/corebackend/device/controller/web/DeviceModelController.java |
listModels | GET /api/device-models | L42-L50
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-api/src/main/java/com/tenghe/corebackend/device/api/dto/model/DeviceModelListItem.java |
DeviceModelListItem | 列表字段 | L3-L58
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/DeviceModelApplicationService.java |
listModels | 查询与分页 | L57-L75

### GET /api/device-models/{modelId}

1) Purpose

- 获取设备模型详情。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | path | modelId | long | 是 | 模型 ID |
   示例：UNKNOWN
4) Response
   Data 字段（DeviceModelDetailResponse）：
   | 字段 | 类型 | 说明 |
   | id | long | 模型 ID |
   | identifier | string | 标识符 |
   | name | string | 名称 |
   | source | string | 来源 |
   | parentModelId | long | 父模型 ID |
   | points | list<DeviceModelPointDto> | 测点列表 |
   DeviceModelPointDto 字段：
   | 字段 | 类型 | 说明 |
   | identifier | string | 点标识 |
   | name | string | 点名称 |
   | type | string | 点类型 |
   | dataType | string | 数据类型 |
   | enumItems | list<string> | 枚举项 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | (N/A) | 模型不存在 | DeviceModelApplicationService |
   /Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/DeviceModelApplicationService.java#L186
   | 500 | (N/A) | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/device-service/device-controller/src/main/java/com/tenghe/corebackend/device/controller/web/GlobalExceptionHandler.java#L17 |
6) Side Effects

- 仅查询。

7) Idempotency

- ASSUMED IDEMPOTENT。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/device-service/device-controller/src/main/java/com/tenghe/corebackend/device/controller/web/DeviceModelController.java |
getModel | GET /api/device-models/{modelId} | L53-L56
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-api/src/main/java/com/tenghe/corebackend/device/api/dto/model/DeviceModelDetailResponse.java |
DeviceModelDetailResponse | 详情字段 | L5-L59
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/DeviceModelApplicationService.java |
getModel | 读取模型 | L77-L80

### POST /api/device-models

1) Purpose

- 创建设备模型。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | body | identifier | string | 是 | 标识符 |
   | body | name | string | 是 | 名称 |
   | body | source | string | 是 | NEW/INHERIT |
   | body | parentModelId | long | 否 | 父模型 ID |
   | body | points | list<DeviceModelPointDto> | 否 | 测点 |
   示例：UNKNOWN
4) Response
   Data 字段（DeviceModelDetailResponse）：
   | 字段 | 类型 | 说明 |
   | id | long | 模型 ID |
   | identifier | string | 标识符 |
   | name | string | 名称 |
   | source | string | 来源 |
   | parentModelId | long | 父模型 ID |
   | points | list<DeviceModelPointDto> | 测点 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | (N/A) | 标识冲突/继承层级/点位校验 | DeviceModelApplicationService |
   /Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/DeviceModelApplicationService.java#L82
   | 500 | (N/A) | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/device-service/device-controller/src/main/java/com/tenghe/corebackend/device/controller/web/GlobalExceptionHandler.java#L17 |
6) Side Effects

- 保存设备模型与测点。

7) Idempotency

- NOT IDEMPOTENT。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/device-service/device-controller/src/main/java/com/tenghe/corebackend/device/controller/web/DeviceModelController.java |
createModel | POST /api/device-models | L59-L68
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-api/src/main/java/com/tenghe/corebackend/device/api/dto/model/CreateDeviceModelRequest.java |
CreateDeviceModelRequest | 创建字段 | L5-L50
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/DeviceModelApplicationService.java |
createModel | 保存模型 | L82-L124

### DELETE /api/device-models/{modelId}

1) Purpose

- 删除设备模型（软删除）。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | path | modelId | long | 是 | 模型 ID |
   示例：UNKNOWN
4) Response
   Data 字段：
   | 字段 | 类型 | 说明 |
   | (null) | - | 无业务返回 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | (N/A) | 绑定产品/子模型/设备 | DeviceModelApplicationService |
   /Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/DeviceModelApplicationService.java#L127
   | 500 | (N/A) | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/device-service/device-controller/src/main/java/com/tenghe/corebackend/device/controller/web/GlobalExceptionHandler.java#L17 |
6) Side Effects

- 软删除模型。

7) Idempotency

- NOT IDEMPOTENT。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/device-service/device-controller/src/main/java/com/tenghe/corebackend/device/controller/web/DeviceModelController.java |
deleteModel | DELETE /api/device-models/{modelId} | L71-L75
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/DeviceModelApplicationService.java |
deleteModel | 删除校验与更新 | L127-L140

### PUT /api/device-models/{modelId}/points

1) Purpose

- 更新设备模型测点。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | path | modelId | long | 是 | 模型 ID |
   | body | points | list<DeviceModelPointDto> | 是 | 测点 |
   示例：UNKNOWN
4) Response
   Data 字段（DeviceModelDetailResponse）：
   | 字段 | 类型 | 说明 |
   | id | long | 模型 ID |
   | identifier | string | 标识符 |
   | name | string | 名称 |
   | source | string | 来源 |
   | parentModelId | long | 父模型 ID |
   | points | list<DeviceModelPointDto> | 测点 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | (N/A) | 父模型点不可修改/类型不匹配 | DeviceModelApplicationService |
   /Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/DeviceModelApplicationService.java#L142
   | 500 | (N/A) | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/device-service/device-controller/src/main/java/com/tenghe/corebackend/device/controller/web/GlobalExceptionHandler.java#L17 |
6) Side Effects

- 更新模型点集合。

7) Idempotency

- ASSUMED IDEMPOTENT。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/device-service/device-controller/src/main/java/com/tenghe/corebackend/device/controller/web/DeviceModelController.java |
updatePoints | PUT /api/device-models/{modelId}/points | L77-L85
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-api/src/main/java/com/tenghe/corebackend/device/api/dto/model/UpdateDeviceModelPointsRequest.java |
UpdateDeviceModelPointsRequest | points 字段 | L5-L12
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/DeviceModelApplicationService.java |
updatePoints | 更新逻辑 | L142-L160

### POST /api/device-models/{modelId}/points/import

1) Purpose

- 导入设备模型测点。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | path | modelId | long | 是 | 模型 ID |
   | body | points | list<DeviceModelPointDto> | 是 | 测点 |
   示例：UNKNOWN
4) Response
   Data 字段（DeviceModelDetailResponse）：
   | 字段 | 类型 | 说明 |
   | id | long | 模型 ID |
   | identifier | string | 标识符 |
   | name | string | 名称 |
   | source | string | 来源 |
   | parentModelId | long | 父模型 ID |
   | points | list<DeviceModelPointDto> | 测点 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | (N/A) | 父模型点不可修改/类型不匹配 | DeviceModelApplicationService |
   /Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/DeviceModelApplicationService.java#L163
   | 500 | (N/A) | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/device-service/device-controller/src/main/java/com/tenghe/corebackend/device/controller/web/GlobalExceptionHandler.java#L17 |
6) Side Effects

- 事务内合并并更新测点。

7) Idempotency

- ASSUMED IDEMPOTENT（相同导入数据）。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/device-service/device-controller/src/main/java/com/tenghe/corebackend/device/controller/web/DeviceModelController.java |
importPoints | POST /api/device-models/{modelId}/points/import | L88-L96
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-api/src/main/java/com/tenghe/corebackend/device/api/dto/model/ImportDeviceModelPointsRequest.java |
ImportDeviceModelPointsRequest | points 字段 | L5-L12
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/DeviceModelApplicationService.java |
importPoints | 合并更新 | L163-L183

### GET /api/products

1) Purpose

- 查询产品列表（分页）。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | query | page | int | 否 | 页码 |
   | query | size | int | 否 | 页大小 |
   示例：UNKNOWN
4) Response
   Data 字段（PageResponse）：
   | 字段 | 类型 | 说明 |
   | items | list<ProductListItem> | 列表 |
   | total | long | 总数 |
   | page | int | 页码 |
   | size | int | 页大小 |
   ProductListItem 字段：
   | 字段 | 类型 | 说明 |
   | id | long | 产品 ID |
   | productType | string | 产品类型 |
   | name | string | 名称 |
   | productKey | string | 产品 Key |
   | deviceModelId | long | 设备模型 ID |
   | accessMode | string | 接入模式 |
   | description | string | 描述 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 500 | (N/A) | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/device-service/device-controller/src/main/java/com/tenghe/corebackend/device/controller/web/GlobalExceptionHandler.java#L17 |
6) Side Effects

- 仅查询。

7) Idempotency

- ASSUMED IDEMPOTENT。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/device-service/device-controller/src/main/java/com/tenghe/corebackend/device/controller/web/ProductController.java |
listProducts | GET /api/products | L38-L46
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-api/src/main/java/com/tenghe/corebackend/device/api/dto/product/ProductListItem.java |
ProductListItem | 列表字段 | L3-L66
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/ProductApplicationService.java |
listProducts | 查询与分页 | L50-L69

### POST /api/products

1) Purpose

- 创建产品。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | body | productType | string | 否 | 产品类型 |
   | body | name | string | 是 | 名称 |
   | body | deviceModelId | long | 是 | 设备模型 ID |
   | body | accessMode | string | 是 | 接入模式 |
   | body | description | string | 否 | 描述 |
   | body | protocolMapping | map<string,string> | 否 | 协议映射 |
   示例：UNKNOWN
4) Response
   Data 字段：
   | 字段 | 类型 | 说明 |
   | id | long | 产品 ID |
   | productKey | string | 产品 Key |
   | productSecret | string | 产品 Secret |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | (N/A) | 模型不存在/字段校验失败 | ProductApplicationService |
   /Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/ProductApplicationService.java#L71
   | 500 | (N/A) | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/device-service/device-controller/src/main/java/com/tenghe/corebackend/device/controller/web/GlobalExceptionHandler.java#L17 |
6) Side Effects

- 保存产品并生成 key/secret。

7) Idempotency

- NOT IDEMPOTENT。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/device-service/device-controller/src/main/java/com/tenghe/corebackend/device/controller/web/ProductController.java |
createProduct | POST /api/products | L49-L59
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-api/src/main/java/com/tenghe/corebackend/device/api/dto/product/CreateProductRequest.java |
CreateProductRequest | 创建字段 | L5-L59
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-api/src/main/java/com/tenghe/corebackend/device/api/dto/product/CreateProductResponse.java |
CreateProductResponse | 返回字段 | L3-L39
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/ProductApplicationService.java |
createProduct | 生成 key/secret 并保存 | L71-L106

### PUT /api/products/{productId}

1) Purpose

- 更新产品信息。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | path | productId | long | 是 | 产品 ID |
   | body | name | string | 否 | 名称 |
   | body | description | string | 否 | 描述 |
   示例：UNKNOWN
4) Response
   Data 字段：
   | 字段 | 类型 | 说明 |
   | (null) | - | 无业务返回 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | (N/A) | 产品不存在/名称过长 | ProductApplicationService |
   /Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/ProductApplicationService.java#L108
   | 500 | (N/A) | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/device-service/device-controller/src/main/java/com/tenghe/corebackend/device/controller/web/GlobalExceptionHandler.java#L17 |
6) Side Effects

- 更新产品名称/描述。

7) Idempotency

- ASSUMED IDEMPOTENT。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/device-service/device-controller/src/main/java/com/tenghe/corebackend/device/controller/web/ProductController.java |
updateProduct | PUT /api/products/{productId} | L62-L71
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-api/src/main/java/com/tenghe/corebackend/device/api/dto/product/UpdateProductRequest.java |
UpdateProductRequest | 更新字段 | L3-L19
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/ProductApplicationService.java |
updateProduct | 更新逻辑 | L108-L118

### PUT /api/products/{productId}/mapping

1) Purpose

- 更新产品协议映射。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | path | productId | long | 是 | 产品 ID |
   | body | protocolMapping | map<string,string> | 否 | 协议映射 |
   示例：UNKNOWN
4) Response
   Data 字段：
   | 字段 | 类型 | 说明 |
   | (null) | - | 无业务返回 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | (N/A) | 产品不存在 | ProductApplicationService |
   /Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/ProductApplicationService.java#L120
   | 500 | (N/A) | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/device-service/device-controller/src/main/java/com/tenghe/corebackend/device/controller/web/GlobalExceptionHandler.java#L17 |
6) Side Effects

- 更新产品协议映射。

7) Idempotency

- ASSUMED IDEMPOTENT。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/device-service/device-controller/src/main/java/com/tenghe/corebackend/device/controller/web/ProductController.java |
updateMapping | PUT /api/products/{productId}/mapping | L74-L82
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-api/src/main/java/com/tenghe/corebackend/device/api/dto/product/UpdateProductMappingRequest.java |
UpdateProductMappingRequest | protocolMapping 字段 | L3-L12
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/ProductApplicationService.java |
updateProtocolMapping | 更新映射 | L120-L124

### DELETE /api/products/{productId}

1) Purpose

- 删除产品（软删除）。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | path | productId | long | 是 | 产品 ID |
   示例：UNKNOWN
4) Response
   Data 字段：
   | 字段 | 类型 | 说明 |
   | (null) | - | 无业务返回 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | (N/A) | 产品下存在设备 | ProductApplicationService |
   /Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/ProductApplicationService.java#L126
   | 500 | (N/A) | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/device-service/device-controller/src/main/java/com/tenghe/corebackend/device/controller/web/GlobalExceptionHandler.java#L17 |
6) Side Effects

- 软删除产品。

7) Idempotency

- NOT IDEMPOTENT。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/device-service/device-controller/src/main/java/com/tenghe/corebackend/device/controller/web/ProductController.java |
deleteProduct | DELETE /api/products/{productId} | L85-L88
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/ProductApplicationService.java |
deleteProduct | 删除校验与更新 | L126-L133

### GET /api/gateways

1) Purpose

- 查询网关列表（支持名称/SN 搜索）。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | query | keyword | string | 否 | 名称或 SN |
   | query | page | int | 否 | 页码 |
   | query | size | int | 否 | 页大小 |
   示例：UNKNOWN
4) Response
   Data 字段（PageResponse）：
   | 字段 | 类型 | 说明 |
   | items | list<GatewayListItem> | 列表 |
   | total | long | 总数 |
   | page | int | 页码 |
   | size | int | 页大小 |
   GatewayListItem 字段：
   | 字段 | 类型 | 说明 |
   | id | long | 网关 ID |
   | name | string | 名称 |
   | type | string | 类型 |
   | sn | string | SN |
   | productId | long | 产品 ID |
   | stationId | long | 电站 ID |
   | status | string | 在线状态 |
   | enabled | boolean | 启用开关 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 500 | (N/A) | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/device-service/device-controller/src/main/java/com/tenghe/corebackend/device/controller/web/GlobalExceptionHandler.java#L17 |
6) Side Effects

- 仅查询。

7) Idempotency

- ASSUMED IDEMPOTENT。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/device-service/device-controller/src/main/java/com/tenghe/corebackend/device/controller/web/GatewayController.java |
listGateways | GET /api/gateways | L35-L44
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-api/src/main/java/com/tenghe/corebackend/device/api/dto/gateway/GatewayListItem.java |
GatewayListItem | 列表字段 | L3-L75
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/GatewayApplicationService.java |
listGateways | 查询与分页 | L51-L73

### POST /api/gateways

1) Purpose

- 创建网关。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | body | name | string | 是 | 名称 |
   | body | type | string | 是 | 类型 |
   | body | sn | string | 是 | SN |
   | body | productId | long | 是 | 网关产品 |
   | body | stationId | long | 是 | 电站 ID |
   示例：UNKNOWN
4) Response
   Data 字段（GatewayListItem）：
   | 字段 | 类型 | 说明 |
   | id | long | 网关 ID |
   | name | string | 名称 |
   | type | string | 类型 |
   | sn | string | SN |
   | productId | long | 产品 ID |
   | stationId | long | 电站 ID |
   | status | string | 在线状态 |
   | enabled | boolean | 启用开关 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | (N/A) | SN 重复/产品类型错误 | GatewayApplicationService |
   /Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/GatewayApplicationService.java#L75
   | 500 | (N/A) | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/device-service/device-controller/src/main/java/com/tenghe/corebackend/device/controller/web/GlobalExceptionHandler.java#L17 |
6) Side Effects

- 保存网关记录。

7) Idempotency

- NOT IDEMPOTENT。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/device-service/device-controller/src/main/java/com/tenghe/corebackend/device/controller/web/GatewayController.java |
createGateway | POST /api/gateways | L47-L56
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-api/src/main/java/com/tenghe/corebackend/device/api/dto/gateway/CreateGatewayRequest.java |
CreateGatewayRequest | 创建字段 | L3-L48
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/GatewayApplicationService.java |
createGateway | 保存网关 | L75-L123

### PUT /api/gateways/{gatewayId}

1) Purpose

- 更新网关信息。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | path | gatewayId | long | 是 | 网关 ID |
   | body | name | string | 否 | 名称 |
   | body | sn | string | 否 | SN |
   | body | productId | long | 否 | 网关产品 |
   | body | stationId | long | 否 | 电站 ID |
   示例：UNKNOWN
4) Response
   Data 字段：
   | 字段 | 类型 | 说明 |
   | (null) | - | 无业务返回 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | (N/A) | SN 冲突/产品类型错误 | GatewayApplicationService |
   /Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/GatewayApplicationService.java#L126
   | 500 | (N/A) | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/device-service/device-controller/src/main/java/com/tenghe/corebackend/device/controller/web/GlobalExceptionHandler.java#L17 |
6) Side Effects

- 更新网关；若电站变更，同步更新子设备 stationId。

7) Idempotency

- ASSUMED IDEMPOTENT。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/device-service/device-controller/src/main/java/com/tenghe/corebackend/device/controller/web/GatewayController.java |
updateGateway | PUT /api/gateways/{gatewayId} | L59-L70
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-api/src/main/java/com/tenghe/corebackend/device/api/dto/gateway/UpdateGatewayRequest.java |
UpdateGatewayRequest | 更新字段 | L3-L39
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/GatewayApplicationService.java |
updateGateway | 更新与同步子设备 | L126-L170

### DELETE /api/gateways/{gatewayId}

1) Purpose

- 删除网关（软删除）。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | path | gatewayId | long | 是 | 网关 ID |
   示例：UNKNOWN
4) Response
   Data 字段：
   | 字段 | 类型 | 说明 |
   | (null) | - | 无业务返回 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | (N/A) | 存在子设备 | GatewayApplicationService |
   /Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/GatewayApplicationService.java#L173
   | 500 | (N/A) | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/device-service/device-controller/src/main/java/com/tenghe/corebackend/device/controller/web/GlobalExceptionHandler.java#L17 |
6) Side Effects

- 软删除网关。

7) Idempotency

- NOT IDEMPOTENT。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/device-service/device-controller/src/main/java/com/tenghe/corebackend/device/controller/web/GatewayController.java |
deleteGateway | DELETE /api/gateways/{gatewayId} | L73-L76
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/GatewayApplicationService.java |
deleteGateway | 删除校验与更新 | L173-L180

### POST /api/gateways/{gatewayId}/enable

1) Purpose

- 启用网关。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | path | gatewayId | long | 是 | 网关 ID |
   示例：UNKNOWN
4) Response
   Data 字段：
   | 字段 | 类型 | 说明 |
   | (null) | - | 无业务返回 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | (N/A) | 网关不存在 | GatewayApplicationService |
   /Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/GatewayApplicationService.java#L182
   | 500 | (N/A) | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/device-service/device-controller/src/main/java/com/tenghe/corebackend/device/controller/web/GlobalExceptionHandler.java#L17 |
6) Side Effects

- 更新网关 enabled=true。

7) Idempotency

- ASSUMED IDEMPOTENT。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/device-service/device-controller/src/main/java/com/tenghe/corebackend/device/controller/web/GatewayController.java |
enableGateway | POST /api/gateways/{gatewayId}/enable | L79-L85
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/GatewayApplicationService.java |
toggleGateway | 更新 enabled | L182-L186

### POST /api/gateways/{gatewayId}/disable

1) Purpose

- 停用网关。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | path | gatewayId | long | 是 | 网关 ID |
   示例：UNKNOWN
4) Response
   Data 字段：
   | 字段 | 类型 | 说明 |
   | (null) | - | 无业务返回 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | (N/A) | 网关不存在 | GatewayApplicationService |
   /Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/GatewayApplicationService.java#L182
   | 500 | (N/A) | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/device-service/device-controller/src/main/java/com/tenghe/corebackend/device/controller/web/GlobalExceptionHandler.java#L17 |
6) Side Effects

- 更新网关 enabled=false。

7) Idempotency

- ASSUMED IDEMPOTENT。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/device-service/device-controller/src/main/java/com/tenghe/corebackend/device/controller/web/GatewayController.java |
disableGateway | POST /api/gateways/{gatewayId}/disable | L88-L94
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/GatewayApplicationService.java |
toggleGateway | 更新 enabled | L182-L186

### GET /api/devices

1) Purpose

- 查询设备列表（分页/过滤）。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | query | productId | long | 否 | 产品 ID |
   | query | keyword | string | 否 | 关键字 |
   | query | page | int | 否 | 页码 |
   | query | size | int | 否 | 页大小 |
   示例：UNKNOWN
4) Response
   Data 字段（PageResponse）：
   | 字段 | 类型 | 说明 |
   | items | list<DeviceListItem> | 列表 |
   | total | long | 总数 |
   | page | int | 页码 |
   | size | int | 页大小 |
   DeviceListItem 字段：
   | 字段 | 类型 | 说明 |
   | id | long | 设备 ID |
   | name | string | 名称 |
   | productId | long | 产品 ID |
   | deviceKey | string | 设备 Key |
   | gatewayId | long | 网关 ID |
   | stationId | long | 电站 ID |
   | status | string | 在线状态 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 500 | (N/A) | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/device-service/device-controller/src/main/java/com/tenghe/corebackend/device/controller/web/GlobalExceptionHandler.java#L17 |
6) Side Effects

- 仅查询。

7) Idempotency

- ASSUMED IDEMPOTENT。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/device-service/device-controller/src/main/java/com/tenghe/corebackend/device/controller/web/DeviceController.java |
listDevices | GET /api/devices | L54-L64
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-api/src/main/java/com/tenghe/corebackend/device/api/dto/device/DeviceListItem.java |
DeviceListItem | 列表字段 | L3-L66
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/DeviceApplicationService.java |
listDevices | 查询与分页 | L82-L93

### POST /api/devices

1) Purpose

- 创建设备。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | body | name | string | 是 | 名称 |
   | body | productId | long | 是 | 产品 ID |
   | body | deviceKey | string | 是 | 设备 Key |
   | body | gatewayId | long | 是 | 网关 ID |
   | body | dynamicAttributes | map<string,object> | 否 | 动态属性 |
   示例：UNKNOWN
4) Response
   Data 字段：
   | 字段 | 类型 | 说明 |
   | id | long | 设备 ID |
   | deviceSecret | string | 设备密钥 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | (N/A) | 设备 Key 冲突/校验失败 | DeviceApplicationService |
   /Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/DeviceApplicationService.java#L95
   | 500 | (N/A) | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/device-service/device-controller/src/main/java/com/tenghe/corebackend/device/controller/web/GlobalExceptionHandler.java#L17 |
6) Side Effects

- 保存设备并生成 deviceSecret。

7) Idempotency

- NOT IDEMPOTENT。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/device-service/device-controller/src/main/java/com/tenghe/corebackend/device/controller/web/DeviceController.java |
createDevice | POST /api/devices | L67-L76
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-api/src/main/java/com/tenghe/corebackend/device/api/dto/device/CreateDeviceRequest.java |
CreateDeviceRequest | 创建字段 | L5-L49
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-api/src/main/java/com/tenghe/corebackend/device/api/dto/device/CreateDeviceResponse.java |
CreateDeviceResponse | 返回字段 | L3-L29
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/DeviceApplicationService.java |
createDevice | 保存设备 | L95-L128

### PUT /api/devices/{deviceId}

1) Purpose

- 更新设备。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | path | deviceId | long | 是 | 设备 ID |
   | body | name | string | 否 | 名称 |
   | body | gatewayId | long | 否 | 网关 ID |
   | body | dynamicAttributes | map<string,object> | 否 | 动态属性 |
   示例：UNKNOWN
4) Response
   Data 字段：
   | 字段 | 类型 | 说明 |
   | (null) | - | 无业务返回 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | (N/A) | 设备不存在/属性校验失败 | DeviceApplicationService |
   /Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/DeviceApplicationService.java#L130
   | 500 | (N/A) | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/device-service/device-controller/src/main/java/com/tenghe/corebackend/device/controller/web/GlobalExceptionHandler.java#L17 |
6) Side Effects

- 更新设备、网关与电站关联、动态属性。

7) Idempotency

- ASSUMED IDEMPOTENT。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/device-service/device-controller/src/main/java/com/tenghe/corebackend/device/controller/web/DeviceController.java |
updateDevice | PUT /api/devices/{deviceId} | L79-L89
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-api/src/main/java/com/tenghe/corebackend/device/api/dto/device/UpdateDeviceRequest.java |
UpdateDeviceRequest | 更新字段 | L3-L30
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/DeviceApplicationService.java |
updateDevice | 更新逻辑 | L130-L147

### POST /api/devices/import/preview

1) Purpose

- 设备导入预览。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | body | rows | list<DeviceImportRowDto> | 否 | 导入行 |
   示例：UNKNOWN
4) Response
   Data 字段（DeviceImportPreviewResponse）：
   | 字段 | 类型 | 说明 |
   | total | int | 总行数 |
   | createCount | int | 新增数 |
   | updateCount | int | 更新数 |
   | invalidCount | int | 无效数 |
   | items | list<DeviceImportPreviewItem> | 行级校验 |
   DeviceImportPreviewItem 字段：
   | 字段 | 类型 | 说明 |
   | rowIndex | int | 行号 |
   | action | string | CREATE/UPDATE/INVALID |
   | message | string | 信息 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 500 | (N/A) | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/device-service/device-controller/src/main/java/com/tenghe/corebackend/device/controller/web/GlobalExceptionHandler.java#L17 |
6) Side Effects

- 仅校验，不落库。

7) Idempotency

- ASSUMED IDEMPOTENT。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/device-service/device-controller/src/main/java/com/tenghe/corebackend/device/controller/web/DeviceController.java |
previewImport | POST /api/devices/import/preview | L92-L111
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-api/src/main/java/com/tenghe/corebackend/device/api/dto/device/DeviceImportPreviewRequest.java |
DeviceImportPreviewRequest | rows 字段 | L3-L12
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-api/src/main/java/com/tenghe/corebackend/device/api/dto/device/DeviceImportPreviewResponse.java |
DeviceImportPreviewResponse | 返回字段 | L5-L50
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/DeviceApplicationService.java |
previewImport | 预览逻辑 | L149-L179

### POST /api/devices/import/commit

1) Purpose

- 提交设备导入。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | body | rows | list<DeviceImportRowDto> | 否 | 导入行 |
   示例：UNKNOWN
4) Response
   Data 字段（DeviceImportCommitResponse）：
   | 字段 | 类型 | 说明 |
   | successCount | int | 成功数 |
   | failureCount | int | 失败数 |
   | failures | list<DeviceImportFailure> | 失败明细 |
   DeviceImportFailure 字段：
   | 字段 | 类型 | 说明 |
   | rowIndex | int | 行号 |
   | message | string | 信息 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 500 | (N/A) | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/device-service/device-controller/src/main/java/com/tenghe/corebackend/device/controller/web/GlobalExceptionHandler.java#L17 |
6) Side Effects

- 根据导入行创建或更新设备记录。

7) Idempotency

- NOT IDEMPOTENT（创建或更新产生变化）。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/device-service/device-controller/src/main/java/com/tenghe/corebackend/device/controller/web/DeviceController.java |
commitImport | POST /api/devices/import/commit | L114-L131
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-api/src/main/java/com/tenghe/corebackend/device/api/dto/device/DeviceImportCommitRequest.java |
DeviceImportCommitRequest | rows 字段 | L3-L12
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-api/src/main/java/com/tenghe/corebackend/device/api/dto/device/DeviceImportCommitResponse.java |
DeviceImportCommitResponse | 返回字段 | L5-L32
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/DeviceApplicationService.java |
commitImport | 提交逻辑 | L182-L221

### GET /api/devices/export

1) Purpose

- 导出设备列表。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | query | productId | long | 否 | 产品 ID |
   | query | keyword | string | 否 | 关键字 |
   示例：UNKNOWN
4) Response
   Data 字段（DeviceExportResponse）：
   | 字段 | 类型 | 说明 |
   | fileName | string | 文件名 |
   | rows | list<DeviceExportRow> | 数据行 |
   DeviceExportRow 字段：
   | 字段 | 类型 | 说明 |
   | name | string | 设备名 |
   | deviceKey | string | 设备 Key |
   | productId | long | 产品 ID |
   | gatewayId | long | 网关 ID |
   | stationId | long | 电站 ID |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | (N/A) | 产品不存在 | DeviceApplicationService |
   /Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/DeviceApplicationService.java#L223
   | 500 | (N/A) | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/device-service/device-controller/src/main/java/com/tenghe/corebackend/device/controller/web/GlobalExceptionHandler.java#L17 |
6) Side Effects

- 仅生成导出数据与文件名（不写文件）。

7) Idempotency

- ASSUMED IDEMPOTENT（同一过滤条件生成不同时间戳文件名）。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/device-service/device-controller/src/main/java/com/tenghe/corebackend/device/controller/web/DeviceController.java |
exportDevices | GET /api/devices/export | L133-L153
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-api/src/main/java/com/tenghe/corebackend/device/api/dto/device/DeviceExportResponse.java |
DeviceExportResponse | 返回字段 | L5-L20
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/DeviceApplicationService.java |
exportDevices | 生成文件名与行 | L223-L253

### GET /api/devices/{deviceId}/telemetry/latest

1) Purpose

- 获取设备最新遥测数据。

2) AuthN/AuthZ

- UNKNOWN。

3) Request
   | 位置 | 参数 | 类型 | 必填 | 说明 |
   | path | deviceId | long | 是 | 设备 ID |
   示例：UNKNOWN
4) Response
   Data 字段：
   | 字段 | 类型 | 说明 |
   | (root) | list<DeviceTelemetryItem> | 遥测列表 |
   DeviceTelemetryItem 字段：
   | 字段 | 类型 | 说明 |
   | pointIdentifier | string | 点标识 |
   | value | object | 值 |
   | updatedAt | string | 更新时间 |
   示例：UNKNOWN
5) Errors
   | http_status | code | condition | source | evidence |
   | 400 | (N/A) | 设备不存在 | DeviceApplicationService |
   /Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/DeviceApplicationService.java#L256
   | 500 | (N/A) | 未捕获异常 | GlobalExceptionHandler |
   /Users/sirgan/Downloads/CoreBackend/device-service/device-controller/src/main/java/com/tenghe/corebackend/device/controller/web/GlobalExceptionHandler.java#L17 |
6) Side Effects

- 仅查询内存遥测仓库。

7) Idempotency

- ASSUMED IDEMPOTENT。

8) Evidence

-
/Users/sirgan/Downloads/CoreBackend/device-service/device-controller/src/main/java/com/tenghe/corebackend/device/controller/web/DeviceController.java |
listLatestTelemetry | GET /api/devices/{deviceId}/telemetry/latest | L156-L163
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-api/src/main/java/com/tenghe/corebackend/device/api/dto/device/DeviceTelemetryItem.java |
DeviceTelemetryItem | 字段定义 | L3-L30
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-application/src/main/java/com/tenghe/corebackend/device/application/service/DeviceApplicationService.java |
listLatestTelemetry | 读取遥测 | L256-L269

## Evidence

-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-api/src/main/java/com/tenghe/corebackend/iam/api/dto/common/ApiResponse.java |
ApiResponse | IAM 通用响应结构 | L1-L28
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-api/src/main/java/com/tenghe/corebackend/device/api/dto/common/ApiResponse.java |
ApiResponse | Device 通用响应结构 | L1-L23

## UNKNOWN/ASSUMED

- UNKNOWN：AuthN/AuthZ 未见过滤器或注解；各 endpoint 的鉴权要求需结合部署配置确认。
- UNKNOWN：请求/响应示例未在代码中给出，示例统一标注为 UNKNOWN。
- ASSUMED：只读接口在未发现写操作时视为幂等，具体幂等性需结合外部状态变更评估。
