-- ============================================================
-- 高校宿舍管理系统 - 完整演示测试数据
-- 说明：仅用于本地开发和功能测试，会清空现有业务数据。
-- 所有演示账号密码均为：123456
-- ============================================================

USE dormitory_management;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE operation_logs;
TRUNCATE TABLE manager_permissions;
TRUNCATE TABLE dorm_assignments;
TRUNCATE TABLE students;
TRUNCATE TABLE dormitories;
TRUNCATE TABLE sys_config;
TRUNCATE TABLE users;
SET FOREIGN_KEY_CHECKS = 1;

-- BCrypt(123456)
SET @demo_password = '$2a$10$m2CC3.eOjf0UwueG3xsJsOK2/aZbOsgPR.1lFdm5u1uhRqaGzdBL.';

-- ============================================================
-- 1. 用户：2 名管理员、3 名宿管、24 名学生
-- ============================================================
INSERT INTO users
    (id, username, password, real_name, role, email, phone, status, last_login_time)
VALUES
    (1, 'admin', @demo_password, '系统管理员', 'admin', 'admin@example.edu.cn', '13800000001', 1, '2026-08-10 09:00:00'),
    (2, 'auditor', @demo_password, '审核管理员', 'admin', 'auditor@example.edu.cn', '13800000002', 1, '2026-08-10 09:10:00'),
    (3, 'manager', @demo_password, '男生宿舍管理员', 'manager', 'manager.m@example.edu.cn', '13800000003', 1, '2026-08-10 08:30:00'),
    (4, 'manager_female', @demo_password, '女生宿舍管理员', 'manager', 'manager.f@example.edu.cn', '13800000004', 1, '2026-08-10 08:35:00'),
    (5, 'manager_readonly', @demo_password, '值班查看员', 'manager', 'manager.ro@example.edu.cn', '13800000005', 1, NULL),
    (6, 'student', @demo_password, '张三', 'student', '20230001@example.edu.cn', '13900000001', 1, '2026-08-10 10:01:00'),
    (7, 'student01', @demo_password, '李强', 'student', '20230002@example.edu.cn', '13900000002', 1, '2026-08-10 10:02:00'),
    (8, 'student02', @demo_password, '王磊', 'student', '20230003@example.edu.cn', '13900000003', 1, '2026-08-10 10:03:00'),
    (9, 'student03', @demo_password, '赵晨', 'student', '20230004@example.edu.cn', '13900000004', 1, NULL),
    (10, 'student04', @demo_password, '陈浩', 'student', '20240001@example.edu.cn', '13900000005', 1, NULL),
    (11, 'student05', @demo_password, '刘洋', 'student', '20240002@example.edu.cn', '13900000006', 1, NULL),
    (12, 'student06', @demo_password, '杨帆', 'student', '20240003@example.edu.cn', '13900000007', 1, NULL),
    (13, 'student07', @demo_password, '黄杰', 'student', '20240004@example.edu.cn', '13900000008', 1, NULL),
    (14, 'student08', @demo_password, '周宇', 'student', '20250001@example.edu.cn', '13900000009', 1, NULL),
    (15, 'student09', @demo_password, '吴昊', 'student', '20250002@example.edu.cn', '13900000010', 1, NULL),
    (16, 'student10', @demo_password, '徐涛', 'student', '20250003@example.edu.cn', '13900000011', 1, NULL),
    (17, 'student11', @demo_password, '孙博', 'student', '20250004@example.edu.cn', '13900000012', 1, NULL),
    (18, 'student12', @demo_password, '李娜', 'student', '20230005@example.edu.cn', '13900000013', 1, '2026-08-10 10:12:00'),
    (19, 'student13', @demo_password, '王芳', 'student', '20230006@example.edu.cn', '13900000014', 1, NULL),
    (20, 'student14', @demo_password, '赵敏', 'student', '20230007@example.edu.cn', '13900000015', 1, NULL),
    (21, 'student15', @demo_password, '陈静', 'student', '20230008@example.edu.cn', '13900000016', 1, NULL),
    (22, 'student16', @demo_password, '刘欣', 'student', '20240005@example.edu.cn', '13900000017', 1, NULL),
    (23, 'student17', @demo_password, '杨雪', 'student', '20240006@example.edu.cn', '13900000018', 1, NULL),
    (24, 'student18', @demo_password, '黄婷', 'student', '20240007@example.edu.cn', '13900000019', 1, NULL),
    (25, 'student19', @demo_password, '周倩', 'student', '20240008@example.edu.cn', '13900000020', 1, NULL),
    (26, 'student20', @demo_password, '吴悦', 'student', '20250005@example.edu.cn', '13900000021', 1, NULL),
    (27, 'student21', @demo_password, '徐琳', 'student', '20250006@example.edu.cn', '13900000022', 1, NULL),
    (28, 'student22', @demo_password, '孙萌', 'student', '20250007@example.edu.cn', '13900000023', 1, NULL),
    (29, 'student23', @demo_password, '郑洁', 'student', '20220001@example.edu.cn', '13900000024', 0, NULL);

