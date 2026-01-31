package cn.fengp.basic.module.nmt.service.studentachievement;

import cn.fengp.basic.framework.common.exception.util.ServiceExceptionUtil;
import cn.fengp.basic.framework.common.pojo.CommonResult;
import cn.fengp.basic.framework.excel.core.util.ExcelUtils;
import cn.fengp.basic.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.fengp.basic.module.nmt.dal.dataobject.classstudent.ClassStudentDO;
import cn.fengp.basic.module.nmt.dal.dataobject.evaluateplan.EvaluatePlanExDO;
import cn.fengp.basic.module.nmt.dal.dataobject.studentachievement.StudentAchievementPlanDO;
import cn.fengp.basic.module.nmt.service.classstudent.ClassStudentService;
import cn.fengp.basic.module.nmt.service.evaluateplan.EvaluatePlanService;
import cn.fengp.basic.module.nmt.service.teachclass.TeachClassService;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.idev.excel.FastExcelFactory;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.fengp.basic.module.nmt.controller.admin.studentachievement.vo.*;
import cn.fengp.basic.module.nmt.dal.dataobject.studentachievement.StudentAchievementDO;
import cn.fengp.basic.framework.common.pojo.PageResult;
import cn.fengp.basic.framework.common.pojo.PageParam;
import cn.fengp.basic.framework.common.util.object.BeanUtils;

import cn.fengp.basic.module.nmt.dal.mysql.studentachievement.StudentAchievementMapper;
import org.springframework.web.multipart.MultipartFile;

