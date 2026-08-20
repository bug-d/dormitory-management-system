package com.university.dorm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.dorm.dto.request.StudentRequest;
import com.university.dorm.entity.Student;

import java.util.List;
import java.util.Map;

/**
 * 学生服务接口
 */
public interface StudentService {

    // ==================== 基础 CRUD ====================

    Student getById(Long id);

    Student getByStudentNo(String studentNo);

    Student getByUserId(Long userId);

    List<Student> listAll();

    Page<Student> pageQuery(Integer pageNum, Integer pageSize, String keyword, String grade, String gender, Integer status, String orderBy, String orderDir);

    void add(StudentRequest request);

    void update(StudentRequest request);

    void delete(Long id);

    void batchDelete(List<Long> ids);

    /**
     * 按条件批量删除学生（跨页删除）
     */
    int deleteByCondition(String keyword, String grade, String gender, Integer status);

    // ==================== 导入导出 ====================

    int batchImport(List<Student> students);

    int importStudents(String filePath);

    void exportStudents(String filePath);

    // ==================== 查询 ====================

    List<Student> getNewStudents();

    List<Student> getActiveStudents();

    List<Student> getByGrade(String grade);

    List<Student> getByGender(String gender);

    List<Student> getByGradeAndGender(String grade, String gender);

    List<Student> getStudentsWithoutDorm();

    List<Student> getStudentsWithDorm();

    // ==================== 统计 ====================

    Long countAll();

    List<Map<String, Object>> countByGrade();

    List<Map<String, Object>> countByMajor();

    Map<String, Object> countByGender();

    // ==================== 状态管理 ====================

    void updateStatus(Long studentId, Integer status);

    void graduate(Long studentId);

    void markAsNew(Long studentId);

    void updateIsNew(Long studentId, String isNew);

    // ==================== 验证 ====================

    boolean existsByStudentNo(String studentNo);

    boolean hasActiveAssignment(Long studentId);

    Map<String, Object> getCurrentDormInfo(Long studentId);
}