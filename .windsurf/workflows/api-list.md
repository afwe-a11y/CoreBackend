---
description: API清单列表
---

## API 清单（Method/Path/Auth/Request/Response）

> 说明：Path 已省略 BasePath（实际为 `/api/v1` + Path）。

### 1. Auth

- `GET  /auth/captcha` (NoAuth) → `Envelope<CaptchaVO>`
- `POST /auth/login` (NoAuth) `LoginRequestDTO` → `Envelope<LoginResultVO>`
- `POST /auth/password-reset/email-code` (NoAuth) `PasswordResetEmailCodeRequestDTO` → `Envelope<{}>`
- `POST /auth/password-reset` (NoAuth) `PasswordResetRequestDTO` → `Envelope<{}>`

### 2. Users

- `GET    /users` (Auth) `UserListQueryDTO` → `Envelope<Page<UserListItemVO>>`
- `GET    /users/{userId}` (Auth) → `Envelope<UserDetailVO>`
- `POST   /users` (Auth) `CreateUserRequestDTO` → `Envelope<UserDetailVO>`
- `PUT    /users/{userId}` (Auth) `UpdateUserRequestDTO` → `Envelope<UserDetailVO>`
- `PATCH  /users/{userId}/status` (Auth) `UpdateUserStatusRequestDTO` → `Envelope<{}>`
- `DELETE /users/{userId}` (Auth) → `Envelope<{}>`

### 3. Roles

- `GET    /roles` (Auth) `PaginationQueryDTO & KeywordQueryDTO` → `Envelope<Page<RoleListItemVO>>`
- `POST   /roles` (Auth) `CreateRoleRequestDTO` → `Envelope<RoleListItemVO>`
- `PUT    /roles/{roleId}/name` (Auth) `UpdateRoleNameRequestDTO` → `Envelope<{}>`
- `PATCH  /roles/{roleId}/status` (Auth) `UpdateRoleStatusRequestDTO` → `Envelope<{}>`
- `DELETE /roles/{roleId}` (Auth) → `Envelope<{}>`
- `GET    /roles/{roleId}/permissions/tree` (Auth) → `Envelope<PermissionTreeNodeVO[]>`
- `PUT    /roles/{roleId}/permissions` (Auth) `UpdateRolePermissionsRequestDTO` → `Envelope<{}>`
- `GET    /roles/{roleId}/members` (Auth) `RoleMemberListQueryDTO` → `Envelope<Page<RoleMemberListItemVO>>`
- `POST   /roles/{roleId}/members` (Auth) `AddRoleMembersRequestDTO` → `Envelope<{}>`
- `DELETE /roles/{roleId}/members/{userId}` (Auth) → `Envelope<{}>`

### 4. Applications

- `GET    /applications` (Auth) `ApplicationListQueryDTO` → `Envelope<Page<ApplicationListItemVO>>`
- `POST   /applications` (Auth) `CreateApplicationRequestDTO` → `Envelope<ApplicationListItemVO>`
- `PUT    /applications/{applicationCode}` (Auth) `UpdateApplicationRequestDTO` → `Envelope<{}>`
- `DELETE /applications/{applicationCode}` (Auth) → `Envelope<{}>`

### 5. Permissions

- `GET   /permissions/tree` (Auth) `PermissionTreeQueryDTO` → `Envelope<PermissionListItemVO[]>`
- `PATCH /permissions/{permissionId}/status` (Auth) `UpdatePermissionStatusRequestDTO` → `Envelope<{}>`

### 6. Device Models & Points

- `GET    /device-models` (Auth) `DeviceModelListQueryDTO` → `Envelope<Page<DeviceModelListItemVO>>`
- `POST   /device-models` (Auth) `CreateDeviceModelRequestDTO` → `Envelope<DeviceModelListItemVO>`
- `DELETE /device-models/{deviceModelId}` (Auth) → `Envelope<{}>`
- `GET    /device-models/{deviceModelId}/points` (Auth) `DeviceModelPointListQueryDTO` →
  `Envelope<Page<DeviceModelPointListItemVO>>`
- `POST   /device-models/{deviceModelId}/points` (Auth) `CreateOrUpdateDeviceModelPointRequestDTO` →
  `Envelope<DeviceModelPointListItemVO>`
- `PUT    /device-models/{deviceModelId}/points/{pointId}` (Auth) `CreateOrUpdateDeviceModelPointRequestDTO` →
  `Envelope<{}>`
- `DELETE /device-models/{deviceModelId}/points/{pointId}` (Auth) → `Envelope<{}>`
- `GET    /device-models/{deviceModelId}/points/export` (Auth) → `binary file`
- `GET    /device-models/{deviceModelId}/points/import-template` (Auth) → `binary file`
- `POST   /device-models/{deviceModelId}/points/import` (Auth) `multipart file` → `Envelope<{}>`

### 7. Products

