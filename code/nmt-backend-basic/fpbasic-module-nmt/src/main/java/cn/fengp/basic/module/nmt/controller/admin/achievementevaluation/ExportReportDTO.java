package cn.fengp.basic.module.nmt.controller.admin.achievementevaluation;

import lombok.Data;

import java.util.Map;

/**
 * @program: nmt-backend-basic
 * @description:
 * @author: fengpeng
 * @create: 2026-03-06 17:11
 **/

@Data
public class ExportReportDTO {
    private Long courseId;
    private Long classId;
    // Key 是前端定义的 'overallChart' 或 'scatter_xxx'
    // Value 是 'data:image/png;base64,xxx' 字符串
    private Map<String, String> chartImages;
}
