-- ============================================================
-- 大学宿舍管理系统 - 测试数据
-- 路径：database/data.sql
-- 说明：插入测试数据
-- ============================================================

USE dormitory_management;

-- ============================================================
-- 1. 用户数据（密码：123456）
-- ============================================================
INSERT INTO users (username, password, real_name, role, status) VALUES
('admin', '$2a$10$m2CC3.eOjf0UwueG3xsJsOK2/aZbOsgPR.1lFdm5u1uhRqaGzdBL.', '系统管理员', 'admin', 1),
('manager', '$2a$10$m2CC3.eOjf0UwueG3xsJsOK2/aZbOsgPR.1lFdm5u1uhRqaGzdBL.', '李宿管', 'manager', 1),
('student', '$2a$10$m2CC3.eOjf0UwueG3xsJsOK2/aZbOsgPR.1lFdm5u1uhRqaGzdBL.', '张三', 'student', 1),
('student1', '$2a$10$m2CC3.eOjf0UwueG3xsJsOK2/aZbOsgPR.1lFdm5u1uhRqaGzdBL.', '李四', 'student', 1),
('student2', '$2a$10$m2CC3.eOjf0UwueG3xsJsOK2/aZbOsgPR.1lFdm5u1uhRqaGzdBL.', '王五', 'student', 1),
('student3', '$2a$10$m2CC3.eOjf0UwueG3xsJsOK2/aZbOsgPR.1lFdm5u1uhRqaGzdBL.', '赵六', 'student', 1);

-- ============================================================
-- 2. 学生数据
-- ============================================================
INSERT INTO students (user_id, student_no, name, gender, grade, major, class_name, is_new, status) VALUES
(3, '2024001', '张三', 'M', '2024', '计算机科学与技术', '计科1班', 'Y', 1),
(4, '2024002', '李四', 'F', '2024', '软件工程', '软件1班', 'Y', 1),
(5, '2024003', '王五', 'M', '2024', '网络工程', '网络1班', 'Y', 1),
(6, '2024004', '赵六', 'F', '2024', '信息安全', '安全1班', 'Y', 1);

-- ============================================================
-- 3. 宿舍数据
-- ============================================================
INSERT INTO dormitories (building_no, floor_no, room_no, gender, capacity, occupied, status) VALUES
('1栋', 1, '101', 'M', 4, 0, 'available'),
('1栋', 1, '102', 'M', 4, 0, 'available'),
('1栋', 2, '201', 'M', 4, 0, 'available'),
('1栋', 2, '202', 'M', 4, 0, 'available'),
('2栋', 1, '101', 'F', 4, 0, 'available'),
('2栋', 1, '102', 'F', 4, 0, 'available'),
('2栋', 2, '201', 'F', 4, 0, 'available'),
('2栋', 2, '202', 'F', 4, 0, 'available');

-- ============================================================
-- 4. 宿舍管理员权限
-- ============================================================
INSERT INTO manager_permissions (manager_id, dorm_id, permission_type) VALUES
(2, 1, 'full'),
(2, 2, 'full'),
(2, 3, 'full'),
(2, 4, 'full');

-- ============================================================
-- 5. 系统配置
-- ============================================================
INSERT INTO sys_config (config_key, config_value, description) VALUES
('semester', '2026-2027-1', '当前学期'),
('select_start_time', '2026-08-15 08:00:00', '新生选宿舍开始时间'),
('select_end_time', '2026-09-01 18:00:00', '新生选宿舍结束时间'),
('max_dorm_capacity', '6', '宿舍最大床位'),
('init_password', '123456', '初始密码');

-- ============================================================
-- 6. 查看数据
-- ============================================================
SELECT '========== 用户数据 ==========' AS '';
SELECT id, username, real_name, role, status FROM users;

SELECT '========== 学生数据 ==========' AS '';
SELECT id, student_no, name, gender, grade, major FROM students;

SELECT '========== 宿舍数据 ==========' AS '';
SELECT id, building_no, room_no, gender, capacity, occupied, status FROM dormitories;

SELECT '========== 系统配置 ==========' AS '';
SELECT config_key, config_value, description FROM sys_config;
