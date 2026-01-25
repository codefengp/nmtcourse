import request from '@/config/axios'

/** 课程考核计划信息 */
export interface EvaluatePlan {
          id: number; // 主键
          objectiveModeId?: number; // 目标评价ID
          objectiveId?: number; // 课程目标ID
          modeId?: number; // 考核方式ID
          score?: number; // 分数/权重
          content?: string; // 考核内容
}

/**
 * 页面 VO
 */
export interface RowVO extends EvaluatePlan{
  objectiveName: string;//课程目标名称
  modeName: string;//考核名称
  totalScore: number;//总分
}

// 课程考核计划 API
export const EvaluatePlanApi = {
  // 查询课程考核计划分页
  getEvaluatePlanPage: async (params: any) => {
    return await request.get({ url: `/nmt/evaluate-plan/page`, params })
  },

  //查询考核计划列表
  listEvaluatePlan: async(courseId: number)=> {
    return await request.get({ url: `/nmt/evaluate-plan/list?courseId=`+ courseId })
  },
  // 保存课程目标考核评价集合
  saveEvaluatePlanList: async (data:  EvaluatePlan[]) => {
      return await request.put({ url: `/nmt/evaluate-plan/save-list`, data })
  },

  // 查询课程考核计划详情
  getEvaluatePlan: async (id: number) => {
    return await request.get({ url: `/nmt/evaluate-plan/get?id=` + id })
  },

  // 新增课程考核计划
  createEvaluatePlan: async (data: EvaluatePlan) => {
    return await request.post({ url: `/nmt/evaluate-plan/create`, data })
  },

  // 修改课程考核计划
  updateEvaluatePlan: async (data: EvaluatePlan) => {
    return await request.put({ url: `/nmt/evaluate-plan/update`, data })
  },

  // 删除课程考核计划
  deleteEvaluatePlan: async (id: number) => {
    return await request.delete({ url: `/nmt/evaluate-plan/delete?id=` + id })
  },

  /** 批量删除课程考核计划 */
  deleteEvaluatePlanList: async (ids: number[]) => {
    return await request.delete({ url: `/nmt/evaluate-plan/delete-list?ids=${ids.join(',')}` })
  },

  // 导出课程考核计划 Excel
  exportEvaluatePlan: async (params) => {
    return await request.download({ url: `/nmt/evaluate-plan/export-excel`, params })
  },

}
