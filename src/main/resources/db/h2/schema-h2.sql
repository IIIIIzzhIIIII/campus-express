-- =====================================================
-- 校园快递驿站管理系统 H2 建表脚本（由 scripts/gen-h2-sql.ps1 自动生成，请勿手工修改）
-- 适用于单机版 / exe：MODE=MySQL
-- =====================================================

-- -----------------------------------------------------
-- 1. 角色表
-- -----------------------------------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `role_code`   VARCHAR(32)  NOT NULL,
    `role_name`   VARCHAR(32)  NOT NULL,
    `remark`      VARCHAR(255) DEFAULT NULL,
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    CONSTRAINT `uk_role_code` UNIQUE (`role_code`)
);


-- -----------------------------------------------------
-- 2. 用户表（管理员/员工）
-- -----------------------------------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `username`    VARCHAR(32)  NOT NULL,
    `password`    VARCHAR(100) NOT NULL,
    `real_name`   VARCHAR(32)  DEFAULT NULL,
    `phone`       VARCHAR(20)  DEFAULT NULL,
    `role_code`   VARCHAR(32)  NOT NULL,
    `status`      TINYINT      NOT NULL DEFAULT 1,
    `remark`      VARCHAR(255) DEFAULT NULL,
    `deleted`     TINYINT      NOT NULL DEFAULT 0,
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    CONSTRAINT `uk_username` UNIQUE (`username`)
);


-- -----------------------------------------------------
-- 3. 操作日志表
-- -----------------------------------------------------
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT,
    `user_id`     BIGINT        DEFAULT NULL,
    `username`    VARCHAR(32)   DEFAULT NULL,
    `module`      VARCHAR(64)   DEFAULT NULL,
    `operation`   VARCHAR(128)  DEFAULT NULL,
    `method`      VARCHAR(255)  DEFAULT NULL,
    `params`      TEXT,
    `ip`          VARCHAR(64)   DEFAULT NULL,
    `status`      TINYINT       NOT NULL DEFAULT 1,
    `error_msg`   VARCHAR(2000) DEFAULT NULL,
    `cost_time`   BIGINT        DEFAULT NULL,
    `create_time` DATETIME      DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);


-- -----------------------------------------------------
-- 4. 登录日志表
-- -----------------------------------------------------
DROP TABLE IF EXISTS `sys_login_log`;
CREATE TABLE `sys_login_log` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT,
    `username`    VARCHAR(32)   DEFAULT NULL,
    `ip`          VARCHAR(64)   DEFAULT NULL,
    `browser`     VARCHAR(128)  DEFAULT NULL,
    `os`          VARCHAR(128)  DEFAULT NULL,
    `status`      TINYINT       NOT NULL DEFAULT 1,
    `msg`         VARCHAR(255)  DEFAULT NULL,
    `login_time`  DATETIME      DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);


-- -----------------------------------------------------
-- 5. 快递公司字典表
-- -----------------------------------------------------
DROP TABLE IF EXISTS `express_company`;
CREATE TABLE `express_company` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT,
    `company_code` VARCHAR(32)  NOT NULL,
    `company_name` VARCHAR(64)  NOT NULL,
    `sort`         INT          NOT NULL DEFAULT 0,
    `status`       TINYINT      NOT NULL DEFAULT 1,
    `create_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    CONSTRAINT `uk_company_code` UNIQUE (`company_code`)
);


-- -----------------------------------------------------
-- 6. 货架库位表
-- -----------------------------------------------------
DROP TABLE IF EXISTS `shelf`;
CREATE TABLE `shelf` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `shelf_no`    VARCHAR(32)  NOT NULL,
    `area`        VARCHAR(32)  DEFAULT NULL,
    `status`      TINYINT      NOT NULL DEFAULT 1,
    `remark`      VARCHAR(255) DEFAULT NULL,
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    CONSTRAINT `uk_shelf_no` UNIQUE (`shelf_no`)
);


-- -----------------------------------------------------
-- 7. 快件主表
-- -----------------------------------------------------
DROP TABLE IF EXISTS `package`;
CREATE TABLE `package` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT,
    `tracking_no`    VARCHAR(64)  NOT NULL,
    `company_id`     BIGINT       DEFAULT NULL,
    `receiver_name`  VARCHAR(64)  NOT NULL,
    `receiver_phone` VARCHAR(20)  NOT NULL,
    `pickup_code`    VARCHAR(16)  DEFAULT NULL,
    `shelf_no`       VARCHAR(32)  DEFAULT NULL,
    `status`         TINYINT      NOT NULL DEFAULT 0,
    `in_time`        DATETIME     DEFAULT CURRENT_TIMESTAMP,
    `notice_time`    DATETIME     DEFAULT NULL,
    `notice_count`   INT          NOT NULL DEFAULT 0,
    `pickup_time`    DATETIME     DEFAULT NULL,
    `pickup_type`    VARCHAR(32)  DEFAULT NULL,
    `operator_id`    BIGINT       DEFAULT NULL,
    `remark`         VARCHAR(255) DEFAULT NULL,
    `deleted`        TINYINT      NOT NULL DEFAULT 0,
    `create_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP,
    `update_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    CONSTRAINT `uk_tracking_no` UNIQUE (`tracking_no`)
);


-- -----------------------------------------------------
-- 8. 快件操作日志表（状态流转记录）
-- -----------------------------------------------------
DROP TABLE IF EXISTS `package_log`;
CREATE TABLE `package_log` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT,
    `package_id`    BIGINT       NOT NULL,
    `tracking_no`   VARCHAR(64)  DEFAULT NULL,
    `action`        VARCHAR(32)  NOT NULL,
    `before_status` TINYINT      DEFAULT NULL,
    `after_status`  TINYINT      DEFAULT NULL,
    `content`       VARCHAR(255) DEFAULT NULL,
    `operator_id`   BIGINT       DEFAULT NULL,
    `operator_name` VARCHAR(32)  DEFAULT NULL,
    `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);


