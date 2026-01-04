# AGENTS — BFF MODE (Backend-for-Frontend Implementation / Orchestration)

## How to use this file
- Use this file when asking Codex to **generate or modify a BFF microservice** for ONE specific application (Web/Mobile).
- This BFF **does NOT own source-of-truth domain data**. Core data & invariants live in Foundation/Mid-Platform services.
- BFF responsibilities: **orchestration**, **data shaping**, **view model building**, **application-specific rules**, **API contract for frontend**.
- If something is not confirmed by spec/code, DO NOT invent facts:
  - Use `UNKNOWN` if cannot confirm.
  - Use `ASSUMED` only when necessary, and include basis + risk.

---

## 0) Global Positioning (NON-NEGOTIABLE)

### 0.1 What we are building
- A **Middle-layer BFF** that is called by frontend clients (Web/Mobile).
- The BFF gets business data by calling **Foundation services** (HTTP/Feign, MQ events, etc.).
- The BFF is allowed to be **application-facing** (DTOs may be page-oriented), but must not “push UI coupling downward” into Foundation services.

### 0.2 What BFF must NOT do
- Must NOT directly read/write Foundation databases.
- Must NOT re-implement Foundation domain invariants if they already exist; prefer delegation.
- Must NOT leak frontend-specific concepts into Foundation service contracts (no “page state machine” requirements for downstream).
- Must NOT implement “distributed transactions” across services by DB-level coupling.

### 0.3 What BFF SHOULD do
- Provide frontend-friendly endpoints that:
  - Reduce round trips (aggregation)
  - Stabilize payloads (view models)
  - Normalize errors
  - Handle partial failures / fallbacks
  - Apply app-specific orchestration rules

---

## 1) BFF MODE Goals (non-negotiable)
1) Build **frontend-facing API contract** (OpenAPI) for the target application.
2) Implement **orchestration** across Foundation services:
  - fan-out / fan-in
  - join/merge
  - enrichment
  - batch queries
3) Implement **data shaping**:
  - view models
  - field normalization (naming, enums)
  - pagination strategy suitable for UI
4) Implement **application-specific rules** that do not belong to generic Foundation services.
5) Ensure **observability & operability**: tracing, metrics, structured logs, audit events (project conventions).
6) Ensure **backward compatibility** for frontend API evolution (versioning strategy below).

---

## 2) Engineering Architecture (DDD-lite + Hexagonal, 7 modules)

### 2.1 Modules
Use the same 7-module structure:
- `model`: BFF-level business types (view concepts, app-specific enums/VO)
- `api`: frontend contract DTOs + OpenAPI + client-facing error model
- `controller`: HTTP endpoints (and optional consumer/schedule if needed)
- `application`: orchestration use-cases, transactions boundary at BFF level
- `interfaces`: ports for downstream calls (Foundation service clients, cache, file storage, etc.)
- `infrastructure`: adapters implementing ports (Feign/HTTP clients, Redis, MQ, persistence if any)
- `main`: Spring Boot entry + wiring

### 2.2 Dependency rules (MUST)
- `controller` → `application` → `interfaces` → `infrastructure`
- `api` depends on `model`
- `application` depends on `model` + `interfaces`
- Forbidden:
  - `controller` must NOT depend on `infrastructure`
  - `application` must NOT depend on `infrastructure`
  - `infrastructure` must NOT depend on `api/controller/main/application`
  - `model` must NOT depend on higher layers

### 2.3 Layer constraints (BFF-adjusted)
- `controller`: no orchestration logic; only request parsing, auth context extraction, DTO↔Command mapping, response mapping.
- `application`: owns orchestration logic, error normalization, fallback decisions, concurrency strategy, and composition rules.
- `interfaces`: define downstream ports and “Port DTO” (anti-corruption layer).
- `infrastructure`: implement ports via Feign/HTTP, MQ, Redis, DB, etc. No usage of `api` DTO here.

---

## 3) Downstream Integration Rules (Foundation services)

### 3.1 Downstream contracts
- Treat each Foundation service as an external dependency with a stable contract:
  - Base URL / service name
  - Auth mechanism (service token / mTLS / gateway)
  - Timeout / retry / circuit breaker policy
  - Error model mapping

### 3.2 Anti-Corruption Layer (MUST)
- Never expose downstream DTOs directly to frontend.
- Map downstream responses into BFF `interfaces` Port DTOs, then into BFF `api` DTOs.
- Keep mapping logic testable and deterministic.

### 3.3 Orchestration patterns (recommended)
- **Aggregation endpoint**: call multiple services in parallel, merge results.
- **Enrichment**: list from service A, batch load details from service B, join by key.
- **Command + query**: write through a single Foundation service, then query to build UI view model.
- **Fallback**: if enrichment fails, degrade gracefully with partial data (if allowed by spec).

### 3.4 Consistency & transactions
- BFF should avoid cross-service strong consistency.
- If a multi-step flow is required:
  - Prefer saga-like orchestration with compensation where feasible.
  - Provide clear idempotency strategy.

