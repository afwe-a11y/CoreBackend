/*
  V1__init_user_permission (v16_multi_org_collab)
  目标数据库：MySQL 8.0+ (InnoDB, utf8mb4)

  核心业务逻辑适配：
  1. 多组织架构：支持主子组织层级。
  2. 跨组织协作：引入 system_user_org 表，区分 INTERNAL(内部) 和 EXTERNAL(外部) 身份。
  3. 应用与角色：角色依附于具体应用实例，权限跟随角色。
  4. 数据隔离：通过 system_user_role 中的 org_id 确定用户在特定组织下的数据视野。

  注意：
  - 已移除所有物理外键约束。
  - 状态字段全量采用 ENUM。
  - 保留了扩展字段 asset_scope 和 attributes。
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 清理旧表
DROP TABLE IF EXISTS sys_login_log;
DROP TABLE IF EXISTS system_login_log;
DROP TABLE IF EXISTS sys_user_role;
DROP TABLE IF EXISTS system_user_role;
DROP TABLE IF EXISTS sys_role_permission;
DROP TABLE IF EXISTS system_role_permission;
DROP TABLE IF EXISTS sys_permission;
DROP TABLE IF EXISTS sys_role;
DROP TABLE IF EXISTS system_role;
DROP TABLE IF EXISTS sys_application;
DROP TABLE IF EXISTS system_application;
DROP TABLE IF EXISTS system_application_permission;
DROP TABLE IF EXISTS sys_template_permission;
DROP TABLE IF EXISTS system_template_permission;
DROP TABLE IF EXISTS sys_application_template;
DROP TABLE IF EXISTS system_application_template;
DROP TABLE IF EXISTS sys_user_org; -- 新增表清理
DROP TABLE IF EXISTS system_user_org; -- 新增表清理
DROP TABLE IF EXISTS sys_user;
DROP TABLE IF EXISTS system_user;
DROP TABLE IF EXISTS sys_organization;
DROP TABLE IF EXISTS system_organization;

-- =========================
-- 1) 组织（数据隔离边界）
-- =========================
CREATE TABLE system_organization
(
    id          BIGINT UNSIGNED             NOT NULL COMMENT '组织ID（雪花算法）',
    parent_id   BIGINT UNSIGNED             NULL COMMENT '父组织ID（顶级组织为空，支持组织树）',
    org_code    VARCHAR(64)                 NOT NULL COMMENT '组织编码（系统唯一标识）',
    org_name    VARCHAR(128)                NOT NULL COMMENT '组织名称',
    org_path    VARCHAR(512)                NULL COMMENT '组织路径（如 0/1/5/，用于快速检索子组织数据）',
    level_no    INT                         NOT NULL DEFAULT 1 COMMENT '层级深度（顶级=1）',
    sort_no     INT                         NOT NULL DEFAULT 0 COMMENT '排序号',
    status      ENUM ('NORMAL', 'DISABLED') NOT NULL DEFAULT 'NORMAL' COMMENT '状态：NORMAL-正常，DISABLED-禁用',
    tenant_code VARCHAR(64)                 NULL COMMENT '租户编码（预留：若未来做SaaS多租户隔离可使用）',
    description TEXT                        NULL COMMENT '组织描述',
    created_by  BIGINT UNSIGNED             NULL COMMENT '创建人ID',
    updated_by  BIGINT UNSIGNED             NULL COMMENT '更新人ID',
    create_time DATETIME                    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME                    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT                     NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    version     INT                         NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    PRIMARY KEY (id),
    UNIQUE KEY uk_org_code_deleted (org_code, deleted),
    KEY idx_parent_deleted (parent_id, deleted),
    KEY idx_org_path (org_path(255)) COMMENT '树形结构查询优化'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='系统组织表（数据隔离的基本单位）';

-- =========================
-- 2) 用户（全平台唯一账号）
-- =========================
CREATE TABLE system_user
(
    id              BIGINT UNSIGNED                       NOT NULL COMMENT '用户ID（雪花算法）',
    home_org_id     BIGINT UNSIGNED                       NOT NULL COMMENT '主归属组织ID（账号的“娘家”，对应内部员工身份）',
    username        VARCHAR(64)                           NOT NULL COMMENT '用户名（全平台唯一）',
    email           VARCHAR(128)                          NULL COMMENT '邮箱（全平台唯一）',
    phone           VARCHAR(32)                           NULL COMMENT '手机号（全平台唯一）',
    password_hash   VARCHAR(255)                          NOT NULL COMMENT '密码哈希值',
    nickname        VARCHAR(64)                           NULL COMMENT '用户昵称',
    avatar_url      VARCHAR(255)                          NULL COMMENT '头像',
    status          ENUM ('NORMAL', 'DISABLED', 'LOCKED') NOT NULL DEFAULT 'NORMAL' COMMENT '状态：NORMAL-正常，DISABLED-禁用，LOCKED-锁定',
    last_login_time DATETIME                              NULL COMMENT '最后登录时间',
    last_login_ip   VARCHAR(64)                           NULL COMMENT '最后登录IP',
    attributes      JSON                                  NULL COMMENT '扩展属性（JSON）',
    created_by      BIGINT UNSIGNED                       NULL COMMENT '创建人ID',
    updated_by      BIGINT UNSIGNED                       NULL COMMENT '更新人ID',
    create_time     DATETIME                              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME                              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT                               NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    version         INT                                   NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username_deleted (username, deleted),
    UNIQUE KEY uk_email_deleted (email, deleted),
    UNIQUE KEY uk_phone_deleted (phone, deleted),
    KEY idx_home_org_deleted (home_org_id, deleted)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='系统用户表（全平台唯一账号池）';

-- =========================
-- 3) 用户-组织关系（多组织协作）
-- =========================
CREATE TABLE system_user_org
(
    id            BIGINT UNSIGNED                        NOT NULL COMMENT 'ID',
    user_id       BIGINT UNSIGNED                        NOT NULL COMMENT '用户ID',
    org_id        BIGINT UNSIGNED                        NOT NULL COMMENT '组织ID（被协作/被加入的组织）',
    identity_type ENUM ('INTERNAL', 'EXTERNAL')          NOT NULL DEFAULT 'INTERNAL' COMMENT '身份类型：INTERNAL-内部员工，EXTERNAL-外部协作者',
    status        ENUM ('NORMAL', 'DISABLED', 'PENDING') NOT NULL DEFAULT 'NORMAL' COMMENT '关联状态：NORMAL-正常，DISABLED-冻结，PENDING-邀请中',
    join_time     DATETIME                               NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    expired_time  DATETIME                               NULL COMMENT '授权过期时间（空代表永久）',
    created_by    BIGINT UNSIGNED                        NULL COMMENT '创建人ID（邀请人）',
    create_time   DATETIME                               NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted       TINYINT                                NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_org_deleted (user_id, org_id, deleted) COMMENT '一个用户在一个组织下只有一条关联记录',
    KEY idx_org_identity (org_id, identity_type) COMMENT '查询某组织的全部外部人员'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='用户-组织关联表（定义用户在各组织的身份）';

-- =========================
-- 4) 应用模板（功能基线）
-- =========================
CREATE TABLE system_application_template
(
    id            BIGINT UNSIGNED             NOT NULL COMMENT '模板ID',
    template_code VARCHAR(64)                 NOT NULL COMMENT '模板编码（如 PMS_SOLAR）',
    template_name VARCHAR(128)                NOT NULL COMMENT '模板名称（如 光伏电站管理系统）',
    description   TEXT                        NULL COMMENT '功能描述',
    status        ENUM ('NORMAL', 'DISABLED') NOT NULL DEFAULT 'NORMAL' COMMENT '状态：NORMAL-正常，DISABLED-下架',
    created_by    BIGINT UNSIGNED             NULL COMMENT '创建人ID',
    create_time   DATETIME                    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time   DATETIME                    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted       TINYINT                     NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    version       INT                         NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    PRIMARY KEY (id),
    UNIQUE KEY uk_tpl_code_deleted (template_code, deleted)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='应用模板表（定义标准应用能力）';

CREATE TABLE system_template_permission
(
    id          BIGINT UNSIGNED                NOT NULL COMMENT '模板权限ID',
    template_id BIGINT UNSIGNED                NOT NULL COMMENT '所属模板ID',
    perm_code   VARCHAR(128)                   NOT NULL COMMENT '权限标识（如 solar:station:view）',
    perm_name   VARCHAR(128)                   NOT NULL COMMENT '权限名称',
    perm_type   ENUM ('MENU', 'BUTTON', 'API') NOT NULL COMMENT '权限类型：MENU-菜单，BUTTON-按钮，API-接口',
    status      ENUM ('ENABLED', 'DISABLED')   NOT NULL DEFAULT 'ENABLED' COMMENT '状态：ENABLED-启用，DISABLED-禁用',
    parent_code VARCHAR(128)                   NULL COMMENT '父权限标识（用于构建树）',
    sort_no     INT                            NOT NULL DEFAULT 0 COMMENT '排序号',
    path        VARCHAR(255)                   NULL COMMENT '前端路由/后端接口路径',
    component   VARCHAR(255)                   NULL COMMENT '前端组件路径',
    icon        VARCHAR(128)                   NULL COMMENT '图标',
    created_by  BIGINT UNSIGNED                NULL COMMENT '创建人ID',
    create_time DATETIME                       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted     TINYINT                        NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_tpl_perm_code (template_id, perm_code, deleted)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='模板权限点位表（应用的全部功能集合）';

-- =========================
-- 5) 应用实例（组织购买/开通的应用）
-- =========================
CREATE TABLE system_application
(
    id            BIGINT UNSIGNED                        NOT NULL COMMENT '应用实例ID',
    org_id        BIGINT UNSIGNED                        NOT NULL COMMENT '所属组织ID（谁的数据）',
    template_id   BIGINT UNSIGNED                        NOT NULL COMMENT '基于哪个模板创建',
    app_code      VARCHAR(64)                            NOT NULL COMMENT '应用实例编码（自动生成或指定）',
    app_name      VARCHAR(128)                           NOT NULL COMMENT '应用显示名称（可自定义）',
    status        ENUM ('NORMAL', 'DISABLED', 'EXPIRED') NOT NULL DEFAULT 'NORMAL' COMMENT '状态：NORMAL-正常，DISABLED-禁用，EXPIRED-过期',
    owner_user_id BIGINT UNSIGNED                        NULL COMMENT '应用负责人ID（通常是该组织的管理员）',
    expire_time   DATETIME                               NULL COMMENT '应用有效期（SaaS场景使用）',
    config_json   JSON                                   NULL COMMENT '应用级配置（JSON格式）',
    create_time   DATETIME                               NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time   DATETIME                               NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted       TINYINT                                NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    version       INT                                    NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    PRIMARY KEY (id),
    UNIQUE KEY uk_org_app_code_deleted (org_id, app_code, deleted) COMMENT '同一组织下应用编码唯一',
    KEY idx_org_status (org_id, status)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='应用实例表（组织开通的具体业务系统）';

-- =========================
-- 5.1) 应用-权限包含集（Application included permissions set）
-- =========================
CREATE TABLE system_application_permission
(
    id          BIGINT UNSIGNED              NOT NULL COMMENT '关联ID',
    app_id      BIGINT UNSIGNED              NOT NULL COMMENT '应用实例ID',
    perm_code   VARCHAR(128)                 NOT NULL COMMENT '权限标识（来源模板 perm_code）',
    status      ENUM ('ENABLED', 'DISABLED') NOT NULL DEFAULT 'ENABLED' COMMENT '状态：ENABLED-启用，DISABLED-禁用',
    created_by  BIGINT UNSIGNED              NULL COMMENT '创建人ID',
    create_time DATETIME                     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME                     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT                      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    version     INT                          NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    PRIMARY KEY (id),
    UNIQUE KEY uk_app_perm_deleted (app_id, perm_code, deleted),
    KEY idx_perm_code (perm_code)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='应用-权限包含集（Application included permissions set）';

-- =========================
-- 6) 角色与权限（应用内隔离）
-- =========================
CREATE TABLE system_role
(
    id              BIGINT UNSIGNED                                      NOT NULL COMMENT '角色ID',
    app_id          BIGINT UNSIGNED                                      NOT NULL COMMENT '所属应用实例ID',
    role_code       VARCHAR(64)                                          NOT NULL COMMENT '角色编码（应用内唯一，如 ADMIN）',
    role_name       VARCHAR(128)                                         NOT NULL COMMENT '角色名称',
    role_type       ENUM ('SYSTEM', 'CUSTOM')                            NOT NULL DEFAULT 'CUSTOM' COMMENT '角色类型：SYSTEM-系统内置不可删，CUSTOM-自定义',
    description     TEXT                                                 NULL COMMENT '角色描述',
    data_scope_type ENUM ('ALL', 'ORG', 'ORG_AND_SUB', 'SELF', 'CUSTOM') NOT NULL DEFAULT 'SELF' COMMENT '数据权限范围预留：ALL-全部，ORG-本组织，SELF-仅本人',
    create_time     DATETIME                                             NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME                                             NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT                                              NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    version         INT                                                  NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    PRIMARY KEY (id),
    UNIQUE KEY uk_app_role_code_deleted (app_id, role_code, deleted)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='应用角色表（定义应用内的职能）';

CREATE TABLE system_role_permission
(
    id          BIGINT UNSIGNED NOT NULL COMMENT '关联ID',
    app_id      BIGINT UNSIGNED NOT NULL COMMENT '应用ID（冗余字段，加速查询）',
    role_id     BIGINT UNSIGNED NOT NULL COMMENT '角色ID',
    perm_code   VARCHAR(128)    NOT NULL COMMENT '权限标识（来源于模板）',
    create_time DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted     TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_perm (role_id, perm_code, deleted),
    KEY idx_app_perm (app_id, perm_code)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='角色-权限关联表';

-- =========================
-- 7) 用户授权（核心：连接用户、组织、应用、角色）
-- =========================
CREATE TABLE system_user_role
(
    id          BIGINT UNSIGNED NOT NULL COMMENT 'ID',
    user_id     BIGINT UNSIGNED NOT NULL COMMENT '用户ID（可能来自其他组织的外部用户）',
    org_id      BIGINT UNSIGNED NOT NULL COMMENT '场景组织ID（用户在哪个组织下干活，实现数据隔离）',
    app_id      BIGINT UNSIGNED NOT NULL COMMENT '应用实例ID（属于该场景组织的应用）',
    role_id     BIGINT UNSIGNED NOT NULL COMMENT '角色ID（该应用下的角色）',
    asset_scope JSON            NULL COMMENT '资产数据权限（扩展：如指定只能看 org_id 下的某几个电站ID列表）',
    create_time DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '授权时间',
    deleted     TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    version     INT             NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    PRIMARY KEY (id),
    -- 核心唯一索引：一个用户在同一个组织下的同一个应用中，通常只分配一个角色（或者多个，看业务，这里假设多角色允许，只做防重）
    -- 如果业务允许用户在一个应用有多个角色，去掉 role_id 即可，改为 uk_user_org_app_role
    UNIQUE KEY uk_user_context_role (user_id, org_id, app_id, role_id, deleted),
    KEY idx_user_org (user_id, org_id) COMMENT '查询用户在某组织下的所有授权'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='用户-角色授权表（决定了“谁”在“哪个组织”能操作“哪个应用”的“什么功能”）';

-- =========================
-- 8) 登录日志
-- =========================
CREATE TABLE system_login_log
(
    id            BIGINT UNSIGNED                       NOT NULL COMMENT '日志ID',
    user_id       BIGINT UNSIGNED                       NOT NULL COMMENT '用户ID',
    username      VARCHAR(64)                           NULL COMMENT '登录账号（冗余）',
    login_portal  ENUM ('ADMIN_PLATFORM', 'APP_CLIENT') NOT NULL COMMENT '登录入口：ADMIN_PLATFORM-管理端，APP_CLIENT-应用端',
    target_org_id BIGINT UNSIGNED                       NULL COMMENT '登录的目标组织ID（如果是切换组织操作）',
    target_app_id BIGINT UNSIGNED                       NULL COMMENT '登录的应用ID（如果是登录具体应用）',
    login_ip      VARCHAR(64)                           NULL COMMENT 'IP地址',
    location      VARCHAR(128)                          NULL COMMENT 'IP归属地',
    browser       VARCHAR(128)                          NULL COMMENT '浏览器信息',
    os            VARCHAR(128)                          NULL COMMENT '操作系统',
    status        ENUM ('SUCCESS', 'FAIL')              NOT NULL COMMENT '登录状态：SUCCESS-成功，FAIL-失败',
    message       VARCHAR(255)                          NULL COMMENT '结果信息（如失败原因）',
    login_time    DATETIME                              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
    deleted       TINYINT                               NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_user_time (user_id, login_time),
    KEY idx_time (login_time)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='系统登录日志表';

SET FOREIGN_KEY_CHECKS = 1;
