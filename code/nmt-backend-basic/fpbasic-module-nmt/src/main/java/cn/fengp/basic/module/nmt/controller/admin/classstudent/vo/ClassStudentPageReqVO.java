package cn.fengp.basic.module.nmt.controller.admin.classstudent.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.fengp.basic.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.fengp.basic.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 班级学生分页 Request VO")
@Data
public class ClassStudentPageReqVO extends PageParam {

    @Schema(description = "学生姓名", example = "张三")
    private String name;

    @Schema(description = "学号")
    private String number;

    @Schema(description = "所属班级ID", example = "7044")
    private Long classId;

}