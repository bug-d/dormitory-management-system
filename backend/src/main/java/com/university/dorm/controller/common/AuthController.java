package com.university.dorm.controller.common;

import com.university.dorm.dto.request.LoginRequest;
import com.university.dorm.dto.response.LoginResponse;
import com.university.dorm.dto.response.Result;
import com.university.dorm.entity.User;
import com.university.dorm.service.UserService;
import com.university.dorm.util.JwtUtil;
import com.university.dorm.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证控制器
 * <p>
 * 路径：backend/src/main/java/com/university/dorm/controller/common/AuthController.java
 * 作用：提供登录、登出等公共接口
 *
 * @author University Dorm Team
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "登录、登出等公共接口")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final SecurityUtil securityUtil;

    /**
     * 用户登录
     *
     * @param request 登录请求（用户名 + 密码）
     * @return 登录响应（Token + 用户信息）
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "使用用户名和密码登录系统")
    public Result<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        log.info("用户登录请求: {}", request.getUsername());
        LoginResponse response = userService.login(request);
        return Result.success("登录成功", response);
    }

    /**
     * 用户登出
     *
     * @param token 请求头中的 Token
     * @return 登出结果
     */
    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "退出登录")
    public Result<Void> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        log.info("用户登出请求");
        if (token != null && token.startsWith("Bearer ")) {
            String jwt = token.substring(7);
            userService.logout(jwt);
        }
        return Result.success("登出成功", null);
    }

    /**
     * 刷新 Token
     *
     * @param token 请求头中的 Token
     * @return 新的 Token
     */
    @PostMapping("/refresh")
    @Operation(summary = "刷新 Token", description = "使用旧 Token 获取新 Token")
    public Result<String> refreshToken(@RequestHeader(value = "Authorization") String token) {
        log.info("刷新 Token 请求");
        if (token != null && token.startsWith("Bearer ")) {
            String jwt = token.substring(7);
            return Result.success("刷新成功", jwtUtil.refreshToken(jwt));
        }
        return Result.error("Token 格式错误");
    }

    /**
     * 测试接口（验证 Token 是否有效）
     *
     * @return 测试结果
     */
    @GetMapping("/test")
    @Operation(summary = "测试接口", description = "用于验证 Token 是否有效")
    public Result<String> test() {
        return Result.success("Token 有效，请求成功");
    }

    @GetMapping("/current-user")
    @Operation(summary = "获取当前用户")
    public Result<User> getCurrentUser() {
        User user = userService.getCurrentUser();
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    @PutMapping("/change-password")
    @Operation(summary = "修改当前用户密码")
    public Result<Void> changePassword(@RequestBody Map<String, String> request) {
        userService.changePassword(
                securityUtil.getCurrentUserId(),
                request.get("oldPassword"),
                request.get("newPassword")
        );
        return Result.success();
    }
}
