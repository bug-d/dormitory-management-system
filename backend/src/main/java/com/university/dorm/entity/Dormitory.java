package com.university.dorm.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.university.dorm.constant.StatusConstant;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 宿舍实体类
 * <p>
 * 路径：backend/src/main/java/com/university/dorm/entity/Dormitory.java
 * 作用：对应数据库 dormitories 表，存储宿舍基础信息
 * <p>
 * 表结构：
 * - id: 宿舍ID（主键，自增）
 * - building_no: 楼栋号
 * - floor_no: 楼层
 * - room_no: 房间号
 * - gender: 性别限制（M-男，F-女）
 * - capacity: 总床位数
 * - occupied: 已入住人数
 * - room_type: 房间类型（standard-标准间，suite-套间）
 * - has_air_conditioner: 是否有空调（1-有，0-无）
 * - has_private_bathroom: 是否有独立卫浴（1-有，0-无）
 * - price_per_term: 每学期费用
 * - status: 状态（available-可用，full-已满，maintenance-维修中，closed-关闭）
 * - description: 备注说明
 * - version: 乐观锁版本号（用于并发控制）
 * - created_at: 创建时间
 * - updated_at: 更新时间
 *
 * @author University Dorm Team
 * @version 1.0.0
 */
@Data
@TableName("dormitories")
public class Dormitory {

    /**
     * 宿舍ID（主键，自增）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 楼栋号（如：1栋、A栋）
     */
    private String buildingNo;

    /**
     * 楼层
     */
    private Integer floorNo;

    /**
     * 房间号
     */
    private String roomNo;

    /**
     * 性别限制：M-男，F-女
     *
     * @see com.university.dorm.constant.StatusConstant#GENDER_MALE
     * @see com.university.dorm.constant.StatusConstant#GENDER_FEMALE
     */
    private String gender;

    /**
     * 总床位数（默认4）
     */
    private Integer capacity;

    /**
     * 已入住人数（默认0）
     */
    private Integer occupied;

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
    private BigDecimal pricePerTerm;

    /**
     * 状态：available-可用，full-已满，maintenance-维修中，closed-关闭
     *
     * @see com.university.dorm.constant.StatusConstant#DORM_AVAILABLE
     * @see com.university.dorm.constant.StatusConstant#DORM_FULL
     * @see com.university.dorm.constant.StatusConstant#DORM_MAINTENANCE
     * @see com.university.dorm.constant.StatusConstant#DORM_CLOSED
     */
    private String status;

    /**
     * 备注说明
     */
    private String description;

    /**
     * 乐观锁版本号（用于并发控制，防止床位超卖）
     */
    @Version
    private Integer version;

    /**
     * 创建时间（自动填充）
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间（自动填充）
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    // ==================== 辅助方法 ====================

    /**
     * 获取剩余床位数
     *
     * @return 剩余床位数
     */
    public Integer getRemainingCapacity() {
        return this.capacity - this.occupied;
    }

    /**
     * 判断宿舍是否已满
     *
     * @return true-已满，false-未满
     */
    public boolean isFull() {
        return this.occupied >= this.capacity;
    }

    /**
     * 判断宿舍是否可用（状态为available且未满）
     *
     * @return true-可用，false-不可用
     */
    public boolean isAvailable() {
        return StatusConstant.DORM_AVAILABLE.equals(this.status) && !isFull();
    }

    /**
     * 判断宿舍是否允许入住（性别匹配且可用）
     *
     * @param studentGender 学生性别
     * @return true-允许入住，false-不允许
     */
    public boolean canCheckin(String studentGender) {
        return this.gender.equals(studentGender) && isAvailable();
    }
}