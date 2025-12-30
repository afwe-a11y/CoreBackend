# 04-data-model.md

## 数据对象清单（PO/Entity）
### Organization
| 字段 | Java 类型 | 说明 | Evidence |
| --- | --- | --- | --- |
| id | Long | 组织 ID | iam-service/iam-model/src/main/java/com/tenghe/corebackend/model/Organization.java | Organization | L9-L20 |
| name | String | 组织名称 | iam-service/iam-model/src/main/java/com/tenghe/corebackend/model/Organization.java | Organization | L9-L20 |
| code | String | 组织编码 | iam-service/iam-model/src/main/java/com/tenghe/corebackend/model/Organization.java | Organization | L9-L20 |
| description | String | 描述 | iam-service/iam-model/src/main/java/com/tenghe/corebackend/model/Organization.java | Organization | L9-L20 |
| status | OrganizationStatus | 状态 | iam-service/iam-model/src/main/java/com/tenghe/corebackend/model/Organization.java | Organization | L9-L20 |
| createdAt | Instant | 创建时间 | iam-service/iam-model/src/main/java/com/tenghe/corebackend/model/Organization.java | Organization | L9-L20 |
| primaryAdminDisplay | String | 管理员显示名 | iam-service/iam-model/src/main/java/com/tenghe/corebackend/model/Organization.java | Organization | L9-L20 |
| contactName | String | 联系人姓名 | iam-service/iam-model/src/main/java/com/tenghe/corebackend/model/Organization.java | Organization | L9-L20 |
| contactPhone | String | 联系人手机 | iam-service/iam-model/src/main/java/com/tenghe/corebackend/model/Organization.java | Organization | L9-L20 |
| contactEmail | String | 联系人邮箱 | iam-service/iam-model/src/main/java/com/tenghe/corebackend/model/Organization.java | Organization | L9-L20 |
| deleted | boolean | 软删除标记 | iam-service/iam-model/src/main/java/com/tenghe/corebackend/model/Organization.java | Organization | L9-L20 |

### User
| 字段 | Java 类型 | 说明 | Evidence |
| --- | --- | --- | --- |
| id | Long | 用户 ID | iam-service/iam-model/src/main/java/com/tenghe/corebackend/model/User.java | User | L9-L19 |
| username | String | 用户名 | iam-service/iam-model/src/main/java/com/tenghe/corebackend/model/User.java | User | L9-L19 |
| name | String | 姓名 | iam-service/iam-model/src/main/java/com/tenghe/corebackend/model/User.java | User | L9-L19 |
| phone | String | 手机 | iam-service/iam-model/src/main/java/com/tenghe/corebackend/model/User.java | User | L9-L19 |
| email | String | 邮箱 | iam-service/iam-model/src/main/java/com/tenghe/corebackend/model/User.java | User | L9-L19 |
| status | UserStatus | 账号状态 | iam-service/iam-model/src/main/java/com/tenghe/corebackend/model/User.java | User | L9-L19 |
| accountType | AccountType | 账号类型 | iam-service/iam-model/src/main/java/com/tenghe/corebackend/model/User.java | User | L9-L19 |
| primaryOrgId | Long | 归属组织 ID | iam-service/iam-model/src/main/java/com/tenghe/corebackend/model/User.java | User | L9-L19 |
| createdAt | Instant | 创建时间 | iam-service/iam-model/src/main/java/com/tenghe/corebackend/model/User.java | User | L9-L19 |
| deleted | boolean | 软删除标记 | iam-service/iam-model/src/main/java/com/tenghe/corebackend/model/User.java | User | L9-L19 |

### RoleGrant
| 字段 | Java 类型 | 说明 | Evidence |
| --- | --- | --- | --- |
| id | Long | 授权 ID | iam-service/iam-model/src/main/java/com/tenghe/corebackend/model/RoleGrant.java | RoleGrant | L9-L17 |
| organizationId | Long | 组织 ID | iam-service/iam-model/src/main/java/com/tenghe/corebackend/model/RoleGrant.java | RoleGrant | L9-L17 |
| userId | Long | 用户 ID | iam-service/iam-model/src/main/java/com/tenghe/corebackend/model/RoleGrant.java | RoleGrant | L9-L17 |
| appId | Long | 应用 ID | iam-service/iam-model/src/main/java/com/tenghe/corebackend/model/RoleGrant.java | RoleGrant | L9-L17 |
| roleCode | String | 角色编码 | iam-service/iam-model/src/main/java/com/tenghe/corebackend/model/RoleGrant.java | RoleGrant | L9-L17 |
| roleCategory | RoleCategory | 角色分类 | iam-service/iam-model/src/main/java/com/tenghe/corebackend/model/RoleGrant.java | RoleGrant | L9-L17 |
| createdAt | Instant | 创建时间 | iam-service/iam-model/src/main/java/com/tenghe/corebackend/model/RoleGrant.java | RoleGrant | L9-L17 |
| deleted | boolean | 软删除标记 | iam-service/iam-model/src/main/java/com/tenghe/corebackend/model/RoleGrant.java | RoleGrant | L9-L17 |

