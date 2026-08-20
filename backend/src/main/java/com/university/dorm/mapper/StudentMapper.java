package com.university.dorm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.university.dorm.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * 学生 Mapper 接口
 * <p>
 * 路径：backend/src/main/java/com/university/dorm/mapper/StudentMapper.java
 * 作用：学生数据访问层，继承 BaseMapper 自动获得 CRUD 方法
 *
 * @author University Dorm Team
 * @version 1.0.0
 */
@Mapper
public interface StudentMapper extends BaseMapper<Student> {

    // ==================== 基础查询 ====================

    /**
     * 根据学号查询学生
     */
    @Select("SELECT * FROM students WHERE student_no = #{studentNo}")
    Student selectByStudentNo(@Param("studentNo") String studentNo);

    /**
     * 根据用户ID查询学生
     */
    @Select("SELECT * FROM students WHERE user_id = #{userId}")
    Student selectByUserId(@Param("userId") Long userId);

    // ==================== 条件查询 ====================

    /**
     * 查询所有新生（is_new = 'Y'）
     */
    @Select("SELECT * FROM students WHERE is_new = 'Y' AND status = 1 ORDER BY created_at DESC")
    List<Student> selectNewStudents();

    /**
     * 查询所有在读学生
     */
    @Select("SELECT * FROM students WHERE status = 1 ORDER BY student_no")
    List<Student> selectActiveStudents();

    /**
     * 根据年级查询学生
     */
    @Select("SELECT * FROM students WHERE grade = #{grade} AND status = 1 ORDER BY student_no")
    List<Student> selectByGrade(@Param("grade") String grade);

    /**
     * 根据性别查询学生
     */
    @Select("SELECT * FROM students WHERE gender = #{gender} AND status = 1 ORDER BY student_no")
    List<Student> selectByGender(@Param("gender") String gender);

    /**
     * 根据专业查询学生
     */
    @Select("SELECT * FROM students WHERE major = #{major} AND status = 1 ORDER BY student_no")
    List<Student> selectByMajor(@Param("major") String major);

    /**
     * 根据年级和性别查询学生
     */
    @Select("SELECT * FROM students WHERE grade = #{grade} AND gender = #{gender} AND status = 1 ORDER BY student_no")
    List<Student> selectByGradeAndGender(@Param("grade") String grade, @Param("gender") String gender);

    // ==================== 验证 ====================

    /**
     * 检查学号是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM students WHERE student_no = #{studentNo}")
    boolean existsByStudentNo(@Param("studentNo") String studentNo);

    // ==================== 入住状态查询 ====================

    /**
     * 查询有活跃入住记录的学生ID列表
     */
    @Select("SELECT DISTINCT student_id FROM dorm_assignments WHERE status = 'active'")
    List<Long> selectStudentIdsWithActiveAssignment();

    // ==================== 统计 ====================

    /**
     * 统计在读学生总数
     */
    @Select("SELECT COUNT(*) FROM students WHERE status = 1")
    Long countActiveStudents();

    /**
     * 统计某年级学生总数
     */
    @Select("SELECT COUNT(*) FROM students WHERE grade = #{grade} AND status = 1")
    Long countByGrade(@Param("grade") String grade);

    /**
     * 统计某性别学生总数
     */
    @Select("SELECT COUNT(*) FROM students WHERE gender = #{gender} AND status = 1")
    Long countByGender(@Param("gender") String gender);

    /**
     * 统计各年级学生数量（用于图表）
     */
    @Select("SELECT grade, COUNT(*) as count FROM students WHERE status = 1 GROUP BY grade ORDER BY grade")
    List<Map<String, Object>> countGroupByGrade();

    /**
     * 统计各专业学生数量
     */
    @Select("SELECT major, COUNT(*) as count FROM students WHERE status = 1 GROUP BY major ORDER BY count DESC")
    List<Map<String, Object>> countGroupByMajor();

    // ==================== 状态更新 ====================

    /**
     * 更新学生状态（毕业/休学）
     */
    @Update("UPDATE students SET status = #{status} WHERE id = #{studentId}")
    int updateStatus(@Param("studentId") Long studentId, @Param("status") Integer status);

    /**
     * 更新学生是否为新生
     */
    @Update("UPDATE students SET is_new = #{isNew} WHERE id = #{studentId}")
    int updateIsNew(@Param("studentId") Long studentId, @Param("isNew") String isNew);
}