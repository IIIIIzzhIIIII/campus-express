-- =====================================================
-- 校园快递驿站管理系统 H2 初始化数据（由 scripts/gen-h2-sql.ps1 自动生成，请勿手工修改）
-- 默认密码均为 123456
-- =====================================================

-- =====================================================
-- 校园快递驿站管理系统 初始化 + 测试数据
-- 密码均为 123456（BCrypt 加密）
-- =====================================================

-- 角色
INSERT INTO `sys_role` (`id`, `role_code`, `role_name`, `remark`) VALUES
(1, 'SUPER_ADMIN', '超级管理员', '拥有全部权限'),
(2, 'STAFF', '驿站员工', '日常入库、取件、问题件登记等操作权限');

-- 用户（admin / 123456，staff / 123456）
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `phone`, `role_code`, `status`, `remark`) VALUES
(1, 'admin', '$2a$10$cxVbkPu1..LFlafQbMdAMuU/ubIVZQ2e99ErP0DoTLttNoMIm85ta', '系统管理员', '13800000001', 'SUPER_ADMIN', 1, '超级管理员'),
(2, 'staff', '$2a$10$cxVbkPu1..LFlafQbMdAMuU/ubIVZQ2e99ErP0DoTLttNoMIm85ta', '王小明', '13800000002', 'STAFF', 1, '驿站值班员工'),
(3, 'lisi', '$2a$10$cxVbkPu1..LFlafQbMdAMuU/ubIVZQ2e99ErP0DoTLttNoMIm85ta', '李思', '13800000003', 'STAFF', 1, '驿站值班员工');

-- 快递公司
INSERT INTO `express_company` (`id`, `company_code`, `company_name`, `sort`, `status`) VALUES
(1, 'SF', '顺丰速运', 1, 1),
(2, 'YTO', '圆通速递', 2, 1),
(3, 'ZTO', '中通快递', 3, 1),
(4, 'STO', '申通快递', 4, 1),
(5, 'YD', '韵达快递', 5, 1),
(6, 'JD', '京东物流', 6, 1),
(7, 'EMS', '中国邮政EMS', 7, 1),
(8, 'ZJS', '宅急送', 8, 1),
(9, 'DBL', '德邦快递', 9, 1),
(10, 'JTSD', '极兔速递', 10, 1),
(11, 'HTKY', '百世快递', 11, 1),
(12, 'YZPY', '邮政包裹', 12, 1);

-- 货架库位（A/B/C 三区）
INSERT INTO `shelf` (`id`, `shelf_no`, `area`, `status`, `remark`) VALUES
(1, 'A-01-01', 'A区', 1, NULL), (2, 'A-01-02', 'A区', 1, NULL), (3, 'A-01-03', 'A区', 1, NULL),
(4, 'A-02-01', 'A区', 1, NULL), (5, 'A-02-02', 'A区', 1, NULL), (6, 'A-02-03', 'A区', 1, NULL),
(7, 'A-03-01', 'A区', 1, NULL), (8, 'A-03-02', 'A区', 1, NULL), (9, 'A-03-03', 'A区', 1, NULL),
(10, 'B-01-01', 'B区', 1, NULL), (11, 'B-01-02', 'B区', 1, NULL), (12, 'B-01-03', 'B区', 1, NULL),
(13, 'B-02-01', 'B区', 1, NULL), (14, 'B-02-02', 'B区', 1, NULL), (15, 'B-02-03', 'B区', 1, NULL),
(16, 'B-03-01', 'B区', 1, NULL), (17, 'B-03-02', 'B区', 1, NULL), (18, 'B-03-03', 'B区', 1, NULL),
(19, 'C-01-01', 'C区', 1, NULL), (20, 'C-01-02', 'C区', 1, NULL), (21, 'C-01-03', 'C区', 1, NULL),
(22, 'C-02-01', 'C区', 1, NULL), (23, 'C-02-02', 'C区', 1, NULL), (24, 'C-02-03', 'C区', 1, NULL),
(25, 'C-03-01', 'C区', 1, NULL), (26, 'C-03-02', 'C区', 1, NULL), (27, 'C-03-03', 'C区', 1, NULL),
(28, 'D-01-01', '大件区', 1, '大件/超重快件'),
(29, 'D-01-02', '大件区', 1, '大件/超重快件'),
(30, 'D-01-03', '大件区', 1, '大件/超重快件');

