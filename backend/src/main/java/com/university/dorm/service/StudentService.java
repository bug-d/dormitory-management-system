package com.university.dorm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.dorm.dto.request.StudentRequest;
import com.university.dorm.entity.Student;

import java.util.List;
import java.util.Map;

/**
 * 学生服务接口
 * <p>
 * 路径：backend/src/main/java/com/university/dorm/service/StudentService.java
 * 作用：定义学生相关的业务方法
 *
 * @author University Dorm Team
 * @version 1.0.0
 */
public interface StudentService {

    // ==================== 基础 CRUD ====================

    /**
     * 根据ID查询学生
     */
    Student getById(Long id);

    /**
     * 根据学号查询学生
     */
    Student getByStudentNo(String studentNo);

    /**
     * 根据用户ID查询学生
     */
    Student getByUserId(Long userId);

    /**
     * 查询所有学生
     */
    List<Student> listAll();

    /**
     * 分页查询学生
     */
    Page<Student> pageQuery(Integer pageNum, Integer pageSize, String keyword, String grade, String gender);

    /**
     * 新增学生
     */
    void add(StudentRequest request);

    /**
     * 更新学生
     */
    void update(StudentRequest request);

    /**
     * 删除学生
     */
    void delete(Long id);

    /**
     * 批量删除学生
     */
    void batchDelete(List<Long> ids);

    // ==================== 导入导出 ====================

    /**
     * 批量导入学生
     *
     * @param students 学生列表
     * @return 导入成功数量
     */
    int batchImport(List<Student> students);

    /**
     * 批量导入学生（Excel文件）
     *
     * @param filePath Excel文件路径
     * @return 导入成功数量
     */
    int importStudents(String filePath);

    /**
     * 导出学生数据（Excel）
     *
     * @param filePath 导出文件路径
     */
    void exportStudents(String filePath);

    // ==================== 查询 ====================

    /**
     * 查询所有新生
     */
    List<Student> getNewStudents();

    /**
     * 查询在读学生
     */
    List<Student> getActiveStudents();

    /**
     * 根据年级查询学生
     */
    List<Student> getByGrade(String grade);

    /**
     * 根据性别查询学生
     */
    List<Student> getByGender(String gender);

    /**
     * 根据年级和性别查询学生
     */
    List<Student> getByGradeAndGender(String grade, String gender);

    /**
     * 查询未分配宿舍的学生
     */
    List<Student> getStudentsWithoutDorm();

    /**
     * 查询已分配宿舍的学生
     */
    List<Student> getStudentsWithDorm();

    // ==================== 统计 ====================

    /**
     * 统计学生总数
     */
    Long countAll();

    /**
     * 统计各年级学生数量
     */
    List<Map<String, Object>> countByGrade();

    /**
     * 统计各专业学生数量
     */
    List<Map<String, Object>> countByMajor();

    /**
     * 统计男女比例
     */
    Map<String, Object> countByGender();

    // ==================== 状态管理 ====================

    /**
     * 更新学生状态
     */
    void updateStatus(Long studentId, Integer status);

    /**
     * 标记为已毕业
     */
    void graduate(Long studentId);

    /**
     * 标记为新生
     */
    void markAsNew(Long studentId);

    /**
     * 更新是否为新生状态
     */
    void updateIsNew(Long studentId, String isNew);

    // ==================== 验证 ====================

    /**
     * 检查学号是否存在
     */
    boolean existsByStudentNo(String studentNo);

    /**
     * 检查学生是否有活跃的入住记录
     */
    boolean hasActiveAssignment(Long studentId);

    /**
     * 获取学生当前宿舍信息
     */
    Map<String, Object> getCurrentDormInfo(Long studentId);
}