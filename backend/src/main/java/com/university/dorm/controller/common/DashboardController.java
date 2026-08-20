package com.university.dorm.controller.common;

import com.university.dorm.dto.response.Result;
import com.university.dorm.mapper.AssignmentMapper;
import com.university.dorm.mapper.DormitoryMapper;
import com.university.dorm.mapper.OperationLogMapper;
import com.university.dorm.mapper.StudentMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 首页仪表盘控制器
 * <p>
 * 路径：backend/src/main/java/com/university/dorm/controller/common/DashboardController.java
 * 作用：提供首页统计数据接口
 *
 * @author University Dorm Team
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Tag(name = "首页仪表盘", description = "首页统计数据接口")
public class DashboardController {

    private final DormitoryMapper dormitoryMapper;
    private final StudentMapper studentMapper;
    private final AssignmentMapper assignmentMapper;
    private final OperationLogMapper operationLogMapper;

    /**
     * 获取首页统计数据（统计卡片）
     * 数据来源：dormitories + dorm_assignments
     */
    @GetMapping("/stats")
    @Operation(summary = "获取首页统计数据")
    public Result<Map<String, Object>> getStats() {
        try {
            Map<String, Object> stats = new HashMap<>();

            // 总床位 = 宿舍数 × 4
            Long totalBeds = dormitoryMapper.selectCount(null) * 4L;

            // 已入住 = status='active' 的记录数
            Long occupiedBeds = assignmentMapper.countAllActive();
            if (occupiedBeds == null) occupiedBeds = 0L;

            // 空床位
            Long emptyBeds = totalBeds - occupiedBeds;

            // 入住率
            String occupancyRate = totalBeds > 0 ?
                String.format("%.1f", (occupiedBeds * 100.0) / totalBeds) : "0";

            stats.put("totalBeds", totalBeds);
            stats.put("occupiedBeds", occupiedBeds);
            stats.put("emptyBeds", emptyBeds > 0 ? emptyBeds : 0);
            stats.put("occupancyRate", occupancyRate);

            log.info("首页统计数据：总床位={}, 已入住={}, 空床位={}, 入住率={}%",
                    totalBeds, occupiedBeds, emptyBeds, occupancyRate);

            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取统计数据失败", e);
            return Result.error("获取统计数据失败");
        }
    }

    /**
     * 获取各楼栋入住率数据（首页柱状图）
     * 数据来源：dormitories 表按楼栋分组统计
     */
    @GetMapping("/chart/building-occupancy")
    @Operation(summary = "获取各楼栋入住率数据")
    public Result<List<Map<String, Object>>> getBuildingOccupancy() {
        try {
            List<Map<String, Object>> result = dormitoryMapper.selectBuildingStatistics();
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取楼栋入住率数据失败", e);
            return Result.error("获取数据失败");
        }
    }

    /**
     * 获取男女比例数据（首页饼图）
     * 数据来源：students 表按性别统计
     */
    @GetMapping("/chart/gender-ratio")
    @Operation(summary = "获取男女比例数据")
    public Result<List<Map<String, Object>>> getGenderRatio() {
        try {
            Long maleCount = studentMapper.countByGender("M");
            Long femaleCount = studentMapper.countByGender("F");

            if (maleCount == null) maleCount = 0L;
            if (femaleCount == null) femaleCount = 0L;

            List<Map<String, Object>> result = List.of(
                Map.of("name", "男生", "value", maleCount, "color", "#409EFF"),
                Map.of("name", "女生", "value", femaleCount, "color", "#F56C6C")
            );
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取男女比例数据失败", e);
            return Result.error("获取数据失败");
        }
    }

    /**
     * 获取最近动态（首页动态列表）
     * 数据来源：operation_logs 表
     */
    @GetMapping("/activities")
    @Operation(summary = "获取最近动态")
    public Result<List<Map<String, Object>>> getActivities(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<Map<String, Object>> activities = operationLogMapper.selectRecentActivities(limit);
            return Result.success(activities);
        } catch (Exception e) {
            log.error("获取最近动态失败", e);
            return Result.error("获取数据失败");
        }
    }
}