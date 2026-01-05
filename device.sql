/*
  device-service (base microservice) - MySQL 8.0+ (InnoDB, utf8mb4)

  Tables:
  - system_device_model           : Device Model (single-layer inheritance)
  - system_device_model_point     : Model points (Attribute/Telemetry definitions)
  - system_product                : Product (device/gateway product), contains ProductKey/Secret, protocol mapping
  - system_gateway                : Gateway instances (edge/virtual)
  - system_device                 : Device instances (sub-devices), connected to gateway

  Principles aligned with base microservice:
  - soft delete via deleted
  - audit fields (created_by/updated_by/create_time/update_time)
  - optimistic lock via version
  - no physical foreign keys (cross-service refs)
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- Drop (child -> parent)
DROP TABLE IF EXISTS system_device;
DROP TABLE IF EXISTS system_gateway;
DROP TABLE IF EXISTS system_product;
DROP TABLE IF EXISTS system_device_model_point;
DROP TABLE IF EXISTS system_device_model;

-- =========================
-- 1) Device Model (设备模型)
-- =========================
CREATE TABLE system_device_model
(
    id              BIGINT UNSIGNED        NOT NULL COMMENT 'Device Model ID',
    identifier      VARCHAR(8)             NOT NULL COMMENT 'Globally unique identifier (2-8 chars, immutable)',
    name            VARCHAR(50)            NOT NULL COMMENT 'Model name (max 50)',
    source          ENUM ('NEW','INHERIT') NOT NULL DEFAULT 'NEW' COMMENT 'Model source',
    parent_model_id BIGINT UNSIGNED        NULL COMMENT 'Parent model id (required if INHERIT, depth<=1)',
    description     TEXT                   NULL COMMENT 'Description',

    created_by      BIGINT UNSIGNED        NULL COMMENT 'Creator user id (from IAM)',
    updated_by      BIGINT UNSIGNED        NULL COMMENT 'Updater user id (from IAM)',
    create_time     DATETIME               NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
    update_time     DATETIME               NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
    deleted         TINYINT                NOT NULL DEFAULT 0 COMMENT 'Soft delete: 0-no, 1-yes',
    version         INT                    NOT NULL DEFAULT 0 COMMENT 'Optimistic lock version',

    PRIMARY KEY (id),
    UNIQUE KEY uk_model_identifier_deleted (identifier, deleted),
    KEY idx_model_parent_deleted (parent_model_id, deleted),
    KEY idx_model_name (name)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='Device Model';

-- =========================
-- 1.1) Device Model Points (测点定义：属性/遥测)
-- =========================
CREATE TABLE system_device_model_point
(
    id              BIGINT UNSIGNED                                                 NOT NULL COMMENT 'Point ID',
    model_id        BIGINT UNSIGNED                                                 NOT NULL COMMENT 'Device model id',
    point_type      ENUM ('ATTRIBUTE','TELEMETRY')                                  NOT NULL COMMENT 'Point type',
    data_type       ENUM ('INT','FLOAT','DOUBLE','STRING','ENUM','BOOL','DATETIME') NOT NULL COMMENT 'Data type',
    identifier      VARCHAR(64)                                                     NOT NULL COMMENT 'Point identifier (unique within model hierarchy; enforced in service)',
    name            VARCHAR(128)                                                    NOT NULL COMMENT 'Point name',
    enum_items_json JSON                                                            NULL COMMENT 'Enum items when data_type=ENUM',
    sort_no         INT                                                             NOT NULL DEFAULT 0 COMMENT 'Sort number',
    description     TEXT                                                            NULL COMMENT 'Description',

    created_by      BIGINT UNSIGNED                                                 NULL COMMENT 'Creator user id (from IAM)',
    updated_by      BIGINT UNSIGNED                                                 NULL COMMENT 'Updater user id (from IAM)',
    create_time     DATETIME                                                        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
    update_time     DATETIME                                                        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
    deleted         TINYINT                                                         NOT NULL DEFAULT 0 COMMENT 'Soft delete: 0-no, 1-yes',
    version         INT                                                             NOT NULL DEFAULT 0 COMMENT 'Optimistic lock version',

    PRIMARY KEY (id),
    UNIQUE KEY uk_model_point_identifier_deleted (model_id, identifier, deleted),
    KEY idx_point_model_type (model_id, point_type),
    KEY idx_point_identifier (identifier)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='Device Model Points';

-- =========================
-- 2) Product (产品)
-- =========================
CREATE TABLE system_product
(
    id               BIGINT UNSIGNED            NOT NULL COMMENT 'Product ID',
    product_type     ENUM ('DEVICE','GATEWAY')  NOT NULL COMMENT 'Product type: DEVICE or GATEWAY',
    name             VARCHAR(50)                NOT NULL COMMENT 'Product name (max 50)',
    description      TEXT                       NULL COMMENT 'Description',

    product_key      VARCHAR(64)                NOT NULL COMMENT 'Globally unique ProductKey (immutable)',
    product_secret   VARCHAR(30)                NOT NULL COMMENT 'ProductSecret (immutable, max 30, english/digits)',
    device_model_id  BIGINT UNSIGNED            NOT NULL COMMENT 'Bound device_model_id (immutable)',
    access_mode      VARCHAR(64)                NOT NULL COMMENT 'Access mode enum (e.g., General_MQTT) (immutable)',
    protocol_mapping JSON                       NULL COMMENT 'Protocol mapping (Model Point ID <-> Physical collection name)',

    status           ENUM ('NORMAL','DISABLED') NOT NULL DEFAULT 'NORMAL' COMMENT 'Status',

    created_by       BIGINT UNSIGNED            NULL COMMENT 'Creator user id (from IAM)',
    updated_by       BIGINT UNSIGNED            NULL COMMENT 'Updater user id (from IAM)',
    create_time      DATETIME                   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
    update_time      DATETIME                   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
    deleted          TINYINT                    NOT NULL DEFAULT 0 COMMENT 'Soft delete: 0-no, 1-yes',
    version          INT                        NOT NULL DEFAULT 0 COMMENT 'Optimistic lock version',

    PRIMARY KEY (id),
    UNIQUE KEY uk_product_key_deleted (product_key, deleted),
    KEY idx_product_type_status (product_type, status),
    KEY idx_product_model (device_model_id),
    KEY idx_product_name (name)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='Product';

-- =========================
-- 3) Gateway (网关实例)
-- =========================
CREATE TABLE system_gateway
(
    id                  BIGINT UNSIGNED           NOT NULL COMMENT 'Gateway ID',
    name                VARCHAR(50)               NOT NULL COMMENT 'Gateway name (max 50)',
    type                ENUM ('EDGE','VIRTUAL')   NOT NULL COMMENT 'Gateway type (immutable)',
    sn                  VARCHAR(20)               NOT NULL COMMENT 'Gateway SN (unique, max 20)',
    product_id          BIGINT UNSIGNED           NOT NULL COMMENT 'Gateway product id (must be product_type=GATEWAY; enforced in service)',
    station_id          BIGINT UNSIGNED           NOT NULL COMMENT 'Station/Plant id (external reference)',
    status              ENUM ('ONLINE','OFFLINE') NOT NULL DEFAULT 'OFFLINE' COMMENT 'Online status (derived from heartbeat)',
    enabled             TINYINT                   NOT NULL DEFAULT 1 COMMENT 'Enable/Disable toggle (1-enabled,0-disabled)',
    last_heartbeat_time DATETIME                  NULL COMMENT 'Last heartbeat time (for ONLINE/OFFLINE inference)',

    created_by          BIGINT UNSIGNED           NULL COMMENT 'Creator user id (from IAM)',
    updated_by          BIGINT UNSIGNED           NULL COMMENT 'Updater user id (from IAM)',
    create_time         DATETIME                  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
    update_time         DATETIME                  NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
    deleted             TINYINT                   NOT NULL DEFAULT 0 COMMENT 'Soft delete: 0-no, 1-yes',
    version             INT                       NOT NULL DEFAULT 0 COMMENT 'Optimistic lock version',

    PRIMARY KEY (id),
    UNIQUE KEY uk_gateway_sn_deleted (sn, deleted),
    KEY idx_gateway_station (station_id),
    KEY idx_gateway_product (product_id),
    KEY idx_gateway_name (name),
    KEY idx_gateway_status_enabled (status, enabled)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='Gateway';

-- =========================
-- 4) Device (设备实例/子设备)
-- =========================
CREATE TABLE system_device
(
    id                  BIGINT UNSIGNED           NOT NULL COMMENT 'Device ID',
    name                VARCHAR(50)               NOT NULL COMMENT 'Device name (max 50)',
    product_id          BIGINT UNSIGNED           NOT NULL COMMENT 'Device product id (immutable; enforced in service)',
    device_key          VARCHAR(30)               NOT NULL COMMENT 'DeviceKey (immutable, max 30)',
    device_secret       VARCHAR(64)               NOT NULL COMMENT 'DeviceSecret (system generated)',
    gateway_id          BIGINT UNSIGNED           NOT NULL COMMENT 'Gateway id (mutable)',
    station_id          BIGINT UNSIGNED           NOT NULL COMMENT 'Station id (read-only; sync with gateway.station_id)',
    status              ENUM ('ONLINE','OFFLINE') NOT NULL DEFAULT 'OFFLINE' COMMENT 'Online status (derived from heartbeat)',
    last_heartbeat_time DATETIME                  NULL COMMENT 'Last heartbeat time (for ONLINE/OFFLINE inference)',
    dynamic_attributes  JSON                      NULL COMMENT 'Dynamic attributes values (based on model-defined attributes)',

    created_by          BIGINT UNSIGNED           NULL COMMENT 'Creator user id (from IAM)',
    updated_by          BIGINT UNSIGNED           NULL COMMENT 'Updater user id (from IAM)',
    create_time         DATETIME                  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
    update_time         DATETIME                  NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
    deleted             TINYINT                   NOT NULL DEFAULT 0 COMMENT 'Soft delete: 0-no, 1-yes',
    version             INT                       NOT NULL DEFAULT 0 COMMENT 'Optimistic lock version',

    PRIMARY KEY (id),
    UNIQUE KEY uk_device_product_key_deleted (product_id, device_key, deleted),
    KEY idx_device_gateway (gateway_id),
    KEY idx_device_station (station_id),
    KEY idx_device_product (product_id),
    KEY idx_device_name (name),
    KEY idx_device_status (status)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='Device';

SET FOREIGN_KEY_CHECKS = 1;