-- -----------------------------------------------------
-- 9. 取件通知记录表
-- -----------------------------------------------------
DROP TABLE IF EXISTS `notice_record`;
CREATE TABLE `notice_record` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT,
    `package_id`    BIGINT       NOT NULL,
    `tracking_no`   VARCHAR(64)  DEFAULT NULL,
    `receiver_phone` VARCHAR(20) DEFAULT NULL,
    `notice_type`   VARCHAR(32)  DEFAULT NULL,
    `content`       VARCHAR(512) DEFAULT NULL,
    `send_status`   TINYINT      NOT NULL DEFAULT 1,
    `send_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);


-- -----------------------------------------------------
-- 10. 问题件表
-- -----------------------------------------------------
DROP TABLE IF EXISTS `problem_package`;
CREATE TABLE `problem_package` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT,
    `package_id`    BIGINT       NOT NULL,
    `tracking_no`   VARCHAR(64)  DEFAULT NULL,
    `problem_type`  VARCHAR(32)  NOT NULL,
    `problem_desc`  VARCHAR(512) DEFAULT NULL,
    `handle_status` TINYINT      NOT NULL DEFAULT 0,
    `handle_result` VARCHAR(512) DEFAULT NULL,
    `handler_id`    BIGINT       DEFAULT NULL,
    `handle_time`   DATETIME     DEFAULT NULL,
    `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);


-- -----------------------------------------------------
-- 11. 系统配置表
-- -----------------------------------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT,
    `config_key`   VARCHAR(64)  NOT NULL,
    `config_value` VARCHAR(255) DEFAULT NULL,
    `remark`       VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `uk_config_key` UNIQUE (`config_key`)
);


