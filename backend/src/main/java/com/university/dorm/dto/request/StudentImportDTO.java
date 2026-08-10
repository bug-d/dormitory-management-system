package com.university.dorm.dto.request;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 学生导入 DTO
 * <p>
 * 路径：backend/src/main/java/com/university/dorm/dto/request/StudentImportDTO.java
 * 作用：接收 Excel 导入的学生数据，使用 EasyExcel 注解映射列
 *
 * @author University Dorm Team
 * @version 1.0.0
 */
@Data
public class StudentImportDTO {

    /**
     * 学号
     */
    @ExcelProperty("学号")
    private String studentNo;

    /**
     * 姓名
     */
    @ExcelProperty("姓名")
    private String name;

    /**
     * 性别（M-男，F-女）
     */
    @ExcelProperty("性别")
    private String gender;

    /**
     * 年级（如：2024）
     */
    @ExcelProperty("年级")
    private String grade;

    /**
     * 专业
     */
    @ExcelProperty("专业")
    private String major;

    /**
     * 班级
     */
    @ExcelProperty("班级")
    private String className;

    /**
     * 手机号
     */
    @ExcelProperty("手机号")
    private String phone;

    /**
     * 身份证号
     */
    @ExcelProperty("身份证号")
    private String idCard;
}