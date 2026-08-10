package com.university.dorm.controller.admin;

import com.university.dorm.dto.request.AuditRequest;
import com.university.dorm.dto.request.BatchRejectRequest;
import com.university.dorm.dto.response.Result;
import com.university.dorm.entity.DormAssignment;
import com.university.dorm.service.AssignmentService;
import com.university.dorm.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin/audit")
@Tag(name = "审核管理", description = "管理员端审核管理接口")
public class AdminAuditController {

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private SecurityUtil securityUtil;

    @GetMapping("/pending")
    @Operation(summary = "查询所有待审核申请")
    public Result<List<DormAssignment>> getPendingList() {
        return Result.success(assignmentService.getPendingList());
    }

    @GetMapping("/pending/{dormId}")
    @Operation(summary = "查询某个宿舍的待审核申请")
    public Result<List<DormAssignment>> getPendingByDormId(@PathVariable Long dormId) {
        return Result.success(assignmentService.getPendingByDormId(dormId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据申请ID查询详情")
    public Result<DormAssignment> getById(@PathVariable Long id) {
        return Result.success(assignmentService.getById(id));
    }

    @PutMapping("/audit")
    @Operation(summary = "审核申请（通过/驳回）")
    public Result<Void> audit(@RequestBody @Valid AuditRequest request) {
        Long auditorId = securityUtil.getCurrentUserId();
        assignmentService.audit(request, auditorId);
        return Result.success();
    }

    @PutMapping("/batch-approve")
    @Operation(summary = "批量审核通过")
    public Result<Integer> batchApprove(@RequestBody List<Long> ids) {
        Long auditorId = securityUtil.getCurrentUserId();
        int successCount = assignmentService.batchApprove(ids, auditorId);
        return Result.success(successCount);
    }

    @PutMapping("/batch-reject")
    @Operation(summary = "批量审核驳回")
    public Result<Integer> batchReject(
            @RequestBody @Valid BatchRejectRequest request) {
        Long auditorId = securityUtil.getCurrentUserId();
        int successCount = assignmentService.batchReject(request.getIds(), auditorId, request.getRemark());
        return Result.success(successCount);
    }

    @GetMapping("/stats/pending-count")
    @Operation(summary = "获取待审核数量")
    public Result<Long> getPendingCount() {
        return Result.success(assignmentService.getPendingCount());
    }

    @GetMapping("/stats/by-status")
    @Operation(summary = "获取各状态申请数量统计")
    public Result<List<Map<String, Object>>> countByStatus() {
        return Result.success(assignmentService.countByStatus());
    }

    @GetMapping("/stats/by-type")
    @Operation(summary = "获取各类型申请数量统计")
    public Result<List<Map<String, Object>>> countByType() {
        return Result.success(assignmentService.countByType());
    }

    @PutMapping("/force-leave/{id}")
    @Operation(summary = "强制退宿")
    public Result<Void> forceLeaveDorm(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "管理员强制退宿") String reason) {
        Long operatorId = securityUtil.getCurrentUserId();
        assignmentService.forceLeaveDorm(id, operatorId, reason);
        return Result.success();
    }

    @GetMapping("/student-dorm/{studentId}")
    @Operation(summary = "获取学生当前入住信息")
    public Result<Map<String, Object>> getStudentDormInfo(@PathVariable Long studentId) {
        return Result.success(assignmentService.getStudentDormInfo(studentId));
    }

    @GetMapping("/dorm-residents/{dormId}")
    @Operation(summary = "获取宿舍入住人员列表")
    public Result<List<Map<String, Object>>> getDormResidents(@PathVariable Long dormId) {
        return Result.success(assignmentService.getDormResidents(dormId));
    }

    @GetMapping("/dorm-history/{dormId}")
    @Operation(summary = "获取宿舍入住历史")
    public Result<List<DormAssignment>> getDormHistory(@PathVariable Long dormId) {
        return Result.success(assignmentService.getHistoryByDormId(dormId));
    }
}