---

## 4) Security & Context Propagation (BFF-specific)

### 4.1 Frontend auth
- Default: stateless token (JWT/OIDC) from frontend.
- Session/cookie is allowed only if the active spec explicitly requires.

### 4.2 Context propagation to Foundation
- Always propagate:
  - `user_id` (or subject)
  - `org_id/tenant_id` if applicable
  - trace id / correlation id
- Decide one model:
  - “On-behalf-of” (pass user context to downstream)
  - “BFF as trusted caller” (service token + explicit headers)
- Do NOT invent; mark `UNKNOWN` if not specified.

### 4.3 Permission handling
- BFF can do UI-level permission shaping (e.g., menu tree building),
  but must not become the source of truth for permission decisions if IAM exists.

---

## 5) Error Model & Compatibility

### 5.1 Unified error response
- Define a single BFF error envelope in `api`:
  - `code` (stable)
  - `message` (human-readable)
  - `details` (optional)
  - `traceId`
- Map downstream errors to BFF codes:
  - Preserve meaningful downstream error codes in `details.downstreamCode` where useful.
  - Never leak internal stack traces.

### 5.2 Backward compatibility
- Prefer additive changes:
  - add fields, new endpoints
  - avoid breaking renames/removals
- If breaking change is unavoidable:
  - introduce versioned route (`/v2/...`) or content-negotiation (if supported)
  - document migration plan

---

## 6) Performance Rules

### 6.1 N+1 avoidance (MUST)
- If a list requires enrichment:
  - use downstream batch endpoints
  - or create a BFF-side batching strategy (within a request)
- Concurrency:
  - parallelize fan-out with bounded concurrency
  - set strict timeouts per downstream call

### 6.2 Caching
- Allowed: BFF can cache derived/read-only data (e.g., dictionaries, menu trees).
- Must define:
  - cache key strategy
  - TTL
  - invalidation trigger (event or time-based)
- Must NOT cache sensitive data without explicit spec.

---

## 7) Business Spec Switchboard (focus-only execution)

> Codex must only implement the currently active spec. Other specs remain untouched unless needed for compilation.

ACTIVE_APP: <NEW_APP>
ACTIVE_SPEC: <USER_SPEC>

### Spec Library (paste feature specs here)

#### USER SPEC

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
  - If user logs in using a system-generated initial password, backend must force **reset password** flow (backend must be able to detect/flag “initial password state”).
- Disabled-account interception (global):
  - If an already-logged-in user becomes disabled by admin, then on any refresh/navigation/API call, backend must return a recognizable error so the client can clear session and redirect to login.

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
  - If an organization is removed from the user, backend must automatically remove all role assignments under that organization (consistency guarantee).
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
- If a permission being removed is already assigned to any role under this application, backend should **block** the removal and return a clear message (or implement a well-defined cleanup strategy if your policy allows).

#### 3.5.2 Delete application
- If the application has any associated roles, deletion must be **blocked** (must remove roles first).
- Deletion must be **soft delete**.

### 3.6 Permission management

#### 3.6.1 Read + status toggle
- Permission tree is readable; only provide enable/disable toggle.

#### 3.6.2 Enable/disable permission
- Toggling updates permission status.
- If disabling a **parent menu**, backend must cascade disable to all child menus/buttons (transactional).

* Lists: Default page size 10, support keyword fuzzy search.

---

## Output format (CODE MODE)
- Prefer a **git-style patch** or a **file-by-file change list**.
- For each file: show path, then a code block with the full changed content or a minimal diff.
- If database changes are required, output a **migration SQL file** separately (do not guess schema; follow the constraints below).

---

## Global business invariants (MUST)
- **Soft delete only**: All delete/removal operations across this project MUST be **logical deletion** (soft delete). Never physically remove records.
- **User email uniqueness**: Email is a supported login identifier; therefore **email MUST be globally unique** across all users.
- **User email required on creation**: When creating a user account, `email` is required (and unique). Phone is optional.


---

## 8) Output format (BFF CODE MODE)
- Prefer **git-style patch** or **file-by-file change list**.
- For each file: show path, then code block with full changed content or minimal diff.
- If new endpoints are added/changed:
  - update `API.md` (frontend-facing contract)
  - update `upgrade.log` if change is substantial
- Do not generate large “doc pack” unless explicitly requested.

---

## 9) Quality Gates (MUST)
- No invented dependencies, endpoints, or fields.
- Controller has no orchestration logic.
- Downstream clients sit behind ports (interfaces).
- Deterministic error mapping.
- Orchestration is unit-testable with mocked downstream ports.
- Timeouts and fallbacks are explicit.

---

## 10) Defaults (ASSUMED unless specified)
- Java 17 + Spring Boot
- Feign/HTTP client for service calls
- OpenAPI for API contract
- Redis optional for caching
- Tracing via standard correlation id headers