-- ============================================================
-- 2. 学生：男女各 12 人，覆盖不同年级、专业和状态
-- ============================================================
INSERT INTO students
    (id, user_id, student_no, name, gender, grade, major, class_name, phone,
     emergency_contact, emergency_phone, is_new, enrollment_date, status)
VALUES
    (1, 6, '20230001', '张三', 'M', '2023', '计算机科学与技术', '计科2301班', '13900000001', '张建国', '13700000001', 'N', '2023-09-01', 1),
    (2, 7, '20230002', '李强', 'M', '2023', '软件工程', '软件2301班', '13900000002', '李明', '13700000002', 'N', '2023-09-01', 1),
    (3, 8, '20230003', '王磊', 'M', '2023', '网络工程', '网络2301班', '13900000003', '王军', '13700000003', 'N', '2023-09-01', 1),
    (4, 9, '20230004', '赵晨', 'M', '2023', '信息安全', '信安2301班', '13900000004', '赵刚', '13700000004', 'N', '2023-09-01', 1),
    (5, 10, '20240001', '陈浩', 'M', '2024', '计算机科学与技术', '计科2401班', '13900000005', '陈伟', '13700000005', 'N', '2024-09-01', 1),
    (6, 11, '20240002', '刘洋', 'M', '2024', '软件工程', '软件2401班', '13900000006', '刘军', '13700000006', 'N', '2024-09-01', 1),
    (7, 12, '20240003', '杨帆', 'M', '2024', '人工智能', '智能2401班', '13900000007', '杨勇', '13700000007', 'N', '2024-09-01', 1),
    (8, 13, '20240004', '黄杰', 'M', '2024', '数据科学与大数据技术', '数据2401班', '13900000008', '黄峰', '13700000008', 'N', '2024-09-01', 1),
    (9, 14, '20250001', '周宇', 'M', '2025', '计算机科学与技术', '计科2501班', '13900000009', '周强', '13700000009', 'Y', '2025-09-01', 1),
    (10, 15, '20250002', '吴昊', 'M', '2025', '软件工程', '软件2501班', '13900000010', '吴斌', '13700000010', 'Y', '2025-09-01', 1),
    (11, 16, '20250003', '徐涛', 'M', '2025', '物联网工程', '物联2501班', '13900000011', '徐伟', '13700000011', 'Y', '2025-09-01', 1),
    (12, 17, '20250004', '孙博', 'M', '2025', '信息安全', '信安2501班', '13900000012', '孙平', '13700000012', 'Y', '2025-09-01', 1),
    (13, 18, '20230005', '李娜', 'F', '2023', '计算机科学与技术', '计科2302班', '13900000013', '李梅', '13700000013', 'N', '2023-09-01', 1),
    (14, 19, '20230006', '王芳', 'F', '2023', '软件工程', '软件2302班', '13900000014', '王丽', '13700000014', 'N', '2023-09-01', 1),
    (15, 20, '20230007', '赵敏', 'F', '2023', '网络工程', '网络2302班', '13900000015', '赵霞', '13700000015', 'N', '2023-09-01', 1),
    (16, 21, '20230008', '陈静', 'F', '2023', '信息安全', '信安2302班', '13900000016', '陈敏', '13700000016', 'N', '2023-09-01', 1),
    (17, 22, '20240005', '刘欣', 'F', '2024', '计算机科学与技术', '计科2402班', '13900000017', '刘芳', '13700000017', 'N', '2024-09-01', 1),
    (18, 23, '20240006', '杨雪', 'F', '2024', '软件工程', '软件2402班', '13900000018', '杨丽', '13700000018', 'N', '2024-09-01', 1),
    (19, 24, '20240007', '黄婷', 'F', '2024', '人工智能', '智能2402班', '13900000019', '黄燕', '13700000019', 'N', '2024-09-01', 1),
    (20, 25, '20240008', '周倩', 'F', '2024', '数据科学与大数据技术', '数据2402班', '13900000020', '周莉', '13700000020', 'N', '2024-09-01', 1),
    (21, 26, '20250005', '吴悦', 'F', '2025', '计算机科学与技术', '计科2502班', '13900000021', '吴芳', '13700000021', 'Y', '2025-09-01', 1),
    (22, 27, '20250006', '徐琳', 'F', '2025', '软件工程', '软件2502班', '13900000022', '徐静', '13700000022', 'Y', '2025-09-01', 1),
    (23, 28, '20250007', '孙萌', 'F', '2025', '数字媒体技术', '数媒2501班', '13900000023', '孙丽', '13700000023', 'Y', '2025-09-01', 2),
    (24, 29, '20220001', '郑洁', 'F', '2022', '计算机科学与技术', '计科2201班', '13900000024', '郑芳', '13700000024', 'N', '2022-09-01', 0);

