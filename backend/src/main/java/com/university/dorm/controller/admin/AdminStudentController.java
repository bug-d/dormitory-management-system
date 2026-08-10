package com.university.dorm.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.dorm.dto.request.StudentRequest;
import com.university.dorm.dto.response.Result;
import com.university.dorm.entity.Student;
import com.university.dorm.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学生管理控制器（管理员）
 * <p>
 * 路径：backend/src/main/java/com/university/dorm/controller/admin/AdminStudentController.java
 * 作用：提供学生相关的管理接口（仅管理员可访问）
 *
 * @author University Dorm Team
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/admin/students")
@RequiredArgsConstructor
@Tag(name = "学生管理", description = "管理员端学生管理接口")
public class AdminStudentController {

    private final StudentService studentService;

    // ==================== 基础 CRUD ====================

    /**
     * 分页查询学生
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param keyword  搜索关键字（学号/姓名）
     * @param grade    年级（可选）
     * @param gender   性别（可选）
     * @return 分页结果
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询学生")
    public Result<Page<Student>> pageQuery(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String gender) {
        Page<Student> page = studentService.pageQuery(pageNum, pageSize, keyword, grade, gender);
        return Result.success(page);
    }

    /**
     * 查询所有学生
     *
     * @return 学生列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询所有学生")
    public Result<List<Student>> listAll() {
        List<Student> students = studentService.listAll();
        return Result.success(students);
    }

    /**
     * 根据ID查询学生
     *
     * @param id 学生ID
     * @return 学生信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询学生")
    public Result<Student> getById(@PathVariable Long id) {
        Student student = studentService.getById(id);
        return Result.success(student);
    }

    /**
     * 根据学号查询学生
     *
     * @param studentNo 学号
     * @return 学生信息
     */
    @GetMapping("/no/{studentNo}")
    @Operation(summary = "根据学号查询学生")
    public Result<Student> getByStudentNo(@PathVariable String studentNo) {
        Student student = studentService.getByStudentNo(studentNo);
        return Result.success(student);
    }

    /**
     * 新增学生
     *
     * @param request 学生请求DTO
     * @return 操作结果
     */
    @PostMapping
    @Operation(summary = "新增学生")
    public Result<Void> add(@RequestBody @Valid StudentRequest request) {
        studentService.add(request);
        return Result.success();
    }

    /**
     * 更新学生
     *
     * @param request 学生请求DTO
     * @return 操作结果
     */
    @PutMapping
    @Operation(summary = "更新学生")
    public Result<Void> update(@RequestBody @Valid StudentRequest request) {
        studentService.update(request);
        return Result.success();
    }

    /**
     * 删除学生
     *
     * @param id 学生ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除学生")
    public Result<Void> delete(@PathVariable Long id) {
        studentService.delete(id);
        return Result.success();
    }

    /**
     * 批量删除学生
     *
     * @param ids 学生ID列表
     * @return 操作结果
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除学生")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        studentService.batchDelete(ids);
        return Result.success();
    }

    // ==================== 查询 ====================

    /**
     * 查询所有新生
     *
     * @return 新生列表
     */
    @GetMapping("/new")
    @Operation(summary = "查询所有新生")
    public Result<List<Student>> getNewStudents() {
        List<Student> students = studentService.getNewStudents();
        return Result.success(students);
    }

    /**
     * 查询在读学生
     *
     * @return 在读学生列表
     */
    @GetMapping("/active")
    @Operation(summary = "查询在读学生")
    public Result<List<Student>> getActiveStudents() {
        List<Student> students = studentService.getActiveStudents();
        return Result.success(students);
    }

    /**
     * 根据年级查询学生
     *
     * @param grade 年级
     * @return 学生列表
     */
    @GetMapping("/grade/{grade}")
    @Operation(summary = "根据年级查询学生")
    public Result<List<Student>> getByGrade(@PathVariable String grade) {
        List<Student> students = studentService.getByGrade(grade);
        return Result.success(students);
    }

    /**
     * 查询未分配宿舍的学生
     *
     * @return 学生列表
     */
    @GetMapping("/without-dorm")
    @Operation(summary = "查询未分配宿舍的学生")
    public Result<List<Student>> getStudentsWithoutDorm() {
        List<Student> students = studentService.getStudentsWithoutDorm();
        return Result.success(students);
    }

    /**
     * 查询已分配宿舍的学生
     *
     * @return 学生列表
     */
    @GetMapping("/with-dorm")
    @Operation(summary = "查询已分配宿舍的学生")
    public Result<List<Student>> getStudentsWithDorm() {
        List<Student> students = studentService.getStudentsWithDorm();
        return Result.success(students);
    }

    // ==================== 统计 ====================

    /**
     * 获取学生总数
     *
     * @return 学生总数
     */
    @GetMapping("/stats/count")
    @Operation(summary = "获取学生总数")
    public Result<Long> getCount() {
        return Result.success(studentService.countAll());
    }

    // ==================== 状态管理 ====================

    /**
     * 标记学生为已毕业
     *
     * @param id 学生ID
     * @return 操作结果
     */
    @PutMapping("/{id}/graduate")
    @Operation(summary = "标记学生为已毕业")
    public Result<Void> graduate(@PathVariable Long id) {
        studentService.graduate(id);
        return Result.success();
    }

    /**
     * 标记学生为新生
     *
     * @param id 学生ID
     * @return 操作结果
     */
    @PutMapping("/{id}/mark-new")
    @Operation(summary = "标记学生为新生")
    public Result<Void> markAsNew(@PathVariable Long id) {
        studentService.markAsNew(id);
        return Result.success();
    }
}