-- 快件测试数据（单号唯一；取件码 6 位）
-- status: 0已入库待通知 1已通知待取件 2已取件 3滞留 4问题件 5已退回
INSERT INTO `package` (`tracking_no`, `company_id`, `receiver_name`, `receiver_phone`, `pickup_code`, `shelf_no`, `status`, `in_time`, `notice_time`, `notice_count`, `pickup_time`, `pickup_type`, `operator_id`, `remark`) VALUES
('SF20260910001', 1, '张伟', '13812340001', '245731', 'A-01-01', 0, DATEADD('HOUR', -5, NOW()), NULL, 0, NULL, NULL, 2, NULL),
('SF20260910002', 1, '李娜', '13812340002', '618402', 'A-01-02', 0, DATEADD('HOUR', -4, NOW()), NULL, 0, NULL, NULL, 2, NULL),
('YTO20260910003', 2, '王芳', '13812340003', '903154', 'A-02-01', 0, DATEADD('HOUR', -3, NOW()), NULL, 0, NULL, NULL, 2, NULL),
('ZTO20260910004', 3, '刘洋', '13812340004', '120876', 'B-01-01', 0, DATEADD('HOUR', -2, NOW()), NULL, 0, NULL, NULL, 3, NULL),
('STO20260910005', 4, '陈静', '13812340005', '556290', 'B-01-02', 0, DATEADD('HOUR', -1, NOW()), NULL, 0, NULL, NULL, 3, NULL),
('YD20260910006', 5, '杨帆', '13812340006', '778123', 'C-01-01', 0, DATEADD('MINUTE', -30, NOW()), NULL, 0, NULL, NULL, 2, NULL),
('JD20260910007', 6, '赵磊', '13812340007', '334598', 'D-01-01', 0, DATEADD('MINUTE', -20, NOW()), NULL, 0, NULL, NULL, 2, '大件'),
('EMS20260910008', 7, '孙悦', '13812340008', '991047', 'A-03-01', 0, DATEADD('MINUTE', -10, NOW()), NULL, 0, NULL, NULL, 2, NULL),
('SF20260909001', 1, '周杰', '13912340001', '452310', 'A-01-03', 1, DATEADD('DAY', -1, NOW()), DATEADD('HOUR', -23, NOW()), 1, NULL, NULL, 2, NULL),
('YTO20260909002', 2, '吴敏', '13912340002', '730284', 'A-02-02', 1, DATEADD('DAY', -1, NOW()), DATEADD('HOUR', -22, NOW()), 1, NULL, NULL, 2, NULL),
('ZTO20260909003', 3, '郑爽', '13912340003', '184537', 'B-01-03', 1, DATEADD('DAY', -1, NOW()), DATEADD('HOUR', -21, NOW()), 1, NULL, NULL, 3, NULL),
('STO20260909004', 4, '冯军', '13912340004', '602951', 'B-02-01', 1, DATEADD('DAY', -1, NOW()), DATEADD('HOUR', -20, NOW()), 1, NULL, NULL, 3, NULL),
('YD20260909005', 5, '何琳', '13912340005', '890163', 'C-01-02', 1, DATEADD('DAY', -1, NOW()), DATEADD('HOUR', -19, NOW()), 1, NULL, NULL, 2, NULL),
('JD20260909006', 6, '高翔', '13912340006', '357210', 'D-01-02', 1, DATEADD('DAY', -1, NOW()), DATEADD('HOUR', -18, NOW()), 1, NULL, NULL, 2, '大件'),
('EMS20260909007', 7, '罗娜', '13912340007', '428690', 'A-03-02', 1, DATEADD('DAY', -1, NOW()), DATEADD('HOUR', -17, NOW()), 1, NULL, NULL, 2, NULL),
('SF20260908001', 1, '谢涛', '13912340008', '761405', 'A-02-03', 1, DATEADD('DAY', -2, NOW()), DATEADD('HOUR', -45, NOW()), 1, NULL, NULL, 2, NULL),
('YTO20260908002', 2, '唐静', '13912340009', '293587', 'B-02-02', 1, DATEADD('DAY', -2, NOW()), DATEADD('HOUR', -44, NOW()), 1, NULL, NULL, 3, NULL),
('ZTO20260908003', 3, '许杰', '13912340010', '514369', 'C-01-03', 1, DATEADD('DAY', -2, NOW()), DATEADD('HOUR', -43, NOW()), 1, NULL, NULL, 2, NULL),
('STO20260908004', 4, '韩雪', '15012340001', '802647', 'C-02-01', 1, DATEADD('DAY', -2, NOW()), DATEADD('HOUR', -42, NOW()), 1, NULL, NULL, 2, NULL),
('YD20260908005', 5, '冯磊', '15012340002', '105983', 'A-03-03', 1, DATEADD('DAY', -2, NOW()), DATEADD('HOUR', -41, NOW()), 1, NULL, NULL, 2, NULL),
('SF20260909002', 1, '董洁', '15012340003', '629048', 'A-01-01', 2, DATEADD('DAY', -3, NOW()), DATEADD('HOUR', -70, NOW()), 1, DATEADD('HOUR', -20, NOW()), 'PICKUP_CODE', 2, NULL),
('YTO20260909003', 2, '梁静', '15012340004', '830126', 'A-02-01', 2, DATEADD('DAY', -3, NOW()), DATEADD('HOUR', -69, NOW()), 1, DATEADD('HOUR', -19, NOW()), 'PHONE_TAIL', 2, NULL),
('ZTO20260909004', 3, '苏杭', '15012340005', '471593', 'B-01-01', 2, DATEADD('DAY', -3, NOW()), DATEADD('HOUR', -68, NOW()), 1, DATEADD('HOUR', -18, NOW()), 'PICKUP_CODE', 3, NULL),
('STO20260909005', 4, '袁媛', '15012340006', '208417', 'B-02-01', 2, DATEADD('DAY', -3, NOW()), DATEADD('HOUR', -67, NOW()), 1, DATEADD('HOUR', -17, NOW()), 'SCAN', 3, NULL),
('YD20260909006', 5, '蒋雯', '15012340007', '645812', 'C-01-01', 2, DATEADD('DAY', -3, NOW()), DATEADD('HOUR', -66, NOW()), 1, DATEADD('HOUR', -16, NOW()), 'PICKUP_CODE', 2, NULL),
('JD20260909007', 6, '蔡明', '15012340008', '392571', 'D-01-03', 2, DATEADD('DAY', -3, NOW()), DATEADD('HOUR', -65, NOW()), 1, DATEADD('HOUR', -15, NOW()), 'PHONE_TAIL', 2, '大件'),
('SF20260908003', 1, '杜鹏', '15012340009', '910234', 'A-03-01', 2, DATEADD('DAY', -4, NOW()), DATEADD('HOUR', -90, NOW()), 1, DATEADD('HOUR', -40, NOW()), 'PICKUP_CODE', 2, NULL),
('YTO20260908004', 2, '范冰', '15012340010', '748120', 'B-03-01', 2, DATEADD('DAY', -4, NOW()), DATEADD('HOUR', -89, NOW()), 1, DATEADD('HOUR', -39, NOW()), 'PICKUP_CODE', 3, NULL),
('ZTO20260908005', 3, '彭宇', '15112340001', '156809', 'C-02-02', 2, DATEADD('DAY', -4, NOW()), DATEADD('HOUR', -88, NOW()), 1, DATEADD('HOUR', -38, NOW()), 'SCAN', 2, NULL),
('STO20260908006', 4, '曹颖', '15112340002', '503274', 'C-03-01', 2, DATEADD('DAY', -4, NOW()), DATEADD('HOUR', -87, NOW()), 1, DATEADD('HOUR', -37, NOW()), 'PHONE_TAIL', 2, NULL),
('YD20260908007', 5, '曾强', '15112340003', '268459', 'A-02-03', 2, DATEADD('DAY', -4, NOW()), DATEADD('HOUR', -86, NOW()), 1, DATEADD('HOUR', -36, NOW()), 'PICKUP_CODE', 2, NULL),
('SF20260907001', 1, '崔健', '15112340004', '718340', 'B-02-03', 3, DATEADD('DAY', -5, NOW()), DATEADD('DAY', -4, NOW()), 2, NULL, NULL, 2, '超时未取'),
('YTO20260907002', 2, '邓超', '15112340005', '204876', 'C-02-03', 3, DATEADD('DAY', -5, NOW()), DATEADD('DAY', -4, NOW()), 2, NULL, NULL, 2, '超时未取'),
('ZTO20260907003', 3, '方文', '15112340006', '905317', 'B-03-02', 3, DATEADD('DAY', -5, NOW()), DATEADD('DAY', -4, NOW()), 1, NULL, NULL, 3, '超时未取'),
('STO20260906001', 4, '龚雪', '15112340007', '437826', 'C-03-02', 3, DATEADD('DAY', -6, NOW()), DATEADD('DAY', -5, NOW()), 2, NULL, NULL, 2, '超时未取'),
('SF20260906002', 1, '霍思', '15112340008', '610592', 'A-03-02', 4, DATEADD('DAY', -6, NOW()), DATEADD('DAY', -5, NOW()), 1, NULL, NULL, 2, '错件'),
('YTO20260906003', 2, '焦俊', '15112340009', '854213', 'B-03-03', 4, DATEADD('DAY', -6, NOW()), DATEADD('DAY', -5, NOW()), 1, NULL, NULL, 3, '破损'),
('ZTO20260906004', 3, '康健', '15112340010', '120674', 'C-03-03', 4, DATEADD('DAY', -6, NOW()), DATEADD('DAY', -5, NOW()), 1, NULL, NULL, 2, '丢件');