-- ============================================================
-- 3. 宿舍：4 栋、16 间，覆盖空闲、满员、维修和关闭状态
-- ============================================================
INSERT INTO dormitories
    (id, building_no, floor_no, room_no, gender, capacity, occupied, room_type,
     has_air_conditioner, has_private_bathroom, price_per_term, status, description, version)
VALUES
    (1, '1栋', 1, '101', 'M', 4, 0, 'standard', 1, 0, 1200.00, 'available', '男生标准四人间', 0),
    (2, '1栋', 1, '102', 'M', 4, 0, 'standard', 1, 0, 1200.00, 'available', '男生标准四人间', 0),
    (3, '1栋', 2, '201', 'M', 4, 0, 'standard', 1, 1, 1500.00, 'available', '带独立卫浴', 0),
    (4, '1栋', 2, '202', 'M', 4, 0, 'standard', 0, 0, 1000.00, 'available', '普通四人间', 0),
    (5, '2栋', 1, '101', 'M', 4, 0, 'suite', 1, 1, 1800.00, 'available', '男生四人套间', 0),
    (6, '2栋', 1, '102', 'M', 4, 0, 'standard', 1, 1, 1500.00, 'available', '男生标准四人间', 0),
    (7, '2栋', 2, '201', 'M', 4, 0, 'standard', 1, 0, 1200.00, 'available', '备用男生宿舍', 0),
    (8, '2栋', 2, '202', 'M', 4, 0, 'standard', 1, 0, 1200.00, 'maintenance', '空调检修中', 0),
    (9, '3栋', 1, '101', 'F', 4, 0, 'standard', 1, 0, 1200.00, 'available', '女生标准四人间', 0),
    (10, '3栋', 1, '102', 'F', 4, 0, 'standard', 1, 0, 1200.00, 'available', '女生标准四人间', 0),
    (11, '3栋', 2, '201', 'F', 4, 0, 'standard', 1, 1, 1500.00, 'available', '带独立卫浴', 0),
    (12, '3栋', 2, '202', 'F', 4, 0, 'standard', 0, 0, 1000.00, 'available', '普通四人间', 0),
    (13, '4栋', 1, '101', 'F', 4, 0, 'suite', 1, 1, 1800.00, 'available', '女生四人套间', 0),
    (14, '4栋', 1, '102', 'F', 4, 0, 'standard', 1, 1, 1500.00, 'available', '女生标准四人间', 0),
    (15, '4栋', 2, '201', 'F', 4, 0, 'standard', 1, 0, 1200.00, 'available', '备用女生宿舍', 0),
    (16, '4栋', 2, '202', 'F', 4, 0, 'standard', 1, 0, 1200.00, 'closed', '本学期暂停使用', 0);

