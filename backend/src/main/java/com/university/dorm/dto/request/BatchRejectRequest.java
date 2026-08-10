package com.university.dorm.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class BatchRejectRequest {

    @NotEmpty(message = "申请ID不能为空")
    private List<Long> ids;

    private String remark = "批量驳回";
}

