import request from '@/config/axios'

/** 考核评价方式信息 */
export interface EvaluateMode {
          id: number; // 主键ID
          name?: string; // 评价方式
          weight?: number; // 权重
          courseId?: number; // 课程ID
  }

// 考核评价方式 API
export const EvaluateModeApi = {
  // 查询考核评价方式分页
  getEvaluateModePage: async (params: any) => {
    return await request.get({ url: `/nmt/evaluate-mode/page`, params })
  },

  // 查询考核评价方式详情
  getEvaluateMode: async (id: number) => {
    return await request.get({ url: `/nmt/evaluate-mode/get?id=` + id })
  },

  // 新增考核评价方式
  createEvaluateMode: async (data: EvaluateMode) => {
    return await request.post({ url: `/nmt/evaluate-mode/create`, data })
  },

  // 修改考核评价方式
  updateEvaluateMode: async (data: EvaluateMode) => {
    return await request.put({ url: `/nmt/evaluate-mode/update`, data })
  },

  // 删除考核评价方式
  deleteEvaluateMode: async (id: number) => {
    return await request.delete({ url: `/nmt/evaluate-mode/delete?id=` + id })
  },

  /** 批量删除考核评价方式 */
  deleteEvaluateModeList: async (ids: number[]) => {
    return await request.delete({ url: `/nmt/evaluate-mode/delete-list?ids=${ids.join(',')}` })
  },

  // 导出考核评价方式 Excel
  exportEvaluateMode: async (params) => {
    return await request.download({ url: `/nmt/evaluate-mode/export-excel`, params })
  },
}
