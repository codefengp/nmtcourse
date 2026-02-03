import request from '@/config/axios'

/** 达成度评价信息 */
export interface AchievementEvaluation {
          id: number; // 主键
          courseId?: number; // 课程ID
          classId?: number; // 教学班级ID
          overallComment: string; // 学生总体评价
          problemAnalysis: string; // 存在问题
          improvementPlan: string; // 课程改进
  }

// 达成度评价 API
export const AchievementEvaluationApi = {
  // 查询达成度评价分页
  getAchievementEvaluationPage: async (params: any) => {
    return await request.get({ url: `/nmt/achievement-evaluation/page`, params })
  },

  // 查询达成度评价详情
  getAchievementEvaluation: async (id: number) => {
    return await request.get({ url: `/nmt/achievement-evaluation/get?id=` + id })
  },

  // 新增达成度评价
  createAchievementEvaluation: async (data: AchievementEvaluation) => {
    return await request.post({ url: `/nmt/achievement-evaluation/create`, data })
  },

  // 修改达成度评价
  updateAchievementEvaluation: async (data: AchievementEvaluation) => {
    return await request.put({ url: `/nmt/achievement-evaluation/update`, data })
  },

  // 删除达成度评价
  deleteAchievementEvaluation: async (id: number) => {
    return await request.delete({ url: `/nmt/achievement-evaluation/delete?id=` + id })
  },

  /** 批量删除达成度评价 */
  deleteAchievementEvaluationList: async (ids: number[]) => {
    return await request.delete({ url: `/nmt/achievement-evaluation/delete-list?ids=${ids.join(',')}` })
  },

  // 导出达成度评价 Excel
  exportAchievementEvaluation: async (params) => {
    return await request.download({ url: `/nmt/achievement-evaluation/export-excel`, params })
  },

  // 查询课程总评分数详情
  getCourseOverallScore: async (courseId: number,classId:number) => {
    return await request.get({ url: `/nmt/achievement-evaluation/get-overall-score?courseId=` + courseId + `&classId=` + classId })
  },

  // 查询课程目标达成评价结果
  getObjectiveAchievementEvaluation: async (courseId: number,classId:number) => {
    return await request.get({ url: `/nmt/achievement-evaluation/get-obj-ache-eval?courseId=` + courseId + `&classId=` + classId })
  },
}
