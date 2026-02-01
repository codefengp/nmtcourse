import request from '@/config/axios'
import type { Dayjs } from 'dayjs';

/** 课程目标达成度评价信息 */
export interface ObjectiveEvaluation {
          id: number; // 主键
          evaluationId?: number; // 课程评价主表ID
          objectiveId?: number; // 课程目标ID
          objectiveName: string; // 目标名称
          objectiveContent: string; // 目标内容
          comment: string; // 评价内容
  }

// 课程目标达成度评价 API
export const ObjectiveEvaluationApi = {
  // 查询课程目标达成度评价分页
  getObjectiveEvaluationPage: async (params: any) => {
    return await request.get({ url: `/nmt/objective-evaluation/page`, params })
  },

  // 查询课程目标达成度评价详情
  getObjectiveEvaluation: async (id: number) => {
    return await request.get({ url: `/nmt/objective-evaluation/get?id=` + id })
  },

  // 新增课程目标达成度评价
  createObjectiveEvaluation: async (data: ObjectiveEvaluation) => {
    return await request.post({ url: `/nmt/objective-evaluation/create`, data })
  },

  // 修改课程目标达成度评价
  updateObjectiveEvaluation: async (data: ObjectiveEvaluation) => {
    return await request.put({ url: `/nmt/objective-evaluation/update`, data })
  },

  // 删除课程目标达成度评价
  deleteObjectiveEvaluation: async (id: number) => {
    return await request.delete({ url: `/nmt/objective-evaluation/delete?id=` + id })
  },

  /** 批量删除课程目标达成度评价 */
  deleteObjectiveEvaluationList: async (ids: number[]) => {
    return await request.delete({ url: `/nmt/objective-evaluation/delete-list?ids=${ids.join(',')}` })
  },

  // 导出课程目标达成度评价 Excel
  exportObjectiveEvaluation: async (params) => {
    return await request.download({ url: `/nmt/objective-evaluation/export-excel`, params })
  },
}