- `GET    /products` (Auth) `ProductListQueryDTO` → `Envelope<Page<ProductListItemVO>>`
- `POST   /products` (Auth) `CreateProductRequestDTO` → `Envelope<ProductDetailVO>`
- `GET    /products/{productId}` (Auth) → `Envelope<ProductDetailVO>`
- `PUT    /products/{productId}` (Auth) `UpdateProductRequestDTO` → `Envelope<{}>`
- `GET    /products/{productId}/secrets` (Auth) → `Envelope<ProductSecretVO>`
- `DELETE /products/{productId}` (Auth) → `Envelope<{}>`
- `GET    /products/{productId}/point-mappings` (Auth) → `Envelope<ProductPointMappingItemVO[]>`
- `PUT    /products/{productId}/point-mappings` (Auth) `UpdateProductPointMappingsRequestDTO` → `Envelope<{}>`
- `GET    /products/{productId}/point-mappings/export` (Auth) → `binary file`

### 8. Gateways

- `GET    /gateways` (Auth) `GatewayListQueryDTO` → `Envelope<Page<GatewayListItemVO>>`
- `POST   /gateways` (Auth) `CreateGatewayRequestDTO` → `Envelope<GatewayListItemVO>`
- `PUT    /gateways/{gatewayId}` (Auth) `UpdateGatewayRequestDTO` → `Envelope<{}>`
- `DELETE /gateways/{gatewayId}` (Auth) → `Envelope<{}>`
- `PATCH  /gateways/{gatewayId}/enable-status` (Auth) `UpdateGatewayEnableStatusRequestDTO` → `Envelope<{}>`

### 9. Devices（deviceCode）

- `GET   /devices` (Auth) `DeviceListQueryDTO` → `Envelope<Page<DeviceListItemVO>>`
- `GET   /devices/stats` (Auth) → `Envelope<DeviceStatsVO>`
- `POST  /devices` (Auth) `CreateDeviceRequestDTO` → `Envelope<DeviceListItemVO>`
- `PUT   /devices/{deviceCode}` (Auth) `UpdateDeviceRequestDTO` → `Envelope<{}>`
- `PATCH /devices/{deviceCode}/enable-status` (Auth) `UpdateDeviceEnableStatusRequestDTO` → `Envelope<{}>`
- `GET   /devices/{deviceCode}/secrets` (Auth) → `Envelope<DeviceSecretVO>`
- `GET   /devices/{deviceCode}/latest-data` (Auth) → `Envelope<DeviceLatestDataVO>`
- `GET   /devices/export` (Auth) → `binary file`
- `GET   /devices/{deviceCode}/config/export` (Auth) → `binary file`
- `GET   /devices/import-template` (Auth) → `binary file`
- `POST  /devices/import/parse` (Auth) `multipart file` → `Envelope<ImportParseResultVO>`
- `POST  /devices/import/confirm` (Auth) `ImportConfirmRequestDTO` → `Envelope<ImportConfirmResultVO>`

### 10. Organizations（organizationCode）

- `GET    /organizations` (Auth) `OrganizationListQueryDTO` → `Envelope<Page<OrganizationListItemVO>>`
- `POST   /organizations` (Auth) `CreateOrganizationRequestDTO` → `Envelope<OrganizationDetailVO>`
- `GET    /organizations/{organizationCode}` (Auth) → `Envelope<OrganizationDetailVO>`
- `PUT    /organizations/{organizationCode}` (Auth) `UpdateOrganizationRequestDTO` → `Envelope<{}>`
- `GET    /organizations/{organizationCode}/delete-preview` (Auth) → `Envelope<OrganizationDeletePreviewVO>`
- `DELETE /organizations/{organizationCode}` (Auth) → `Envelope<{}>`
- `POST   /organizations/{organizationCode}/admin` (Auth) `AssignOrganizationAdminRequestDTO` → `Envelope<{}>`
- `GET    /organizations/{organizationCode}/members/internal` (Auth) `OrganizationInternalMemberListQueryDTO` →
  `Envelope<Page<OrganizationInternalMemberItemVO>>`
- `POST   /organizations/{organizationCode}/members/internal` (Auth) `CreateOrganizationInternalMemberRequestDTO` →
  `Envelope<OrganizationInternalMemberItemVO>`
- `PUT    /organizations/{organizationCode}/members/internal/{userId}` (Auth)
  `UpdateOrganizationInternalMemberRequestDTO` → `Envelope<{}>`
- `DELETE /organizations/{organizationCode}/members/internal/{userId}` (Auth) → `Envelope<{}>`
- `GET    /organizations/{organizationCode}/members/external/search` (Auth) `OrganizationExternalMemberSearchQueryDTO` →
  `Envelope<Page<OrganizationExternalMemberItemVO>>`
- `POST   /organizations/{organizationCode}/members/external` (Auth) `AddOrganizationExternalMemberRequestDTO` →
  `Envelope<{}>`
- `DELETE /organizations/{organizationCode}/members/external/{userId}` (Auth) → `Envelope<{}>`