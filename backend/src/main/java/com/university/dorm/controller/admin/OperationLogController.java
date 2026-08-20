package com.university.dorm.controller.admin;

import com.university.dorm.dto.response.Result;
import com.university.dorm.entity.OperationLog;
import com.university.dorm.mapper.OperationLogMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/logs")
@RequiredArgsConstructor
@Tag(name = "操作日志", description = "管理员端操作日志查看")
public class OperationLogController {

    private final OperationLogMapper operationLogMapper;

    @GetMapping("/recent")
    @Operation(summary = "获取最近操作日志")
    public Result<List<OperationLog>> getRecentLogs(
            @RequestParam(defaultValue = "20") int limit) {
        List<OperationLog> logs = operationLogMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<OperationLog>()
                .orderByDesc(OperationLog::getCreatedAt)
                .last("LIMIT " + limit)
        );
        return Result.success(logs);
    }
}