-- 快件操作日志（示例：入库、通知、取件）
INSERT INTO `package_log` (`package_id`, `tracking_no`, `action`, `before_status`, `after_status`, `content`, `operator_id`, `operator_name`, `create_time`) VALUES
(1, 'SF20260910001', 'IN', NULL, 0, '快件入库登记', 2, '王小明', DATEADD('HOUR', -5, NOW())),
(9, 'SF20260909001', 'IN', NULL, 0, '快件入库登记', 2, '王小明', DATEADD('DAY', -1, NOW())),
(9, 'SF20260909001', 'NOTICE', 0, 1, '发送取件通知（模拟短信）', 2, '王小明', DATEADD('HOUR', -23, NOW())),
(21, 'SF20260909002', 'IN', NULL, 0, '快件入库登记', 2, '王小明', DATEADD('DAY', -3, NOW())),
(21, 'SF20260909002', 'NOTICE', 0, 1, '发送取件通知（模拟短信）', 2, '王小明', DATEADD('HOUR', -70, NOW())),
(21, 'SF20260909002', 'PICKUP', 1, 2, '取件确认（取件码）', 2, '王小明', DATEADD('HOUR', -20, NOW()));

-- 取件通知记录
INSERT INTO `notice_record` (`package_id`, `tracking_no`, `receiver_phone`, `notice_type`, `content`, `send_status`, `send_time`) VALUES
(9, 'SF20260909001', '13912340001', 'SMS', '【校园驿站】您有快递已到，取件码 452310，货架 A-01-03，请凭码取件。', 1, DATEADD('HOUR', -23, NOW())),
(21, 'SF20260909002', '15012340003', 'SMS', '【校园驿站】您有快递已到，取件码 629048，货架 A-01-01，请凭码取件。', 1, DATEADD('HOUR', -70, NOW()));

