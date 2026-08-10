package com.university.dorm.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录请求 DTO
 * <p>
 * 路径：backend/src/main/java/com/university/dorm/dto/request/LoginRequest.java
 * 作用：接收前端登录请求参数（用户名和密码）
 *
 * @author University Dorm Team
 * @version 1.0.0
 */
@Data
public class LoginRequest {

    /**
     * 用户名（不能为空）
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码（不能为空）
     */
    @NotBlank(message = "密码不能为空")
    private String password;
}