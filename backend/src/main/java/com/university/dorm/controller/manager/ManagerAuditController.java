package com.university.dorm.controller.manager;

import com.university.dorm.dto.request.AuditRequest;
import com.university.dorm.dto.request.BatchRejectRequest;
import com.university.dorm.dto.response.Result;
import com.university.dorm.entity.DormAssignment;
import com.university.dorm.exception.BusinessException;
import com.university.dorm.service.AssignmentService;
import com.university.dorm.service.PermissionService;
import com.university.dorm.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/manager/audit")
@RequiredArgsConstructor
public class ManagerAuditController {

    private final AssignmentService assignmentService;
    private final PermissionService permissionService;
    private final SecurityUtil securityUtil;

    @GetMapping("/pending")
    public Result<List<DormAssignment>> getPendingList() {
        Long managerId = securityUtil.getCurrentUserId();
        List<Long> dormIds = permissionService.getManagedDormIds(managerId);
        return Result.success(assignmentService.getPendingList().stream()
                .filter(item -> dormIds.contains(item.getDormId()))
                .collect(Collectors.toList()));
    }

    @PutMapping("/audit")
    public Result<Void> audit(@RequestBody @Valid AuditRequest request) {
        Long managerId = securityUtil.getCurrentUserId();
        checkAssignmentPermission(managerId, request.getAssignmentId());
        assignmentService.audit(request, managerId);
        return Result.success();
    }

    @PutMapping("/batch-approve")
    public Result<Integer> batchApprove(@RequestBody List<Long> ids) {
        Long managerId = securityUtil.getCurrentUserId();
        ids.forEach(id -> checkAssignmentPermission(managerId, id));
        return Result.success(assignmentService.batchApprove(ids, managerId));
    }

    @PutMapping("/batch-reject")
    public Result<Integer> batchReject(@RequestBody @Valid BatchRejectRequest request) {
        Long managerId = securityUtil.getCurrentUserId();
        request.getIds().forEach(id -> checkAssignmentPermission(managerId, id));
        return Result.success(assignmentService.batchReject(
                request.getIds(), managerId, request.getRemark()));
    }

    @GetMapping("/stats/pending-count")
    public Result<Long> getPendingCount() {
        return Result.success((long) getPendingList().getData().size());
    }

    @GetMapping("/stats/by-status")
    public Result<List<Map<String, Object>>> countByStatus() {
        Long managerId = securityUtil.getCurrentUserId();
        List<Long> dormIds = permissionService.getManagedDormIds(managerId);
        Map<String, Long> counts = assignmentService.getPendingList().stream()
                .filter(item -> dormIds.contains(item.getDormId()))
                .collect(Collectors.groupingBy(DormAssignment::getStatus, LinkedHashMap::new, Collectors.counting()));
        List<Map<String, Object>> result = new ArrayList<>();
        counts.forEach((status, count) -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("status", status);
            item.put("count", count);
            result.add(item);
        });
        return Result.success(result);
    }

    private void checkAssignmentPermission(Long managerId, Long assignmentId) {
        DormAssignment assignment = assignmentService.getById(assignmentId);
        if (assignment == null) {
            throw new BusinessException("申请不存在");
        }
        permissionService.checkFullPermission(managerId, assignment.getDormId());
    }
}

