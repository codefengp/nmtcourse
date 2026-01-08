package cn.fengp.basic.module.nmt.service.evaluateplan;

import cn.fengp.basic.framework.common.exception.ServerException;
import cn.fengp.basic.framework.common.exception.util.ServiceExceptionUtil;
import cn.fengp.basic.module.nmt.dal.dataobject.evaluateplan.EvaluatePlanExDO;
import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import cn.fengp.basic.module.nmt.controller.admin.evaluateplan.vo.*;
import cn.fengp.basic.module.nmt.dal.dataobject.evaluateplan.EvaluatePlanDO;
import cn.fengp.basic.framework.common.pojo.PageResult;
import cn.fengp.basic.framework.common.pojo.PageParam;
import cn.fengp.basic.framework.common.util.object.BeanUtils;

import cn.fengp.basic.module.nmt.dal.mysql.evaluateplan.EvaluatePlanMapper;

import static cn.fengp.basic.framework.common.exception.enums.GlobalErrorCodeConstants.BAD_REQUEST;
import static cn.fengp.basic.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.fengp.basic.framework.common.util.collection.CollectionUtils.convertList;
import static cn.fengp.basic.framework.common.util.collection.CollectionUtils.diffList;
import static cn.fengp.basic.module.nmt.enums.ErrorCodeConstants.*;

/**
 * 课程考核计划 Service 实现类
 *
 * @author fengpeng
 */
@Service
@Validated
public class EvaluatePlanServiceImpl implements EvaluatePlanService {

    @Resource
    private EvaluatePlanMapper evaluatePlanMapper;

    @Override
    public Long createEvaluatePlan(EvaluatePlanSaveReqVO createReqVO) {
        // 插入
        EvaluatePlanDO evaluatePlan = BeanUtils.toBean(createReqVO, EvaluatePlanDO.class);
        evaluatePlanMapper.insert(evaluatePlan);

        // 返回
        return evaluatePlan.getId();
    }

    @Override
    public void updateEvaluatePlan(EvaluatePlanSaveReqVO updateReqVO) {
        // 校验存在
        validateEvaluatePlanExists(updateReqVO.getId());
        // 更新
        EvaluatePlanDO updateObj = BeanUtils.toBean(updateReqVO, EvaluatePlanDO.class);
        evaluatePlanMapper.updateById(updateObj);
    }

    @Override
    public void deleteEvaluatePlan(Long id) {
        // 校验存在
        validateEvaluatePlanExists(id);
        // 删除
        evaluatePlanMapper.deleteById(id);
    }

    @Override
        public void deleteEvaluatePlanListByIds(List<Long> ids) {
        // 删除
        evaluatePlanMapper.deleteByIds(ids);
        }


    private void validateEvaluatePlanExists(Long id) {
        if (evaluatePlanMapper.selectById(id) == null) {
            throw exception(EVALUATE_PLAN_NOT_EXISTS);
        }
    }

    @Override
    public EvaluatePlanDO getEvaluatePlan(Long id) {
        return evaluatePlanMapper.selectById(id);
    }

    @Override
    public PageResult<EvaluatePlanDO> getEvaluatePlanPage(EvaluatePlanPageReqVO pageReqVO) {
        return evaluatePlanMapper.selectPage(pageReqVO);
    }

    @Override
    public List<EvaluatePlanExDO> listEvaluatePlan(Long courseId) {
        if(courseId == null || courseId == 0){
            throw exception(BAD_REQUEST);
        }
        return evaluatePlanMapper.listEvaluatePlan(courseId);
    }

