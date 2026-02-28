package cn.fengp.basic.module.nmt.service.studentachievement;

import cn.fengp.basic.framework.common.exception.util.ServiceExceptionUtil;
import cn.fengp.basic.framework.excel.core.util.ExcelUtils;
import cn.fengp.basic.module.nmt.dal.dataobject.classstudent.ClassStudentDO;
import cn.fengp.basic.module.nmt.dal.dataobject.evaluateplan.EvaluatePlanExDO;
import cn.fengp.basic.module.nmt.dal.dataobject.studentachievement.StudentAchievementPlanDO;
import cn.fengp.basic.module.nmt.service.classstudent.ClassStudentService;
import cn.fengp.basic.module.nmt.service.evaluateplan.EvaluatePlanService;
import cn.fengp.basic.module.nmt.service.studentachievement.dto.HeaderDataDTO;
import cn.fengp.basic.module.nmt.service.studentachievement.dto.ImportRowDTO;
import cn.fengp.basic.module.nmt.service.studentachievement.dto.RowDataDTO;
import cn.fengp.basic.module.nmt.service.studentachievement.dto.TemplateDataDTO;
import cn.fengp.basic.module.nmt.service.studentachievement.excel.ExcelTemplateHelper;
import cn.hutool.core.io.resource.ResourceUtil;
import com.alibaba.fastjson.JSONObject;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import cn.fengp.basic.module.nmt.controller.admin.studentachievement.vo.*;
import cn.fengp.basic.module.nmt.dal.dataobject.studentachievement.StudentAchievementDO;
import cn.fengp.basic.framework.common.pojo.PageResult;
import cn.fengp.basic.framework.common.util.object.BeanUtils;

import cn.fengp.basic.module.nmt.dal.mysql.studentachievement.StudentAchievementMapper;
import org.springframework.web.multipart.MultipartFile;

import static cn.fengp.basic.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.fengp.basic.framework.common.util.collection.CollectionUtils.convertList;
import static cn.fengp.basic.module.nmt.enums.ErrorCodeConstants.*;

/**
 * 学生成绩 Service 实现类
 *
 * @author fengpeng
 */
@Service
@Validated
public class StudentAchievementServiceImpl implements StudentAchievementService {

    @Resource
    private StudentAchievementMapper studentAchievementMapper;

    @Resource
    private EvaluatePlanService evaluatePlanService;

    @Resource
    private ClassStudentService classStudentService;

    @Override
    public Long createStudentAchievement(StudentAchievementSaveReqVO createReqVO) {
        // 插入
        StudentAchievementDO studentAchievement = BeanUtils.toBean(createReqVO, StudentAchievementDO.class);
        studentAchievementMapper.insert(studentAchievement);

        // 返回
        return studentAchievement.getId();
    }

    @Override
    public void updateStudentAchievement(StudentAchievementSaveReqVO updateReqVO) {
        // 校验存在
        validateStudentAchievementExists(updateReqVO.getId());
        // 更新
        StudentAchievementDO updateObj = BeanUtils.toBean(updateReqVO, StudentAchievementDO.class);
        studentAchievementMapper.updateById(updateObj);
    }

    @Override
    public void deleteStudentAchievement(Long id) {
        // 校验存在
        validateStudentAchievementExists(id);
        // 删除
        studentAchievementMapper.deleteById(id);
    }

    @Override
        public void deleteStudentAchievementListByIds(List<Long> ids) {
        // 删除
        studentAchievementMapper.deleteByIds(ids);
        }


    private void validateStudentAchievementExists(Long id) {
        if (studentAchievementMapper.selectById(id) == null) {
            throw exception(STUDENT_ACHIEVEMENT_NOT_EXISTS);
        }
    }

    @Override
    public StudentAchievementDO getStudentAchievement(Long id) {
        return studentAchievementMapper.selectById(id);
    }

    @Override
    public PageResult<StudentAchievementDO> getStudentAchievementPage(StudentAchievementPageReqVO pageReqVO) {
        return studentAchievementMapper.selectPage(pageReqVO);
    }

    @Override
    public List<StudentAchievementDO> listStudentAchievement(Long classId) {
        return studentAchievementMapper.listStudentAchievement(classId);
    }

