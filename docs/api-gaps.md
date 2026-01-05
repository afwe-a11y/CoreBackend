# API 清单差异记录

> 生成时间: 2024-12-30
> 最后更新: 2024-12-30
> 说明: 本文档记录 API 清单与实际实现之间的差异，包括缺失的 API 和 Method 不一致的 API

---

## 一、Method 不一致的 API（暂不修改，待确认）

| 模块          | 清单 Method | 实际 Method | 路径                                   | 说明            |
|-------------|-----------|-----------|--------------------------------------|---------------|
| Users       | `PATCH`   | `PUT`     | `/users/{userId}/status`             | 状态更新建议用 PATCH |
| Permissions | `PATCH`   | `PUT`     | `/permissions/{permissionId}/status` | 状态更新建议用 PATCH |

---

## 二、路径调整记录（已修正）

| 模块            | 清单路径                              | 原实现路径                 | 修正状态        |
|---------------|-----------------------------------|-----------------------|-------------|
| Auth          | `/auth/password-reset/email-code` | `/password/send-code` | ✅ 已迁移       |
| Auth          | `/auth/password-reset`            | `/password/reset`     | ✅ 已迁移       |
| Organizations | `/{organizationCode}`             | `/{organizationId}`   | ✅ 已修正为 Code |
| Organizations | `/delete-preview`                 | `/delete-info`        | ✅ 已修正       |
| Organizations | `/admin`                          | `/admin/assign`       | ✅ 已修正       |
| Applications  | `/{applicationCode}`              | `/{appId}`            | ✅ 已修正为 Code |
| Devices       | `/{deviceCode}`                   | `/{deviceId}`         | ✅ 已修正为 Code |
| Devices       | `/latest-data`                    | `/telemetry/latest`   | ✅ 已修正       |
| Devices       | `/import/parse`                   | `/import/preview`     | ✅ 已修正       |
| Devices       | `/import/confirm`                 | `/import/commit`      | ✅ 已修正       |
| Products      | `/point-mappings`                 | `/mapping`            | ✅ 已修正       |
| DeviceModels  | `/api/v1/device-models`           | `/api/device-models`  | ✅ 已修正       |
| Products      | `/api/v1/products`                | `/api/products`       | ✅ 已修正       |
| Gateways      | `/api/v1/gateways`                | `/api/gateways`       | ✅ 已修正       |
| Devices       | `/api/v1/devices`                 | `/api/devices`        | ✅ 已修正       |

---

## 三、清单中缺失但实际已实现的 API（建议补充到清单）

| 模块            | API                                                                        | 说明       |
|---------------|----------------------------------------------------------------------------|----------|
| Auth          | `POST /auth/logout`                                                        | 用户登出     |
| Auth          | `GET /auth/validate`                                                       | 验证会话有效性  |
| Roles         | `GET /roles/{roleId}`                                                      | 获取角色详情   |
| Applications  | `GET /applications/{applicationCode}`                                      | 获取应用详情   |
| Organizations | `GET /organizations/{organizationCode}/admin/search`                       | 搜索管理员候选人 |
| Members       | `POST /organizations/{organizationCode}/members/internal/{userId}/disable` | 停用内部成员   |
| Members       | `GET /organizations/{organizationCode}/members/external`                   | 获取外部成员列表 |
| Device Models | `GET /device-models/{deviceModelId}`                                       | 获取设备模型详情 |

---

## 四、待补齐业务逻辑的 API（已创建端点）

### iam-service ✅ 已完成

- [x] `PUT /roles/{roleId}/name` - RoleController.updateRoleName() ✅
- [x] `PATCH /roles/{roleId}/status` - RoleController.updateRoleStatus() ✅
- [x] `GET /roles/{roleId}/permissions/tree` - RoleController.getRolePermissionsTree() ✅
- [x] `DELETE /roles/{roleId}/members/{userId}` - RoleController.removeRoleMember() ✅

### device-service ❌ 待实现

