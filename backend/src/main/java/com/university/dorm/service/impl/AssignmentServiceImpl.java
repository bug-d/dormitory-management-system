package com.university.dorm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.dorm.constant.StatusConstant;
import com.university.dorm.dto.request.AssignmentRequest;
import com.university.dorm.dto.request.AuditRequest;
import com.university.dorm.entity.DormAssignment;
import com.university.dorm.entity.Dormitory;
import com.university.dorm.entity.Student;
import com.university.dorm.exception.BusinessException;
import com.university.dorm.mapper.AssignmentMapper;
import com.university.dorm.mapper.DormitoryMapper;
import com.university.dorm.mapper.StudentMapper;
import com.university.dorm.service.AssignmentService;
import com.university.dorm.service.OperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AssignmentServiceImpl implements AssignmentService {

    @Autowired
    private AssignmentMapper assignmentMapper;

    @Autowired
    private DormitoryMapper dormitoryMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private OperationLogService operationLogService;

    // ==================== 申请相关 ====================

    @Override
    @Transactional
    public void applyCheckin(Long studentId, AssignmentRequest request) {
        // 1. 检查学生是否存在
        Student student = studentMapper.selectById(studentId);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }

        // 2. 检查是否有活跃或待审核记录
        if (assignmentMapper.hasActiveOrPending(studentId)) {
            throw new BusinessException("您已有待审核或已入住的申请，不能重复申请");
        }

        // 3. 检查宿舍是否可用
        Dormitory dorm = dormitoryMapper.selectById(request.getDormId());
        if (dorm == null) {
            throw new BusinessException("宿舍不存在");
        }
        if (!dorm.isAvailable()) {
            throw new BusinessException("该宿舍已满或不可用");
        }
        if (!dorm.getGender().equals(student.getGender())) {
            throw new BusinessException("性别不匹配，不能选择该宿舍");
        }

        // 4. 增加宿舍占用数（乐观锁）
        boolean success = incrementOccupiedWithLock(request.getDormId());
        if (!success) {
            throw new BusinessException("床位已被抢，请重新选择");
        }

        // 5. 创建申请记录
        DormAssignment assignment = new DormAssignment();
        assignment.setStudentId(studentId);
        assignment.setDormId(request.getDormId());
        assignment.setBedNo(request.getBedNo());
        assignment.setStartDate(LocalDate.now());
        assignment.setStatus(StatusConstant.ASSIGNMENT_PENDING);
        assignment.setType(StatusConstant.TYPE_NEW_CHECKIN);
        assignment.setApplyReason(request.getApplyReason());
        assignment.setSemester(request.getSemester());

        assignmentMapper.insert(assignment);
        log.info("学生 {} 申请入住宿舍 {} 成功", studentId, request.getDormId());

        // ⭐ 记录申请日志到 operation_logs 表
        operationLogService.saveLog(
                "APPLY",
                "ASSIGNMENT",
                assignment.getId(),
                student.getName() + " 申请入住 " + dorm.getBuildingNo() + "-" + dorm.getRoomNo() + "-" + request.getBedNo()
        );
    }

    @Override
    @Transactional
    public void applyTransfer(Long studentId, AssignmentRequest request) {
        // 1. 检查学生是否存在且已入住
        Student student = studentMapper.selectById(studentId);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }

        DormAssignment current = assignmentMapper.selectActiveByStudentId(studentId);
        if (current == null) {
            throw new BusinessException("您当前没有入住宿舍，请先申请入住");
        }

        // 2. 检查是否有待审核申请
        if (assignmentMapper.hasPendingAssignment(studentId)) {
            throw new BusinessException("您已有待审核的申请，请等待审核完成");
        }

        // 3. 检查目标宿舍是否可用
        Dormitory dorm = dormitoryMapper.selectById(request.getDormId());
        if (dorm == null) {
            throw new BusinessException("目标宿舍不存在");
        }
        if (!dorm.isAvailable()) {
            throw new BusinessException("目标宿舍已满或不可用");
        }
        if (!dorm.getGender().equals(student.getGender())) {
            throw new BusinessException("性别不匹配");
        }

        // 4. 增加目标宿舍占用数
        boolean success = incrementOccupiedWithLock(request.getDormId());
        if (!success) {
            throw new BusinessException("目标宿舍床位已被抢，请重新选择");
        }

        // 5. 创建换宿舍申请
        DormAssignment assignment = new DormAssignment();
        assignment.setStudentId(studentId);
        assignment.setDormId(request.getDormId());
        assignment.setBedNo(request.getBedNo());
        assignment.setStartDate(LocalDate.now());
        assignment.setStatus(StatusConstant.ASSIGNMENT_PENDING);
        assignment.setType(StatusConstant.TYPE_TRANSFER);
        assignment.setApplyReason(request.getApplyReason());
        assignment.setSemester(request.getSemester());

        assignmentMapper.insert(assignment);
        log.info("学生 {} 申请换宿舍到 {} 成功", studentId, request.getDormId());

        // ⭐ 记录调宿申请日志
        Dormitory oldDorm = dormitoryMapper.selectById(current.getDormId());
        operationLogService.saveLog(
                "TRANSFER",
                "ASSIGNMENT",
                assignment.getId(),
                student.getName() + " 申请从 " + oldDorm.getBuildingNo() + "-" + oldDorm.getRoomNo() +
                        " 调宿到 " + dorm.getBuildingNo() + "-" + dorm.getRoomNo()
        );
    }

    @Override
    @Transactional
    public void cancelApplication(Long assignmentId, Long studentId) {
        DormAssignment assignment = assignmentMapper.selectById(assignmentId);
        if (assignment == null) {
            throw new BusinessException("申请不存在");
        }

        if (!assignment.getStudentId().equals(studentId)) {
            throw new BusinessException("无权操作此申请");
        }

        if (!assignment.isPending()) {
            throw new BusinessException("只有待审核的申请可以撤销");
        }

        // 释放床位
        dormitoryMapper.decrementOccupied(assignment.getDormId(),
            dormitoryMapper.selectById(assignment.getDormId()).getVersion());
        dormitoryMapper.autoUpdateStatus(assignment.getDormId());

        // 更新状态
        assignment.setStatus(StatusConstant.ASSIGNMENT_CANCELED);
        assignmentMapper.updateById(assignment);
        log.info("学生 {} 撤销申请 {}", studentId, assignmentId);
    }

    // ==================== 审核相关 ====================

    @Override
    @Transactional
    public void audit(AuditRequest request, Long auditorId) {
        DormAssignment assignment = assignmentMapper.selectById(request.getAssignmentId());
        if (assignment == null) {
            throw new BusinessException("申请不存在");
        }

        if (!assignment.isPending()) {
            throw new BusinessException("该申请已处理");
        }

        if ("approve".equals(request.getAction())) {
            approveAssignment(assignment, auditorId, request.getRemark());
        } else if ("reject".equals(request.getAction())) {
            rejectAssignment(assignment, auditorId, request.getRemark());
        } else {
            throw new BusinessException("无效的审核动作");
        }
    }

    private void approveAssignment(DormAssignment assignment, Long auditorId, String remark) {
        // 再次检查宿舍是否可用
        Dormitory dorm = dormitoryMapper.selectById(assignment.getDormId());
        if (dorm == null || dorm.isFull()) {
            throw new BusinessException("宿舍已满，无法通过");
        }

        // 更新申请状态
        int updated = assignmentMapper.approveAssignment(assignment.getId(), auditorId, remark);
        if (updated == 0) {
            throw new BusinessException("审核失败，该申请可能已被处理");
        }

        // 更新宿舍入住人数
        boolean success = incrementOccupiedWithLock(assignment.getDormId());
        if (!success) {
            throw new BusinessException("床位已被抢，请重新审核");
        }

        // 如果是换宿舍，释放旧宿舍床位
        if (StatusConstant.TYPE_TRANSFER.equals(assignment.getType())) {
            DormAssignment oldActive = assignmentMapper.selectActiveByStudentId(assignment.getStudentId());
            if (oldActive != null) {
                dormitoryMapper.decrementOccupied(oldActive.getDormId(),
                    dormitoryMapper.selectById(oldActive.getDormId()).getVersion());
                dormitoryMapper.autoUpdateStatus(oldActive.getDormId());
                oldActive.setStatus(StatusConstant.ASSIGNMENT_LEFT);
                oldActive.setEndDate(LocalDate.now());
                assignmentMapper.updateById(oldActive);
            }
        }

        // 将当前申请状态改为已入住
        assignmentMapper.activateAssignment(assignment.getId());

        // ⭐ 记录审核通过日志
        Student student = studentMapper.selectById(assignment.getStudentId());
        operationLogService.saveLog(
                "AUDIT",
                "ASSIGNMENT",
                assignment.getId(),
                "管理员审核通过 " + student.getName() + " 的入住申请（" + dorm.getBuildingNo() + "-" + dorm.getRoomNo() + "）"
        );

        log.info("审核通过申请 {}", assignment.getId());
    }

    private void rejectAssignment(DormAssignment assignment, Long auditorId, String remark) {
        int updated = assignmentMapper.rejectAssignment(assignment.getId(), auditorId, remark);
        if (updated == 0) {
            throw new BusinessException("驳回失败，该申请可能已被处理");
        }

        // 释放床位
        dormitoryMapper.decrementOccupied(assignment.getDormId(),
            dormitoryMapper.selectById(assignment.getDormId()).getVersion());
        dormitoryMapper.autoUpdateStatus(assignment.getDormId());

        // ⭐ 记录审核驳回日志
        Student student = studentMapper.selectById(assignment.getStudentId());
        operationLogService.saveLog(
                "AUDIT",
                "ASSIGNMENT",
                assignment.getId(),
                "管理员驳回 " + student.getName() + " 的入住申请，理由：" + remark
        );

        log.info("审核驳回申请 {}", assignment.getId());
    }

    @Override
    @Transactional
    public int batchApprove(List<Long> assignmentIds, Long auditorId) {
        int successCount = 0;
        for (Long id : assignmentIds) {
            try {
                AuditRequest request = new AuditRequest();
                request.setAssignmentId(id);
                request.setAction("approve");
                request.setRemark("批量通过");
                audit(request, auditorId);
                successCount++;
            } catch (Exception e) {
                log.warn("批量审核通过失败: {}", e.getMessage());
            }
        }
        return successCount;
    }

    @Override
    @Transactional
    public int batchReject(List<Long> assignmentIds, Long auditorId, String remark) {
        int successCount = 0;
        for (Long id : assignmentIds) {
            try {
                AuditRequest request = new AuditRequest();
                request.setAssignmentId(id);
                request.setAction("reject");
                request.setRemark(remark);
                audit(request, auditorId);
                successCount++;
            } catch (Exception e) {
                log.warn("批量审核驳回失败: {}", e.getMessage());
            }
        }
        return successCount;
    }

    // ==================== 退宿相关 ====================

    @Override
    @Transactional
    public void leaveDorm(Long assignmentId, Long studentId) {
        DormAssignment assignment = assignmentMapper.selectById(assignmentId);
        if (assignment == null) {
            throw new BusinessException("入住记录不存在");
        }

        if (!assignment.getStudentId().equals(studentId)) {
            throw new BusinessException("无权操作");
        }

        if (!assignment.isActive()) {
            throw new BusinessException("该记录不是当前入住状态");
        }

        // 退宿
        int updated = assignmentMapper.leaveDorm(assignmentId, LocalDate.now().toString());
        if (updated == 0) {
            throw new BusinessException("退宿失败");
        }

        // 释放床位
        dormitoryMapper.decrementOccupied(assignment.getDormId(),
            dormitoryMapper.selectById(assignment.getDormId()).getVersion());
        dormitoryMapper.autoUpdateStatus(assignment.getDormId());

        // ⭐ 记录退宿日志
        Student student = studentMapper.selectById(studentId);
        Dormitory dorm = dormitoryMapper.selectById(assignment.getDormId());
        operationLogService.saveLog(
                "CHECKOUT",
                "ASSIGNMENT",
                assignment.getId(),
                student.getName() + " 退宿 " + dorm.getBuildingNo() + "-" + dorm.getRoomNo()
        );

        log.info("学生 {} 退宿成功", studentId);
    }

    @Override
    @Transactional
    public void forceLeaveDorm(Long assignmentId, Long operatorId, String reason) {
        DormAssignment assignment = assignmentMapper.selectById(assignmentId);
        if (assignment == null) {
            throw new BusinessException("入住记录不存在");
        }

        if (!assignment.isActive()) {
            throw new BusinessException("该记录不是当前入住状态");
        }

        assignmentMapper.leaveDorm(assignmentId, LocalDate.now().toString());

        dormitoryMapper.decrementOccupied(assignment.getDormId(),
            dormitoryMapper.selectById(assignment.getDormId()).getVersion());
        dormitoryMapper.autoUpdateStatus(assignment.getDormId());

        // ⭐ 记录强制退宿日志
        Student student = studentMapper.selectById(assignment.getStudentId());
        Dormitory dorm = dormitoryMapper.selectById(assignment.getDormId());
        operationLogService.saveLog(
                "CHECKOUT",
                "ASSIGNMENT",
                assignment.getId(),
                "管理员强制 " + student.getName() + " 退宿 " + dorm.getBuildingNo() + "-" + dorm.getRoomNo() +
                        "，原因：" + reason
        );

        log.info("管理员 {} 强制退宿学生 {}，原因：{}", operatorId, assignment.getStudentId(), reason);
    }

    // ==================== 查询 ====================

    @Override
    public DormAssignment getById(Long id) {
        return assignmentMapper.selectById(id);
    }

    @Override
    public DormAssignment getActiveByStudentId(Long studentId) {
        return assignmentMapper.selectActiveByStudentId(studentId);
    }

    @Override
    public List<DormAssignment> getByStudentId(Long studentId) {
        return assignmentMapper.selectByStudentId(studentId);
    }

    @Override
    public List<DormAssignment> getActiveByDormId(Long dormId) {
        return assignmentMapper.selectActiveByDormId(dormId);
    }

    @Override
    public List<DormAssignment> getPendingList() {
        return assignmentMapper.selectPendingList();
    }

    @Override
    public List<DormAssignment> getPendingByDormId(Long dormId) {
        return assignmentMapper.selectPendingByDormId(dormId);
    }

    @Override
    public Page<DormAssignment> pageQuery(Integer pageNum, Integer pageSize, Long studentId, Long dormId, String status, String type) {
        Page<DormAssignment> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<DormAssignment> wrapper = new LambdaQueryWrapper<>();

        if (studentId != null) {
            wrapper.eq(DormAssignment::getStudentId, studentId);
        }
        if (dormId != null) {
            wrapper.eq(DormAssignment::getDormId, dormId);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(DormAssignment::getStatus, status);
        }
        if (type != null && !type.isEmpty()) {
            wrapper.eq(DormAssignment::getType, type);
        }

        wrapper.orderByDesc(DormAssignment::getCreatedAt);
        return assignmentMapper.selectPage(page, wrapper);
    }

    @Override
    public List<DormAssignment> getBySemester(String semester) {
        return assignmentMapper.selectBySemester(semester);
    }

    @Override
    public List<DormAssignment> getHistoryByDormId(Long dormId) {
        return assignmentMapper.selectHistoryByDormId(dormId);
    }

    // ==================== 统计 ====================

    @Override
    public List<Map<String, Object>> countByStatus() {
        List<Map<String, Object>> result = new ArrayList<>();

        Long pendingCount = assignmentMapper.selectCount(
            new LambdaQueryWrapper<DormAssignment>()
                .eq(DormAssignment::getStatus, StatusConstant.ASSIGNMENT_PENDING)
        );
        Long approvedCount = assignmentMapper.selectCount(
            new LambdaQueryWrapper<DormAssignment>()
                .eq(DormAssignment::getStatus, StatusConstant.ASSIGNMENT_APPROVED)
        );
        Long activeCount = assignmentMapper.selectCount(
            new LambdaQueryWrapper<DormAssignment>()
                .eq(DormAssignment::getStatus, StatusConstant.ASSIGNMENT_ACTIVE)
        );
        Long rejectedCount = assignmentMapper.selectCount(
            new LambdaQueryWrapper<DormAssignment>()
                .eq(DormAssignment::getStatus, StatusConstant.ASSIGNMENT_REJECTED)
        );
        Long leftCount = assignmentMapper.selectCount(
            new LambdaQueryWrapper<DormAssignment>()
                .eq(DormAssignment::getStatus, StatusConstant.ASSIGNMENT_LEFT)
        );

        Map<String, Object> map1 = new HashMap<>();
        map1.put("status", "待审核");
        map1.put("count", pendingCount);
        result.add(map1);

        Map<String, Object> map2 = new HashMap<>();
        map2.put("status", "已通过");
        map2.put("count", approvedCount);
        result.add(map2);

        Map<String, Object> map3 = new HashMap<>();
        map3.put("status", "已入住");
        map3.put("count", activeCount);
        result.add(map3);

        Map<String, Object> map4 = new HashMap<>();
        map4.put("status", "已驳回");
        map4.put("count", rejectedCount);
        result.add(map4);

        Map<String, Object> map5 = new HashMap<>();
        map5.put("status", "已退宿");
        map5.put("count", leftCount);
        result.add(map5);

        return result;
    }

    @Override
    public List<Map<String, Object>> countByType() {
        List<Map<String, Object>> result = new ArrayList<>();

        Long newCheckinCount = assignmentMapper.selectCount(
            new LambdaQueryWrapper<DormAssignment>()
                .eq(DormAssignment::getType, StatusConstant.TYPE_NEW_CHECKIN)
        );
        Long transferCount = assignmentMapper.selectCount(
            new LambdaQueryWrapper<DormAssignment>()
                .eq(DormAssignment::getType, StatusConstant.TYPE_TRANSFER)
        );
        Long graduateLeaveCount = assignmentMapper.selectCount(
            new LambdaQueryWrapper<DormAssignment>()
                .eq(DormAssignment::getType, StatusConstant.TYPE_GRADUATE_LEAVE)
        );
        Long otherCount = assignmentMapper.selectCount(
            new LambdaQueryWrapper<DormAssignment>()
                .eq(DormAssignment::getType, StatusConstant.TYPE_OTHER)
        );

        Map<String, Object> map1 = new HashMap<>();
        map1.put("type", "新生入住");
        map1.put("count", newCheckinCount);
        result.add(map1);

        Map<String, Object> map2 = new HashMap<>();
        map2.put("type", "调宿");
        map2.put("count", transferCount);
        result.add(map2);

        Map<String, Object> map3 = new HashMap<>();
        map3.put("type", "毕业离校");
        map3.put("count", graduateLeaveCount);
        result.add(map3);

        Map<String, Object> map4 = new HashMap<>();
        map4.put("type", "其他");
        map4.put("count", otherCount);
        result.add(map4);

        return result;
    }

    @Override
    public List<Map<String, Object>> countBySemester(String semester) {
        return assignmentMapper.countByTypeAndSemester(semester);
    }

    @Override
    public Long getPendingCount() {
        return assignmentMapper.countPending();
    }

    // ==================== 验证 ====================

    @Override
    public boolean hasActiveAssignment(Long studentId) {
        return assignmentMapper.hasActiveAssignment(studentId);
    }

    @Override
    public boolean hasPendingAssignment(Long studentId) {
        return assignmentMapper.hasPendingAssignment(studentId);
    }

    @Override
    public boolean hasActiveOrPending(Long studentId) {
        return assignmentMapper.hasActiveOrPending(studentId);
    }

    @Override
    public boolean belongsToStudent(Long assignmentId, Long studentId) {
        DormAssignment assignment = assignmentMapper.selectById(assignmentId);
        return assignment != null && assignment.getStudentId().equals(studentId);
    }

    // ==================== 宿舍入住信息 ====================

    @Override
    public Map<String, Object> getStudentDormInfo(Long studentId) {
        DormAssignment active = assignmentMapper.selectActiveByStudentId(studentId);
        if (active == null) {
            return null;
        }

        Dormitory dorm = dormitoryMapper.selectById(active.getDormId());
        Map<String, Object> result = new HashMap<>();
        result.put("assignment", active);
        result.put("dorm", dorm);
        return result;
    }

    @Override
    public List<Map<String, Object>> getDormResidents(Long dormId) {
        List<DormAssignment> activeList = assignmentMapper.selectActiveByDormId(dormId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (DormAssignment assignment : activeList) {
            Student student = studentMapper.selectById(assignment.getStudentId());
            Map<String, Object> info = new HashMap<>();
            info.put("assignment", assignment);
            info.put("student", student);
            result.add(info);
        }
        return result;
    }

    // ==================== 私有辅助方法 ====================

    private boolean incrementOccupiedWithLock(Long dormId) {
        Dormitory dorm = dormitoryMapper.selectById(dormId);
        if (dorm == null || dorm.isFull()) {
            return false;
        }
        int updated = dormitoryMapper.incrementOccupied(dormId, dorm.getVersion());
        if (updated > 0) {
            dormitoryMapper.autoUpdateStatus(dormId);
            return true;
        }
        return false;
    }
}