package cn.fengp.basic.module.nmt.controller.admin.teachclass;

import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.constraints.*;
import jakarta.validation.*;
import jakarta.servlet.http.*;
import java.util.*;
import java.io.IOException;

import cn.fengp.basic.framework.common.pojo.PageParam;
import cn.fengp.basic.framework.common.pojo.PageResult;
import cn.fengp.basic.framework.common.pojo.CommonResult;
import cn.fengp.basic.framework.common.util.object.BeanUtils;
import static cn.fengp.basic.framework.common.pojo.CommonResult.success;

import cn.fengp.basic.framework.excel.core.util.ExcelUtils;

import cn.fengp.basic.framework.apilog.core.annotation.ApiAccessLog;
import static cn.fengp.basic.framework.apilog.core.enums.OperateTypeEnum.*;

import cn.fengp.basic.module.nmt.controller.admin.teachclass.vo.*;
import cn.fengp.basic.module.nmt.dal.dataobject.teachclass.TeachClassDO;
import cn.fengp.basic.module.nmt.service.teachclass.TeachClassService;

@Tag(name = "管理后台 - 教学班级")
@RestController
@RequestMapping("/nmt/teach-class")
@Validated
public class TeachClassController {

    @Resource
    private TeachClassService teachClassService;

    @PostMapping("/create")
    @Operation(summary = "创建教学班级")
    @PreAuthorize("@ss.hasPermission('nmt:teach-class:create')")
    public CommonResult<Long> createTeachClass(@Valid @RequestBody TeachClassSaveReqVO createReqVO) {
        return success(teachClassService.createTeachClass(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新教学班级")
    @PreAuthorize("@ss.hasPermission('nmt:teach-class:update')")
    public CommonResult<Boolean> updateTeachClass(@Valid @RequestBody TeachClassSaveReqVO updateReqVO) {
        teachClassService.updateTeachClass(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除教学班级")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('nmt:teach-class:delete')")
    public CommonResult<Boolean> deleteTeachClass(@RequestParam("id") Long id) {
        teachClassService.deleteTeachClass(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除教学班级")
                @PreAuthorize("@ss.hasPermission('nmt:teach-class:delete')")
    public CommonResult<Boolean> deleteTeachClassList(@RequestParam("ids") List<Long> ids) {
        teachClassService.deleteTeachClassListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得教学班级")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('nmt:teach-class:query')")
    public CommonResult<TeachClassRespVO> getTeachClass(@RequestParam("id") Long id) {
        TeachClassDO teachClass = teachClassService.getTeachClass(id);
        return success(BeanUtils.toBean(teachClass, TeachClassRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得教学班级分页")
    @PreAuthorize("@ss.hasPermission('nmt:teach-class:query')")
    public CommonResult<PageResult<TeachClassRespVO>> getTeachClassPage(@Valid TeachClassPageReqVO pageReqVO) {
        PageResult<TeachClassDO> pageResult = teachClassService.getTeachClassPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, TeachClassRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出教学班级 Excel")
    @PreAuthorize("@ss.hasPermission('nmt:teach-class:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportTeachClassExcel(@Valid TeachClassPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<TeachClassDO> list = teachClassService.getTeachClassPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "教学班级.xls", "数据", TeachClassRespVO.class,
                        BeanUtils.toBean(list, TeachClassRespVO.class));
    }

}