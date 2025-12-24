import request from '@/config/axios'

/** 课程目标信息 */
export interface CourseObjective {
          id: number; // 主键ID
          name?: string; // 名称
          content: string; // 内容
          expectValue: number; // 期望值
          courseId?: number; // 课程ID
  }

// 课程目标 API
export const CourseObjectiveApi = {
  // 查询课程目标分页
  getCourseObjectivePage: async (params: any) => {
    return await request.get({ url: `/nmt/course-objective/page`, params })
  },

  // 查询课程目标详情
  getCourseObjective: async (id: number) => {
    return await request.get({ url: `/nmt/course-objective/get?id=` + id })
  },

  // 新增课程目标
  createCourseObjective: async (data: CourseObjective) => {
    return await request.post({ url: `/nmt/course-objective/create`, data })
  },

  // 修改课程目标
  updateCourseObjective: async (data: CourseObjective) => {
    return await request.put({ url: `/nmt/course-objective/update`, data })
  },

  // 删除课程目标
  deleteCourseObjective: async (id: number) => {
    return await request.delete({ url: `/nmt/course-objective/delete?id=` + id })
  },

  /** 批量删除课程目标 */
  deleteCourseObjectiveList: async (ids: number[]) => {
    return await request.delete({ url: `/nmt/course-objective/delete-list?ids=${ids.join(',')}` })
  },

  // 导出课程目标 Excel
  exportCourseObjective: async (params) => {
    return await request.download({ url: `/nmt/course-objective/export-excel`, params })
  },
}
