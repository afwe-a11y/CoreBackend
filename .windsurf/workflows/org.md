---
description: org prompt
---

## 0) Domain Definitions

### Organization (组织)
- “Organization” is a **data boundary**. When created/updated, define which **Applications (可使用应用)** it can use.
- An Organization may have **0..N usable applications** (it can exist structurally even with no apps).

### Member types
- **Internal member (内部成员)**: a user account that belongs to the organization (can be created/edited/deleted here).
- **External member (外部成员)**: a user account that belongs to another organization, but is **associated** to the current organization for access (can be associated/removed here).

### Organization Admin (组织管理员)
- A “primary admin” displayed on organization list as the organization’s “管理员”.
- Admin assignment is done by selecting an **existing platform user** and granting them the org’s “组织管理员” role.

---

## 1) Data Model Requirements (Backend)

### 1.1 Organization fields
Store at minimum:
- `id`: snowflake style **19–21 digits**, unique, system generated
- `name` (组织名称): required, 1–50 chars, **unique globally across all statuses**
- `code` (组织编码): required, **unique globally**, allowed chars: **letters/digits/underscore only**
- `description` (描述): optional, max 200 chars on create; max 400 chars on edit
- `status`: enum {`NORMAL`(正常), `DISABLED`(停用)}; default `NORMAL`
- `created_at`: date/time; list display needs `YYYY-MM-DD`
- `primary_admin_display`: the admin name shown in list (<=20 chars, english letters + digits)
- Contact (联系人) fields (optional, max 1 contact):
  - `contact_name`: optional, allow mixed chars
  - `contact_phone`: optional, must be 11 digits format
  - `contact_email`: optional, must be valid email format

### 1.2 Organization ⇄ Application
- Maintain mapping: Organization has 0..N usable apps (by app IDs).
- Update must support add/remove apps.
- **Important risk rule**: if removing an app that users are already authorized for, backend must handle cleanup (typically remove related role grants for that app).

### 1.3 Users & Membership (minimum needed)
User attributes touched by this module:
- `username`: <=20 chars, **english letters + digits only**, immutable after creation
- `name`: optional, <=20 chars, allow mixed chars
- `phone`: optional
- `email`: **required** (globally unique)
- `status`: {NORMAL, DISABLED}
- `account_type`: {管理端, 应用端}
- role bindings (see below)

Membership/association:
- Internal membership: user belongs to organization
- External association: user belongs to source org, but is linked to current org

### 1.4 Role selection payload (“关联角色”)
When creating an internal member:
- Field: `关联组织` = multiple org IDs (required)
- Field: `关联角色` = selected **App-Role combinations** (required)
- Role selector must be loaded using **Organization IDs** and only show roles under apps that those organizations have.

---

## 2) API Behaviors (derive endpoints from the flows)

> You can choose exact paths, but must implement the behaviors below.
> All list APIs must support pagination (page size default 10) where applicable.

### 2.1 Organization List
Behavior:
- Default sort: **created time desc**
- Pagination: **10 items per page**, return total count
- Search: fuzzy match on **name OR id**

Response fields needed by UI:
- `名称` (org name)
- `ID`
- `成员数(I)` internal member count (>=0)
- `成员数(O)` external member count (>=0)
- `管理员` (primary admin display)
- `状态` (正常/停用)
- `创建时间` (`YYYY-MM-DD`)

### 2.2 Create Organization
Input fields:
- `组织名称` (required): 1–50 chars, globally unique across all statuses
  - if duplicate: error message must be exactly: `该组织名称已被占用`
- `组织编码` (required): globally unique, regex `^[A-Za-z0-9_]+$`
- `可使用应用` (optional): list of app IDs, allow empty list
- `描述` (optional): max 200 chars

Behavior:
- generate `id` (snowflake 19–21 digits)
- default `status = NORMAL`
- persist org + org-app mappings

### 2.3 Assign Organization Admin (快捷入口)
Flow:
- Search existing users by keyword (fuzzy): username/email/phone
- Select a user → confirm → backend grants that user the org’s **“组织管理员”** role
- On success: refresh org list, show “分配成功”