    /**
     * 【第一阶段：下载模板】
     * 1.准备表头数据、所有业务数据
     * 2.渲染模板
     * 3.导出模板
     * @param response
     * @param params
     * @throws IOException
     */
    @Override
    public void downloadTemplate(HttpServletResponse response, JSONObject params) throws IOException {
        // 调用通用校验方法
        JSONObject bizParams = this.getBizParams(params);
        Long classId = bizParams.getLong("classId");
        Long courseId = bizParams.getLong("courseId");
        // 1.准备表头数据、所有业务数据
        TemplateDataDTO templateData = new TemplateDataDTO(this.getHeaderDataDTOS(courseId)
                , this.getRowDataDTOS(classId));
        try (InputStream ins = ResourceUtil.getStream("template/achievement.xlsx");
             XSSFWorkbook work = new XSSFWorkbook(ins)) {
            // 2. 渲染模板
            ExcelTemplateHelper.renderTemplate(work, templateData,6);
            // 3. 导出模板
            try (OutputStream out = response.getOutputStream()) {
                work.write(out);
                ExcelUtils.write(response, "学生成绩模板.xlsx");
            }
        }
    }

    /**
     * 查询表头数据(考核方式、考核内容、课程目标、总分)
     * @param courseId
     * @return
     */
    private List<HeaderDataDTO> getHeaderDataDTOS(Long courseId) {
        // 转换列 DTO
        List<EvaluatePlanExDO> plans = evaluatePlanService.listEvaluatePlan(courseId);
        // 严格按顺序转换为 HeaderDataDTO
        List<HeaderDataDTO> headers = new ArrayList<>();
        for (EvaluatePlanExDO p : plans) {
            HeaderDataDTO dto = new HeaderDataDTO();
            dto.setPlanId(p.getId());
            dto.setModeName(p.getModeName());
            dto.setContent(p.getContent());
            dto.setObjectiveName(p.getObjectiveName());
            dto.setScore(p.getScore().toString());
            headers.add(dto); // 这里的 List 会保持 SQL 的原始顺序
        }
        return headers;
    }

    /**
     * 查询学生成绩数据
     * @param classId
     * @return
     */
    private List<RowDataDTO> getRowDataDTOS(Long classId) {
        // 转换行 DTO (使用 LinkedHashMap 保持数据库查询顺序)
        List<StudentAchievementPlanDO> achievements = studentAchievementMapper.listStudentWithAchievement(classId);
        Map<Long, RowDataDTO> studentMap = new LinkedHashMap<>();
        for (StudentAchievementPlanDO sa : achievements) {
            //判断studentId 是否存在
            RowDataDTO row = studentMap.computeIfAbsent(sa.getStudentId(), k -> {
                RowDataDTO r = new RowDataDTO();
                r.setStudentId(sa.getStudentId());
                r.setStudentName(sa.getStudentName());
                r.setStudentNumber(sa.getStudentNumber());
                return r;
            });
            //判断学生考核计划是否为空、不为空设置成绩
            if (sa.getPlanId() != null) {
                //Map<Long, String> scores, Key: planId, Value: score
                row.getScores().put(sa.getPlanId(), sa.getScore() != null ? sa.getScore().toString() : "");
            }
        }
        return new ArrayList<>(studentMap.values());
    }

    /**
     * 【第二阶段：验证导入数据】
     *  1.查询数据库已有学生信息数据、考核计划数据
     *  2.获取导入表格数据
     *  3.数据校验判断
     * @param file
     * @param params
     * @return
     */
    @Override
    public JSONObject validateImport(MultipartFile file, JSONObject params) throws IOException {
        // 调用通用校验方法
        JSONObject bizParams = this.getBizParams(params);
        Long classId = bizParams.getLong("classId");
        Long courseId = bizParams.getLong("courseId");
        // 1. 查询数据库已有学生信息数据、考核计划数据
        List<ClassStudentDO> studentList = classStudentService.listClassStudent(classId);
        Map<String, ClassStudentDO> stuMap = studentList.stream()
                .collect(Collectors.toMap(s -> s.getNumber() + s.getName(), s -> s, (v1, v2) -> v1));

        List<EvaluatePlanExDO> plans = evaluatePlanService.listEvaluatePlan(courseId);
        Map<Long, BigDecimal> planMap = plans.stream()
                .collect(Collectors.toMap(EvaluatePlanExDO::getId, EvaluatePlanExDO::getScore));

        // 2.获取导入表格数据
        List<ImportRowDTO> allRows = parseExcelToImportDTOs(file,6,4);

        // 3.数据校验判断
        List<ImportRowDTO> successData = new ArrayList<>();
        List<ImportRowDTO> failData = new ArrayList<>();

        for (ImportRowDTO row : allRows) {
            // --- 调用独立的校验方法 ---
            this.doBusinessValidate(row, stuMap, planMap);
            if (row.hasError()) {
                failData.add(row);
            } else {
                successData.add(row);
            }
        }

        // 4. 返回结果
        JSONObject res = new JSONObject();
        res.put("successData", successData);
        res.put("failData", failData);
        return res;
    }

