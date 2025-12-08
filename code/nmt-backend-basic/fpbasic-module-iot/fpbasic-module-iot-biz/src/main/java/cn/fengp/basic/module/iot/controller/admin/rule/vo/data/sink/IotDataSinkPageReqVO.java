package cn.fengp.basic.module.iot.controller.admin.rule.vo.data.sink;

import cn.fengp.basic.framework.common.enums.CommonStatusEnum;
import cn.fengp.basic.framework.common.pojo.PageParam;
import cn.fengp.basic.framework.common.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.fengp.basic.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - IoT 数据流转目的分页 Request VO")
@Data
public class IotDataSinkPageReqVO extends PageParam {

    @Schema(description = "数据目的名称", example = "赵六")
    private String name;

    @Schema(description = "数据目的状态", example = "2")
    @InEnum(CommonStatusEnum.class)
    private Integer status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}