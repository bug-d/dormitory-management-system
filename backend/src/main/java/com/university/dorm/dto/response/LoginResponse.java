package com.university.dorm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应 DTO
 * <p>
 * 路径：backend/src/main/java/com/university/dorm/dto/response/LoginResponse.java
 * 作用：封装登录成功后的返回数据（Token + 用户信息）
 *
 * @author University Dorm Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    /**
     * JWT Token（用于后续请求的身份认证）
     */
    private String token;

    /**
     * Token 类型（固定为 Bearer）
     */
    private String tokenType;

    /**
     * Token 过期时间（单位：秒）
     */
    private Long expiresIn;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名（登录账号）
     */
    private String username;

    /**
     * 用户真实姓名
     */
    private String realName;

    /**
     * 用户角色：admin-系统管理员，manager-宿舍管理员，student-学生
     */
    private String role;

    /**
     * 角色中文名称
     */
    private String roleName;

    /**
     * 用户状态：1-启用，0-禁用
     */
    private Integer status;
}