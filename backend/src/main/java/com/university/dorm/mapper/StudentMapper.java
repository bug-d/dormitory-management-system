package com.university.dorm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.university.dorm.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

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

    /**
     * 根据学号查询学生
     *
     * @param studentNo 学号
     * @return 学生对象
     */
    @Select("SELECT * FROM students WHERE student_no = #{studentNo}")
    Student selectByStudentNo(@Param("studentNo") String studentNo);

    /**
     * 根据用户ID查询学生
     *
     * @param userId 用户ID
     * @return 学生对象
     */
    @Select("SELECT * FROM students WHERE user_id = #{userId}")
    Student selectByUserId(@Param("userId") Long userId);

    /**
     * 查询所有新生（is_new = 'Y'）
     *
     * @return 新生列表
     */
    @Select("SELECT * FROM students WHERE is_new = 'Y' AND status = 1 ORDER BY created_at DESC")
    List<Student> selectNewStudents();

    /**
     * 查询所有在读学生
     *
     * @return 在读学生列表
     */
    @Select("SELECT * FROM students WHERE status = 1 ORDER BY student_no")
    List<Student> selectActiveStudents();

    /**
     * 根据年级查询学生
     *
     * @param grade 年级
     * @return 学生列表
     */
    @Select("SELECT * FROM students WHERE grade = #{grade} AND status = 1 ORDER BY student_no")
    List<Student> selectByGrade(@Param("grade") String grade);

    /**
     * 根据性别查询学生
     *
     * @param gender 性别（M-男，F-女）
     * @return 学生列表
     */
    @Select("SELECT * FROM students WHERE gender = #{gender} AND status = 1 ORDER BY student_no")
    List<Student> selectByGender(@Param("gender") String gender);

    /**
     * 根据专业查询学生
     *
     * @param major 专业
     * @return 学生列表
     */
    @Select("SELECT * FROM students WHERE major = #{major} AND status = 1 ORDER BY student_no")
    List<Student> selectByMajor(@Param("major") String major);

    /**
     * 根据年级和性别查询学生
     *
     * @param grade  年级
     * @param gender 性别
     * @return 学生列表
     */
    @Select("SELECT * FROM students WHERE grade = #{grade} AND gender = #{gender} AND status = 1 ORDER BY student_no")
    List<Student> selectByGradeAndGender(@Param("grade") String grade, @Param("gender") String gender);

    /**
     * 检查学号是否存在
     *
     * @param studentNo 学号
     * @return 存在返回 true，否则返回 false
     */
    @Select("SELECT COUNT(*) > 0 FROM students WHERE student_no = #{studentNo}")
    boolean existsByStudentNo(@Param("studentNo") String studentNo);

    /**
     * 查询有入住记录的学生ID列表
     *
     * @return 学生ID列表
     */
    @Select("SELECT DISTINCT student_id FROM dorm_assignments WHERE status = 'active'")
    List<Long> selectStudentIdsWithActiveAssignment();

    /**
     * 统计在读学生总数
     *
     * @return 学生总数
     */
    @Select("SELECT COUNT(*) FROM students WHERE status = 1")
    Long countActiveStudents();

    /**
     * 统计某年级学生总数
     *
     * @param grade 年级
     * @return 学生总数
     */
    @Select("SELECT COUNT(*) FROM students WHERE grade = #{grade} AND status = 1")
    Long countByGrade(@Param("grade") String grade);

    /**
     * 统计某性别学生总数
     *
     * @param gender 性别
     * @return 学生总数
     */
    @Select("SELECT COUNT(*) FROM students WHERE gender = #{gender} AND status = 1")
    Long countByGender(@Param("gender") String gender);

    /**
     * 更新学生状态（毕业/休学）
     *
     * @param studentId 学生ID
     * @param status    状态（0-已毕业，2-休学）
     * @return 影响行数
     */
    @Update("UPDATE students SET status = #{status} WHERE id = #{studentId}")
    int updateStatus(@Param("studentId") Long studentId, @Param("status") Integer status);

    /**
     * 更新学生是否为新生
     *
     * @param studentId 学生ID
     * @param isNew     是否新生（Y/N）
     * @return 影响行数
     */
    @Update("UPDATE students SET is_new = #{isNew} WHERE id = #{studentId}")
    int updateIsNew(@Param("studentId") Long studentId, @Param("isNew") String isNew);
}