import static cn.fengp.basic.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.fengp.basic.framework.common.util.collection.CollectionUtils.convertList;
import static cn.fengp.basic.framework.common.util.collection.CollectionUtils.diffList;
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
        if(bizParams == null || !StringUtils.hasText(bizParams.getString("classId"))){
            throw ServiceExceptionUtil.invalidParamException("班级标识不能为空");
        }
        if(!StringUtils.hasText(bizParams.getString("courseId"))){
            throw ServiceExceptionUtil.invalidParamException("课程标识不能为空");
        }
        Long classId = bizParams.getLong("classId");
        Long courseId = bizParams.getLong("courseId");
        //1.查询考核数据，学生成绩
        //表头数据
        List<JSONObject> column = this.queryWithFormatPlan(courseId);
        //学生成绩
        List<JSONObject> stuAchievements = this.queryWithFormatStudentAchievement(classId);

        //2.渲染表格
        InputStream ins = ResourceUtil.getStream("template/achievement.xlsx");
        XSSFWorkbook work = new XSSFWorkbook(ins);
        XSSFCellStyle style = this.createHeaderStyle(work);
        XSSFCellStyle style1 = this.createRedStyle(work);
        //得到excel的第0张表
        XSSFSheet sheet = work.getSheetAt(0);
        //2.1 设置表头-返回隐藏行计划id
        XSSFRow row4 = this.buildHeader(sheet, column, style, style1);
        //2.2 设置学生数据
        this.fillStudentData(sheet, stuAchievements, row4);

        //3.导出模板
        OutputStream out = response.getOutputStream();
        work.write(out);// 将数据写出去
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
        if(bizParams == null || !StringUtils.hasText(bizParams.getString("classId"))){
            throw ServiceExceptionUtil.invalidParamException("班级标识不能为空");
        }
        Long classId = bizParams.getLong("classId");
        //1.获取导入数据
        JSONObject importData = this.getImportData(file,4,6);
        //2.验证表格数据
        return this.validateImpData(importData,classId,4);
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
        //1.获取导入数据、参数
        JSONObject bizParams = params.getJSONObject("bizParams");
        if (bizParams == null || !StringUtils.hasText(bizParams.getString("classId"))) {
            throw ServiceExceptionUtil.invalidParamException("班级标识不能为空");
        }
        if (!StringUtils.hasText(bizParams.getString("courseId"))) {
            throw ServiceExceptionUtil.invalidParamException("课程标识不能为空");
        }
        Long courseId = bizParams.getLong("courseId");
        JSONArray successData = params.getJSONArray("successData");
        JSONArray failData = params.getJSONArray("failData");
        //2.渲染表格
        //2.1设置表头内容(查询考核数据，学生成绩)
        List<JSONObject> column = this.queryWithFormatPlan(courseId);
        InputStream ins = ResourceUtil.getStream("template/achievement.xlsx");
        XSSFWorkbook work = new XSSFWorkbook(ins);
        XSSFCellStyle style = this.createHeaderStyle(work);
        XSSFCellStyle style1 = this.createRedStyle(work);
        //得到excel的第0张表
        XSSFSheet sheet = work.getSheetAt(0);
        //设置表头-返回隐藏行计划id
        XSSFRow row4 = this.buildHeader(sheet, column, style, style1);
        //表头行数
        int headRowCount = 6;
        //2.2 设置验证错误行内容
        if (!CollectionUtils.isEmpty(failData)) {
            for (int i = 0; i < failData.size(); i++) {
                JSONObject var = (JSONObject) failData.get(i);
                XSSFRow rowi = sheet.createRow(i + headRowCount);
                for (int j = 0; j < row4.getLastCellNum(); j++) {
                    XSSFCell cellij = rowi.createCell(j);
                    cellij.setCellValue(var.getString(row4.getCell(j).getStringCellValue()));
                    //2.3 设置错误批注 (根据key: 隐藏行计划id__error 获取错误提示批注)
                    if (var.get(row4.getCell(j).getStringCellValue() + "__error") != null) {
                        XSSFDrawing p = sheet.createDrawingPatriarch();
                        XSSFComment comment = p.createCellComment(new XSSFClientAnchor(0, 0, 0, 0, (short) 3, 3, (short) 5, 6));
                        //输入批注信息
                        comment.setString(new XSSFRichTextString(var.getString(row4.getCell(j).getStringCellValue() + "__error")));
                        cellij.setCellComment(comment);
                    }
                }
            }
        }
        //2.4 分割行标识
        XSSFRow rowFailEnd = sheet.createRow(headRowCount + failData.size());
        XSSFCell cellFailEnd = rowFailEnd.createCell(0);
        cellFailEnd.setCellStyle(style1);
        cellFailEnd.setCellValue("------------------------错误数据分割行，下方是验证通过的数据行----------------------");
        CellRangeAddress a = new CellRangeAddress(headRowCount + failData.size(), headRowCount + failData.size(), 0, 8);
        sheet.addMergedRegion(a);
        //2.5 设置验证正确数据
        if (!CollectionUtils.isEmpty(successData)) {
            for (int i = 0; i < successData.size(); i++) {
                JSONObject var = (JSONObject) successData.get(i);
                XSSFRow rowi = sheet.createRow(headRowCount + 1 + failData.size() + i);
                for (int j = 0; j < row4.getLastCellNum(); j++) {
                    XSSFCell cellij = rowi.createCell(j);
                    cellij.setCellValue(var.getString(row4.getCell(j).getStringCellValue()));
                }
            }
        }
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
        List<StudentAchievementDO> resultList = new ArrayList<>();
        for (Object object : successData) {
            JSONObject sud = (JSONObject) object;
            //4.1.判断成绩是否已存在，存在则更新，不存在则新增--通过studentId、planId判断
            for (EvaluatePlanExDO plan : planList) {
                Optional<StudentAchievementDO> achDo = existAchievementList.stream()
                        .filter(e -> e.getStudentId().equals(sud.getLong("studentId"))
                                && e.getPlanId().equals(plan.getId())).findFirst();
                StudentAchievementDO achievementDO = achDo.orElseGet(StudentAchievementDO::new);
                if(!achDo.isPresent()){
                    achievementDO.setStudentId(sud.getLong("studentId"));
                    achievementDO.setPlanId(plan.getId());
                    achievementDO.setObjectiveId(plan.getObjectiveId());//课程目标
                    achievementDO.setModeId(plan.getModeId());//考核方式
                }
                //k: planId,v: score
                String score = sud.getString(plan.getId().toString());
                if(!StringUtils.hasText(score)){
                    throw ServiceExceptionUtil.invalidParamException("导入数据缺少考核计划内容，请检查表格");
                }
                //格式：两位小数，直接截取
                achievementDO.setScore(new BigDecimal(score).setScale(2, RoundingMode.DOWN));
                //保存数据
                resultList.add(achievementDO);
            }
        }
        //5.导入数据 -> 更新/新增
        studentAchievementMapper.insertOrUpdate(resultList);
    }

    /**
     * 获取导入数据
     * @param file
     * @param hideRowNum 隐藏行号
     * @param firstRowNum 第一行有效数据行号
     * @return
     */
    private JSONObject getImportData(MultipartFile file,int hideRowNum,int firstRowNum) throws IOException {
        JSONObject result = new JSONObject();
        List<JSONObject> textList = new ArrayList<JSONObject>();
        InputStream inputStream = file.getInputStream();
        String sourceFileName = file.getOriginalFilename();
        Workbook wk = null;
        if(sourceFileName.endsWith(".xls")){
            throw ServiceExceptionUtil.invalidParamException("导入文件格式xls与模版格式xlsx不匹配，请重新下载模版导入");
        }else if (sourceFileName.endsWith(".xlsx")){
            wk= new XSSFWorkbook(inputStream);
        }
        if(Objects.nonNull(wk)){
            //获取第一张Sheet表
            Sheet sheet=wk.getSheetAt(0);
            Row hideRow = sheet.getRow(hideRowNum);
            //表头数据
            String[][] strColumn =new String[firstRowNum][hideRow.getLastCellNum()];
            strColumn[hideRowNum][0] = "number";
            strColumn[hideRowNum][1] = "name";
            for(Row r : sheet){
                //保存表头数据
                if(r.getRowNum()<firstRowNum){
                    for(Cell c:r){
                        if(!StringUtils.hasText(strColumn[c.getRowIndex()][c.getColumnIndex()]) ){
                            c.setCellType(CellType.STRING);
                            strColumn[c.getRowIndex()][c.getColumnIndex()] = c.getStringCellValue();
                        }
                    }
                    continue;
                }
                //表体内容数据
                JSONObject info = new JSONObject();
                info.put("row_num", r.getRowNum());
                for(int i=0;i<r.getLastCellNum() ;i++){
                    Cell cell = r.getCell(i);
                    if(cell!=null){
                        cell.setCellType(CellType.STRING);
                        //隐藏行计划id为key
                        info.put(strColumn[hideRowNum][i], cell.getStringCellValue());
                    }else {
                        info.put(strColumn[hideRowNum][i],"");
                    }
                }
                textList.add(info);
            }
            result.put("strColumn", strColumn);
            result.put("textList", textList);
        }
        return result;
    }

    /**
     * 验证导入数据
     *  1.获取班级学生信息
     *  2.验证数据
     *      2.1 验证学生是否存在
     *      2.2 验证成绩为数字，且在0到计划总分之间
     *  3.返回验证结果
     * @param importData
     * @param classId
     * @param hideRowNum 隐藏行号
     * @return
     */
    private JSONObject validateImpData(JSONObject importData, Long classId, int hideRowNum) {
        String[][] strColumn = (String[][]) importData.get("strColumn");
        if(Objects.isNull(importData.get("textList"))){
            throw ServiceExceptionUtil.invalidParamException("导入数据为空，请检查表格数据");
        }
        List<JSONObject> textList = (List<JSONObject>) importData.get("textList");
        JSONObject resultJO = new JSONObject();
        List<JSONObject> successData = new ArrayList<JSONObject>();
        List<JSONObject> failData = new ArrayList<JSONObject>();

        //1.获取班级学生信息
        List<ClassStudentDO> existStuList = classStudentService.listClassStudent(classId);
        //2.验证数据
        for (JSONObject var : textList) {
            boolean pass = true;
            String number = var.getString("number");
            String name = var.getString("name");
            boolean stuExist = false;
            //2.1 判断学生是否存在，且是该班级学生
            for (ClassStudentDO existStu : existStuList) {
                boolean isNotEmpty = StringUtils.hasText(number) && StringUtils.hasText(name);
                if (isNotEmpty && number.equals(existStu.getNumber()) && name.equals(existStu.getName())) {
                    stuExist = true;
                    var.put("studentId", existStu.getId());
                    break;
                }
            }
            //2.2 验证成绩为数字，且在0到计划总分之间
            if (stuExist) {
                //依次判断每个考核计划分数
                for (int i = 2; i < strColumn[hideRowNum].length; i++) {
                    //计划总分
                    String totalScore = strColumn[hideRowNum - 1][i];
                    //学生成绩
                    String score = var.getString(strColumn[hideRowNum][i]);
                    if (!this.isNumeric(totalScore) || !this.isNumeric(score)) {
                        var.put(strColumn[hideRowNum][i] + "__error", "成绩填写有误！学生成绩和总分必须为数字！");
                        pass = false;
                        continue;
                    }
                    BigDecimal tolScore = new BigDecimal(totalScore);
                    BigDecimal stuScore = new BigDecimal(score);
                    if (stuScore.compareTo(BigDecimal.ZERO) < 0 || stuScore.compareTo(tolScore) > 0) {
                        var.put(strColumn[hideRowNum][i] + "__error", "成绩填写有误！成绩范围是0到总分之间！");
                        pass = false;
                    }
                }
            } else {
                var.put(strColumn[hideRowNum][0] + "__error", "该教学班级未找到学生关联数据！请检查！");
                pass = false;
            }
            if (pass) {
                successData.add(var);
            } else {
                failData.add(var);
            }
        }
        //3.返回验证结果
        resultJO.put("successData", successData);
        if (failData.size() > 0) {
            resultJO.put("failData", failData);
        }
        return resultJO;
    }


    /**
     * 设置表头内容
     * @param sheet
     * @param column
     * @return
     */
    private XSSFRow buildHeader(XSSFSheet sheet,List<JSONObject> column,XSSFCellStyle style,XSSFCellStyle style1) {
        XSSFRow row0 = sheet.createRow(0);
        XSSFRow row1 = sheet.createRow(1);
        XSSFRow row2 = sheet.createRow(2);
        XSSFRow row3 = sheet.createRow(3);
        XSSFRow row4 = sheet.createRow(4);
        XSSFRow row5 = sheet.createRow(5);
        //计划id隐藏行
        row4.setZeroHeight(true);
        row0.createCell(0).setCellValue("考核方式");
        row1.createCell(0).setCellValue("考核内容");
        row2.createCell(0).setCellValue("课程目标");
        row3.createCell(0).setCellValue("总分值");
        row4.createCell(0).setCellValue("number");
        row4.createCell(1).setCellValue("name");
        row5.createCell(0).setCellValue("学号");
        row5.createCell(1).setCellValue("姓名");

        XSSFCell cell52 = row5.createCell(2);
        cell52.setCellValue("--说明：直接根据学生学号、姓名输入对应的的成绩，不要改动表头的任何数据--");
        cell52.setCellStyle(style1);
        // 设置样式
        row0.getCell(0).setCellStyle(style);
        row1.getCell(0).setCellStyle(style);
        row2.getCell(0).setCellStyle(style);
        row3.getCell(0).setCellStyle(style);
        row5.getCell(0).setCellStyle(style);
        row5.getCell(1).setCellStyle(style);

        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 1));
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 1));
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 1));
        sheet.addMergedRegion(new CellRangeAddress(5, 5, 2, 10));
        // 动态表头
        for (int i = 0, index = 2; i < column.size(); i++) {
            JSONObject col = column.get(i);
            List<JSONObject> planList = (List<JSONObject>) col.get("planList");

            XSSFCell cell0Index = row0.createCell(index);
            cell0Index.setCellValue(col.getString("modeName"));
            cell0Index.setCellStyle(style);
            //一个课程目标多个考核计划
            if (planList.size() > 1) {
                sheet.addMergedRegion(
                        new CellRangeAddress(0, 0, index, index + planList.size() - 1));
            }
            for (int j = 0; j < planList.size(); j++) {
                JSONObject obj = planList.get(j);

                XSSFCell c1 = row1.createCell(index + j);
                c1.setCellValue(obj.getString("content"));
                c1.setCellStyle(style);

                XSSFCell c2 = row2.createCell(index + j);
                c2.setCellValue(obj.getString("objectiveName"));
                c2.setCellStyle(style);

                XSSFCell c3 = row3.createCell(index + j);
                c3.setCellValue(obj.getString("score"));
                c3.setCellStyle(style);
                row4.createCell(index + j)
                        .setCellValue(obj.getString("planId"));
            }
            index += planList.size();
        }
        return row4; // 返回隐藏行，后面学生成绩要用
    }

    /**
     * 设置学生成绩
     * @param sheet
     * @param stuAchievements
     * @param row4
     */
    private void fillStudentData(XSSFSheet sheet,List<JSONObject> stuAchievements,XSSFRow row4) {
        //考核计划id隐藏行
        short a = row4.getLastCellNum();
        for(int i=0;i<stuAchievements.size();i++){
            JSONObject param = stuAchievements.get(i);
            XSSFRow dataRow = sheet.createRow(6+i);
            dataRow.createCell(0)
                    .setCellValue(param.getString("studentNumber"));
            dataRow.createCell(1)
                    .setCellValue(param.getString("studentName"));
            for(int j = 2;j<a;j++){
                dataRow.createCell(j).setCellValue(param.getString(row4.getCell(j)
                                                .getStringCellValue()
                                                .toLowerCase()));
            }
        }
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

    /**
     * 设置单元格内容居中
     * @param work
     * @return
     */
    private XSSFCellStyle createHeaderStyle(XSSFWorkbook work) {
        XSSFCellStyle style = work.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        XSSFFont font = work.createFont();
        font.setBold(true);
        style.setFont(font);

        return style;
    }

    /**
     * 字体红色
     * @param work
     * @return
     */
    private XSSFCellStyle createRedStyle(XSSFWorkbook work) {
        XSSFCellStyle style = work.createCellStyle();
        XSSFFont font = work.createFont();
        font.setColor(IndexedColors.RED.getIndex());
        style.setFont(font);
        return style;
    }

    /**
     * 判断字符串是否是整数或者小数
     * @param str
     * @return
     */
    public static boolean isNumeric(String str){
        if(!StringUtils.hasText(str)){
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]*\\.?[0-9]+");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }
}