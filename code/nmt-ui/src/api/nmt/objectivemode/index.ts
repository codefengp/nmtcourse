import request from '@/config/axios'

/** 课程目标考核评价信息 */
export interface ObjectiveMode {
          id?: number; // 主键ID
          courseId?: number;//课程ID
          objectiveId?: number; // 课程目标ID
          objectiveName?: string; // 课程目标
          modeId?: number; // 考核方式ID
          modeName?: string; // 考核方式
          score?: number; // 分值
  }

// 课程目标考核评价 API
export const ObjectiveModeApi = {
  // 查询课程目标考核评价分页
  getObjectiveModePage: async (params: any) => {
    return await request.get({ url: `/nmt/objective-mode/page`, params })
  },

    // 查询课程目标考核评价
    listObjectiveMode: async (courseId: number) => {
        return await request.get({ url: `/nmt/objective-mode/list?courseId=` + courseId })
    },

  // 查询课程目标考核评价详情
  getObjectiveMode: async (id: number) => {
    return await request.get({ url: `/nmt/objective-mode/get?id=` + id })
  },

  // 新增课程目标考核评价
  createObjectiveMode: async (data: ObjectiveMode) => {
    return await request.post({ url: `/nmt/objective-mode/create`, data })
  },

  // 修改课程目标考核评价
  updateObjectiveMode: async (data: ObjectiveMode) => {
    return await request.put({ url: `/nmt/objective-mode/update`, data })
  },

    // 修改课程目标考核评价集合
    updateObjectiveModeList: async (data:  ObjectiveMode[]) => {
        return await request.put({ url: `/nmt/objective-mode/update-list`, data })
    },

  // 删除课程目标考核评价
  deleteObjectiveMode: async (id: number) => {
    return await request.delete({ url: `/nmt/objective-mode/delete?id=` + id })
  },

  /** 批量删除课程目标考核评价 */
  deleteObjectiveModeList: async (ids: number[]) => {
    return await request.delete({ url: `/nmt/objective-mode/delete-list?ids=${ids.join(',')}` })
  },

  // 导出课程目标考核评价 Excel
  exportObjectiveMode: async (params) => {
    return await request.download({ url: `/nmt/objective-mode/export-excel`, params })
  },
}
