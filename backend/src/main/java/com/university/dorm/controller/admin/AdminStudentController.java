package com.university.dorm.controller.admin;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.dorm.dto.request.StudentImportDTO;
import com.university.dorm.dto.request.StudentRequest;
import com.university.dorm.dto.response.Result;
import com.university.dorm.entity.Student;
import com.university.dorm.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/students")
@Tag(name = "学生管理", description = "管理员端学生管理接口")
public class AdminStudentController {

    @Autowired
    private StudentService studentService;

    // ==================== 分页查询 ====================

    @GetMapping("/page")
    @Operation(summary = "分页查询学生")
    public Result<Page<Student>> pageQuery(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String orderBy,
            @RequestParam(required = false) String orderDir) {
        Page<Student> page = studentService.pageQuery(pageNum, pageSize, keyword, grade, gender, status, orderBy, orderDir);
        return Result.success(page);
    }

    @GetMapping("/list")
    @Operation(summary = "查询所有学生")
    public Result<List<Student>> listAll() {
        return Result.success(studentService.listAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询学生")
    public Result<Student> getById(@PathVariable Long id) {
        return Result.success(studentService.getById(id));
    }

    @GetMapping("/no/{studentNo}")
    @Operation(summary = "根据学号查询学生")
    public Result<Student> getByStudentNo(@PathVariable String studentNo) {
        return Result.success(studentService.getByStudentNo(studentNo));
    }

    // ==================== 增删改 ====================

    @PostMapping
    @Operation(summary = "新增学生")
    public Result<Void> add(@RequestBody @Valid StudentRequest request) {
        studentService.add(request);
        return Result.success();
    }

    @PutMapping
    @Operation(summary = "更新学生")
    public Result<Void> update(@RequestBody @Valid StudentRequest request) {
        studentService.update(request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除学生")
    public Result<Void> delete(@PathVariable Long id) {
        studentService.delete(id);
        return Result.success();
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除学生")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        studentService.batchDelete(ids);
        return Result.success();
    }

    /**
     * 按条件批量删除学生（跨页删除）
     */
    @DeleteMapping("/delete-by-condition")
    @Operation(summary = "按条件批量删除学生")
    public Result<Integer> deleteByCondition(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) Integer status) {
        int count = studentService.deleteByCondition(keyword, grade, gender, status);
        return Result.success(count);
    }

    // ==================== 导入导出 ====================

    @PostMapping("/import")
    @Operation(summary = "导入学生（Excel）")
    public Result<Integer> importStudents(@RequestParam("file") MultipartFile file) {
        try {
            log.info("开始导入学生数据，文件名: {}", file.getOriginalFilename());

            if (file.isEmpty()) {
                return Result.error("上传文件为空");
            }

            String filename = file.getOriginalFilename();
            if (filename == null || !(filename.endsWith(".xlsx") || filename.endsWith(".xls"))) {
                return Result.error("请上传 .xlsx 或 .xls 格式的 Excel 文件");
            }

            List<Student> students = new ArrayList<>();

            EasyExcel.read(file.getInputStream(), StudentImportDTO.class, new ReadListener<StudentImportDTO>() {
                @Override
                public void invoke(StudentImportDTO data, AnalysisContext context) {
                    if (data.getStudentNo() == null || data.getStudentNo().isEmpty()) {
                        return;
                    }
                    Student student = new Student();
                    student.setStudentNo(data.getStudentNo());
                    student.setName(data.getName());
                    String gender = data.getGender();
                    if ("男".equals(gender)) {
                        student.setGender("M");
                    } else if ("女".equals(gender)) {
                        student.setGender("F");
                    } else {
                        student.setGender(gender);
                    }
                    student.setGrade(data.getGrade());
                    student.setMajor(data.getMajor());
                    student.setClassName(data.getClassName());
                    student.setPhone(data.getPhone());
                    student.setIdCard(data.getIdCard());
                    student.setIsNew("Y");
                    student.setStatus(1);
                    students.add(student);
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {
                    log.info("Excel 读取完成，共读取 {} 条数据", students.size());
                }
            }).sheet().doRead();

            if (students.isEmpty()) {
                return Result.error("文件为空或格式不正确");
            }

            int count = studentService.batchImport(students);
            return Result.success(count);

        } catch (Exception e) {
            log.error("导入学生失败", e);
            return Result.error("导入失败：" + e.getMessage());
        }
    }

    // ==================== 查询 ====================

    @GetMapping("/new")
    @Operation(summary = "查询所有新生")
    public Result<List<Student>> getNewStudents() {
        return Result.success(studentService.getNewStudents());
    }

    @GetMapping("/active")
    @Operation(summary = "查询在读学生")
    public Result<List<Student>> getActiveStudents() {
        return Result.success(studentService.getActiveStudents());
    }

    @GetMapping("/grade/{grade}")
    @Operation(summary = "根据年级查询学生")
    public Result<List<Student>> getByGrade(@PathVariable String grade) {
        return Result.success(studentService.getByGrade(grade));
    }

    @GetMapping("/without-dorm")
    @Operation(summary = "查询未分配宿舍的学生")
    public Result<List<Student>> getStudentsWithoutDorm() {
        return Result.success(studentService.getStudentsWithoutDorm());
    }

    @GetMapping("/with-dorm")
    @Operation(summary = "查询已分配宿舍的学生")
    public Result<List<Student>> getStudentsWithDorm() {
        return Result.success(studentService.getStudentsWithDorm());
    }

    // ==================== 统计 ====================

    @GetMapping("/stats/count")
    @Operation(summary = "获取学生总数")
    public Result<Long> getCount() {
        return Result.success(studentService.countAll());
    }

    // ==================== 状态管理 ====================

    @PutMapping("/{id}/graduate")
    @Operation(summary = "标记学生为已毕业")
    public Result<Void> graduate(@PathVariable Long id) {
        studentService.graduate(id);
        return Result.success();
    }

    @PutMapping("/{id}/mark-new")
    @Operation(summary = "标记学生为新生")
    public Result<Void> markAsNew(@PathVariable Long id) {
        studentService.markAsNew(id);
        return Result.success();
    }
}