### 2.4 Delete Organization
Confirm dialog shows org name + “拥有用户” count (backend must provide these values).
Backend deletion logic:
- Internal users under the org: **soft delete or soft delete** (pick one consistent strategy)
- External users: do **not** delete their accounts; **only unlink association** with the org
- On success: org list refresh + “删除成功”
- Same logic must be reused when deleting from org detail page

### 2.5 Organization Detail — Read & Update
Read returns:
- `名称`, `ID`, `创建时间`, `描述`, `可使用应用` (current apps), `状态`
- contact fields (max 1): `姓名`, `手机号`, `邮箱`

Update validations:
- `名称`: required, 1–50 chars
- `描述`: optional, max 400 chars
- `可使用应用`: optional list; support add/remove
  - removing apps must trigger backend handling of impacted user role grants for those apps (cleanup)
- `状态`: enum NORMAL/DISABLED
- contact:
  - `手机号` optional, must be 11 digits format if present
  - `邮箱` optional, must be valid email format if present

### 2.6 Members — Internal List
Return rows with:
- `用户名`, `手机`, `邮箱`, `角色`, `账号状态` (正常/停用)

Row operations:
- Edit → opens “修改内部成员”
- Disable → requires a second confirmation consistent with “用户管理” module (show at least: username/phone/email/account type/role); backend action = set status DISABLED
- Delete → “删除内部成员确认弹窗” → backend deletes user (physical or soft)

### 2.7 Create Internal Member
Input fields:
- `用户名` (required): <=20 chars, english letters + digits only
- `姓名` (optional): <=20 chars
- `邮箱`: **required** (globally unique)
- `手机号`: optional
- `关联组织` (required): multi-select org IDs
  - if org IDs change, the existing `关联角色` selections must be cleared (backend should reject mismatched roles if client fails to clear)
- `关联角色` (required): chosen via role selector
  - if no `关联组织`, selecting roles must be blocked (backend validation required)
  - selector data source: only apps owned by selected org IDs
- `状态` default = NORMAL

On success:
- return at least: created `用户名` and `手机号` (for success prompt “手机号验证码登录并激活账号”)

### 2.8 Update Internal Member (“修改内部成员”)
Rules:
- `用户名` is read-only (immutable)
- `状态` can be toggled (正常/停用)
- `姓名` optional <=20
- `邮箱` is required (globally unique); `手机号` is optional
- `账号类型` required: {管理端, 应用端}
  - if 管理端: set “管理端角色”
  - if 应用端: set “应用端角色”

### 2.9 External Members — List
Return rows with:
- `用户名`, `归属组织` (source org name), `手机`, `邮箱`

### 2.10 Link External Member (“关联外部成员”)
Two-step flow:
1) Search by username or account ID
2) Confirm and add

Validation rules:
- Can only add users who are **NOT** from the current organization
  - if user belongs to current org: reject with message “不可添加本组织成员”
- If user is already an external member (i.e., already associated as external), adding must be rejected and the message should include the org name they are already an external member of (per PRD text).

### 2.11 Unlink External Member (“移除外部成员”)
- Confirmation includes username/phone/source org
- Backend removes the association; user can no longer access this org
- On success: refresh external member list + “操作成功”

---

## 3) Non-UI Constraints & Consistency

### 3.1 Pagination defaults
- Org list: default page size 10, created time desc, return total count.
- Member lists: pagination behavior may match system standard; implement at least pagination support if list can grow.

### 3.2 Validation & Error handling (minimum)
- Enforce uniqueness at DB level for:
  - Organization `name` (unique across all statuses)
  - Organization `code`
- Enforce format constraints as described in section 1/2.
- For “组织名称” duplication error, message must be exactly: `该组织名称已被占用`.

### 3.3 Transactionality
- Deleting an organization must be transactional across:
  - org record
  - org-app mapping
  - internal users delete/soft delete
  - external associations unlink
- Updating org apps must be transactional with any required role-grant cleanup.