-- =====================================================
-- 校园快递驿站管理系统 数据库初始化脚本
-- MySQL 8.0
-- =====================================================

CREATE DATABASE IF NOT EXISTS `campus_express`
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;

USE `campus_express`;

-- -----------------------------------------------------
-- 1. 角色表
-- -----------------------------------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `role_code`   VARCHAR(32)  NOT NULL COMMENT '角色编码：SUPER_ADMIN 超级管理员 / STAFF 驿站员工',
    `role_name`   VARCHAR(32)  NOT NULL COMMENT '角色名称',
    `remark`      VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '角色表';

-- -----------------------------------------------------
-- 2. 用户表（管理员/员工）
-- -----------------------------------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `username`    VARCHAR(32)  NOT NULL COMMENT '登录账号',
    `password`    VARCHAR(100) NOT NULL COMMENT '密码（BCrypt 加密）',
    `real_name`   VARCHAR(32)  DEFAULT NULL COMMENT '真实姓名',
    `phone`       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    `role_code`   VARCHAR(32)  NOT NULL COMMENT '角色编码',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1 启用 0 禁用',
    `remark`      VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 正常 1 已删除',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '用户表';

-- -----------------------------------------------------
-- 3. 操作日志表
-- -----------------------------------------------------
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`     BIGINT        DEFAULT NULL COMMENT '操作人ID',
    `username`    VARCHAR(32)   DEFAULT NULL COMMENT '操作人账号',
    `module`      VARCHAR(64)   DEFAULT NULL COMMENT '操作模块',
    `operation`   VARCHAR(128)  DEFAULT NULL COMMENT '操作描述',
    `method`      VARCHAR(255)  DEFAULT NULL COMMENT '请求方法',
    `params`      TEXT          COMMENT '请求参数',
    `ip`          VARCHAR(64)   DEFAULT NULL COMMENT '来源IP',
    `status`      TINYINT       NOT NULL DEFAULT 1 COMMENT '是否成功：1 成功 0 失败',
    `error_msg`   VARCHAR(2000) DEFAULT NULL COMMENT '异常信息',
    `cost_time`   BIGINT        DEFAULT NULL COMMENT '耗时(毫秒)',
    `create_time` DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (`id`),
    KEY `idx_oper_username` (`username`),
    KEY `idx_oper_create_time` (`create_time`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '操作日志表';

-- -----------------------------------------------------
-- 4. 登录日志表
-- -----------------------------------------------------
DROP TABLE IF EXISTS `sys_login_log`;
CREATE TABLE `sys_login_log` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `username`    VARCHAR(32)   DEFAULT NULL COMMENT '登录账号',
    `ip`          VARCHAR(64)   DEFAULT NULL COMMENT '登录IP',
    `browser`     VARCHAR(128)  DEFAULT NULL COMMENT '浏览器',
    `os`          VARCHAR(128)  DEFAULT NULL COMMENT '操作系统',
    `status`      TINYINT       NOT NULL DEFAULT 1 COMMENT '是否成功：1 成功 0 失败',
    `msg`         VARCHAR(255)  DEFAULT NULL COMMENT '提示信息',
    `login_time`  DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
    PRIMARY KEY (`id`),
    KEY `idx_login_username` (`username`),
    KEY `idx_login_time` (`login_time`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '登录日志表';

-- -----------------------------------------------------
-- 5. 快递公司字典表
-- -----------------------------------------------------
DROP TABLE IF EXISTS `express_company`;
CREATE TABLE `express_company` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `company_code` VARCHAR(32)  NOT NULL COMMENT '公司编码',
    `company_name` VARCHAR(64)  NOT NULL COMMENT '公司名称',
    `sort`         INT          NOT NULL DEFAULT 0 COMMENT '排序（越小越靠前）',
    `status`       TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1 启用 0 停用',
    `create_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_company_code` (`company_code`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '快递公司字典表';

-- -----------------------------------------------------
-- 6. 货架库位表
-- -----------------------------------------------------
DROP TABLE IF EXISTS `shelf`;
CREATE TABLE `shelf` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `shelf_no`    VARCHAR(32)  NOT NULL COMMENT '货架号（如 A-01-02）',
    `area`        VARCHAR(32)  DEFAULT NULL COMMENT '区域（如 A区）',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1 启用 0 停用',
    `remark`      VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_shelf_no` (`shelf_no`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '货架库位表';

-- -----------------------------------------------------
-- 7. 快件主表
-- -----------------------------------------------------
DROP TABLE IF EXISTS `package`;
CREATE TABLE `package` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `tracking_no`    VARCHAR(64)  NOT NULL COMMENT '快递单号',
    `company_id`     BIGINT       DEFAULT NULL COMMENT '快递公司ID',
    `receiver_name`  VARCHAR(64)  NOT NULL COMMENT '收件人姓名',
    `receiver_phone` VARCHAR(20)  NOT NULL COMMENT '收件人手机号',
    `pickup_code`    VARCHAR(16)  DEFAULT NULL COMMENT '取件码（6位数字）',
    `shelf_no`       VARCHAR(32)  DEFAULT NULL COMMENT '货架号',
    `status`         TINYINT      NOT NULL DEFAULT 0 COMMENT '状态：0已入库待通知 1已通知待取件 2已取件 3滞留 4问题件 5已退回',
    `in_time`        DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '入库时间',
    `notice_time`    DATETIME     DEFAULT NULL COMMENT '通知时间',
    `notice_count`   INT          NOT NULL DEFAULT 0 COMMENT '通知次数',
    `pickup_time`    DATETIME     DEFAULT NULL COMMENT '取件时间',
    `pickup_type`    VARCHAR(32)  DEFAULT NULL COMMENT '取件凭证方式：PICKUP_CODE 取件码 / PHONE_TAIL 手机号后四位+单号 / SCAN 扫码枪',
    `operator_id`    BIGINT       DEFAULT NULL COMMENT '入库操作人ID',
    `remark`         VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `deleted`        TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 正常 1 已删除',
    `create_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tracking_no` (`tracking_no`),
    KEY `idx_receiver_phone` (`receiver_phone`),
    KEY `idx_pickup_code` (`pickup_code`),
    KEY `idx_status` (`status`),
    KEY `idx_in_time` (`in_time`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '快件主表';

-- -----------------------------------------------------
-- 8. 快件操作日志表（状态流转记录）
-- -----------------------------------------------------
DROP TABLE IF EXISTS `package_log`;
CREATE TABLE `package_log` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `package_id`    BIGINT       NOT NULL COMMENT '快件ID',
    `tracking_no`   VARCHAR(64)  DEFAULT NULL COMMENT '快递单号',
    `action`        VARCHAR(32)  NOT NULL COMMENT '操作类型：IN入库 NOTICE通知 PICKUP取件 PROBLEM转问题 RETURN退回',
    `before_status` TINYINT      DEFAULT NULL COMMENT '操作前状态',
    `after_status`  TINYINT      DEFAULT NULL COMMENT '操作后状态',
    `content`       VARCHAR(255) DEFAULT NULL COMMENT '操作内容描述',
    `operator_id`   BIGINT       DEFAULT NULL COMMENT '操作人ID',
    `operator_name` VARCHAR(32)  DEFAULT NULL COMMENT '操作人姓名',
    `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (`id`),
    KEY `idx_pkg_log_package_id` (`package_id`),
    KEY `idx_pkg_log_create_time` (`create_time`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '快件操作日志表';

-- -----------------------------------------------------
-- 9. 取件通知记录表
-- -----------------------------------------------------
DROP TABLE IF EXISTS `notice_record`;
CREATE TABLE `notice_record` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `package_id`    BIGINT       NOT NULL COMMENT '快件ID',
    `tracking_no`   VARCHAR(64)  DEFAULT NULL COMMENT '快递单号',
    `receiver_phone` VARCHAR(20) DEFAULT NULL COMMENT '收件人手机号',
    `notice_type`   VARCHAR(32)  DEFAULT NULL COMMENT '通知方式：SMS 短信 / SITE 站内',
    `content`       VARCHAR(512) DEFAULT NULL COMMENT '通知内容',
    `send_status`   TINYINT      NOT NULL DEFAULT 1 COMMENT '发送状态：1 成功 0 失败',
    `send_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    PRIMARY KEY (`id`),
    KEY `idx_notice_package_id` (`package_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '取件通知记录表';

-- -----------------------------------------------------
-- 10. 问题件表
-- -----------------------------------------------------
DROP TABLE IF EXISTS `problem_package`;
CREATE TABLE `problem_package` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `package_id`    BIGINT       NOT NULL COMMENT '关联快件ID',
    `tracking_no`   VARCHAR(64)  DEFAULT NULL COMMENT '快递单号',
    `problem_type`  VARCHAR(32)  NOT NULL COMMENT '问题类型：STAY滞留 WRONG错件 DAMAGE破损 LOST丢件 REJECT拒收 RETURN退回 OTHER其他',
    `problem_desc`  VARCHAR(512) DEFAULT NULL COMMENT '问题描述',
    `handle_status` TINYINT      NOT NULL DEFAULT 0 COMMENT '处理状态：0待处理 1处理中 2已处理',
    `handle_result` VARCHAR(512) DEFAULT NULL COMMENT '处理结果',
    `handler_id`    BIGINT       DEFAULT NULL COMMENT '处理人ID',
    `handle_time`   DATETIME     DEFAULT NULL COMMENT '处理时间',
    `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '登记时间',
    PRIMARY KEY (`id`),
    KEY `idx_problem_package_id` (`package_id`),
    KEY `idx_problem_type` (`problem_type`),
    KEY `idx_problem_handle_status` (`handle_status`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '问题件表';

-- -----------------------------------------------------
-- 11. 系统配置表
-- -----------------------------------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `config_key`   VARCHAR(64)  NOT NULL COMMENT '配置键',
    `config_value` VARCHAR(255) DEFAULT NULL COMMENT '配置值',
    `remark`       VARCHAR(255) DEFAULT NULL COMMENT '说明',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '系统配置表';

-- -----------------------------------------------------
-- 12. 学生用户表（用户端注册账号）
-- -----------------------------------------------------
DROP TABLE IF EXISTS `student_user`;
CREATE TABLE `student_user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `username`    VARCHAR(32)  NOT NULL COMMENT '登录账号（学号/手机号）',
    `password`    VARCHAR(100) NOT NULL COMMENT '密码（BCrypt 加密）',
    `real_name`   VARCHAR(32)  DEFAULT NULL COMMENT '姓名',
    `phone`       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1 启用 0 禁用',
    `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 正常 1 已删除',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_student_username` (`username`),
    KEY `idx_student_phone` (`phone`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '学生用户表';

-- -----------------------------------------------------
-- 13. 寄件订单表（用户端在线寄件）
-- -----------------------------------------------------
DROP TABLE IF EXISTS `send_order`;
CREATE TABLE `send_order` (
    `id`               BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `order_no`         VARCHAR(32)  NOT NULL COMMENT '寄件单号',
    `student_id`       BIGINT       NOT NULL COMMENT '下单学生ID',
    `company_id`       BIGINT       DEFAULT NULL COMMENT '快递公司ID',
    `tracking_no`      VARCHAR(64)  DEFAULT NULL COMMENT '运单号（驿站揽收后回填）',
    `sender_name`      VARCHAR(64)  DEFAULT NULL COMMENT '寄件人姓名',
    `sender_phone`     VARCHAR(20)  DEFAULT NULL COMMENT '寄件人手机号',
    `sender_address`   VARCHAR(255) DEFAULT NULL COMMENT '寄件地址/上门取件地点',
    `receiver_name`    VARCHAR(64)  DEFAULT NULL COMMENT '收件人姓名',
    `receiver_phone`   VARCHAR(20)  DEFAULT NULL COMMENT '收件人手机号',
    `receiver_address` VARCHAR(255) DEFAULT NULL COMMENT '收件地址',
    `item_name`        VARCHAR(64)  DEFAULT NULL COMMENT '物品名称',
    `weight`           DECIMAL(6,2) DEFAULT NULL COMMENT '重量(kg)',
    `expect_time`      DATETIME     DEFAULT NULL COMMENT '期望取件时间',
    `remark`           VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `status`           TINYINT      NOT NULL DEFAULT 0 COMMENT '状态：0待揽收 1已揽收 2运输中 3派送中 4已签收 5已取消',
    `deleted`          TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 正常 1 已删除',
    `create_time`      DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
    `update_time`      DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_send_order_no` (`order_no`),
    KEY `idx_send_student_id` (`student_id`),
    KEY `idx_send_status` (`status`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '寄件订单表';

-- -----------------------------------------------------
-- 14. 寄件物流轨迹表
-- -----------------------------------------------------
DROP TABLE IF EXISTS `send_order_log`;
CREATE TABLE `send_order_log` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `order_id`    BIGINT       NOT NULL COMMENT '寄件订单ID',
    `status`      TINYINT      DEFAULT NULL COMMENT '节点状态',
    `content`     VARCHAR(255) DEFAULT NULL COMMENT '轨迹描述',
    `location`    VARCHAR(128) DEFAULT NULL COMMENT '所在网点/城市',
    `operator`    VARCHAR(32)  DEFAULT NULL COMMENT '操作人',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '时间',
    PRIMARY KEY (`id`),
    KEY `idx_send_log_order_id` (`order_id`),
    KEY `idx_send_log_create_time` (`create_time`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '寄件物流轨迹表';

-- -----------------------------------------------------
-- 15. 存件（寄存）订单表
-- -----------------------------------------------------
DROP TABLE IF EXISTS `deposit_order`;
CREATE TABLE `deposit_order` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `order_no`     VARCHAR(32)  NOT NULL COMMENT '存件单号',
    `student_id`   BIGINT       NOT NULL COMMENT '寄存学生ID',
    `item_name`    VARCHAR(64)  NOT NULL COMMENT '物品名称',
    `quantity`     INT          NOT NULL DEFAULT 1 COMMENT '物品数量',
    `deposit_code` VARCHAR(16)  DEFAULT NULL COMMENT '存件码（6位数字）',
    `shelf_no`     VARCHAR(32)  DEFAULT NULL COMMENT '驿站分配货架',
    `deposit_days` INT          NOT NULL DEFAULT 3 COMMENT '预计存放天数',
    `expect_time`  DATETIME     DEFAULT NULL COMMENT '预计送达驿站时间',
    `remark`       VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `status`       TINYINT      NOT NULL DEFAULT 0 COMMENT '状态：0待到站 1在库 2已取出 3已取消',
    `in_time`      DATETIME     DEFAULT NULL COMMENT '驿站接收（上架）时间',
    `out_time`     DATETIME     DEFAULT NULL COMMENT '取走时间',
    `operator_id`  BIGINT       DEFAULT NULL COMMENT '经办人ID',
    `deleted`      TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 正常 1 已删除',
    `create_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    `update_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_deposit_order_no` (`order_no`),
    KEY `idx_deposit_student_id` (`student_id`),
    KEY `idx_deposit_code` (`deposit_code`),
    KEY `idx_deposit_status` (`status`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '存件（寄存）订单表';


