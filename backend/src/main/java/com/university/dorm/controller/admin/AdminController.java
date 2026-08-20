package com.university.dorm.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.dorm.dto.response.Result;
import com.university.dorm.entity.User;
import com.university.dorm.service.UserService;
import com.university.dorm.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin")
@Tag(name = "系统管理员管理", description = "系统管理员专用接口")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityUtil securityUtil;

    // ==================== 个人信息 ====================

    @GetMapping("/info")
    @Operation(summary = "获取当前管理员信息")
    public Result<User> getInfo() {
        User user = userService.getCurrentUser();
        return Result.success(user);
    }

    // ==================== 用户管理 ====================

    /**
     * 分页查询用户（支持排序）
     */
    @GetMapping("/users/page")
    @Operation(summary = "分页查询用户")
    public Result<Page<User>> pageUsers(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String orderBy,
            @RequestParam(required = false) String orderDir) {
        Page<User> page = userService.pageQuery(pageNum, pageSize, keyword, role, status, orderBy, orderDir);
        return Result.success(page);
    }

    @GetMapping("/users/list")
    @Operation(summary = "查询所有用户")
    public Result<List<User>> listUsers() {
        List<User> users = userService.listAll();
        return Result.success(users);
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "根据ID查询用户")
    public Result<User> getUserById(@PathVariable Long id) {
        User user = userService.getById(id);
        return Result.success(user);
    }

    @PostMapping("/users")
    @Operation(summary = "新增用户")
    public Result<Void> addUser(@RequestBody @Valid User user) {
        userService.add(user);
        return Result.success();
    }

    @PutMapping("/users")
    @Operation(summary = "更新用户")
    public Result<Void> updateUser(@RequestBody @Valid User user) {
        userService.update(user);
        return Result.success();
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "删除用户")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return Result.success();
    }

    @PutMapping("/users/{id}/enable")
    @Operation(summary = "启用用户")
    public Result<Void> enableUser(@PathVariable Long id) {
        userService.enable(id);
        return Result.success();
    }

    @PutMapping("/users/{id}/disable")
    @Operation(summary = "禁用用户")
    public Result<Void> disableUser(@PathVariable Long id) {
        userService.disable(id);
        return Result.success();
    }

    @PutMapping("/users/{id}/reset-password")
    @Operation(summary = "重置用户密码")
    public Result<String> resetPassword(@PathVariable Long id) {
        String newPassword = userService.resetPassword(id);
        return Result.success(newPassword);
    }

    // ==================== 角色查询 ====================

    @GetMapping("/users/admins")
    @Operation(summary = "查询所有系统管理员")
    public Result<List<User>> listAdmins() {
        List<User> admins = userService.getByRole("admin");
        return Result.success(admins);
    }

    @GetMapping("/users/managers")
    @Operation(summary = "查询所有宿舍管理员")
    public Result<List<User>> listManagers() {
        List<User> managers = userService.getByRole("manager");
        return Result.success(managers);
    }

    // ==================== 统计 ====================

    @GetMapping("/users/stats/count")
    @Operation(summary = "获取用户总数")
    public Result<Long> getUserCount() {
        return Result.success(userService.countAll());
    }
}