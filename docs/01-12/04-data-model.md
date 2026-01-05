# 04-data-model.md

## DDL 判定

Decision: No
Evidence:

1)
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-infrastructure/src/main/java/com/tenghe/corebackend/iam/infrastructure/persistence/po/OrganizationPo.java |
OrganizationPo | 仅包含字段定义，无表名/列类型注解 | L7-L15
2)
/Users/sirgan/Downloads/CoreBackend/device-service/device-infrastructure/src/main/java/com/tenghe/corebackend/device/infrastructure/persistence/po/DevicePo.java |
DevicePo | 仅包含字段定义，无表名/列类型注解 | L7-L22
Excluded:

- (无)

## 数据持久化概览

- 两个服务均使用 MyBatis XML 映射 SQL，表名在 Mapper XML 中显式声明。
- 软删除通过 deleted 字段实现；多数查询以 deleted=0 过滤。
- JSON 字段用于承载扩展属性（组织描述、用户属性、设备动态属性、产品协议映射、设备模型点枚举）。

## IAM 持久化要点

- system_organization：组织基础信息（含 description/status/deleted）。
- system_user：用户基础信息（含 attributes JSON）。
- system_role：角色基础信息。
- system_user_org：组织成员关系（identity_type 区分 INTERNAL/EXTERNAL）。
- 组织描述字段复合：description 内部保存描述/联系人/管理员展示名 JSON。
- 用户 attributes 字段保存初始密码标记、失败次数、锁定时间、账号类型。

## Device 持久化要点

- system_device_model：设备模型主表。
- system_device_model_point：设备模型点表。
- system_product：产品表，包含 protocol_mapping JSON。
- system_gateway：网关表。
- system_device：设备表，包含 dynamic_attributes JSON。

## JSON 字段结构（基于代码）

- Organization.description：包含 description/primaryAdminDisplay/contactName/contactPhone/contactEmail。
- User.attributes：包含 initialPasswordFlag/failedLoginAttempts/lockedUntil/accountType。
- Product.protocolMapping：Map<String,String> JSON。
- Device.dynamicAttributes：Map<String,Object> JSON。
- DeviceModelPoint.enumItemsJson：List<String> JSON。

## Evidence

- /Users/sirgan/Downloads/CoreBackend/iam-service/iam-infrastructure/src/main/resources/mapper/OrganizationMapper.xml |
  OrganizationMapper | system_organization 表名与 deleted 字段 | L13-L27
- /Users/sirgan/Downloads/CoreBackend/iam-service/iam-infrastructure/src/main/resources/mapper/UserMapper.xml |
  UserMapper | system_user 表名与 attributes 字段 | L13-L31
- /Users/sirgan/Downloads/CoreBackend/iam-service/iam-infrastructure/src/main/resources/mapper/RoleMapper.xml |
  RoleMapper | system_role 表名 | L15-L21
- /Users/sirgan/Downloads/CoreBackend/iam-service/iam-infrastructure/src/main/resources/mapper/UserOrgMapper.xml |
  UserOrgMapper | system_user_org 表名与 identity_type | L16-L38
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-infrastructure/src/main/java/com/tenghe/corebackend/iam/infrastructure/persistence/repository/MyBatisOrganizationRepository.java |
encodeDescription/applyDescription | 组织 description 复合 JSON | L94-L131
-
/Users/sirgan/Downloads/CoreBackend/iam-service/iam-infrastructure/src/main/java/com/tenghe/corebackend/iam/infrastructure/persistence/repository/MyBatisUserRepository.java |
buildAttributes/applyAttributes | 用户 attributes JSON | L130-L158
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-infrastructure/src/main/resources/mapper/DeviceModelMapper.xml |
DeviceModelMapper | system_device_model 表名 | L11-L18
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-infrastructure/src/main/resources/mapper/DeviceModelPointMapper.xml |
DeviceModelPointMapper | system_device_model_point 表名 | L15-L22
- /Users/sirgan/Downloads/CoreBackend/device-service/device-infrastructure/src/main/resources/mapper/ProductMapper.xml |
  ProductMapper | system_product 表名 | L16-L23
- /Users/sirgan/Downloads/CoreBackend/device-service/device-infrastructure/src/main/resources/mapper/GatewayMapper.xml |
  GatewayMapper | system_gateway 表名 | L13-L19
- /Users/sirgan/Downloads/CoreBackend/device-service/device-infrastructure/src/main/resources/mapper/DeviceMapper.xml |
  DeviceMapper | system_device 表名与 dynamic_attributes | L18-L35
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-infrastructure/src/main/java/com/tenghe/corebackend/device/infrastructure/persistence/repository/MyBatisProductRepository.java |
toModel/toPo | protocolMapping JSON | L67-L101
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-infrastructure/src/main/java/com/tenghe/corebackend/device/infrastructure/persistence/repository/MyBatisDeviceRepository.java |
toModel/toPo | dynamicAttributes JSON | L93-L124
-
/Users/sirgan/Downloads/CoreBackend/device-service/device-infrastructure/src/main/java/com/tenghe/corebackend/device/infrastructure/persistence/repository/MyBatisDeviceModelRepository.java |
toPointModel/encodeEnumItems | enumItems JSON | L126-L170

## UNKNOWN/ASSUMED

- UNKNOWN：PO 未提供表名与 SQL 类型注解，无法生成 DDL。
- ASSUMED：表字段约束（长度、NOT NULL、索引）需以数据库实际 DDL 为准。
