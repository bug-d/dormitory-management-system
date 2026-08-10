package com.university.dorm.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 宿舍请求 DTO
 * <p>
 * 路径：backend/src/main/java/com/university/dorm/dto/request/DormRequest.java
 * 作用：接收前端添加/更新宿舍时的请求参数
 *
 * @author University Dorm Team
 * @version 1.0.0
 */
@Data
public class DormRequest {

    /**
     * 宿舍ID（更新时必传）
     */
    private Long id;

    /**
     * 楼栋号（不能为空）
     */
    @NotBlank(message = "楼栋号不能为空")
    private String buildingNo;

    /**
     * 楼层（不能为空）
     */
    @NotNull(message = "楼层不能为空")
    @Min(value = 1, message = "楼层最小为1")
    @Max(value = 30, message = "楼层最大为30")
    private Integer floorNo;

    /**
     * 房间号（不能为空）
     */
    @NotBlank(message = "房间号不能为空")
    private String roomNo;

    /**
     * 性别限制：M-男，F-女（不能为空）
     */
    @NotBlank(message = "性别限制不能为空")
    @Pattern(regexp = "^[MF]$", message = "性别只能为 M 或 F")
    private String gender;

    /**
     * 总床位数（不能为空，范围2-6）
     */
    @NotNull(message = "床位数不能为空")
    @Min(value = 2, message = "床位数最小为2")
    @Max(value = 6, message = "床位数最大为6")
    private Integer capacity;

    /**
     * 房间类型：standard-标准间，suite-套间
     */
    private String roomType;

    /**
     * 是否有空调：1-有，0-无
     */
    private Integer hasAirConditioner;

    /**
     * 是否有独立卫浴：1-有，0-无
     */
    private Integer hasPrivateBathroom;

    /**
     * 每学期费用
     */
    @Min(value = 0, message = "费用不能为负数")
    private BigDecimal pricePerTerm;

    /**
     * 状态：available-可用，full-已满，maintenance-维修中，closed-关闭
     */
    @Pattern(regexp = "^(available|full|maintenance|closed)$", 
             message = "状态只能为 available/full/maintenance/closed")
    private String status;

    /**
     * 备注说明
     */
    private String description;
}