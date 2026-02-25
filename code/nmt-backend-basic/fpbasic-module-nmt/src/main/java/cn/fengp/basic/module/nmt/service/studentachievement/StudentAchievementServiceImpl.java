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
        TemplateDataDTO templateData = this.prepareTemplateData(classId, courseId);
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
     * 准备表头数据、所有业务数据
     * 1.查询表头数据(考核方式、考核内容、课程目标、总分)
     * 2.查询学生成绩数据
     * @param classId
     * @param courseId
     * @return
     */
    private TemplateDataDTO prepareTemplateData(Long classId, Long courseId) {
        // 1. 获取表头数据
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

        // 2.查询学生成绩数据
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
        return new TemplateDataDTO(headers, new ArrayList<>(studentMap.values()));
    }

    /**
     * 验证导入数据
     *  1.获取表格数据
     *  2.验证表格数据
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
        // 1. 准备校验上下文 (Context)
        List<ClassStudentDO> studentList = classStudentService.listClassStudent(classId);
        Map<String, ClassStudentDO> stuMap = studentList.stream()
                .collect(Collectors.toMap(s -> s.getNumber() + s.getName(), s -> s, (v1, v2) -> v1));

        List<EvaluatePlanExDO> plans = evaluatePlanService.listEvaluatePlan(courseId);
        Map<Long, BigDecimal> planMap = plans.stream()
                .collect(Collectors.toMap(EvaluatePlanExDO::getId, EvaluatePlanExDO::getScore));

        // 2. 解析数据 (第一步：获取数据)
        List<ImportRowDTO> allRows = parseExcelToImportDTOs(file);

        // 3. 执行校验 (第二步：验证数据)
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
     * 解析 excel导入数据,并转成dto格式
     * @param file
     * @return
     * @throws IOException
     */
    private List<ImportRowDTO> parseExcelToImportDTOs(MultipartFile file) throws IOException {
        String sourceFileName = file.getOriginalFilename();
        if(sourceFileName.endsWith(".xls")){
            throw ServiceExceptionUtil.invalidParamException("导入文件格式xls与模版格式xlsx不匹配，请重新下载模版导入");
        }
        List<ImportRowDTO> list = new ArrayList<>();
        try (InputStream is = file.getInputStream(); Workbook wk = new XSSFWorkbook(is)) {
            Sheet sheet = wk.getSheetAt(0);
            Row hideRow = sheet.getRow(4); // 隐藏行 planId

            for (int i = 6; i <= sheet.getLastRowNum(); i++) {
                Row r = sheet.getRow(i);
                if (r == null || ExcelTemplateHelper.isRowEmpty(r)) continue;

                ImportRowDTO dto = new ImportRowDTO();
                dto.setRowIndex(i + 1);
                dto.setStudentNumber(ExcelTemplateHelper.getCellValue(r.getCell(0)));
                dto.setStudentName(ExcelTemplateHelper.getCellValue(r.getCell(1)));

                for (int j = 2; j < hideRow.getLastCellNum(); j++) {
                    String planIdStr = ExcelTemplateHelper.getCellValue(hideRow.getCell(j));
                    if (ExcelTemplateHelper.isNumeric(planIdStr)) {
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
     * @param row 待校验的数据行
     * @param stuMap 学生索引上下文
     * @param planMap 考核计划分值上限上下文
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

        // 2. 验证每一项成绩
        row.getScores().forEach((planId, scoreStr) -> {
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
     * 导出错误
     *  1.获取导入数据、参数
     *  2.渲染表格
     *      2.1 设置表头内容
     *      2.2 设置验证错误行内容
     *      2.3 设置错误批注
     *      2.4 分割行标识
     *      2.5 设置验证正确数据
     *  3.导出错误模板
     * @param response
     * @param params
     * @throws IOException
     */
    @Override
    public void outFail(HttpServletResponse response, JSONObject params) throws IOException {
        // 调用通用校验方法
        JSONObject bizParams = this.getBizParams(params);
        Long classId = bizParams.getLong("courseId");
        Long courseId = bizParams.getLong("courseId");
        List<ImportRowDTO> successData = params.getJSONArray("successData").toJavaList(ImportRowDTO.class);
        List<ImportRowDTO> failData = params.getJSONArray("failData").toJavaList(ImportRowDTO.class);

        // 3. 获取表头元数据
        TemplateDataDTO templateData = this.prepareTemplateData(classId, courseId);

        // 4. 执行渲染
        try (InputStream ins = ResourceUtil.getStream("template/achievement.xlsx");
             XSSFWorkbook work = new XSSFWorkbook(ins)) {

            // 调用 Helper 中专门为失败设计的渲染入口
            ExcelTemplateHelper.renderFail(work, templateData, 6,failData, successData);

            // 5. 导出
            ExcelUtils.write(response, "成绩导入错误核对表.xlsx");
            work.write(response.getOutputStream());
        }
    }

    /**
     * 导入数据
     *  1.获取导入数据、参数
     *  2.获取所有考核计划数据
     *  3.获取数据库已有成绩数据
     *  4.获取表格所有考核计划成绩
     *      4.1.判断成绩是否已存在，存在则更新，不存在则新增--通过studentId、planId判断
     *  5.导入数据 -> 更新/新增
     * @param params
     */
    @Override
    public void importExcel(JSONObject params) {
        // 调用通用校验方法
        JSONObject bizParams = this.getBizParams(params);
        Long classId = bizParams.getLong("classId");
        Long courseId = bizParams.getLong("courseId");
        // 将 JSONObject 序列化为我们定义的 DTO
        List<ImportRowDTO> successData = params.getJSONArray("successData").toJavaList(ImportRowDTO.class);
        if (CollectionUtils.isEmpty(successData)) {
            throw ServiceExceptionUtil.invalidParamException("没有可导入的有效数据");
        }

        // 2. 获取所有考核计划元数据
        List<EvaluatePlanExDO> planList = evaluatePlanService.listEvaluatePlan(courseId);

        // 3. 性能优化：预处理数据库已有成绩
        // 将已有成绩转为 Map，Key 为 "studentId_planId"，实现 O(1) 级别的快速查找
        List<StudentAchievementDO> existAchievementList = studentAchievementMapper.listStudentAchievement(classId);
        Map<String, StudentAchievementDO> existMap = existAchievementList.stream()
                .collect(Collectors.toMap(
                        e -> e.getStudentId() + "_" + e.getPlanId(),
                        e -> e,
                        (v1, v2) -> v1 // 防重处理
                ));
        // 4. 构建待入库的列表
        List<StudentAchievementDO> resultList = new ArrayList<>();

        for (ImportRowDTO rowDto : successData) {
            Long studentId = rowDto.getStudentId();
            for (EvaluatePlanExDO plan : planList) {
                Long planId = plan.getId();
                String key = studentId + "_" + planId;
                // 4.1 判断是更新还是新增
                StudentAchievementDO achievementDO = existMap.getOrDefault(key, new StudentAchievementDO());
                // 如果是新增，初始化基础信息
                if (achievementDO.getId() == null) {
                    achievementDO.setStudentId(studentId);
                    achievementDO.setPlanId(planId);
                    achievementDO.setObjectiveId(plan.getObjectiveId());
                    achievementDO.setModeId(plan.getModeId());
                }
                // 4.2 从 DTO 的 Scores Map 中获取对应计划的成绩
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
        // 5. 批量执行更新/新增
        // 注意：这里的 insertOrUpdate 需要在 MyBatis 中使用 ON DUPLICATE KEY UPDATE 或相关批量语法
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