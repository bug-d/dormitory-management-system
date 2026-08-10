-- ============================================================
-- 大学宿舍管理系统 - 数据库建表脚本
-- 路径：database/schema.sql
-- 说明：创建所有表结构
-- ============================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS dormitory_management 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_general_ci;

USE dormitory_management;

-- ============================================================
-- 1. 用户表（统一登录账号）
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '登录账号',
    password VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密）',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    role ENUM('admin', 'manager', 'student') NOT NULL DEFAULT 'student' COMMENT '角色',
    email VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    avatar VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    status TINYINT DEFAULT 1 COMMENT '状态：1-启用 0-禁用',
    last_login_time DATETIME DEFAULT NULL COMMENT '最后登录时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_role (role),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ============================================================
-- 2. 学生信息表
-- ============================================================
CREATE TABLE IF NOT EXISTS students (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '学生ID',
    user_id INT NOT NULL COMMENT '关联用户ID',
    student_no VARCHAR(20) NOT NULL UNIQUE COMMENT '学号',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    gender ENUM('M', 'F') NOT NULL COMMENT '性别：M-男 F-女',
    grade VARCHAR(10) NOT NULL COMMENT '年级（如：2024）',
    major VARCHAR(100) NOT NULL COMMENT '专业',
    class_name VARCHAR(50) DEFAULT NULL COMMENT '班级',
    id_card VARCHAR(18) DEFAULT NULL COMMENT '身份证号',
    phone VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    emergency_contact VARCHAR(50) DEFAULT NULL COMMENT '紧急联系人',
    emergency_phone VARCHAR(20) DEFAULT NULL COMMENT '紧急联系电话',
    is_new ENUM('Y', 'N') DEFAULT 'Y' COMMENT '是否新生：Y-是 N-否',
    enrollment_date DATE DEFAULT NULL COMMENT '入学日期',
    status TINYINT DEFAULT 1 COMMENT '状态：1-在读 0-已毕业 2-休学',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_student_no (student_no),
    INDEX idx_grade (grade),
    INDEX idx_gender (gender),
    INDEX idx_is_new (is_new),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生信息表';

-- ============================================================
-- 3. 宿舍表
-- ============================================================
CREATE TABLE IF NOT EXISTS dormitories (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '宿舍ID',
    building_no VARCHAR(10) NOT NULL COMMENT '楼栋号',
    floor_no INT NOT NULL COMMENT '楼层',
    room_no VARCHAR(10) NOT NULL COMMENT '房间号',
    gender ENUM('M', 'F') NOT NULL COMMENT '性别限制：M-男 F-女',
    capacity INT DEFAULT 4 COMMENT '总床位数',
    occupied INT DEFAULT 0 COMMENT '已入住人数',
    room_type VARCHAR(20) DEFAULT 'standard' COMMENT '房间类型：standard-标准间 suite-套间',
    has_air_conditioner TINYINT DEFAULT 0 COMMENT '是否有空调：1-有 0-无',
    has_private_bathroom TINYINT DEFAULT 0 COMMENT '是否有独立卫浴：1-有 0-无',
    price_per_term DECIMAL(10,2) DEFAULT 0.00 COMMENT '每学期费用',
    status ENUM('available', 'full', 'maintenance', 'closed') DEFAULT 'available' COMMENT '状态',
    description TEXT DEFAULT NULL COMMENT '备注说明',
    version INT DEFAULT 0 COMMENT '乐观锁版本号',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_building_room (building_no, room_no),
    INDEX idx_building (building_no),
    INDEX idx_gender (gender),
    INDEX idx_status (status),
    INDEX idx_floor (floor_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宿舍信息表';

-- ============================================================
-- 4. 入住记录表
-- ============================================================
CREATE TABLE IF NOT EXISTS dorm_assignments (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    student_id INT NOT NULL COMMENT '学生ID',
    dorm_id INT NOT NULL COMMENT '宿舍ID',
    bed_no VARCHAR(5) DEFAULT NULL COMMENT '床号（A/B/C/D）',
    start_date DATE NOT NULL COMMENT '入住开始日期',
    end_date DATE DEFAULT NULL COMMENT '入住结束日期（NULL表示当前入住）',
    status ENUM('pending', 'approved', 'rejected', 'active', 'left', 'canceled') 
        DEFAULT 'pending' COMMENT '状态',
    type ENUM('new_checkin', 'transfer', 'graduate_leave', 'other') 
        DEFAULT 'new_checkin' COMMENT '类型',
    semester VARCHAR(20) DEFAULT NULL COMMENT '学期',
    apply_reason TEXT DEFAULT NULL COMMENT '申请理由',
    audit_time DATETIME DEFAULT NULL COMMENT '审核时间',
    auditor_id INT DEFAULT NULL COMMENT '审核人ID',
    audit_remark VARCHAR(255) DEFAULT NULL COMMENT '审核备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (dorm_id) REFERENCES dormitories(id) ON DELETE CASCADE,
    FOREIGN KEY (auditor_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_student (student_id),
    INDEX idx_dorm (dorm_id),
    INDEX idx_status (status),
    INDEX idx_type (type),
    INDEX idx_semester (semester)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入住记录表';

-- ============================================================
-- 5. 宿舍管理员权限表
-- ============================================================
CREATE TABLE IF NOT EXISTS manager_permissions (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '权限ID',
    manager_id INT NOT NULL COMMENT '宿舍管理员用户ID',
    dorm_id INT NOT NULL COMMENT '管辖宿舍ID',
    permission_type ENUM('full', 'readonly') DEFAULT 'full' COMMENT '权限类型：full-完全控制 readonly-只读',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (manager_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (dorm_id) REFERENCES dormitories(id) ON DELETE CASCADE,
    UNIQUE KEY uk_manager_dorm (manager_id, dorm_id),
    INDEX idx_manager (manager_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宿舍管理员权限表';

-- ============================================================
-- 6. 系统配置表
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_config (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '配置ID',
    config_key VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
    config_value VARCHAR(255) NOT NULL COMMENT '配置值',
    description VARCHAR(255) DEFAULT NULL COMMENT '配置说明',
    status TINYINT DEFAULT 1 COMMENT '状态：1-启用 0-禁用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- ============================================================
-- 7. 操作日志表
-- ============================================================
CREATE TABLE IF NOT EXISTS operation_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    user_id INT NOT NULL COMMENT '操作人ID',
    username VARCHAR(50) DEFAULT NULL COMMENT '操作人账号',
    operation_type VARCHAR(50) NOT NULL COMMENT '操作类型',
    target_type VARCHAR(50) DEFAULT NULL COMMENT '目标类型',
    target_id INT DEFAULT NULL COMMENT '目标ID',
    operation_detail TEXT DEFAULT NULL COMMENT '操作详情',
    ip_address VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    user_agent VARCHAR(255) DEFAULT NULL COMMENT '浏览器信息',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    INDEX idx_user (user_id),
    INDEX idx_created (created_at),
    INDEX idx_operation (operation_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';
