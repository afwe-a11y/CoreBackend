---
description: 数据ddl存储规范
---

# [M-STORAGE] 通用-数据存储规范

## 启用条件

- ✅ 启用条件：涉及 MySQL 表设计、PO/Mapper/Repository、或查询性能要求
- ⛔ 不启用：不落库/不涉及持久化

## 规范内容

### MySQL 8.0 必有字段

以下所有字段均非业务字段，只是记录此行的时间和状态，以 BaseEntityPO 类存储，其余表继承此基类。

| 字段名         | 类型          | 是否必填 | 描述          |
|-------------|-------------|------|-------------|
| id          | bigint(20)  | 是    | 雪花ID        |
| created_by  | varchar(20) | 是    | 创建人ID       |
| updated_by  | varchar(20) | 是    | 创建时有值，修改人ID |
| create_time | datetime(3) | 是    | 仅创建时有值，无法更新 |
| update_time | datetime(3) | 是    | 创建时有值，每次随更新 |
| deleted     | tiny(1)     | 是    | 是否软删除，默认0   |
| version     | int         | 是    | 乐观锁版本号      |

### 数据库字段命名

- 所有关联字段结尾需要用 `id` 或 `code`，例如：`architectureTemplateCode`
