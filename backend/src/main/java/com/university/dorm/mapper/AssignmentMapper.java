package com.university.dorm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.university.dorm.entity.DormAssignment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * 入住记录 Mapper 接口
 * <p>
 * 路径：backend/src/main/java/com/university/dorm/mapper/AssignmentMapper.java
 * 作用：入住记录数据访问层，继承 BaseMapper 自动获得 CRUD 方法
 *
 * @author University Dorm Team
 * @version 1.0.0
 */
@Mapper
public interface AssignmentMapper extends BaseMapper<DormAssignment> {

    /**
     * 根据学生ID查询当前入住记录（状态为 active）
     *
     * @param studentId 学生ID
     * @return 入住记录
     */
    @Select("SELECT * FROM dorm_assignments WHERE student_id = #{studentId} AND status = 'active'")
    DormAssignment selectActiveByStudentId(@Param("studentId") Long studentId);

    /**
     * 根据学生ID查询所有入住记录（按时间倒序）
     *
     * @param studentId 学生ID
     * @return 入住记录列表
     */
    @Select("SELECT * FROM dorm_assignments WHERE student_id = #{studentId} ORDER BY created_at DESC")
    List<DormAssignment> selectByStudentId(@Param("studentId") Long studentId);

    /**
     * 根据宿舍ID查询当前入住记录（状态为 active）
     *
     * @param dormId 宿舍ID
     * @return 入住记录列表
     */
    @Select("SELECT * FROM dorm_assignments WHERE dorm_id = #{dormId} AND status = 'active'")
    List<DormAssignment> selectActiveByDormId(@Param("dormId") Long dormId);

    /**
     * 查询所有待审核的申请记录
     *
     * @return 待审核列表
     */
    @Select("SELECT * FROM dorm_assignments WHERE status = 'pending' ORDER BY created_at ASC")
    List<DormAssignment> selectPendingList();

    /**
     * 查询某个宿舍的待审核申请
     *
     * @param dormId 宿舍ID
     * @return 待审核列表
     */
    @Select("SELECT * FROM dorm_assignments WHERE dorm_id = #{dormId} AND status = 'pending' ORDER BY created_at ASC")
    List<DormAssignment> selectPendingByDormId(@Param("dormId") Long dormId);

    /**
     * 根据学期查询入住记录
     *
     * @param semester 学期
     * @return 入住记录列表
     */
    @Select("SELECT * FROM dorm_assignments WHERE semester = #{semester} ORDER BY created_at DESC")
    List<DormAssignment> selectBySemester(@Param("semester") String semester);

    /**
     * 根据类型查询入住记录
     *
     * @param type 类型
     * @return 入住记录列表
     */
    @Select("SELECT * FROM dorm_assignments WHERE type = #{type} ORDER BY created_at DESC")
    List<DormAssignment> selectByType(@Param("type") String type);

    /**
     * 统计某宿舍当前入住人数
     *
     * @param dormId 宿舍ID
     * @return 入住人数
     */
    @Select("SELECT COUNT(*) FROM dorm_assignments WHERE dorm_id = #{dormId} AND status = 'active'")
    Long countActiveByDormId(@Param("dormId") Long dormId);

    /**
     * 检查学生是否已有活跃入住记录
     *
     * @param studentId 学生ID
     * @return true-有，false-没有
     */
    @Select("SELECT COUNT(*) > 0 FROM dorm_assignments WHERE student_id = #{studentId} AND status = 'active'")
    boolean hasActiveAssignment(@Param("studentId") Long studentId);

    /**
     * 检查学生是否有待审核申请
     *
     * @param studentId 学生ID
     * @return true-有，false-没有
     */
    @Select("SELECT COUNT(*) > 0 FROM dorm_assignments WHERE student_id = #{studentId} AND status = 'pending'")
    boolean hasPendingAssignment(@Param("studentId") Long studentId);

    /**
     * 更新状态为已入住（审核通过）
     *
     * @param assignmentId 申请ID
     * @param auditorId    审核人ID
     * @param remark       审核备注
     * @return 影响行数
     */
    @Update("UPDATE dorm_assignments SET status = 'approved', audit_time = NOW(), auditor_id = #{auditorId}, audit_remark = #{remark} WHERE id = #{assignmentId} AND status = 'pending'")
    int approveAssignment(@Param("assignmentId") Long assignmentId, 
                          @Param("auditorId") Long auditorId, 
                          @Param("remark") String remark);

    /**
     * 更新状态为已驳回
     *
     * @param assignmentId 申请ID
     * @param auditorId    审核人ID
     * @param remark       驳回理由
     * @return 影响行数
     */
    @Update("UPDATE dorm_assignments SET status = 'rejected', audit_time = NOW(), auditor_id = #{auditorId}, audit_remark = #{remark} WHERE id = #{assignmentId} AND status = 'pending'")
    int rejectAssignment(@Param("assignmentId") Long assignmentId, 
                         @Param("auditorId") Long auditorId, 
                         @Param("remark") String remark);

    /**
     * 更新状态为已入住
     *
     * @param assignmentId 申请ID
     * @return 影响行数
     */
    @Update("UPDATE dorm_assignments SET status = 'active' WHERE id = #{assignmentId} AND status = 'approved'")
    int activateAssignment(@Param("assignmentId") Long assignmentId);

    /**
     * 退宿（更新状态为已退宿）
     *
     * @param assignmentId 入住记录ID
     * @param endDate      退宿日期
     * @return 影响行数
     */
    @Update("UPDATE dorm_assignments SET status = 'left', end_date = #{endDate} WHERE id = #{assignmentId} AND status = 'active'")
    int leaveDorm(@Param("assignmentId") Long assignmentId, @Param("endDate") String endDate);

    /**
     * 获取各状态申请数量统计
     *
     * @return 统计结果
     */
    @Select("SELECT status, COUNT(*) as count FROM dorm_assignments GROUP BY status")
    List<Map<String, Object>> countByStatus();

    /**
     * 获取某学期的入住统计
     *
     * @param semester 学期
     * @return 统计结果
     */
    @Select("SELECT type, COUNT(*) as count FROM dorm_assignments WHERE semester = #{semester} GROUP BY type")
    List<Map<String, Object>> countByTypeAndSemester(@Param("semester") String semester);

    /**
     * 获取某宿舍的入住历史
     *
     * @param dormId 宿舍ID
     * @return 入住记录列表
     */
    @Select("SELECT * FROM dorm_assignments WHERE dorm_id = #{dormId} ORDER BY created_at DESC")
    List<DormAssignment> selectHistoryByDormId(@Param("dormId") Long dormId);

    /**
     * 查询待审核申请的数量
     *
     * @return 数量
     */
    @Select("SELECT COUNT(*) FROM dorm_assignments WHERE status = 'pending'")
    Long countPending();

    /**
     * 查询学生是否有活跃或待审核的记录（用于防止重复申请）
     *
     * @param studentId 学生ID
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM dorm_assignments WHERE student_id = #{studentId} AND status IN ('pending', 'active')")
    boolean hasActiveOrPending(@Param("studentId") Long studentId);
}