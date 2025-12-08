package cn.fengp.basic.module.iot.controller.admin.alert;

import cn.fengp.basic.framework.common.pojo.CommonResult;
import cn.fengp.basic.framework.common.pojo.PageResult;
import cn.fengp.basic.framework.common.util.object.BeanUtils;
import cn.fengp.basic.module.iot.controller.admin.alert.vo.recrod.IotAlertRecordPageReqVO;
import cn.fengp.basic.module.iot.controller.admin.alert.vo.recrod.IotAlertRecordProcessReqVO;
import cn.fengp.basic.module.iot.controller.admin.alert.vo.recrod.IotAlertRecordRespVO;
import cn.fengp.basic.module.iot.dal.dataobject.alert.IotAlertRecordDO;
import cn.fengp.basic.module.iot.service.alert.IotAlertRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.fengp.basic.framework.common.pojo.CommonResult.success;
import static java.util.Collections.singleton;

@Tag(name = "管理后台 - IoT 告警记录")
@RestController
@RequestMapping("/iot/alert-record")
@Validated
public class IotAlertRecordController {

    @Resource
    private IotAlertRecordService alertRecordService;

    @GetMapping("/get")
    @Operation(summary = "获得告警记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('iot:alert-record:query')")
    public CommonResult<IotAlertRecordRespVO> getAlertRecord(@RequestParam("id") Long id) {
        IotAlertRecordDO alertRecord = alertRecordService.getAlertRecord(id);
        return success(BeanUtils.toBean(alertRecord, IotAlertRecordRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得告警记录分页")
    @PreAuthorize("@ss.hasPermission('iot:alert-record:query')")
    public CommonResult<PageResult<IotAlertRecordRespVO>> getAlertRecordPage(@Valid IotAlertRecordPageReqVO pageReqVO) {
        PageResult<IotAlertRecordDO> pageResult = alertRecordService.getAlertRecordPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, IotAlertRecordRespVO.class));
    }

    @PutMapping("/process")
    @Operation(summary = "处理告警记录")
    @PreAuthorize("@ss.hasPermission('iot:alert-record:process')")
    public CommonResult<Boolean> processAlertRecord(@Valid @RequestBody IotAlertRecordProcessReqVO processReqVO) {
        alertRecordService.processAlertRecordList(singleton(processReqVO.getId()), processReqVO.getProcessRemark());
        return success(true);
    }

}