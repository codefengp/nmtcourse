package cn.fengp.basic.module.nmt.service.studentachievement.dto;

import lombok.Data;

/**
 * 通用列数据传输对象
 */
@Data
public class ColumnDataDTO {
    private Long planId;
    private String modeName;//考核方式
    private String content;//考核计划
    private String objectiveName;//课程目标
    private String score; // 总分值
}