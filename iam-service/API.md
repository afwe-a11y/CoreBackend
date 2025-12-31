# IAM Service API

Base path: `/api`

Response envelope:
- `ApiResponse<T>`: `code`, `message`, `data`
- `PageResponse<T>`: `page`, `pageSize`, `total`, `data`

## Auth

### GET `/auth/captcha`
Response: `ApiResponse<CaptchaResponse>`
- `CaptchaResponse`: `captchaKey`, `captchaImage`

### POST `/auth/login`
Request: `LoginRequest`
- `identifier`, `password`, `captcha`, `captchaKey`
Response: `ApiResponse<LoginResponse>`
- `LoginResponse`: `userId`, `username`, `token`, `requirePasswordReset`

### POST `/auth/logout`
Headers:
- `Authorization: Bearer <token>` (optional)
Response: `ApiResponse<Void>`

### GET `/auth/validate`
Headers:
- `Authorization: Bearer <token>` (optional)
Response: `ApiResponse<Boolean>`

## Password

### POST `/password/send-code`
Headers:
- `X-User-Id` (optional)
Request: `SendEmailCodeRequest`
- `email`
Response: `ApiResponse<Void>`

### POST `/password/reset`
Headers:
- `X-User-Id` (optional)
Request: `ResetPasswordRequest`
- `oldPassword`, `newPassword`, `emailCode`
Response: `ApiResponse<Void>`

## Organization

### GET `/organizations`
Query:
- `keyword`, `page`, `size`
Response: `ApiResponse<PageResponse<OrganizationListItem>>`
- `OrganizationListItem`: `id`, `name`, `internalMemberCount`, `externalMemberCount`, `primaryAdminDisplay`, `status`, `createdDate`

### POST `/organizations`
Request: `CreateOrganizationRequest`
- `name`, `code`, `description`, `appIds`
Response: `ApiResponse<CreateOrganizationResponse>`
- `CreateOrganizationResponse`: `id`

### GET `/organizations/{organizationId}`
Response: `ApiResponse<OrganizationDetailResponse>`
- `OrganizationDetailResponse`: `id`, `name`, `createdDate`, `description`, `appIds`, `status`, `contactName`, `contactPhone`, `contactEmail`

### PUT `/organizations/{organizationId}`
Request: `UpdateOrganizationRequest`
- `name`, `description`, `appIds`, `status`, `contactName`, `contactPhone`, `contactEmail`
Response: `ApiResponse<Void>`

### DELETE `/organizations/{organizationId}`
Response: `ApiResponse<Void>`

### GET `/organizations/{organizationId}/delete-info`
Response: `ApiResponse<DeleteOrganizationInfoResponse>`
- `DeleteOrganizationInfoResponse`: `name`, `userCount`

### GET `/organizations/{organizationId}/admin/search`
Query:
- `keyword`
Response: `ApiResponse<List<UserSummary>>`
- `UserSummary`: `id`, `username`, `name`, `phone`, `email`

### POST `/organizations/{organizationId}/admin/assign`
Request: `AssignAdminRequest`
- `userId`
Response: `ApiResponse<Void>`

## Organization Members

### GET `/organizations/{organizationId}/members/internal`
Query:
- `page`, `size`
Response: `ApiResponse<PageResponse<InternalMemberListItem>>`
- `InternalMemberListItem`: `username`, `phone`, `email`, `roles`, `status`

### POST `/organizations/{organizationId}/members/internal`
Request: `CreateInternalMemberRequest`
- `username`, `name`, `phone`, `email`, `organizationIds`, `roleSelections`, `status`, `accountType`
Response: `ApiResponse<CreateInternalMemberResponse>`
- `CreateInternalMemberResponse`: `username`, `phone`

### PUT `/organizations/{organizationId}/members/internal/{userId}`
Request: `UpdateInternalMemberRequest`
- `name`, `phone`, `email`, `status`, `accountType`
Response: `ApiResponse<Void>`

### POST `/organizations/{organizationId}/members/internal/{userId}/disable`
Response: `ApiResponse<Void>`

### DELETE `/organizations/{organizationId}/members/internal/{userId}`
Response: `ApiResponse<Void>`

### GET `/organizations/{organizationId}/members/external`
Query:
- `page`, `size`
Response: `ApiResponse<PageResponse<ExternalMemberListItem>>`
- `ExternalMemberListItem`: `username`, `sourceOrganizationName`, `phone`, `email`

### GET `/organizations/{organizationId}/members/external/search`
Query:
- `keyword`
Response: `ApiResponse<List<UserSummary>>`
- `UserSummary`: `id`, `username`, `name`, `phone`, `email`

