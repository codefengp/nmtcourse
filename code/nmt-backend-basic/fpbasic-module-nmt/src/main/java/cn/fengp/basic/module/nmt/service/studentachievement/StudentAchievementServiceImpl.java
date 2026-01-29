package cn.fengp.basic.module.nmt.service.studentachievement;

import cn.fengp.basic.framework.common.exception.util.ServiceExceptionUtil;
import cn.fengp.basic.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.fengp.basic.module.nmt.dal.dataobject.evaluateplan.EvaluatePlanExDO;
import cn.fengp.basic.module.nmt.dal.dataobject.studentachievement.StudentAchievementPlanDO;
import cn.fengp.basic.module.nmt.service.evaluateplan.EvaluatePlanService;
import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.fengp.basic.module.nmt.controller.admin.studentachievement.vo.*;
import cn.fengp.basic.module.nmt.dal.dataobject.studentachievement.StudentAchievementDO;
import cn.fengp.basic.framework.common.pojo.PageResult;
import cn.fengp.basic.framework.common.pojo.PageParam;
import cn.fengp.basic.framework.common.util.object.BeanUtils;

import cn.fengp.basic.module.nmt.dal.mysql.studentachievement.StudentAchievementMapper;

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

    @Override
    public void downloadTemplate(HttpServletResponse response, JSONObject params) {
        JSONObject bizParams = params.getJSONObject("bizParams");
        if(bizParams == null || !StringUtils.hasText(bizParams.getString("classId"))){
            throw ServiceExceptionUtil.invalidParamException("班级标识不能为空");
        }
        if(!StringUtils.hasText(bizParams.getString("courseId"))){
            throw ServiceExceptionUtil.invalidParamException("课程标识不能为空");
        }
        Long classId = bizParams.getLong("classId");
        Long courseId = bizParams.getLong("courseId");

    }

    /**
     * 查询并格式化考核方式表头数据
     * @param courseId
     * @return
     */
    private List<JSONObject> queryWithFormatPlan(Long courseId) {
        List<EvaluatePlanExDO> planList = evaluatePlanService.listEvaluatePlan(courseId);
        List<JSONObject> column = new ArrayList<>();//表头数据
        JSONObject newMode = new JSONObject();
        JSONObject newPlan = new JSONObject();
        for (EvaluatePlanExDO plan : planList) {
            boolean isNewMode = true;//是否新考核方式
            boolean isNewPlan = true;//是否新考核计划
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
            newStu.put(achievement.getPlanId().toString(),achievement.getScore());
            if(isNewStu){
                newStu.put("studentId",achievement.getStudentId());
                newStu.put("studentName",achievement.getStudentName());
                newStu.put("studentNumber",achievement.getStudentNumber());
                result.add(newStu);
            }
        }
        return result;
    }

}