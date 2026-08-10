package com.university.dorm.controller.admin;

import com.university.dorm.dto.response.Result;
import com.university.dorm.entity.User;
import com.university.dorm.service.UserService;
import com.university.dorm.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统管理员控制器
 * <p>
 * 路径：backend/src/main/java/com/university/dorm/controller/admin/AdminController.java
 * 作用：提供系统管理员相关的接口
 *
 * @author University Dorm Team
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "系统管理员管理", description = "系统管理员专用接口")
public class AdminController {

    private final UserService userService;
    private final SecurityUtil securityUtil;

    // ==================== 个人信息 ====================

    /**
     * 获取当前管理员信息
     *
     * @return 管理员信息
     */
    @GetMapping("/info")
    @Operation(summary = "获取当前管理员信息")
    public Result<User> getInfo() {
        User user = userService.getCurrentUser();
        return Result.success(user);
    }

    // ==================== 用户管理 ====================

    /**
     * 查询所有用户
     *
     * @return 用户列表
     */
    @GetMapping("/users")
    @Operation(summary = "查询所有用户")
    public Result<List<User>> listUsers() {
        List<User> users = userService.listAll();
        return Result.success(users);
    }

    /**
     * 分页查询用户
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param keyword  搜索关键字
     * @return 分页结果
     */
    @GetMapping("/users/page")
    @Operation(summary = "分页查询用户")
    public Result<Object> pageUsers(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        return Result.success(userService.pageQuery(pageNum, pageSize, keyword));
    }

    /**
     * 新增用户
     *
     * @param user 用户对象
     * @return 操作结果
     */
    @PostMapping("/users")
    @Operation(summary = "新增用户")
    public Result<Void> addUser(@RequestBody @Valid User user) {
        userService.add(user);
        return Result.success();
    }

    /**
     * 更新用户
     *
     * @param user 用户对象
     * @return 操作结果
     */
    @PutMapping("/users")
    @Operation(summary = "更新用户")
    public Result<Void> updateUser(@RequestBody @Valid User user) {
        userService.update(user);
        return Result.success();
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 操作结果
     */
    @DeleteMapping("/users/{id}")
    @Operation(summary = "删除用户")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return Result.success();
    }

    /**
     * 启用用户
     *
     * @param id 用户ID
     * @return 操作结果
     */
    @PutMapping("/users/{id}/enable")
    @Operation(summary = "启用用户")
    public Result<Void> enableUser(@PathVariable Long id) {
        userService.enable(id);
        return Result.success();
    }

    /**
     * 禁用用户
     *
     * @param id 用户ID
     * @return 操作结果
     */
    @PutMapping("/users/{id}/disable")
    @Operation(summary = "禁用用户")
    public Result<Void> disableUser(@PathVariable Long id) {
        userService.disable(id);
        return Result.success();
    }

    /**
     * 重置用户密码
     *
     * @param id 用户ID
     * @return 新密码
     */
    @PutMapping("/users/{id}/reset-password")
    @Operation(summary = "重置用户密码")
    public Result<String> resetPassword(@PathVariable Long id) {
        String newPassword = userService.resetPassword(id);
        return Result.success("重置密码成功，新密码为: " + newPassword);
    }

    // ==================== 管理员管理 ====================

    /**
     * 查询所有系统管理员
     *
     * @return 管理员列表
     */
    @GetMapping("/admins")
    @Operation(summary = "查询所有系统管理员")
    public Result<List<User>> listAdmins() {
        List<User> admins = userService.getByRole("admin");
        return Result.success(admins);
    }

    /**
     * 查询所有宿舍管理员
     *
     * @return 宿舍管理员列表
     */
    @GetMapping("/managers")
    @Operation(summary = "查询所有宿舍管理员")
    public Result<List<User>> listManagers() {
        List<User> managers = userService.getByRole("manager");
        return Result.success(managers);
    }

    // ==================== 统计 ====================

    /**
     * 获取用户总数
     *
     * @return 用户总数
     */
    @GetMapping("/stats/count")
    @Operation(summary = "获取用户总数")
    public Result<Long> getUserCount() {
        return Result.success(userService.countAll());
    }
}