-- 问题件
INSERT INTO `problem_package` (`package_id`, `tracking_no`, `problem_type`, `problem_desc`, `handle_status`, `handle_result`, `handler_id`, `handle_time`, `create_time`) VALUES
(36, 'SF20260906002', 'WRONG', '收件人反馈地址错误，快件发错校区', 1, NULL, NULL, NULL, DATEADD('DAY', -5, NOW())),
(37, 'YTO20260906003', 'DAMAGE', '外包装破损，内件疑似受损', 0, NULL, NULL, NULL, DATEADD('DAY', -5, NOW())),
(38, 'ZTO20260906004', 'LOST', '学生查询未找到，疑似丢失', 2, '已联系快递公司理赔，快件标记退回', 1, DATEADD('DAY', -4, NOW()), DATEADD('DAY', -5, NOW()));

-- =====================================================
-- 用户端（学生）测试数据：账号 stu001 / stu002，密码均为 123456
-- =====================================================
INSERT INTO `student_user` (`id`, `username`, `password`, `real_name`, `phone`, `status`) VALUES
(1, 'stu001', '$2a$10$cxVbkPu1..LFlafQbMdAMuU/ubIVZQ2e99ErP0DoTLttNoMIm85ta', '张文博', '13812340001', 1),
(2, 'stu002', '$2a$10$cxVbkPu1..LFlafQbMdAMuU/ubIVZQ2e99ErP0DoTLttNoMIm85ta', '李思彤', '13812340002', 1);