    /**
     * 解析 excel导入业务数据,并转成dto格式
     * @param file
     * @param headerCursor 表头行数
     * @param hideRowIndex 隐藏行
     * @return
     * @throws IOException
     */
    private List<ImportRowDTO> parseExcelToImportDTOs(MultipartFile file,int headerCursor,int hideRowIndex) throws IOException {
        String sourceFileName = file.getOriginalFilename();
        if(sourceFileName.endsWith(".xls")){
            throw ServiceExceptionUtil.invalidParamException("导入文件格式xls与模版格式xlsx不匹配，请重新下载模版导入");
        }
        List<ImportRowDTO> list = new ArrayList<>();
        try (InputStream is = file.getInputStream(); Workbook wk = new XSSFWorkbook(is)) {
            Sheet sheet = wk.getSheetAt(0);
            Row hideRow = sheet.getRow(hideRowIndex); // 隐藏行 planId

            for (int i = headerCursor; i <= sheet.getLastRowNum(); i++) {
                Row r = sheet.getRow(i);
                //判断是否空行
                if (r == null || ExcelTemplateHelper.isRowEmpty(r)) continue;

                ImportRowDTO dto = new ImportRowDTO();
                dto.setRowIndex(i + 1);//行号
                //学号、姓名
                dto.setStudentNumber(ExcelTemplateHelper.getCellValue(r.getCell(0)));
                dto.setStudentName(ExcelTemplateHelper.getCellValue(r.getCell(1)));
                //成绩
                for (int j = 2; j < hideRow.getLastCellNum(); j++) {
                    String planIdStr = ExcelTemplateHelper.getCellValue(hideRow.getCell(j));
                    if (ExcelTemplateHelper.isNumeric(planIdStr)) {
                        //属性scores为Map类型 -> K: planId, V: score
                        dto.getScores().put(Long.valueOf(planIdStr), ExcelTemplateHelper.getCellValue(r.getCell(j)));
                    }
                }
                list.add(dto);
            }
        }
        return list;
    }

    /**
     * 核心业务校验逻辑
     *  1. 验证学生是否存在
     *  2. 验证每一项成绩在0到总分之间
     * @param row 待校验的数据行
     * @param stuMap 学生信息 k: number + name v: student
     * @param planMap 考核计划分值 k: planId v: 总分score
     */
    private void doBusinessValidate(ImportRowDTO row, Map<String, ClassStudentDO> stuMap, Map<Long, BigDecimal> planMap) {
        // 1. 验证学生是否存在
        String stuKey = row.getStudentNumber() + row.getStudentName();
        ClassStudentDO stu = stuMap.get(stuKey);
        if (stu == null) {
            row.addError("student", "该学生不属于当前班级，请检查学号和姓名");
        } else {
            row.setStudentId(stu.getId());
        }
        // 2. 验证每一项成绩在0到总分之间
        row.getScores().forEach((planId, scoreStr) -> {
            // 考核计划总分
            BigDecimal maxScore = planMap.get(planId);
            if (!StringUtils.hasText(scoreStr) || !ExcelTemplateHelper.isNumeric(scoreStr)) {
                row.addError(planId.toString(), "成绩必须为有效数字");
            } else {
                BigDecimal stuScore = new BigDecimal(scoreStr);
                if (stuScore.compareTo(BigDecimal.ZERO) < 0 || (maxScore != null && stuScore.compareTo(maxScore) > 0)) {
                    row.addError(planId.toString(), "分值超出合法范围(0-" + (maxScore == null ? "未知" : maxScore) + ")");
                }
            }
        });
    }

    /**
     * 【第三阶段：导出错误】
     *  1.获取表头元数据
     *  2.渲染表格
     *  3.导出错误信息
     * @param response
     * @param params
     * @throws IOException
     */
    @Override
    public void outFail(HttpServletResponse response, JSONObject params) throws IOException {
        // 获取参数内容
        JSONObject bizParams = this.getBizParams(params);
        Long courseId = bizParams.getLong("courseId");
        List<ImportRowDTO> successData = params.getJSONArray("successData").toJavaList(ImportRowDTO.class);
        List<ImportRowDTO> failData = params.getJSONArray("failData").toJavaList(ImportRowDTO.class);
        // 1. 获取表头元数据
        List<HeaderDataDTO> headers = this.getHeaderDataDTOS(courseId);
        // 2. 渲染表格
        try (InputStream ins = ResourceUtil.getStream("template/achievement.xlsx");
             XSSFWorkbook work = new XSSFWorkbook(ins)) {
            // 调用 Helper 中专门为失败设计的渲染入口
            ExcelTemplateHelper.renderFail(work, headers, 6,failData, successData);
            // 3. 导出错误信息
            ExcelUtils.write(response, "成绩导入错误表.xlsx");
            work.write(response.getOutputStream());
        }
    }

