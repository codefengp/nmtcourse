package cn.fengp.basic.module.nmt.service.studentachievement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 通用导出模板数据包装
 */
@Data
@AllArgsConstructor
public class TemplateDataDTO {
    private List<ColumnDataDTO> columns;
    private List<RowDataDTO> rows;
}