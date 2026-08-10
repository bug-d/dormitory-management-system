package com.university.dorm.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类
 * <p>
 * 路径：backend/src/main/java/com/university/dorm/entity/User.java
 * 作用：对应数据库 users 表，存储用户登录账号信息
 * <p>
 * 表结构：
 * - id: 用户ID（主键，自增）
 * - username: 登录账号（唯一）
 * - password: 密码（BCrypt加密）
 * - real_name: 真实姓名
 * - role: 角色（admin/manager/student）
 * - email: 邮箱
 * - phone: 手机号
 * - avatar: 头像URL
 * - status: 状态（1-启用 0-禁用）
 * - last_login_time: 最后登录时间
 * - created_at: 创建时间
 * - updated_at: 更新时间
 *
 * @author University Dorm Team
 * @version 1.0.0
 */
@Data
@TableName("users")
public class User {

    /**
     * 用户ID（主键，自增）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 登录账号（唯一）
     */
    private String username;

    /**
     * 密码（BCrypt加密存储）
     */
    private String password;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 角色：admin-系统管理员，manager-宿舍管理员，student-学生
     *
     * @see com.university.dorm.constant.RoleConstant
     */
    private String role;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 状态：1-启用，0-禁用
     *
     * @see com.university.dorm.constant.StatusConstant#USER_ENABLED
     * @see com.university.dorm.constant.StatusConstant#USER_DISABLED
     */
    private Integer status;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 创建时间（自动填充）
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间（自动填充）
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}