package com.university.dorm.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 宿舍管理员权限实体类
 * <p>
 * 路径：backend/src/main/java/com/university/dorm/entity/ManagerPermission.java
 * 作用：对应数据库 manager_permissions 表，控制宿舍管理员能管理哪些宿舍
 * <p>
 * 表结构：
 * - id: 权限ID（主键，自增）
 * - manager_id: 宿舍管理员用户ID（外键 → users.id）
 * - dorm_id: 管辖宿舍ID（外键 → dormitories.id）
 * - permission_type: 权限类型（full-完全控制，readonly-只读）
 * - created_at: 创建时间
 *
 * @author University Dorm Team
 * @version 1.0.0
 */
@Data
@TableName("manager_permissions")
public class ManagerPermission {

    /**
     * 权限ID（主键，自增）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 宿舍管理员用户ID（外键 → users.id）
     * 关联 users 表中 role = 'manager' 的用户
     */
    private Long managerId;

    /**
     * 管辖宿舍ID（外键 → dormitories.id）
     */
    private Long dormId;

    /**
     * 权限类型：
     * full-完全控制（可增删改查），readonly-只读（仅查看）
     *
     * @see com.university.dorm.constant.StatusConstant#PERMISSION_FULL
     * @see com.university.dorm.constant.StatusConstant#PERMISSION_READONLY
     */
    private String permissionType;

    /**
     * 创建时间（自动填充）
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}