    /**
     * 【第四阶段：导入数据】
     *  1.查询数据库学生已有成绩
     *  2.设置导入数据集合(判断是否已有学生成绩数据，无则新增、有则更新)
     *      2.1 如果是新增，初始化基础信息
     *      2.2 设置成绩
     *  3.批量执行更新/新增
     * @param params
     */
    @Override
    public void importExcel(JSONObject params) {
        // 获取参数
        JSONObject bizParams = this.getBizParams(params);
        Long classId = bizParams.getLong("classId");
        Long courseId = bizParams.getLong("courseId");
        // 将 JSONObject 序列化为我们定义的 DTO
        List<ImportRowDTO> successData = params.getJSONArray("successData").toJavaList(ImportRowDTO.class);
        if (CollectionUtils.isEmpty(successData)) {
            throw ServiceExceptionUtil.invalidParamException("没有可导入的有效数据");
        }
        // 获取所有考核计划元数据
        List<EvaluatePlanExDO> planList = evaluatePlanService.listEvaluatePlan(courseId);

        // 1. 查询数据库学生已有成绩
        // 将已有成绩转为 Map，Key 为 "studentId_planId"，实现 O(1) 级别的快速查找
        List<StudentAchievementDO> existAchievementList = studentAchievementMapper.listStudentAchievement(classId);
        Map<String, StudentAchievementDO> existMap = existAchievementList.stream()
                .collect(Collectors.toMap(
                        e -> e.getStudentId() + "_" + e.getPlanId(),
                        e -> e,
                        (v1, v2) -> v1 // 防重处理
                ));

        // 2. 设置导入数据集合(判断是否已有学生成绩数据，无则新增、有则更新)
        List<StudentAchievementDO> resultList = new ArrayList<>();
        for (ImportRowDTO rowDto : successData) {
            Long studentId = rowDto.getStudentId();
            for (EvaluatePlanExDO plan : planList) {
                Long planId = plan.getId();
                String key = studentId + "_" + planId;
                // 判断是更新还是新增
                StudentAchievementDO achievementDO = existMap.getOrDefault(key, new StudentAchievementDO());
                // 2.1 如果是新增，初始化基础信息
                if (achievementDO.getId() == null) {
                    achievementDO.setStudentId(studentId);
                    achievementDO.setPlanId(planId);
                    achievementDO.setObjectiveId(plan.getObjectiveId());
                    achievementDO.setModeId(plan.getModeId());
                }
                // 2.2 设置成绩
                String scoreStr = rowDto.getScores().get(planId);
                if (!StringUtils.hasText(scoreStr)) {
                    // 如果在第二阶段校验得当，这里通常不会报错，但为了健壮性保留异常
                    throw ServiceExceptionUtil.invalidParamException("学号 [" + rowDto.getStudentNumber() + "] 缺少考核项 [" + plan.getContent() + "] 的成绩");
                }
                // 格式化成绩：两位小数，直接截取（向下取整）
                achievementDO.setScore(new BigDecimal(scoreStr).setScale(2, RoundingMode.DOWN));
                resultList.add(achievementDO);
            }
        }
        // 3. 批量执行更新/新增
        if (!resultList.isEmpty()) {
            studentAchievementMapper.insertOrUpdate(resultList);
        }
    }

    /**
     * 导出成绩表
     * @param response
     * @param params
     * @throws IOException
     */
    @Override
    public void exportExcelData(HttpServletResponse response, JSONObject params) throws IOException {
        //直接调用下载模板
        this.downloadTemplate(response, params);
    }

    /**
     * 统一判断业务参数
     * @param params
     * @return
     */
    private JSONObject getBizParams(JSONObject params) {
        JSONObject bizParams = params.getJSONObject("bizParams");
        if (bizParams == null) {
            throw ServiceExceptionUtil.invalidParamException("业务参数不能为空");
        }
        if (!StringUtils.hasText(bizParams.getString("classId"))) {
            throw ServiceExceptionUtil.invalidParamException("班级标识不能为空");
        }
        if (!StringUtils.hasText(bizParams.getString("courseId"))) {
            throw ServiceExceptionUtil.invalidParamException("课程标识不能为空");
        }
        return bizParams;
    }

}