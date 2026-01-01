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
ACTIVE_SPEC: <DEVICE_SPEC>

### Spec Library (paste feature specs here)

#### DEVICE SPEC

## **0\) Domain Definitions**

### **Device Model (设备模型)**

* **Defines capabilities**: The blueprint for devices, defining standard attributes and telemetry points.
* **Inheritance**: Supports single-layer inheritance (Parent Model \-\> Child Model).
* **Identifier**: Globally unique, immutable after creation.

### **Product (产品)**

* **Communication Contract**: Defines how devices connect (Protocol, Access Mode).
* **Security Boundary**: Generates ProductKey and ProductSecret for authentication.
* **Immutability**: Key configurations are locked upon creation to prevent data parsing errors.

### **Gateway (网关)**

* **Physical Node**: Represents a physical gateway device (Edge Gateway) or Virtual Gateway.
* **Association**: Must belong to a Station and a Gateway Product.
* **Control**: Can be enabled/disabled; disabling stops data processing for all sub-devices.

### **Device (设备)**

* **Asset Instance**: The actual equipment generating data.
* **Dependencies**: Must define Product (what is it?) and Gateway (where is it connected?).
* **Keys**: Identified by DeviceKey (unique within product or globally).

## **1\) Data Model Requirements (Backend)**

### **1.1 Device Model Management**

Fields:

* identifier: 2-8 chars, english+digits, **globally unique**, immutable.
* name: max 50 chars, required.
* source: Enum {NEW, INHERIT}.
* parent\_model\_id: required if source=INHERIT. **Max inheritance depth \= 1**.
* points: List of Attribute/Telemetry definitions.
  * type: Attribute vs Telemetry.
  * data\_type: int, float, double, string, enum, bool, datetime.
  * identifier: Unique within model hierarchy.

### **1.2 Product Management**

Fields:

* name: max 50 chars.
* product\_key: Globally unique, system generated (or manual), **immutable**.
* product\_secret: max 30 chars, english/digits, **immutable**.
* device\_model\_id: **immutable** link to Device Model.
* access\_mode: Enum (e.g., General\_MQTT), **immutable**.
* protocol\_mapping: JSON/Map, maps Model Point ID \<-\> Physical Collection Name.

### **1.3 Gateway Management**

Fields:

* name: max 50 chars.
* type: Enum {EDGE, VIRTUAL}, **immutable**.
* sn: max 20 chars, **unique**.
* product\_id: Link to Gateway Product.
* station\_id: Link to Station (Power Plant).
* status: Enum {ONLINE, OFFLINE}.
* enabled: Boolean (Enable/Disable).

### **1.4 Device Management**

Fields:

* name: max 50 chars.
* product\_id: **immutable**.
* device\_key: max 30 chars, **immutable**.
* device\_secret: System generated.
* gateway\_id: Mutable link to Gateway.
* station\_id: Read-only, syncs with Gateway's station.
* dynamic\_attributes: JSON, stores values for Model-defined attributes.

## **2\) API Behaviors (derive endpoints from the flows)**

### **2.1 Device Model APIs**

* **Create**:
  * Validations: Identifier regex & uniqueness; Parent existence (if inherit).
  * Logic: Allow single-level inheritance only.
* **Delete**:
  * **Blocking Rule A**: Fail if used by any Product/Device.
  * **Blocking Rule B**: Fail if used as Parent by another Model.
  * Action: Physical delete if checks pass.
* **Point Management**:
  * Add/Edit Points: Validate data type compatibility; Enum requires items.
  * Import Points: Transactional "All-or-Nothing". Match by identifier (Update existing, Create new).

### **2.2 Product APIs**

* **Create**:
  * Generate unique ProductKey & Secret.
  * Persist immutable bindings (Model, Access Mode).
* **Delete**:
  * **Blocking Rule**: Fail if Count(Devices) \> 0\.
* **Update**:
  * Only allow modifying Name, Description.
  * **Protocol Mapping**: Allow editing mapping rules (collection names) for data parsing.

### **2.3 Gateway APIs**

* **List**: Support search by Name/SN.
* **Create/Edit**:
  * Validate SN uniqueness.
  * Type is immutable.
* **Delete**:
  * **Blocking Rule**: Fail if Gateway has ANY sub-devices (regardless of status).
* **Toggle Status (Enable/Disable)**:
  * If Disabled: Backend MUST stop processing upstream data from this gateway.

### **2.4 Device APIs**

* **List**: Filter by Product, Search by Name/ID. Show Status (Online/Offline).
* **Create**:
  * Auto-fill Station from Gateway.
  * Validate Dynamic Attributes against Device Model definitions.
* **Import Wizard**:
  * Step 1: Parse Excel.
  * Step 2: Preview & Duplicate Check (Logic: ID empty \-\> Create; ID exists \-\> Update).
  * Step 3: Write only valid records. Return success/fail counts.
* **Export**:
  * File name format: {Product}\_{Description}\_{Timestamp}.xlsx.
  * Scope: Respect current filters (not just current page).
* **Real-time Data**:
  * Query latest telemetry for device. Sort by update time desc.

## **3\) Non-UI Constraints & Consistency**

### **3.1 Immutability Rules**

* **Critical Fields**: Identifier (Model), ProductKey, DeviceKey, AccessMode, DeviceModel (in Product) CANNOT be changed after creation.
* **Reasoning**: Changing these breaks data parsing, historical data queries, and device connectivity.

### **3.2 Transactionality**

* **Import Operations**: Point import must be atomic (All-or-Nothing).
* **Cascading Logic**: Deleting entities must strictly follow Blocking Rules (No orphan records).

### **3.3 Data consistency**

* **Station Sync**: Device station\_id must always match gateway.station\_id.
* **Status Logic**: Gateway Online/Offline is inferred from heartbeat/SN; not manually set (except Enable/Disable toggle).

### **3.4 Pagination defaults**

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