    /**
     * 保存课程考核计划集合
     * @param reqVOs
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveEvaluatePlanList(List<EvaluatePlanSaveReqVO> reqVOs) {
        if(CollectionUtils.isEmpty(reqVOs)){
            throw ServiceExceptionUtil.invalidParamException("集合数据为空");
        }
        //1.初始化结果集合
        //删除数据集合
        List<Long> deleteIds = new ArrayList<>();
        //更新数据集合
        List<EvaluatePlanDO> updateList = new ArrayList<>();
        //新增数据集合
        List<EvaluatePlanDO> insertList = new ArrayList<>();

        //2.建立ID到现有数据的映射
        Map<Long, EvaluatePlanDO> existMap = evaluatePlanMapper.selectList()
                .stream()
                .collect(Collectors.toMap(EvaluatePlanDO::getId, Function.identity()));

        // 3.处理每个数据
        for (EvaluatePlanSaveReqVO reqVO : reqVOs) {
            //处理内容数据
            this.processContent(reqVO, existMap, deleteIds, updateList, insertList);
        }
        // 4.批量处理数据
        this.executeBatchOperations(deleteIds,updateList,insertList);
    }


    /**
     * 处理内容数据
     * 无id
     *      1.content为空，直接返回
     *      2.content不为空，顿号（、）分隔
     *        2.1 数组所有数据新增计划
     * 有id
     *     1.content为空，直接删除考核数据
     *     2.content不为空，顿号（、）分隔
     *       2.1 数组等于1，判断是否更新
     *       2.2 数组大于1，第一条判断是否更新，后面新增计划数据
     * @param reqVO
     * @param existMap
     * @param deleteIds
     * @param updateList
     * @param insertList
     */
    private void processContent(EvaluatePlanSaveReqVO reqVO,
                                Map<Long, EvaluatePlanDO> existMap,
                                List<Long> deleteIds,
                                List<EvaluatePlanDO> updateList,
                                List<EvaluatePlanDO> insertList) {

        // 1. 获取有效内容数组
        String[] contents = StringUtils.hasText(reqVO.getContent())
                ? org.apache.commons.lang3.StringUtils.split(reqVO.getContent(), "、")
                : new String[0];
        // 2. 无ID的情况：全部新增
        if (reqVO.getId() == null || reqVO.getId() == 0) {
            if (contents.length > 0) {
                Arrays.stream(contents)
                        .map(content -> createNewDO(reqVO, content, getScore(reqVO)))
                        .forEach(insertList::add);
            }
            return;
        }
        // 3. 有ID的情况
        EvaluatePlanDO existDO = existMap.get(reqVO.getId());
        if (existDO == null) {
            throw ServiceExceptionUtil.invalidParamException("主键{}在数据库中不存在", reqVO.getId());
        }
        if (contents.length == 0) {
            deleteIds.add(reqVO.getId());
            return;
        }
        // 处理第一条
        if (needUpdate(existDO, contents[0], getScore(reqVO))) {
            updateList.add(createUpdateDO(reqVO, contents[0]));
        }
        // 处理剩余记录
        Arrays.stream(contents, 1, contents.length)
                .map(content -> createNewDO(reqVO, content, getScore(reqVO)))
                .forEach(insertList::add);
    }

    /**
     * 新建计划数据
     * @param reqVO
     * @param content
     * @param score
     * @return
     */
    private EvaluatePlanDO createNewDO(EvaluatePlanSaveReqVO reqVO, String content, BigDecimal score) {
        EvaluatePlanDO newDO = BeanUtils.toBean(reqVO, EvaluatePlanDO.class);
        newDO.setId(null);
        newDO.setContent(content);
        newDO.setScore(score);
        return newDO;
    }

    /**
     * 更新计划数据
     * @param reqVO
     * @param content
     * @return
     */
    private EvaluatePlanDO createUpdateDO(EvaluatePlanSaveReqVO reqVO, String content) {
        EvaluatePlanDO updateDO = BeanUtils.toBean(reqVO, EvaluatePlanDO.class);
        updateDO.setContent(content);
        updateDO.setScore(getScore(reqVO));
        return updateDO;
    }

    /**
     * 判断是否需要更新计划
     * @param existDO
     * @param content
     * @param newScore
     * @return
     */
    private boolean needUpdate(EvaluatePlanDO existDO, String content, BigDecimal newScore) {
        return !content.equals(existDO.getContent())
                || !Objects.equals(newScore, existDO.getScore());
    }

    /**
     * 设置分数
     * @param reqVO
     * @return
     */
    private BigDecimal getScore(EvaluatePlanSaveReqVO reqVO) {
        return reqVO.getScore() != null ? reqVO.getScore() : BigDecimal.ZERO;
    }

    /**
     * 批量处理数据
     * @param deleteIds
     * @param updateList
     * @param insertList
     */
    private void executeBatchOperations(List<Long> deleteIds,
                                        List<EvaluatePlanDO> updateList,
                                        List<EvaluatePlanDO> insertList) {
        // 添加事务保证一致性
        if (!deleteIds.isEmpty()) {
            evaluatePlanMapper.deleteByIds(deleteIds);
        }
        if (!updateList.isEmpty()) {
            evaluatePlanMapper.updateBatch(updateList);
        }
        if (!insertList.isEmpty()) {
            evaluatePlanMapper.insertBatch(insertList);
        }
    }

}

