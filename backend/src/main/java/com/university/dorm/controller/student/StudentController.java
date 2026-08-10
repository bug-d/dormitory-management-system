package com.university.dorm.controller.student;

import com.university.dorm.dto.request.AssignmentRequest;
import com.university.dorm.dto.response.Result;
import com.university.dorm.entity.DormAssignment;
import com.university.dorm.entity.Dormitory;
import com.university.dorm.entity.Student;
import com.university.dorm.service.AssignmentService;
import com.university.dorm.service.DormitoryService;
import com.university.dorm.service.StudentService;
import com.university.dorm.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 学生端控制器
 * <p>
 * 路径：backend/src/main/java/com/university/dorm/controller/student/StudentController.java
 * 作用：提供学生端接口（仅学生可访问）
 * 路径前缀：/student
 *
 * @author University Dorm Team
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/student")
@Tag(name = "学生端", description = "学生端接口")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private DormitoryService dormitoryService;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private SecurityUtil securityUtil;

    // ==================== 个人信息 ====================

    /**
     * 获取当前学生信息
     */
    @GetMapping("/info")
    @Operation(summary = "获取当前学生信息")
    public Result<Student> getInfo() {
        Long userId = securityUtil.getCurrentUserId();
        Student student = studentService.getByUserId(userId);
        if (student == null) {
            return Result.error("学生信息不存在");
        }
        return Result.success(student);
    }

    // ==================== 选宿舍 ====================

    /**
     * 查询可选宿舍（根据性别）
     */
    @GetMapping("/dorms/available")
    @Operation(summary = "查询可选宿舍")
    public Result<List<Dormitory>> getAvailableDorms() {
        Long userId = securityUtil.getCurrentUserId();
        Student student = studentService.getByUserId(userId);
        if (student == null) {
            return Result.error("学生信息不存在");
        }
        List<Dormitory> dorms = dormitoryService.getAvailableDormsByGender(student.getGender());
        return Result.success(dorms);
    }

    /**
     * 申请入住（选宿舍）
     */
    @PostMapping("/apply-checkin")
    @Operation(summary = "申请入住（选宿舍）")
    public Result<Void> applyCheckin(@RequestBody @Valid AssignmentRequest request) {
        Long userId = securityUtil.getCurrentUserId();
        Student student = studentService.getByUserId(userId);
        if (student == null) {
            return Result.error("学生信息不存在");
        }
        assignmentService.applyCheckin(student.getId(), request);
        return Result.success();
    }

    /**
     * 申请换宿舍
     */
    @PostMapping("/apply-transfer")
    @Operation(summary = "申请换宿舍")
    public Result<Void> applyTransfer(@RequestBody @Valid AssignmentRequest request) {
        Long userId = securityUtil.getCurrentUserId();
        Student student = studentService.getByUserId(userId);
        if (student == null) {
            return Result.error("学生信息不存在");
        }
        assignmentService.applyTransfer(student.getId(), request);
        return Result.success();
    }

    /**
     * 撤销申请
     */
    @DeleteMapping("/applications/{assignmentId}")
    @Operation(summary = "撤销申请")
    public Result<Void> cancelApplication(@PathVariable Long assignmentId) {
        Long userId = securityUtil.getCurrentUserId();
        Student student = studentService.getByUserId(userId);
        if (student == null) {
            return Result.error("学生信息不存在");
        }
        assignmentService.cancelApplication(assignmentId, student.getId());
        return Result.success();
    }

    // ==================== 我的宿舍 ====================

    /**
     * 获取我的宿舍信息
     */
    @GetMapping("/my-dorm")
    @Operation(summary = "获取我的宿舍信息")
    public Result<Map<String, Object>> getMyDorm() {
        Long userId = securityUtil.getCurrentUserId();
        Student student = studentService.getByUserId(userId);
        if (student == null) {
            return Result.error("学生信息不存在");
        }
        Map<String, Object> dormInfo = assignmentService.getStudentDormInfo(student.getId());
        if (dormInfo == null) {
            return Result.error("您当前没有入住宿舍");
        }
        DormAssignment assignment = (DormAssignment) dormInfo.get("assignment");
        dormInfo.put("roommates", assignmentService.getDormResidents(assignment.getDormId()));
        return Result.success(dormInfo);
    }

    /**
     * 退宿
     */
    @PutMapping("/leave-dorm")
    @Operation(summary = "退宿")
    public Result<Void> leaveDorm() {
        Long userId = securityUtil.getCurrentUserId();
        Student student = studentService.getByUserId(userId);
        if (student == null) {
            return Result.error("学生信息不存在");
        }
        DormAssignment active = assignmentService.getActiveByStudentId(student.getId());
        if (active == null) {
            return Result.error("您当前没有入住宿舍");
        }
        assignmentService.leaveDorm(active.getId(), student.getId());
        return Result.success();
    }

    // ==================== 申请记录 ====================

    /**
     * 获取我的申请记录列表
     */
    @GetMapping("/applications")
    @Operation(summary = "获取我的申请记录列表")
    public Result<List<DormAssignment>> getMyApplications() {
        Long userId = securityUtil.getCurrentUserId();
        Student student = studentService.getByUserId(userId);
        if (student == null) {
            return Result.error("学生信息不存在");
        }
        List<DormAssignment> assignments = assignmentService.getByStudentId(student.getId());
        return Result.success(assignments);
    }

    /**
     * 获取待审核申请数量
     */
    @GetMapping("/applications/pending-count")
    @Operation(summary = "获取待审核申请数量")
    public Result<Long> getPendingCount() {
        Long userId = securityUtil.getCurrentUserId();
        Student student = studentService.getByUserId(userId);
        if (student == null) {
            return Result.error("学生信息不存在");
        }
        boolean hasPending = assignmentService.hasPendingAssignment(student.getId());
        return Result.success(hasPending ? 1L : 0L);
    }

    // ==================== 宿舍查询 ====================

    /**
     * 查询所有宿舍楼栋
     */
    @GetMapping("/dorms/buildings")
    @Operation(summary = "查询所有宿舍楼栋")
    public Result<List<String>> getAllBuildings() {
        List<String> buildings = dormitoryService.getAllBuildings();
        return Result.success(buildings);
    }

    /**
     * 根据楼栋查询宿舍
     */
    @GetMapping("/dorms/building/{buildingNo}")
    @Operation(summary = "根据楼栋查询宿舍")
    public Result<List<Dormitory>> getDormsByBuilding(@PathVariable String buildingNo) {
        List<Dormitory> dorms = dormitoryService.getByBuilding(buildingNo);
        return Result.success(dorms);
    }

    /**
     * 查询宿舍详情
     */
    @GetMapping("/dorms/{id}")
    @Operation(summary = "查询宿舍详情")
    public Result<Dormitory> getDormDetail(@PathVariable Long id) {
        Dormitory dorm = dormitoryService.getById(id);
        if (dorm == null) {
            return Result.notFound("宿舍不存在");
        }
        return Result.success(dorm);
    }
}
