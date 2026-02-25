package cn.fengp.basic.module.nmt.service.studentachievement.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 业务行数据传输对象
 */
@Data
public class RowDataDTO {
    private Long studentId;
    private String studentNumber;
    private String studentName;
    // Key: planId, Value: score
    private Map<Long, String> scores = new HashMap<>();
}