-- 寄件订单（status: 0待揽收 1已揽收 2运输中 3派送中 4已签收 5已取消）
INSERT INTO `send_order` (`id`, `order_no`, `student_id`, `company_id`, `tracking_no`, `sender_name`, `sender_phone`, `sender_address`, `receiver_name`, `receiver_phone`, `receiver_address`, `item_name`, `weight`, `expect_time`, `status`, `create_time`) VALUES
(1, 'S20260913000001', 1, 1, 'SF7788990011', '张文博', '13812340001', '3号宿舍楼一楼', '王丽', '13900001111', '江苏省南京市玄武区中山路 1 号', '衣物', 1.50, DATEADD('DAY', -2, NOW()), 2, DATEADD('DAY', -2, NOW())),
(2, 'S20260913000002', 1, 3, NULL, '张文博', '13812340001', '校园快递驿站', '赵强', '13700002222', '浙江省杭州市西湖区文一路 2 号', '书籍', 2.00, NULL, 0, DATEADD('HOUR', -3, NOW())),
(3, 'S20260913000003', 2, 6, 'JD8899001122', '李思彤', '13812340002', '2号宿舍楼一楼', '陈静', '13600003333', '上海市浦东新区张江路 3 号', '文件', 0.50, DATEADD('DAY', -1, NOW()), 4, DATEADD('DAY', -3, NOW()));

-- 寄件物流轨迹
INSERT INTO `send_order_log` (`order_id`, `status`, `content`, `location`, `operator`, `create_time`) VALUES
(1, 0, '寄件订单已提交，等待驿站揽收', '校园快递驿站', '张文博', DATEADD('DAY', -2, NOW())),
(1, 1, '驿站已揽收，运单号 SF7788990011', '校园快递驿站', '王小明', DATEADD('HOUR', -47, NOW())),
(1, 2, '快件已运输中', '南京转运中心', '王小明', DATEADD('HOUR', -20, NOW())),
(2, 0, '寄件订单已提交，等待驿站揽收', '校园快递驿站', '张文博', DATEADD('HOUR', -3, NOW())),
(3, 0, '寄件订单已提交，等待驿站揽收', '校园快递驿站', '李思彤', DATEADD('DAY', -3, NOW())),
(3, 1, '驿站已揽收，运单号 JD8899001122', '校园快递驿站', '李思', DATEADD('HOUR', -70, NOW())),
(3, 2, '快件已运输中', '南京转运中心', '李思', DATEADD('HOUR', -60, NOW())),
(3, 3, '快件已派送中', '上海浦东新区网点', '李思', DATEADD('HOUR', -30, NOW())),
(3, 4, '快件已签收，签收人：陈静', '上海浦东新区网点', '李思', DATEADD('HOUR', -26, NOW()));

-- 存件（寄存）单（status: 0待到站 1在库 2已取出 3已取消）
INSERT INTO `deposit_order` (`id`, `order_no`, `student_id`, `item_name`, `quantity`, `deposit_code`, `shelf_no`, `deposit_days`, `status`, `in_time`, `operator_id`, `create_time`) VALUES
(1, 'D20260912000001', 1, '考研资料', 2, '302145', 'B-01-01', 7, 1, DATEADD('DAY', -1, NOW()), 2, DATEADD('DAY', -2, NOW())),
(2, 'D20260913000002', 2, '行李箱', 1, '884520', NULL, 5, 0, NULL, NULL, DATEADD('HOUR', -5, NOW()));

-- 数据中显式指定了主键，需要把自增序列推后，避免后续插入主键冲突
ALTER TABLE `sys_role` ALTER COLUMN `id` RESTART WITH 1000;
ALTER TABLE `sys_user` ALTER COLUMN `id` RESTART WITH 1000;
ALTER TABLE `express_company` ALTER COLUMN `id` RESTART WITH 1000;
ALTER TABLE `shelf` ALTER COLUMN `id` RESTART WITH 1000;
ALTER TABLE `student_user` ALTER COLUMN `id` RESTART WITH 1000;
ALTER TABLE `send_order` ALTER COLUMN `id` RESTART WITH 1000;
ALTER TABLE `deposit_order` ALTER COLUMN `id` RESTART WITH 1000;