-- ============================================================
-- 4. 宿管权限：男女宿管完全控制，值班人员只读查看
-- ============================================================
INSERT INTO manager_permissions (manager_id, dorm_id, permission_type)
SELECT 3, id, 'full' FROM dormitories WHERE gender = 'M';

INSERT INTO manager_permissions (manager_id, dorm_id, permission_type)
SELECT 4, id, 'full' FROM dormitories WHERE gender = 'F';

INSERT INTO manager_permissions (manager_id, dorm_id, permission_type)
SELECT 5, id, 'readonly' FROM dormitories;

-- ============================================================
-- 5. 入住和申请记录
-- active: 15；pending: 4；rejected: 2；canceled: 2；left: 3
-- ============================================================
INSERT INTO dorm_assignments
    (id, student_id, dorm_id, bed_no, start_date, end_date, status, type, semester,
     apply_reason, audit_time, auditor_id, audit_remark, created_at)
VALUES
    (1, 1, 1, 'A', '2025-09-01', NULL, 'active', 'new_checkin', '2025-2026-1', '新生统一入住', '2025-08-28 09:00:00', 1, '资料齐全，同意入住', '2025-08-25 10:00:00'),
    (2, 2, 1, 'B', '2025-09-01', NULL, 'active', 'new_checkin', '2025-2026-1', '新生统一入住', '2025-08-28 09:05:00', 1, '资料齐全，同意入住', '2025-08-25 10:05:00'),
    (3, 3, 1, 'C', '2025-09-01', NULL, 'active', 'new_checkin', '2025-2026-1', '新生统一入住', '2025-08-28 09:10:00', 1, '资料齐全，同意入住', '2025-08-25 10:10:00'),
    (4, 4, 1, 'D', '2025-09-01', NULL, 'active', 'new_checkin', '2025-2026-1', '新生统一入住', '2025-08-28 09:15:00', 1, '资料齐全，同意入住', '2025-08-25 10:15:00'),
    (5, 5, 2, 'A', '2025-09-01', NULL, 'active', 'new_checkin', '2025-2026-1', '申请入住', '2025-08-28 09:20:00', 2, '同意', '2025-08-26 11:00:00'),
    (6, 6, 2, 'B', '2025-09-01', NULL, 'active', 'new_checkin', '2025-2026-1', '申请入住', '2025-08-28 09:25:00', 2, '同意', '2025-08-26 11:05:00'),
    (7, 7, 3, 'A', '2025-09-01', NULL, 'active', 'new_checkin', '2025-2026-1', '申请入住', '2025-08-28 09:30:00', 1, '同意', '2025-08-26 11:10:00'),
    (8, 8, 2, 'C', '2025-09-01', '2026-02-20', 'left', 'new_checkin', '2025-2026-1', '原宿舍入住', '2025-08-28 09:35:00', 1, '同意', '2025-08-26 11:15:00'),
    (9, 8, 3, 'B', '2026-02-21', NULL, 'active', 'transfer', '2025-2026-2', '希望调到安静楼层', '2026-02-20 14:00:00', 2, '情况属实，同意调宿', '2026-02-18 09:00:00'),
    (10, 13, 9, 'A', '2025-09-01', NULL, 'active', 'new_checkin', '2025-2026-1', '新生统一入住', '2025-08-28 10:00:00', 1, '同意', '2025-08-25 10:30:00'),
    (11, 14, 9, 'B', '2025-09-01', NULL, 'active', 'new_checkin', '2025-2026-1', '新生统一入住', '2025-08-28 10:05:00', 1, '同意', '2025-08-25 10:35:00'),
    (12, 15, 9, 'C', '2025-09-01', NULL, 'active', 'new_checkin', '2025-2026-1', '新生统一入住', '2025-08-28 10:10:00', 1, '同意', '2025-08-25 10:40:00'),
    (13, 16, 9, 'D', '2025-09-01', NULL, 'active', 'new_checkin', '2025-2026-1', '新生统一入住', '2025-08-28 10:15:00', 1, '同意', '2025-08-25 10:45:00'),
    (14, 17, 10, 'A', '2025-09-01', NULL, 'active', 'new_checkin', '2025-2026-1', '申请入住', '2025-08-28 10:20:00', 2, '同意', '2025-08-26 11:30:00'),
    (15, 18, 10, 'B', '2025-09-01', NULL, 'active', 'new_checkin', '2025-2026-1', '申请入住', '2025-08-28 10:25:00', 2, '同意', '2025-08-26 11:35:00'),
    (16, 19, 11, 'A', '2025-09-01', NULL, 'active', 'new_checkin', '2025-2026-1', '申请入住', '2025-08-28 10:30:00', 1, '同意', '2025-08-26 11:40:00'),
    (17, 9, 4, 'A', '2026-08-15', NULL, 'pending', 'new_checkin', '2026-2027-1', '希望入住靠近教学楼的宿舍', NULL, NULL, NULL, '2026-08-10 08:20:00'),
    (18, 20, 12, 'A', '2026-08-15', NULL, 'pending', 'new_checkin', '2026-2027-1', '申请新学期入住', NULL, NULL, NULL, '2026-08-10 08:25:00'),
    (19, 6, 3, 'C', '2026-08-15', NULL, 'pending', 'transfer', '2026-2027-1', '希望调到同专业同学宿舍', NULL, NULL, NULL, '2026-08-10 08:30:00'),
    (20, 18, 11, 'B', '2026-08-15', NULL, 'pending', 'transfer', '2026-2027-1', '原宿舍作息差异较大', NULL, NULL, NULL, '2026-08-10 08:35:00'),
    (21, 10, 4, 'B', '2026-03-01', NULL, 'rejected', 'new_checkin', '2025-2026-2', '申请入住', '2026-02-25 14:00:00', 2, '材料不完整，请补充后重新申请', '2026-02-24 10:00:00'),
    (22, 21, 12, 'B', '2026-03-01', NULL, 'rejected', 'new_checkin', '2025-2026-2', '申请入住', '2026-02-25 14:05:00', 2, '宿舍安排调整，暂不通过', '2026-02-24 10:05:00'),
    (23, 11, 5, 'A', '2026-03-01', NULL, 'canceled', 'new_checkin', '2025-2026-2', '申请入住后个人撤销', NULL, NULL, NULL, '2026-02-24 11:00:00'),
    (24, 22, 13, 'A', '2026-03-01', NULL, 'canceled', 'new_checkin', '2025-2026-2', '家庭原因暂缓入住', NULL, NULL, NULL, '2026-02-24 11:05:00'),
    (25, 12, 6, 'A', '2025-09-01', '2026-06-30', 'left', 'new_checkin', '2025-2026-1', '历史入住记录', '2025-08-28 11:00:00', 1, '同意', '2025-08-26 12:00:00'),
    (26, 24, 14, 'A', '2022-09-01', '2026-06-30', 'left', 'graduate_leave', '2025-2026-2', '毕业离校退宿', '2026-06-25 09:00:00', 1, '毕业手续完成', '2026-06-20 09:00:00');

