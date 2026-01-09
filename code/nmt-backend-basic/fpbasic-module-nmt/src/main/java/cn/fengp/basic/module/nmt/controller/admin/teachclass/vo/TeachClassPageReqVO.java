package cn.fengp.basic.module.nmt.controller.admin.teachclass.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.fengp.basic.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.fengp.basic.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 教学班级分页 Request VO")
@Data
public class TeachClassPageReqVO extends PageParam {

    @Schema(description = "班级编号")
    private String number;

    @Schema(description = "班级名称", example = "芋艿")
    private String name;

    @Schema(description = "班级人数")
    private Integer totalNumber;

    @Schema(description = "负责教师", example = "赵六")
    private String teacherName;

}