### OrganizationApp
| 字段 | Java 类型 | 说明 | Evidence |
| --- | --- | --- | --- |
| organizationId | Long | 组织 ID | iam-service/iam-model/src/main/java/com/tenghe/corebackend/model/OrganizationApp.java | OrganizationApp | L8-L11 |
| appId | Long | 应用 ID | iam-service/iam-model/src/main/java/com/tenghe/corebackend/model/OrganizationApp.java | OrganizationApp | L8-L11 |
| deleted | boolean | 软删除标记 | iam-service/iam-model/src/main/java/com/tenghe/corebackend/model/OrganizationApp.java | OrganizationApp | L8-L11 |

### OrgMembership
| 字段 | Java 类型 | 说明 | Evidence |
| --- | --- | --- | --- |
| organizationId | Long | 组织 ID | iam-service/iam-model/src/main/java/com/tenghe/corebackend/model/OrgMembership.java | OrgMembership | L9-L13 |
| userId | Long | 用户 ID | iam-service/iam-model/src/main/java/com/tenghe/corebackend/model/OrgMembership.java | OrgMembership | L9-L13 |
| createdAt | Instant | 创建时间 | iam-service/iam-model/src/main/java/com/tenghe/corebackend/model/OrgMembership.java | OrgMembership | L9-L13 |
| deleted | boolean | 软删除标记 | iam-service/iam-model/src/main/java/com/tenghe/corebackend/model/OrgMembership.java | OrgMembership | L9-L13 |

### ExternalMembership
| 字段 | Java 类型 | 说明 | Evidence |
| --- | --- | --- | --- |
| organizationId | Long | 组织 ID | iam-service/iam-model/src/main/java/com/tenghe/corebackend/model/ExternalMembership.java | ExternalMembership | L9-L14 |
| userId | Long | 用户 ID | iam-service/iam-model/src/main/java/com/tenghe/corebackend/model/ExternalMembership.java | ExternalMembership | L9-L14 |
| sourceOrganizationId | Long | 来源组织 ID | iam-service/iam-model/src/main/java/com/tenghe/corebackend/model/ExternalMembership.java | ExternalMembership | L9-L14 |
| createdAt | Instant | 创建时间 | iam-service/iam-model/src/main/java/com/tenghe/corebackend/model/ExternalMembership.java | ExternalMembership | L9-L14 |
| deleted | boolean | 软删除标记 | iam-service/iam-model/src/main/java/com/tenghe/corebackend/model/ExternalMembership.java | ExternalMembership | L9-L14 |

## DDL 生成说明
- 未生成 `10-ddl-reference.sql`：PO 未见表名映射与列 SQL 类型声明，无法满足 PO-only DDL 规则。
- 未发现 PO 继承关系，继承字段展开不适用。

## Evidence
- iam-service/iam-model/src/main/java/com/tenghe/corebackend/model/Organization.java | Organization | PO 字段 | L1-L20
- iam-service/iam-model/src/main/java/com/tenghe/corebackend/model/User.java | User | PO 字段 | L1-L19
- iam-service/iam-model/src/main/java/com/tenghe/corebackend/model/RoleGrant.java | RoleGrant | PO 字段 | L1-L17
- iam-service/iam-model/src/main/java/com/tenghe/corebackend/model/OrganizationApp.java | OrganizationApp | PO 字段 | L1-L11
- iam-service/iam-model/src/main/java/com/tenghe/corebackend/model/OrgMembership.java | OrgMembership | PO 字段 | L1-L13
- iam-service/iam-model/src/main/java/com/tenghe/corebackend/model/ExternalMembership.java | ExternalMembership | PO 字段 | L1-L14

## UNKNOWN/ASSUMED
- 表名映射 UNKNOWN：PO 未见 @Table/@TableName 等注解。
- 字段 SQL 类型 UNKNOWN：PO 未见显式列类型或类型映射规则。
- 物理库表索引/外键/约束 UNKNOWN。
