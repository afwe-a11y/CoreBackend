---
description: 用户权限
---

## 3) USER & PERMISSION MANAGEMENT (v25.01) — BACKEND SPEC

> Scope: Backend only. Ignore pure UI layout/styling. Preserve fixed Chinese error messages if specified.

### 3.0 Domain model (IAM)

#### Entities

- **User**: login principal.
- **Organization**: data boundary; a user can be associated with multiple organizations.
- **Application**: has an “included permissions set” chosen by admin.
- **Role**: belongs to an Application; aggregates a set of permissions.
- **Permission**: menu/button permissions; hierarchical (tree); has enabled/disabled status.

#### Core relations (implementation-oriented)

- User ↔ Organization: many-to-many (user can belong to multiple orgs).
- User ↔ Role: many-to-many, with org/app dimensions (recommended mapping key: `user_id, org_id, app_id, role_id`).
- Application ↔ Permission: many-to-many (the included permission subset for the app).
- Role ↔ Permission: many-to-many (must be a subset of the Role’s Application included-permissions).

### 3.1 Authentication / account security

#### 3.1.1 Login

- Login identifier: **username OR email OR phone** + password + captcha.
- Required fields validation: identifier, password, captcha must be present.
- Lock policy: **10 consecutive wrong password attempts** ⇒ lock account for **15 minutes**.
- Initial password enforcement:
    - If user logs in using a system-generated initial password, backend must force **reset password** flow (backend
      must be able to detect/flag “initial password state”).
- Disabled-account interception (global):
    - If an already-logged-in user becomes disabled by admin, then on any refresh/navigation/API call, backend must
      return a recognizable error so the client can clear session and redirect to login.

### 3.2 Reset password (email verification)

- Send email code:
    - Email is auto-filled from the user’s bound email; backend must verify it matches the stored email.
    - Code TTL: **5 minutes**.
    - Rate limit: after successful send, enforce **30 seconds cooldown** (anti-spam).
- Confirm reset:
    - Validate old password correct.
    - Validate email code correct and not expired.
    - New password rule: length **8–20**, and must contain at least **two** of: letters / digits / special characters.
    - On success: clear “initial password” flag (if any) and invalidate relevant sessions/tokens.

### 3.3 User management

#### 3.3.1 List/query (recommended)

Support filtering by:

- username or ID (username/real-name fuzzy; ID exact)
- status (all/normal/disabled)
- organizations (multi-select)
- roles

List fields at minimum:

- id, username, name, phone, email, associated organizations, roles, created_at, status.

#### 3.3.2 Create user

Business rules:

- `username`: required, ≤20, **globally unique**, letters+digits only (if already constrained by project).
- `email`: **required**, valid format, **globally unique**.
- `phone`: optional, valid format.
- `organizations`: required, multi-select.
- `roles`: required; role selector depends on selected organizations:
    - must select organizations first; roles must belong to apps that the selected organizations can use.
- `status`: default NORMAL.
- On success: system must deliver **initial password** to the user’s email.

#### 3.3.3 Update user

- `username` is immutable.
- Allow changing `email` (if allowed by your policy) but must remain globally unique.
- Organization changes:
    - If an organization is removed from the user, backend must automatically remove all role assignments under that
      organization (consistency guarantee).
- Role changes:
    - Saved role assignments must be a subset of the roles available under current organizations/apps.

#### 3.3.4 Enable/disable user

- Toggle user status.
- Disabled users must be globally blocked (see 3.1.1).

#### 3.3.5 Delete user

- MUST be **soft delete** only (logical deletion).

### 3.4 Role management

#### 3.4.1 Role constraints

- Roles are grouped by their owning application.
- Preset roles (e.g., “超级管理员”, “组织管理员”):
    - role name not editable.
    - status cannot be toggled.

#### 3.4.2 Create/update role

- `app_id`: required on create; typically immutable on update.
- `role_name`: required; **unique within the same application**.
- `role_code`: required; **globally unique**.
- `description`: optional.

#### 3.4.3 Configure role permissions

- Permission tree data source is NOT the global permissions set:
    - It must be the **included permissions subset** for the role’s application (from Application Management).
- Persist role-permission mappings on save.

#### 3.4.4 Delete role

- If the role is assigned to any user, deletion must be **blocked** (require removing members first).
- Otherwise allow deletion as **soft delete**.

#### 3.4.5 Role members (user-role binding)

- Support batch add/remove members for a role.
- “Visible range” / candidate users must be constrained by the operator’s permission scope (backend must enforce).

### 3.5 Application management

#### 3.5.1 Create/update application

- `app_name`: required.
- `app_code`: required; **globally unique**.
- `included_permissions`: required; must select at least 1 permission from the global permission set.
- `status`: enable/disable.
- Persist app-permission mappings.

Editing risk (removing permissions):

- If a permission being removed is already assigned to any role under this application, backend should **block** the
  removal and return a clear message (or implement a well-defined cleanup strategy if your policy allows).

#### 3.5.2 Delete application

- If the application has any associated roles, deletion must be **blocked** (must remove roles first).
- Deletion must be **soft delete**.

### 3.6 Permission management

#### 3.6.1 Read + status toggle

- Permission tree is readable; only provide enable/disable toggle.

#### 3.6.2 Enable/disable permission

- Toggling updates permission status.
- If disabling a **parent menu**, backend must cascade disable to all child menus/buttons (transactional).