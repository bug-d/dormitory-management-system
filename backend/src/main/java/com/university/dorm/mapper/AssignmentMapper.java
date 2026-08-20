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
 *
 * @author University Dorm Team
 * @version 1.0.0
 */
@Mapper
public interface AssignmentMapper extends BaseMapper<DormAssignment> {

    // ==================== 查询 ====================

    @Select("SELECT * FROM dorm_assignments WHERE student_id = #{studentId} AND status = 'active'")
    DormAssignment selectActiveByStudentId(@Param("studentId") Long studentId);

    @Select("SELECT * FROM dorm_assignments WHERE student_id = #{studentId} ORDER BY created_at DESC")
    List<DormAssignment> selectByStudentId(@Param("studentId") Long studentId);

    @Select("SELECT * FROM dorm_assignments WHERE dorm_id = #{dormId} AND status = 'active'")
    List<DormAssignment> selectActiveByDormId(@Param("dormId") Long dormId);

    @Select("SELECT * FROM dorm_assignments WHERE status = 'pending' ORDER BY created_at ASC")
    List<DormAssignment> selectPendingList();

    @Select("SELECT * FROM dorm_assignments WHERE dorm_id = #{dormId} AND status = 'pending' ORDER BY created_at ASC")
    List<DormAssignment> selectPendingByDormId(@Param("dormId") Long dormId);

    @Select("SELECT * FROM dorm_assignments WHERE semester = #{semester} ORDER BY created_at DESC")
    List<DormAssignment> selectBySemester(@Param("semester") String semester);

    @Select("SELECT * FROM dorm_assignments WHERE type = #{type} ORDER BY created_at DESC")
    List<DormAssignment> selectByType(@Param("type") String type);

    @Select("SELECT * FROM dorm_assignments WHERE dorm_id = #{dormId} ORDER BY created_at DESC")
    List<DormAssignment> selectHistoryByDormId(@Param("dormId") Long dormId);

    /**
     * 根据宿舍ID和床号查询活跃入住记录（带行锁 FOR UPDATE）
     */
    @Select("SELECT * FROM dorm_assignments WHERE dorm_id = #{dormId} AND bed_no = #{bedNo} AND status = 'active' FOR UPDATE")
    DormAssignment selectActiveAssignmentByDormAndBed(@Param("dormId") Long dormId, @Param("bedNo") String bedNo);

    // ==================== 统计 ====================

    @Select("SELECT COUNT(*) FROM dorm_assignments WHERE dorm_id = #{dormId} AND status = 'active'")
    Long countActiveByDormId(@Param("dormId") Long dormId);

    @Select("SELECT COUNT(*) FROM dorm_assignments WHERE status = 'active'")
    Long countAllActive();

    @Select("SELECT status, COUNT(*) as count FROM dorm_assignments GROUP BY status")
    List<Map<String, Object>> countByStatus();

    @Select("SELECT type, COUNT(*) as count FROM dorm_assignments GROUP BY type")
    List<Map<String, Object>> countByType();

    @Select("SELECT type, COUNT(*) as count FROM dorm_assignments WHERE semester = #{semester} GROUP BY type")
    List<Map<String, Object>> countByTypeAndSemester(@Param("semester") String semester);

    // ==================== 验证 ====================

    @Select("SELECT COUNT(*) > 0 FROM dorm_assignments WHERE student_id = #{studentId} AND status = 'active'")
    boolean hasActiveAssignment(@Param("studentId") Long studentId);

    @Select("SELECT COUNT(*) > 0 FROM dorm_assignments WHERE student_id = #{studentId} AND status = 'pending'")
    boolean hasPendingAssignment(@Param("studentId") Long studentId);

    @Select("SELECT COUNT(*) FROM dorm_assignments WHERE status = 'pending'")
    Long countPending();

    @Select("SELECT COUNT(*) > 0 FROM dorm_assignments WHERE student_id = #{studentId} AND status IN ('pending', 'active')")
    boolean hasActiveOrPending(@Param("studentId") Long studentId);

    // ==================== 更新 ====================

    @Update("UPDATE dorm_assignments SET status = 'approved', audit_time = NOW(), auditor_id = #{auditorId}, audit_remark = #{remark} WHERE id = #{assignmentId} AND status = 'pending'")
    int approveAssignment(@Param("assignmentId") Long assignmentId,
                          @Param("auditorId") Long auditorId,
                          @Param("remark") String remark);

    @Update("UPDATE dorm_assignments SET status = 'rejected', audit_time = NOW(), auditor_id = #{auditorId}, audit_remark = #{remark} WHERE id = #{assignmentId} AND status = 'pending'")
    int rejectAssignment(@Param("assignmentId") Long assignmentId,
                         @Param("auditorId") Long auditorId,
                         @Param("remark") String remark);

    @Update("UPDATE dorm_assignments SET status = 'active' WHERE id = #{assignmentId} AND status = 'approved'")
    int activateAssignment(@Param("assignmentId") Long assignmentId);

    @Update("UPDATE dorm_assignments SET status = 'left', end_date = #{endDate} WHERE id = #{assignmentId} AND status = 'active'")
    int leaveDorm(@Param("assignmentId") Long assignmentId, @Param("endDate") String endDate);
}