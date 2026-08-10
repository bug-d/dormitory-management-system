package com.university.dorm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.dorm.constant.RoleConstant;
import com.university.dorm.constant.StatusConstant;
import com.university.dorm.dto.request.StudentRequest;
import com.university.dorm.entity.Student;
import com.university.dorm.entity.User;
import com.university.dorm.entity.DormAssignment;
import com.university.dorm.entity.Dormitory;
import com.university.dorm.exception.BusinessException;
import com.university.dorm.mapper.StudentMapper;
import com.university.dorm.mapper.UserMapper;
import com.university.dorm.mapper.AssignmentMapper;
import com.university.dorm.mapper.DormitoryMapper;
import com.university.dorm.service.StudentService;
import com.university.dorm.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学生服务实现类
 * <p>
 * 路径：backend/src/main/java/com/university/dorm/service/impl/StudentServiceImpl.java
 *
 * @author University Dorm Team
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentMapper studentMapper;
    private final UserMapper userMapper;
    private final PasswordUtil passwordUtil;
    private final AssignmentMapper assignmentMapper;
    private final DormitoryMapper dormitoryMapper;

    // ==================== 基础 CRUD ====================

    @Override
    public Student getById(Long id) {
        return studentMapper.selectById(id);
    }

    @Override
    public Student getByStudentNo(String studentNo) {
        return studentMapper.selectByStudentNo(studentNo);
    }

    @Override
    public Student getByUserId(Long userId) {
        return studentMapper.selectByUserId(userId);
    }

    @Override
    public List<Student> listAll() {
        return studentMapper.selectList(null);
    }

    @Override
    public Page<Student> pageQuery(Integer pageNum, Integer pageSize, String keyword, String grade, String gender) {
        Page<Student> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();

        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Student::getStudentNo, keyword)
                    .or()
                    .like(Student::getName, keyword);
        }
        if (grade != null && !grade.isEmpty()) {
            wrapper.eq(Student::getGrade, grade);
        }
        if (gender != null && !gender.isEmpty()) {
            wrapper.eq(Student::getGender, gender);
        }

        wrapper.orderByDesc(Student::getCreatedAt);
        return studentMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional
    public void add(StudentRequest request) {
        // 1. 检查学号是否已存在
        if (studentMapper.existsByStudentNo(request.getStudentNo())) {
            throw new BusinessException("学号 " + request.getStudentNo() + " 已存在");
        }

        // 2. 创建用户账号
        User user = new User();
        user.setUsername(request.getStudentNo());
        user.setPassword(passwordUtil.encode("123456"));
        user.setRealName(request.getName());
        user.setRole(RoleConstant.STUDENT);
        user.setStatus(StatusConstant.USER_ENABLED);
        userMapper.insert(user);

        // 3. 创建学生记录
        Student student = new Student();
        student.setUserId(user.getId());
        student.setStudentNo(request.getStudentNo());
        student.setName(request.getName());
        student.setGender(request.getGender());
        student.setGrade(request.getGrade());
        student.setMajor(request.getMajor());
        student.setClassName(request.getClassName());
        student.setIdCard(request.getIdCard());
        student.setPhone(request.getPhone());
        student.setEmergencyContact(request.getEmergencyContact());
        student.setEmergencyPhone(request.getEmergencyPhone());
        student.setIsNew("Y");
        student.setStatus(StatusConstant.STUDENT_ACTIVE);
        student.setEnrollmentDate(LocalDate.now());

        studentMapper.insert(student);
        log.info("新增学生成功: {}", request.getStudentNo());
    }

    @Override
    @Transactional
    public void update(StudentRequest request) {
        Student existing = studentMapper.selectById(request.getId());
        if (existing == null) {
            throw new BusinessException("学生不存在");
        }

        // 如果学号变更，检查是否重复
        if (!existing.getStudentNo().equals(request.getStudentNo())) {
            if (studentMapper.existsByStudentNo(request.getStudentNo())) {
                throw new BusinessException("学号 " + request.getStudentNo() + " 已存在");
            }
        }

        // 更新学生信息
        existing.setStudentNo(request.getStudentNo());
        existing.setName(request.getName());
        existing.setGender(request.getGender());
        existing.setGrade(request.getGrade());
        existing.setMajor(request.getMajor());
        existing.setClassName(request.getClassName());
        existing.setIdCard(request.getIdCard());
        existing.setPhone(request.getPhone());
        existing.setEmergencyContact(request.getEmergencyContact());
        existing.setEmergencyPhone(request.getEmergencyPhone());

        studentMapper.updateById(existing);
        log.info("更新学生成功: {}", request.getStudentNo());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Student student = studentMapper.selectById(id);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }

        // 检查是否有活跃入住记录
        if (hasActiveAssignment(id)) {
            throw new BusinessException("该学生已入住宿舍，请先办理退宿");
        }

        // 删除学生和关联用户
        studentMapper.deleteById(id);
        userMapper.deleteById(student.getUserId());
        log.info("删除学生成功: {}", student.getStudentNo());
    }

    @Override
    @Transactional
    public void batchDelete(List<Long> ids) {
        for (Long id : ids) {
            try {
                delete(id);
            } catch (Exception e) {
                log.warn("批量删除失败: {}", e.getMessage());
            }
        }
    }

    // ==================== 导入导出 ====================

    @Override
    @Transactional
    public int batchImport(List<Student> students) {
        int successCount = 0;
        for (Student student : students) {
            try {
                // 检查学号是否已存在
                if (studentMapper.existsByStudentNo(student.getStudentNo())) {
                    log.warn("学号 {} 已存在，跳过", student.getStudentNo());
                    continue;
                }

                // 创建用户账号
                User user = new User();
                user.setUsername(student.getStudentNo());
                user.setPassword(passwordUtil.encode("123456"));
                user.setRealName(student.getName());
                user.setRole(RoleConstant.STUDENT);
                user.setStatus(StatusConstant.USER_ENABLED);
                userMapper.insert(user);

                // 关联用户ID
                student.setUserId(user.getId());
                student.setStatus(StatusConstant.STUDENT_ACTIVE);
                student.setIsNew(StatusConstant.IS_NEW_YES);
                studentMapper.insert(student);
                successCount++;

                log.info("导入学生成功: {}", student.getStudentNo());

            } catch (Exception e) {
                log.error("导入学生失败: {}, 错误: {}", student.getStudentNo(), e.getMessage());
            }
        }
        log.info("批量导入完成，成功 {} 条，失败 {} 条", successCount, students.size() - successCount);
        return successCount;
    }

    @Override
    public int importStudents(String filePath) {
        // 使用 EasyExcel 读取，由 Controller 层处理
        return 0;
    }

    @Override
    public void exportStudents(String filePath) {
        // 使用 EasyExcel 导出
        log.info("导出学生数据: {}", filePath);
    }

    // ==================== 查询 ====================

    @Override
    public List<Student> getNewStudents() {
        return studentMapper.selectNewStudents();
    }

    @Override
    public List<Student> getActiveStudents() {
        return studentMapper.selectActiveStudents();
    }

    @Override
    public List<Student> getByGrade(String grade) {
        return studentMapper.selectByGrade(grade);
    }

    @Override
    public List<Student> getByGender(String gender) {
        return studentMapper.selectByGender(gender);
    }

    @Override
    public List<Student> getByGradeAndGender(String grade, String gender) {
        return studentMapper.selectByGradeAndGender(grade, gender);
    }

    @Override
    public List<Student> getStudentsWithoutDorm() {
        List<Student> allStudents = studentMapper.selectActiveStudents();
        List<Long> withDormIds = studentMapper.selectStudentIdsWithActiveAssignment();
        return allStudents.stream()
                .filter(s -> !withDormIds.contains(s.getId()))
                .toList();
    }

    @Override
    public List<Student> getStudentsWithDorm() {
        List<Long> withDormIds = studentMapper.selectStudentIdsWithActiveAssignment();
        if (withDormIds.isEmpty()) {
            return new ArrayList<>();
        }
        return studentMapper.selectBatchIds(withDormIds);
    }

    // ==================== 统计 ====================

    @Override
    public Long countAll() {
        return studentMapper.countActiveStudents();
    }

    @Override
    public List<Map<String, Object>> countByGrade() {
        return groupActiveStudentsBy(Student::getGrade, "grade");
    }

    @Override
    public List<Map<String, Object>> countByMajor() {
        return groupActiveStudentsBy(Student::getMajor, "major");
    }

    @Override
    public Map<String, Object> countByGender() {
        Long maleCount = studentMapper.countByGender(StatusConstant.GENDER_MALE);
        Long femaleCount = studentMapper.countByGender(StatusConstant.GENDER_FEMALE);
        Map<String, Object> result = new HashMap<>();
        result.put("male", maleCount);
        result.put("female", femaleCount);
        return result;
    }

    // ==================== 状态管理 ====================

    @Override
    public void updateStatus(Long studentId, Integer status) {
        studentMapper.updateStatus(studentId, status);
    }

    @Override
    public void graduate(Long studentId) {
        studentMapper.updateStatus(studentId, StatusConstant.STUDENT_GRADUATED);
    }

    @Override
    public void markAsNew(Long studentId) {
        studentMapper.updateIsNew(studentId, StatusConstant.IS_NEW_YES);
    }

    @Override
    public void updateIsNew(Long studentId, String isNew) {
        studentMapper.updateIsNew(studentId, isNew);
    }

    // ==================== 验证 ====================

    @Override
    public boolean existsByStudentNo(String studentNo) {
        return studentMapper.existsByStudentNo(studentNo);
    }

    @Override
    public boolean hasActiveAssignment(Long studentId) {
        return assignmentMapper.hasActiveAssignment(studentId);
    }

    @Override
    public Map<String, Object> getCurrentDormInfo(Long studentId) {
        DormAssignment assignment = assignmentMapper.selectActiveByStudentId(studentId);
        if (assignment == null) {
            return null;
        }
        Dormitory dormitory = dormitoryMapper.selectById(assignment.getDormId());
        Map<String, Object> result = new HashMap<>();
        result.put("assignment", assignment);
        result.put("dorm", dormitory);
        return result;
    }

    private List<Map<String, Object>> groupActiveStudentsBy(
            java.util.function.Function<Student, String> classifier, String key) {
        Map<String, Long> counts = new java.util.LinkedHashMap<>();
        for (Student student : studentMapper.selectActiveStudents()) {
            String value = classifier.apply(student);
            if (value != null && !value.isBlank()) {
                counts.merge(value, 1L, Long::sum);
            }
        }
        List<Map<String, Object>> result = new ArrayList<>();
        counts.forEach((value, count) -> {
            Map<String, Object> item = new HashMap<>();
            item.put(key, value);
            item.put("count", count);
            result.add(item);
        });
        return result;
    }
}
