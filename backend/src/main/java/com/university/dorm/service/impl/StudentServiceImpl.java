package com.university.dorm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.dorm.constant.RoleConstant;
import com.university.dorm.constant.StatusConstant;
import com.university.dorm.dto.request.StudentRequest;
import com.university.dorm.entity.Student;
import com.university.dorm.entity.User;
import com.university.dorm.exception.BusinessException;
import com.university.dorm.mapper.StudentMapper;
import com.university.dorm.mapper.UserMapper;
import com.university.dorm.service.StudentService;
import com.university.dorm.util.PasswordUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordUtil passwordUtil;

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
    public Page<Student> pageQuery(Integer pageNum, Integer pageSize, String keyword, String grade, String gender, Integer status, String orderBy, String orderDir) {
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
        if (status != null) {
            wrapper.eq(Student::getStatus, status);
        }

        // 排序
        if (orderBy != null && !orderBy.isEmpty()) {
            boolean isAsc = "asc".equalsIgnoreCase(orderDir);
            switch (orderBy) {
                case "id":
                    wrapper.orderBy(true, isAsc, Student::getId);
                    break;
                case "studentNo":
                    wrapper.orderBy(true, isAsc, Student::getStudentNo);
                    break;
                case "name":
                    wrapper.orderBy(true, isAsc, Student::getName);
                    break;
                case "grade":
                    wrapper.orderBy(true, isAsc, Student::getGrade);
                    break;
                default:
                    wrapper.orderBy(true, isAsc, Student::getId);
                    break;
            }
        } else {
            wrapper.orderByAsc(Student::getId);
        }

        return studentMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional
    public void add(StudentRequest request) {
        if (studentMapper.existsByStudentNo(request.getStudentNo())) {
            throw new BusinessException("学号 " + request.getStudentNo() + " 已存在");
        }

        User user = new User();
        user.setUsername(request.getStudentNo());
        user.setPassword(passwordUtil.encode("123456"));
        user.setRealName(request.getName());
        user.setRole(RoleConstant.STUDENT);
        user.setStatus(StatusConstant.USER_ENABLED);
        userMapper.insert(user);

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

        if (!existing.getStudentNo().equals(request.getStudentNo())) {
            if (studentMapper.existsByStudentNo(request.getStudentNo())) {
                throw new BusinessException("学号 " + request.getStudentNo() + " 已存在");
            }
        }

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

        if (hasActiveAssignment(id)) {
            throw new BusinessException("该学生已入住宿舍，请先办理退宿");
        }

        studentMapper.deleteById(id);
        userMapper.deleteById(student.getUserId());
        log.info("删除学生成功: {}", student.getStudentNo());
    }

    @Override
    @Transactional
    public void batchDelete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        int successCount = 0;
        for (Long id : ids) {
            try {
                Student student = studentMapper.selectById(id);
                if (student == null) {
                    log.warn("学生 ID {} 不存在，跳过", id);
                    continue;
                }
                if (hasActiveAssignment(id)) {
                    log.warn("学生 {} 已入住宿舍，跳过", student.getStudentNo());
                    continue;
                }
                studentMapper.deleteById(id);
                userMapper.deleteById(student.getUserId());
                successCount++;
                log.info("删除学生成功: {}", student.getStudentNo());
            } catch (Exception e) {
                log.error("删除学生失败, ID: {}, 错误: {}", id, e.getMessage());
            }
        }
        log.info("批量删除完成，成功 {} 条，失败 {} 条", successCount, ids.size() - successCount);
    }

    /**
     * 按条件批量删除学生（跨页删除）
     */
    @Override
    @Transactional
    public int deleteByCondition(String keyword, String grade, String gender, Integer status) {
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
        if (status != null) {
            wrapper.eq(Student::getStatus, status);
        }

        List<Student> students = studentMapper.selectList(wrapper);
        if (students.isEmpty()) {
            log.info("没有符合条件的学");
            return 0;
        }

        int count = 0;
        for (Student student : students) {
            try {
                if (hasActiveAssignment(student.getId())) {
                    log.warn("学生 {} 已入住宿舍，跳过", student.getStudentNo());
                    continue;
                }
                studentMapper.deleteById(student.getId());
                userMapper.deleteById(student.getUserId());
                count++;
                log.info("按条件删除学生成功: {}", student.getStudentNo());
            } catch (Exception e) {
                log.error("按条件删除学生失败: {}", student.getStudentNo(), e);
            }
        }

        log.info("按条件删除完成：成功 {} 条", count);
        return count;
    }

    // ==================== 导入导出 ====================

    @Override
    @Transactional
    public int batchImport(List<Student> students) {
        int successCount = 0;
        for (Student student : students) {
            try {
                if (studentMapper.existsByStudentNo(student.getStudentNo())) {
                    log.warn("学号 {} 已存在，跳过", student.getStudentNo());
                    continue;
                }

                User user = new User();
                user.setUsername(student.getStudentNo());
                user.setPassword(passwordUtil.encode("123456"));
                user.setRealName(student.getName());
                user.setRole(RoleConstant.STUDENT);
                user.setStatus(StatusConstant.USER_ENABLED);
                userMapper.insert(user);

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
        return 0;
    }

    @Override
    public void exportStudents(String filePath) {
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
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            List<Student> allStudents = studentMapper.selectList(null);
            Map<String, Integer> gradeCount = new HashMap<>();
            for (Student s : allStudents) {
                String grade = s.getGrade();
                if (grade != null) {
                    gradeCount.put(grade, gradeCount.getOrDefault(grade, 0) + 1);
                }
            }
            for (Map.Entry<String, Integer> entry : gradeCount.entrySet()) {
                Map<String, Object> item = new HashMap<>();
                item.put("grade", entry.getKey());
                item.put("count", entry.getValue());
                result.add(item);
            }
        } catch (Exception e) {
            log.error("统计年级人数失败", e);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> countByMajor() {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            List<Student> allStudents = studentMapper.selectList(null);
            Map<String, Integer> majorCount = new HashMap<>();
            for (Student s : allStudents) {
                String major = s.getMajor();
                if (major != null) {
                    majorCount.put(major, majorCount.getOrDefault(major, 0) + 1);
                }
            }
            for (Map.Entry<String, Integer> entry : majorCount.entrySet()) {
                Map<String, Object> item = new HashMap<>();
                item.put("major", entry.getKey());
                item.put("count", entry.getValue());
                result.add(item);
            }
        } catch (Exception e) {
            log.error("统计专业人数失败", e);
        }
        return result;
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
        // TODO: 通过入住记录查询
        return false;
    }

    @Override
    public Map<String, Object> getCurrentDormInfo(Long studentId) {
        return null;
    }
}