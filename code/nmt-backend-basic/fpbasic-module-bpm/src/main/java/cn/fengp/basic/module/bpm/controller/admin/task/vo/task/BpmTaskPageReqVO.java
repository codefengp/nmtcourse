package cn.fengp.basic.module.bpm.controller.admin.task.vo.task;

import cn.fengp.basic.framework.common.pojo.PageParam;
import cn.fengp.basic.framework.common.util.date.DateUtils;
import cn.fengp.basic.framework.common.validation.InEnum;
import cn.fengp.basic.module.bpm.enums.task.BpmTaskStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 流程任务的的分页 Request VO") // 待办、已办，都使用该分页
@Data
public class BpmTaskPageReqVO extends PageParam {

    @Schema(description = "流程任务名", example = "芋道")
    private String name;

    @Schema(description = "流程分类", example = "1")
    private String category;

    @Schema(description = "流程定义的标识", example = "2048")
    private String processDefinitionKey; // 精准匹配

    @Schema(description = "审批状态", example = "1")
    @InEnum(BpmTaskStatusEnum.class)
    private Integer status; // 仅【已办】使用

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