-- 仅 active 记录计入宿舍人数；维修和关闭状态保持不变。
UPDATE dormitories d
LEFT JOIN (
    SELECT dorm_id, COUNT(*) AS active_count
    FROM dorm_assignments
    WHERE status = 'active'
    GROUP BY dorm_id
) active_assignments ON active_assignments.dorm_id = d.id
SET d.occupied = COALESCE(active_assignments.active_count, 0),
    d.status = CASE
        WHEN d.status IN ('maintenance', 'closed') THEN d.status
        WHEN COALESCE(active_assignments.active_count, 0) >= d.capacity THEN 'full'
        ELSE 'available'
    END;

-- ============================================================
-- 6. 系统配置
-- ============================================================
INSERT INTO sys_config (config_key, config_value, description, status)
VALUES
    ('semester', '2026-2027-1', '当前学期', 1),
    ('select_start_time', '2026-08-01 08:00:00', '新生选宿舍开始时间', 1),
    ('select_end_time', '2026-09-01 18:00:00', '新生选宿舍结束时间', 1),
    ('max_dorm_capacity', '4', '宿舍最大床位', 1),
    ('init_password', '123456', '演示账号初始密码', 1),
    ('transfer_enabled', 'true', '是否开放调宿申请', 1),
    ('maintenance_notice', '2栋202宿舍正在检修', '维修公告', 1);

