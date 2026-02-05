package cn.fengp.basic.module.nmt.service.studentachievement;

import cn.fengp.basic.framework.common.exception.util.ServiceExceptionUtil;
import cn.fengp.basic.framework.excel.core.util.ExcelUtils;
import cn.fengp.basic.module.nmt.dal.dataobject.evaluateplan.EvaluatePlanExDO;
import cn.fengp.basic.module.nmt.dal.dataobject.studentachievement.StudentAchievementPlanDO;
import cn.fengp.basic.module.nmt.service.evaluateplan.EvaluatePlanService;
import cn.fengp.basic.module.nmt.service.studentachievement.importer.ImportExcelParser;
import cn.fengp.basic.module.nmt.service.studentachievement.importer.StudentAchievementValidator;
import com.alibaba.fastjson.JSONArray;
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
    private ImportExcelParser importExcelParser;


    @Resource
    private StudentAchievementValidator studentAchievementValidator;


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
     * 1.查询考核数据，学生成绩
     * 2.渲染表格
     *     2.1 设置表头内容
     *     2.2 设置学生数据
     * 3.导出模板
     * @param response
     * @param params
     * @throws IOException
     */
    @Override
    public void downloadTemplate(HttpServletResponse response, JSONObject params) throws IOException {
        JSONObject bizParams = params.getJSONObject("bizParams");
        Long classId = bizParams.getLong("classId");
        Long courseId = bizParams.getLong("courseId");

        // 1. 查业务数据（Service 的职责）
        List<JSONObject> column = this.queryWithFormatPlan(courseId);
        List<JSONObject> stuAchievements = this.queryWithFormatStudentAchievement(classId);

        // 2. 构建 Excel（交给 Builder）
        StudentAchievementExcelBuilder builder =
                new StudentAchievementExcelBuilder(column, stuAchievements, new JSONArray(), new JSONArray());

        XSSFWorkbook workbook = builder.buildTemplateWorkbook();

        // 3. 输出（Service 只负责 I/O）
        OutputStream out = response.getOutputStream();
        workbook.write(out);
        ExcelUtils.write(response, "学生成绩模板.xlsx");
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
        JSONObject bizParams = params.getJSONObject("bizParams");
        if (bizParams == null || !StringUtils.hasText(bizParams.getString("classId"))) {
            throw ServiceExceptionUtil.invalidParamException("班级标识不能为空");
        }
        Long classId = bizParams.getLong("classId");

        // 1. 解析 Excel
        ImportExcelData importData = importExcelParser.parse(file, 4, 6);

        // 2. 校验数据
        return studentAchievementValidator.validate(importData, classId);
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
        JSONObject bizParams = params.getJSONObject("bizParams");
        Long courseId = bizParams.getLong("courseId");

        // 获取表头
        List<JSONObject> column = this.queryWithFormatPlan(courseId);

        // 错误数据、成功数据
        JSONArray failData = params.getJSONArray("failData");
        JSONArray successData = params.getJSONArray("successData");

        // 构建 Excel
        StudentAchievementExcelBuilder builder = new StudentAchievementExcelBuilder(column, new ArrayList<>(), failData, successData);
        XSSFWorkbook work = builder.buildOutFailWorkbook();

        //3.导出错误模板
        OutputStream out = response.getOutputStream();
        work.write(out);// 将数据写出去
        ExcelUtils.write(response, "错误信息模板.xlsx");
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
        //1.获取导入数据、参数
        JSONObject bizParams = params.getJSONObject("bizParams");
        if(bizParams == null || !StringUtils.hasText(bizParams.getString("classId"))){
            throw ServiceExceptionUtil.invalidParamException("班级标识不能为空");
        }
        if(bizParams == null || !StringUtils.hasText(bizParams.getString("courseId"))){
            throw ServiceExceptionUtil.invalidParamException("课程标识不能为空");
        }
        Long classId = bizParams.getLong("classId");
        Long courseId = bizParams.getLong("courseId");
        JSONArray successData = params.getJSONArray("successData");
        if (CollectionUtils.isEmpty(successData)) {
            throw ServiceExceptionUtil.invalidParamException("导入数据不能为空");
        }

        //2.获取所有考核计划数据
        List<EvaluatePlanExDO> planList = evaluatePlanService.listEvaluatePlan(courseId);

        //3.获取数据库已有成绩数据
        List<StudentAchievementDO> existAchievementList = studentAchievementMapper.listStudentAchievement(classId);

        //4.获取表格所有考核计划成绩
        List<StudentAchievementDO> resultList =
                buildAchievementList(successData, planList, existAchievementList);

        //5.导入数据 -> 更新/新增
        studentAchievementMapper.insertOrUpdate(resultList);
    }

    @Override
    public void exportExcelData(HttpServletResponse response, JSONObject params) throws IOException {
        //直接调用下载模板
        this.downloadTemplate(response, params);
    }


    /**
     * 查询并格式化考核方式表头数据
     * @param courseId
     * @return
     */
    private List<JSONObject> queryWithFormatPlan(Long courseId) {
        List<EvaluatePlanExDO> planList = evaluatePlanService.listEvaluatePlan(courseId);
        List<JSONObject> column = new ArrayList<>();//表头数据
        for (EvaluatePlanExDO plan : planList) {
            boolean isNewMode = true;//是否新考核方式
            boolean isNewPlan = true;//是否新考核计划
            JSONObject newMode = new JSONObject();
            JSONObject newPlan = new JSONObject();
            //判断是否已有考核方式
            for (JSONObject col : column) {
                if(StringUtils.hasText(col.getString("modeId"))
                        && col.getString("modeId").equals(plan.getModeId().toString())){
                    isNewMode = false;
                    newMode = col;
                    break;
                }
            }
            //新考核方式
            if(isNewMode){
                newMode.put("modeId",plan.getModeId());
                newMode.put("modeName",plan.getModeName());
                newMode.put("planCount",1);
                List<JSONObject> subPlanList = new ArrayList<>();
                newPlan.put("planId",plan.getId());
                newPlan.put("content",plan.getContent());
                newPlan.put("objectiveName",plan.getObjectiveName());
                newPlan.put("score",plan.getScore());
                subPlanList.add(newPlan);//考核方式下新增考核计划
                newMode.put("planList",subPlanList);//考核方式下新增计划集合
                column.add(newMode);//表头新增考核方式
            }else{
                //判断是否已有考核计划
                List<JSONObject> existPlanList = (List<JSONObject>)newMode.get("planList");
                for (JSONObject existPlan : existPlanList) {
                    if(existPlan.getString("planId").equals(plan.getId().toString())){
                        isNewPlan = false;
                        break;
                    }
                }
                //新考核计划
                if(isNewPlan){
                    newPlan.put("planId",plan.getId());
                    newPlan.put("content",plan.getContent());
                    newPlan.put("objectiveName",plan.getObjectiveName());
                    newPlan.put("score",plan.getScore());
                    existPlanList.add(newPlan);//考核方式下新增考核计划
                    newMode.put("planCount",existPlanList.size());//更新考核方式下考核计划数量
                }
            }
        }
        return column;
    }

    /**
     * 查询并格式化学生成绩
     * @param classId
     * @return
     */
    private List<JSONObject> queryWithFormatStudentAchievement(Long classId) {
        List<StudentAchievementPlanDO> achievementList = studentAchievementMapper.listStudentWithAchievement(classId);
        List<JSONObject> result = new ArrayList<JSONObject>();
        for (StudentAchievementPlanDO achievement : achievementList) {
            boolean isNewStu = true;
            JSONObject newStu = new JSONObject();
            for (JSONObject res : result) {
                if(res.getString("studentId").equals(achievement.getStudentId().toString())){
                    isNewStu = false;
                    newStu = res;
                    break;
                }
            }
            //用计划标识作key,添加计划成绩
            if(Objects.nonNull(achievement.getPlanId())){
                newStu.put(achievement.getPlanId().toString(),achievement.getScore());
            }
            if(isNewStu){
                newStu.put("studentId",achievement.getStudentId());
                newStu.put("studentName",achievement.getStudentName());
                newStu.put("studentNumber",achievement.getStudentNumber());
                result.add(newStu);
            }
        }
        return result;
    }

    private List<StudentAchievementDO> buildAchievementList(
            JSONArray successData,
            List<EvaluatePlanExDO> planList,
            List<StudentAchievementDO> existAchievementList) {

        List<StudentAchievementDO> resultList = new ArrayList<>();

        for (Object object : successData) {
            JSONObject sud = (JSONObject) object;

            for (EvaluatePlanExDO plan : planList) {
                Optional<StudentAchievementDO> achDo = existAchievementList.stream()
                        .filter(e -> e.getStudentId().equals(sud.getLong("studentId"))
                                && e.getPlanId().equals(plan.getId()))
                        .findFirst();

                StudentAchievementDO achievementDO = achDo.orElseGet(StudentAchievementDO::new);

                if (!achDo.isPresent()) {
                    achievementDO.setStudentId(sud.getLong("studentId"));
                    achievementDO.setPlanId(plan.getId());
                    achievementDO.setObjectiveId(plan.getObjectiveId());
                    achievementDO.setModeId(plan.getModeId());
                }

                String score = sud.getString(plan.getId().toString());
                if (!StringUtils.hasText(score)) {
                    throw ServiceExceptionUtil.invalidParamException("导入数据缺少考核计划内容，请检查表格");
                }

                achievementDO.setScore(new BigDecimal(score).setScale(2, RoundingMode.DOWN));

                resultList.add(achievementDO);
            }
        }
        return resultList;
    }

}