### POST `/organizations/{organizationId}/members/external`
Request: `LinkExternalMemberRequest`
- `userId`
Response: `ApiResponse<Void>`

### DELETE `/organizations/{organizationId}/members/external/{userId}`
Response: `ApiResponse<Void>`

## Users

### GET `/users`
Query:
- `keyword`, `status`, `organizationIds`, `roleCodes`, `page`, `size`
Response: `ApiResponse<PageResponse<UserListItem>>`
- `UserListItem`: `id`, `username`, `name`, `phone`, `email`, `status`, `organizationNames`, `roleNames`, `createdDate`

### POST `/users`
Request: `CreateUserRequest`
- `username`, `name`, `phone`, `email`, `organizationIds`, `roleSelections`
Response: `ApiResponse<String>` (userId)

### GET `/users/{userId}`
Response: `ApiResponse<UserDetailResponse>`
- `UserDetailResponse`: `id`, `username`, `name`, `phone`, `email`, `status`, `createdDate`, `organizationIds`, `roleSelections`

### PUT `/users/{userId}`
Request: `UpdateUserRequest`
- `name`, `phone`, `email`, `organizationIds`, `roleSelections`
Response: `ApiResponse<Void>`

### PUT `/users/{userId}/status`
Request: `ToggleUserStatusRequest`
- `status`
Response: `ApiResponse<Void>`

### DELETE `/users/{userId}`
Response: `ApiResponse<Void>`

## Roles

### GET `/roles`
Query:
- `appId`, `keyword`, `page`, `size`
Response: `ApiResponse<PageResponse<RoleListItem>>`
- `RoleListItem`: `id`, `appId`, `appName`, `roleName`, `roleCode`, `description`, `status`, `preset`, `memberCount`, `createdDate`

### POST `/roles`
Request: `CreateRoleRequest`
- `appId`, `roleName`, `roleCode`, `description`
Response: `ApiResponse<String>` (roleId)

### GET `/roles/{roleId}`
Response: `ApiResponse<RoleDetailResponse>`
- `RoleDetailResponse`: `id`, `appId`, `appName`, `roleName`, `roleCode`, `description`, `status`, `preset`, `createdDate`, `permissionIds`

### PUT `/roles/{roleId}`
Request: `UpdateRoleRequest`
- `roleName`, `description`, `status`
Response: `ApiResponse<Void>`

### PUT `/roles/{roleId}/permissions`
Request: `ConfigureRolePermissionsRequest`
- `permissionIds`
Response: `ApiResponse<Void>`

### DELETE `/roles/{roleId}`
Response: `ApiResponse<Void>`

### GET `/roles/{roleId}/members`
Query:
- `organizationId`, `page`, `size`
Response: `ApiResponse<PageResponse<RoleMember>>`
- `RoleMember`: `userId`, `username`, `name`, `phone`, `email`

### POST `/roles/{roleId}/members`
Request: `BatchRoleMemberRequest`
- `organizationId`, `userIds`
Response: `ApiResponse<Void>`

### DELETE `/roles/{roleId}/members`
Request: `BatchRoleMemberRequest`
- `organizationId`, `userIds`
Response: `ApiResponse<Void>`

## Applications

### GET `/applications`
Query:
- `keyword`, `page`, `size`
Response: `ApiResponse<PageResponse<ApplicationListItem>>`
- `ApplicationListItem`: `id`, `appName`, `appCode`, `description`, `status`, `createdDate`

### POST `/applications`
Request: `CreateApplicationRequest`
- `appName`, `appCode`, `description`, `permissionIds`
Response: `ApiResponse<String>` (appId)

### GET `/applications/{appId}`
Response: `ApiResponse<ApplicationDetailResponse>`
- `ApplicationDetailResponse`: `id`, `appName`, `appCode`, `description`, `status`, `createdDate`, `permissionIds`

### PUT `/applications/{appId}`
Request: `UpdateApplicationRequest`
- `appName`, `description`, `status`, `permissionIds`
Response: `ApiResponse<Void>`

### DELETE `/applications/{appId}`
Response: `ApiResponse<Void>`

## Permissions

### GET `/permissions/tree`
Response: `ApiResponse<List<PermissionTreeNode>>`
- `PermissionTreeNode`: `id`, `permissionCode`, `permissionName`, `permissionType`, `status`, `sortOrder`, `children`

### PUT `/permissions/{permissionId}/status`
Request: `TogglePermissionStatusRequest`
- `status`
Response: `ApiResponse<Void>`