-- -----------------------------------------------------
-- 12. 学生用户表（用户端注册账号）
-- -----------------------------------------------------
DROP TABLE IF EXISTS `student_user`;
CREATE TABLE `student_user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `username`    VARCHAR(32)  NOT NULL,
    `password`    VARCHAR(100) NOT NULL,
    `real_name`   VARCHAR(32)  DEFAULT NULL,
    `phone`       VARCHAR(20)  DEFAULT NULL,
    `status`      TINYINT      NOT NULL DEFAULT 1,
    `deleted`     TINYINT      NOT NULL DEFAULT 0,
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    CONSTRAINT `uk_student_username` UNIQUE (`username`)
);


-- -----------------------------------------------------
-- 13. 寄件订单表（用户端在线寄件）
-- -----------------------------------------------------
DROP TABLE IF EXISTS `send_order`;
CREATE TABLE `send_order` (
    `id`               BIGINT       NOT NULL AUTO_INCREMENT,
    `order_no`         VARCHAR(32)  NOT NULL,
    `student_id`       BIGINT       NOT NULL,
    `company_id`       BIGINT       DEFAULT NULL,
    `tracking_no`      VARCHAR(64)  DEFAULT NULL,
    `sender_name`      VARCHAR(64)  DEFAULT NULL,
    `sender_phone`     VARCHAR(20)  DEFAULT NULL,
    `sender_address`   VARCHAR(255) DEFAULT NULL,
    `receiver_name`    VARCHAR(64)  DEFAULT NULL,
    `receiver_phone`   VARCHAR(20)  DEFAULT NULL,
    `receiver_address` VARCHAR(255) DEFAULT NULL,
    `item_name`        VARCHAR(64)  DEFAULT NULL,
    `weight`           DECIMAL(6,2) DEFAULT NULL,
    `expect_time`      DATETIME     DEFAULT NULL,
    `remark`           VARCHAR(255) DEFAULT NULL,
    `status`           TINYINT      NOT NULL DEFAULT 0,
    `deleted`          TINYINT      NOT NULL DEFAULT 0,
    `create_time`      DATETIME     DEFAULT CURRENT_TIMESTAMP,
    `update_time`      DATETIME     DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    CONSTRAINT `uk_send_order_no` UNIQUE (`order_no`)
);


-- -----------------------------------------------------
-- 14. 寄件物流轨迹表
-- -----------------------------------------------------
DROP TABLE IF EXISTS `send_order_log`;
CREATE TABLE `send_order_log` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `order_id`    BIGINT       NOT NULL,
    `status`      TINYINT      DEFAULT NULL,
    `content`     VARCHAR(255) DEFAULT NULL,
    `location`    VARCHAR(128) DEFAULT NULL,
    `operator`    VARCHAR(32)  DEFAULT NULL,
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);


-- -----------------------------------------------------
-- 15. 存件（寄存）订单表
-- -----------------------------------------------------
DROP TABLE IF EXISTS `deposit_order`;
CREATE TABLE `deposit_order` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT,
    `order_no`     VARCHAR(32)  NOT NULL,
    `student_id`   BIGINT       NOT NULL,
    `item_name`    VARCHAR(64)  NOT NULL,
    `quantity`     INT          NOT NULL DEFAULT 1,
    `deposit_code` VARCHAR(16)  DEFAULT NULL,
    `shelf_no`     VARCHAR(32)  DEFAULT NULL,
    `deposit_days` INT          NOT NULL DEFAULT 3,
    `expect_time`  DATETIME     DEFAULT NULL,
    `remark`       VARCHAR(255) DEFAULT NULL,
    `status`       TINYINT      NOT NULL DEFAULT 0,
    `in_time`      DATETIME     DEFAULT NULL,
    `out_time`     DATETIME     DEFAULT NULL,
    `operator_id`  BIGINT       DEFAULT NULL,
    `deleted`      TINYINT      NOT NULL DEFAULT 0,
    `create_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    `update_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    CONSTRAINT `uk_deposit_order_no` UNIQUE (`order_no`)
);

-- 索引（原 MySQL 脚本中的 KEY 定义）
CREATE INDEX `idx_oper_username` ON `sys_oper_log` (`username`);
CREATE INDEX `idx_oper_create_time` ON `sys_oper_log` (`create_time`);
CREATE INDEX `idx_login_username` ON `sys_login_log` (`username`);
CREATE INDEX `idx_login_time` ON `sys_login_log` (`login_time`);
CREATE INDEX `idx_receiver_phone` ON `package` (`receiver_phone`);
CREATE INDEX `idx_pickup_code` ON `package` (`pickup_code`);
CREATE INDEX `idx_status` ON `package` (`status`);
CREATE INDEX `idx_in_time` ON `package` (`in_time`);
CREATE INDEX `idx_pkg_log_package_id` ON `package_log` (`package_id`);
CREATE INDEX `idx_pkg_log_create_time` ON `package_log` (`create_time`);
CREATE INDEX `idx_notice_package_id` ON `notice_record` (`package_id`);
CREATE INDEX `idx_problem_package_id` ON `problem_package` (`package_id`);
CREATE INDEX `idx_problem_type` ON `problem_package` (`problem_type`);
CREATE INDEX `idx_problem_handle_status` ON `problem_package` (`handle_status`);
CREATE INDEX `idx_student_phone` ON `student_user` (`phone`);
CREATE INDEX `idx_send_student_id` ON `send_order` (`student_id`);
CREATE INDEX `idx_send_status` ON `send_order` (`status`);
CREATE INDEX `idx_send_log_order_id` ON `send_order_log` (`order_id`);
CREATE INDEX `idx_send_log_create_time` ON `send_order_log` (`create_time`);
CREATE INDEX `idx_deposit_student_id` ON `deposit_order` (`student_id`);
CREATE INDEX `idx_deposit_code` ON `deposit_order` (`deposit_code`);
CREATE INDEX `idx_deposit_status` ON `deposit_order` (`status`);