-- ============================================================
-- 7. 操作日志
-- ============================================================
INSERT INTO operation_logs
    (user_id, username, operation_type, target_type, target_id, operation_detail,
     ip_address, user_agent, created_at)
VALUES
    (1, 'admin', 'LOGIN', 'USER', 1, '管理员登录系统', '127.0.0.1', 'Chrome', '2026-08-10 09:00:00'),
    (2, 'auditor', 'AUDIT_APPROVE', 'ASSIGNMENT', 9, '通过学生调宿申请', '127.0.0.1', 'Chrome', '2026-08-10 09:10:00'),
    (3, 'manager', 'VIEW_DORMS', 'DORMITORY', NULL, '查看男生宿舍列表', '127.0.0.1', 'Edge', '2026-08-10 09:20:00'),
    (4, 'manager_female', 'VIEW_DORMS', 'DORMITORY', NULL, '查看女生宿舍列表', '127.0.0.1', 'Edge', '2026-08-10 09:25:00'),
    (6, 'student', 'LOGIN', 'USER', 6, '学生登录系统', '127.0.0.1', 'Chrome', '2026-08-10 10:01:00'),
    (14, 'student08', 'APPLY_CHECKIN', 'ASSIGNMENT', 17, '提交入住申请', '127.0.0.1', 'Chrome', '2026-08-10 10:05:00'),
    (25, 'student19', 'APPLY_CHECKIN', 'ASSIGNMENT', 18, '提交入住申请', '127.0.0.1', 'Chrome', '2026-08-10 10:06:00'),
    (11, 'student05', 'APPLY_TRANSFER', 'ASSIGNMENT', 19, '提交调宿申请', '127.0.0.1', 'Chrome', '2026-08-10 10:07:00'),
    (23, 'student17', 'APPLY_TRANSFER', 'ASSIGNMENT', 20, '提交调宿申请', '127.0.0.1', 'Chrome', '2026-08-10 10:08:00');

-- ============================================================
-- 8. 初始化校验摘要
-- ============================================================
SELECT 'users' AS item, COUNT(*) AS count FROM users
UNION ALL SELECT 'students', COUNT(*) FROM students
UNION ALL SELECT 'dormitories', COUNT(*) FROM dormitories
UNION ALL SELECT 'manager_permissions', COUNT(*) FROM manager_permissions
UNION ALL SELECT 'assignments', COUNT(*) FROM dorm_assignments
UNION ALL SELECT 'active_assignments', COUNT(*) FROM dorm_assignments WHERE status = 'active'
UNION ALL SELECT 'pending_assignments', COUNT(*) FROM dorm_assignments WHERE status = 'pending'
UNION ALL SELECT 'operation_logs', COUNT(*) FROM operation_logs;

SELECT d.id, d.building_no, d.room_no, d.capacity, d.occupied, d.status,
       COALESCE(a.active_count, 0) AS active_count
FROM dormitories d
LEFT JOIN (
    SELECT dorm_id, COUNT(*) AS active_count
    FROM dorm_assignments
    WHERE status = 'active'
    GROUP BY dorm_id
) a ON a.dorm_id = d.id
ORDER BY d.id;
