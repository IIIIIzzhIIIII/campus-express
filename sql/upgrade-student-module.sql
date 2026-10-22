-- =====================================================
-- 用户端（学生）模块升级脚本
-- 说明：已初始化过数据库（执行过 schema.sql / data.sql）的项目，
--       直接执行本脚本即可创建新增的 4 张表与演示数据；
--       全新安装可直接按 README 执行 schema.sql + data.sql。
-- 幂等：可重复执行
-- =====================================================
USE `campus_express`;

-- 1. 学生用户表（用户端注册账号）
CREATE TABLE IF NOT EXISTS `student_user` (
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

-- 2. 寄件订单表（用户端在线寄件）
CREATE TABLE IF NOT EXISTS `send_order` (
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

-- 3. 寄件物流轨迹表
CREATE TABLE IF NOT EXISTS `send_order_log` (
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

-- 4. 存件（寄存）订单表
CREATE TABLE IF NOT EXISTS `deposit_order` (
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

-- ===================== 演示数据（可重复执行） =====================
-- 用户端测试账号：stu001 / stu002，密码均为 123456
INSERT IGNORE INTO `student_user` (`id`, `username`, `password`, `real_name`, `phone`, `status`) VALUES
(1, 'stu001', '$2a$10$cxVbkPu1..LFlafQbMdAMuU/ubIVZQ2e99ErP0DoTLttNoMIm85ta', '张文博', '13812340001', 1),
(2, 'stu002', '$2a$10$cxVbkPu1..LFlafQbMdAMuU/ubIVZQ2e99ErP0DoTLttNoMIm85ta', '李思彤', '13812340002', 1);

-- 寄件订单（status: 0待揽收 1已揽收 2运输中 3派送中 4已签收 5已取消）
INSERT IGNORE INTO `send_order` (`id`, `order_no`, `student_id`, `company_id`, `tracking_no`, `sender_name`, `sender_phone`, `sender_address`, `receiver_name`, `receiver_phone`, `receiver_address`, `item_name`, `weight`, `expect_time`, `status`, `create_time`) VALUES
(1, 'S20260913000001', 1, 1, 'SF7788990011', '张文博', '13812340001', '3号宿舍楼一楼', '王丽', '13900001111', '江苏省南京市玄武区中山路 1 号', '衣物', 1.50, NOW() - INTERVAL 2 DAY, 2, NOW() - INTERVAL 2 DAY),
(2, 'S20260913000002', 1, 3, NULL, '张文博', '13812340001', '校园快递驿站', '赵强', '13700002222', '浙江省杭州市西湖区文一路 2 号', '书籍', 2.00, NULL, 0, NOW() - INTERVAL 3 HOUR),
(3, 'S20260913000003', 2, 6, 'JD8899001122', '李思彤', '13812340002', '2号宿舍楼一楼', '陈静', '13600003333', '上海市浦东新区张江路 3 号', '文件', 0.50, NOW() - INTERVAL 1 DAY, 4, NOW() - INTERVAL 3 DAY);

-- 寄件物流轨迹
INSERT IGNORE INTO `send_order_log` (`id`, `order_id`, `status`, `content`, `location`, `operator`, `create_time`) VALUES
(1, 1, 0, '寄件订单已提交，等待驿站揽收', '校园快递驿站', '张文博', NOW() - INTERVAL 2 DAY),
(2, 1, 1, '驿站已揽收，运单号 SF7788990011', '校园快递驿站', '王小明', NOW() - INTERVAL 47 HOUR),
(3, 1, 2, '快件已运输中', '南京转运中心', '王小明', NOW() - INTERVAL 20 HOUR),
(4, 2, 0, '寄件订单已提交，等待驿站揽收', '校园快递驿站', '张文博', NOW() - INTERVAL 3 HOUR),
(5, 3, 0, '寄件订单已提交，等待驿站揽收', '校园快递驿站', '李思彤', NOW() - INTERVAL 3 DAY),
(6, 3, 1, '驿站已揽收，运单号 JD8899001122', '校园快递驿站', '李思', NOW() - INTERVAL 70 HOUR),
(7, 3, 2, '快件已运输中', '南京转运中心', '李思', NOW() - INTERVAL 60 HOUR),
(8, 3, 3, '快件已派送中', '上海浦东新区网点', '李思', NOW() - INTERVAL 30 HOUR),
(9, 3, 4, '快件已签收，签收人：陈静', '上海浦东新区网点', '李思', NOW() - INTERVAL 26 HOUR);

-- 存件（寄存）单（status: 0待到站 1在库 2已取出 3已取消）
INSERT IGNORE INTO `deposit_order` (`id`, `order_no`, `student_id`, `item_name`, `quantity`, `deposit_code`, `shelf_no`, `deposit_days`, `status`, `in_time`, `operator_id`, `create_time`) VALUES
(1, 'D20260912000001', 1, '考研资料', 2, '302145', 'B-01-01', 7, 1, NOW() - INTERVAL 1 DAY, 2, NOW() - INTERVAL 2 DAY),
(2, 'D20260913000002', 2, '行李箱', 1, '884520', NULL, 5, 0, NULL, NULL, NOW() - INTERVAL 5 HOUR);
