package com.university.dorm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.dorm.dto.request.AssignmentRequest;
import com.university.dorm.dto.request.AuditRequest;
import com.university.dorm.entity.DormAssignment;

import java.util.List;
import java.util.Map;

/**
 * 入住记录服务接口
 * <p>
 * 路径：backend/src/main/java/com/university/dorm/service/AssignmentService.java
 * 作用：定义入住申请相关的业务方法
 *
 * @author University Dorm Team
 * @version 1.0.0
 */
public interface AssignmentService {

    // ==================== 申请相关 ====================

    /**
     * 学生申请入住（选宿舍）
     *
     * @param studentId 学生ID
     * @param request   申请请求
     */
    void applyCheckin(Long studentId, AssignmentRequest request);

    /**
     * 学生申请换宿舍
     *
     * @param studentId 学生ID
     * @param request   申请请求
     */
    void applyTransfer(Long studentId, AssignmentRequest request);

    /**
     * 撤销申请（仅限待审核状态）
     *
     * @param assignmentId 申请ID
     * @param studentId    学生ID（校验权限）
     */
    void cancelApplication(Long assignmentId, Long studentId);

    // ==================== 审核相关 ====================

    /**
     * 审核申请（通过/驳回）
     *
     * @param request 审核请求
     * @param auditorId 审核人ID
     */
    void audit(AuditRequest request, Long auditorId);

    /**
     * 批量审核通过
     *
     * @param assignmentIds 申请ID列表
     * @param auditorId     审核人ID
     * @return 成功数量
     */
    int batchApprove(List<Long> assignmentIds, Long auditorId);

    /**
     * 批量审核驳回
     *
     * @param assignmentIds 申请ID列表
     * @param auditorId     审核人ID
     * @param remark        驳回理由
     * @return 成功数量
     */
    int batchReject(List<Long> assignmentIds, Long auditorId, String remark);

    // ==================== 退宿相关 ====================

    /**
     * 学生退宿
     *
     * @param assignmentId 入住记录ID
     * @param studentId    学生ID（校验权限）
     */
    void leaveDorm(Long assignmentId, Long studentId);

    /**
     * 强制退宿（管理员操作）
     *
     * @param assignmentId 入住记录ID
     * @param operatorId   操作人ID
     * @param reason       退宿原因
     */
    void forceLeaveDorm(Long assignmentId, Long operatorId, String reason);

    // ==================== 查询 ====================

    /**
     * 根据ID查询申请记录
     *
     * @param id 申请ID
     * @return 申请记录
     */
    DormAssignment getById(Long id);

    /**
     * 查询学生当前活跃的入住记录（已入住）
     *
     * @param studentId 学生ID
     * @return 入住记录
     */
    DormAssignment getActiveByStudentId(Long studentId);

    /**
     * 查询学生的所有申请记录（历史）
     *
     * @param studentId 学生ID
     * @return 申请列表（按时间倒序）
     */
    List<DormAssignment> getByStudentId(Long studentId);

    /**
     * 查询宿舍当前入住人员
     *
     * @param dormId 宿舍ID
     * @return 入住记录列表
     */
    List<DormAssignment> getActiveByDormId(Long dormId);

    /**
     * 查询所有待审核申请
     *
     * @return 待审核列表（按时间正序）
     */
    List<DormAssignment> getPendingList();

    /**
     * 查询某个宿舍的待审核申请
     *
     * @param dormId 宿舍ID
     * @return 待审核列表
     */
    List<DormAssignment> getPendingByDormId(Long dormId);

    /**
     * 分页查询申请记录
     *
     * @param pageNum   页码
     * @param pageSize  每页大小
     * @param studentId 学生ID（可选）
     * @param dormId    宿舍ID（可选）
     * @param status    状态（可选）
     * @param type      类型（可选）
     * @return 分页结果
     */
    Page<DormAssignment> pageQuery(Integer pageNum, Integer pageSize, Long studentId, Long dormId, String status, String type);

    /**
     * 根据学期查询申请记录
     *
     * @param semester 学期
     * @return 申请列表
     */
    List<DormAssignment> getBySemester(String semester);

    /**
     * 查询宿舍入住历史
     *
     * @param dormId 宿舍ID
     * @return 入住记录列表
     */
    List<DormAssignment> getHistoryByDormId(Long dormId);

    // ==================== 统计 ====================

    /**
     * 统计各状态申请数量
     *
     * @return 统计数据
     */
    List<Map<String, Object>> countByStatus();

    /**
     * 统计各类型申请数量
     *
     * @return 统计数据
     */
    List<Map<String, Object>> countByType();

    /**
     * 统计某学期的入住数据
     *
     * @param semester 学期
     * @return 统计数据
     */
    List<Map<String, Object>> countBySemester(String semester);

    /**
     * 获取待审核数量
     *
     * @return 待审核数量
     */
    Long getPendingCount();

    // ==================== 验证 ====================

    /**
     * 检查学生是否有活跃的入住记录
     *
     * @param studentId 学生ID
     * @return true-有，false-没有
     */
    boolean hasActiveAssignment(Long studentId);

    /**
     * 检查学生是否有待审核的申请
     *
     * @param studentId 学生ID
     * @return true-有，false-没有
     */
    boolean hasPendingAssignment(Long studentId);

    /**
     * 检查学生是否有活跃或待审核的记录（防止重复申请）
     *
     * @param studentId 学生ID
     * @return true-有，false-没有
     */
    boolean hasActiveOrPending(Long studentId);

    /**
     * 检查申请是否属于该学生
     *
     * @param assignmentId 申请ID
     * @param studentId    学生ID
     * @return true-是，false-不是
     */
    boolean belongsToStudent(Long assignmentId, Long studentId);

    // ==================== 宿舍入住信息 ====================

    /**
     * 获取学生当前入住信息（含宿舍详情）
     *
     * @param studentId 学生ID
     * @return 入住信息
     */
    Map<String, Object> getStudentDormInfo(Long studentId);

    /**
     * 获取宿舍入住人员列表（含学生信息）
     *
     * @param dormId 宿舍ID
     * @return 入住人员列表
     */
    List<Map<String, Object>> getDormResidents(Long dormId);
}