- [ ] `GET /device-models/{deviceModelId}/points` - DeviceModelController.listPoints()
- [ ] `POST /device-models/{deviceModelId}/points` - DeviceModelController.createPoint()
- [ ] `PUT /device-models/{deviceModelId}/points/{pointId}` - DeviceModelController.updatePoint()
- [ ] `DELETE /device-models/{deviceModelId}/points/{pointId}` - DeviceModelController.deletePoint()
- [ ] `GET /device-models/{deviceModelId}/points/export` - DeviceModelController.exportPoints()
- [ ] `GET /device-models/{deviceModelId}/points/import-template` - DeviceModelController.getImportTemplate()
- [ ] `GET /products/{productId}` - ProductController.getProductDetail()
- [ ] `GET /products/{productId}/secrets` - ProductController.getProductSecrets()
- [ ] `GET /products/{productId}/point-mappings` - ProductController.getPointMappings()
- [ ] `GET /products/{productId}/point-mappings/export` - ProductController.exportPointMappings()
- [ ] `PATCH /gateways/{gatewayId}/enable-status` - GatewayController.updateGatewayEnableStatus()
- [ ] `GET /devices/stats` - DeviceController.getDeviceStats()
- [ ] `PATCH /devices/{deviceCode}/enable-status` - DeviceController.updateDeviceEnableStatus()
- [ ] `GET /devices/{deviceCode}/secrets` - DeviceController.getDeviceSecrets()
- [ ] `GET /devices/{deviceCode}/config/export` - DeviceController.exportDeviceConfig()
- [ ] `GET /devices/import-template` - DeviceController.getImportTemplate()

---

## 五、ApplicationService 方法实现状态

### iam-service - RoleApplicationService ✅ 已完成

```java
void updateRoleName(Long roleId, String roleName); // ✅ 已实现
void updateRoleStatus(Long roleId, String status); // ✅ 已实现
List<PermissionTreeNodeResult> getRolePermissionsTree(Long roleId); // ✅ 已实现
void removeRoleMember(Long roleId, Long userId); // ✅ 已实现
```

### iam-service - OrganizationApplicationService ✅ 已完成

```java
OrganizationDetailResult getOrganizationDetailByCode(String code); // ✅ 已实现
void deleteOrganizationByCode(String code); // ✅ 已实现
DeleteOrganizationInfoResult getDeleteInfoByCode(String code); // ✅ 已实现
List<UserSummaryResult> searchAdminCandidatesByCode(String code, String keyword); // ✅ 已实现
```

### iam-service - MemberApplicationService ✅ 已完成

```java
PageResult<InternalMemberListItemResult> listInternalMembersByCode(String code, Integer page, Integer size); // ✅ 已实现
void disableInternalMemberByCode(String code, Long userId); // ✅ 已实现
void deleteInternalMemberByCode(String code, Long userId); // ✅ 已实现
PageResult<ExternalMemberListItemResult> listExternalMembersByCode(String code, Integer page, Integer size); // ✅ 已实现
List<UserSummaryResult> searchExternalCandidatesByCode(String code, String keyword); // ✅ 已实现
void unlinkExternalMemberByCode(String code, Long userId); // ✅ 已实现
```

### iam-service - ApplicationApplicationService ✅ 已完成

```java
ApplicationDetailResult getApplicationDetailByCode(String code); // ✅ 已实现
void deleteApplicationByCode(String code); // ✅ 已实现
```

### iam-service - PasswordResetApplicationService ✅ 已完成

```java
void sendEmailCode(SendEmailCodeCommand command); // ✅ 支持 email 查找用户
void resetPassword(ResetPasswordCommand command); // ✅ 支持忘记密码流程
```

### device-service - DeviceModelApplicationService ❌ 待实现

```java
PageResult<DeviceModelPointResult> listPoints(Long modelId, Integer page, Integer size);
DeviceModelPointResult createPoint(Long modelId, DeviceModelPointCommand command);
void updatePoint(Long modelId, Long pointId, DeviceModelPointCommand command);
void deletePoint(Long modelId, Long pointId);
byte[] exportPoints(Long modelId);
byte[] getPointsImportTemplate(Long modelId);
```

### device-service - ProductApplicationService

```java
ProductDetailResult getProductDetail(Long productId);
ProductSecretResult getProductSecrets(Long productId);
List<ProductPointMappingResult> getPointMappings(Long productId);
byte[] exportPointMappings(Long productId);
```

### device-service - DeviceApplicationService

```java
DeviceStatsResult getDeviceStats();
void updateDeviceEnableStatusByCode(String code, Boolean enabled);
DeviceSecretResult getDeviceSecretsByCode(String code);
byte[] exportDeviceConfigByCode(String code);
byte[] getImportTemplate();
List<DeviceTelemetryResult> getLatestDataByCode(String code);
```
