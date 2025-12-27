import request from '@/config/axios'

/** 课程信息信息 */
export interface CourseInfo {
          id: number; // 主键ID
          number?: string; // 编号
          name?: string; // 名称
          majorType?: number; // 所属专业
          courseType?: number; // 课程类型
          courseProperty: undefined; // 课程属性
          courseHour?: number; // 总学时
          courseScore?: number; // 总学分
          grade?: number; // 授课年级
          term?: number; // 授课学期
          teacherName: string; // 授课教师
  }

// 课程信息 API
export const CourseInfoApi = {
  // 查询课程信息分页
  getCourseInfoPage: async (params: any) => {
    return await request.get({ url: `/nmt/course-info/page`, params })
  },

  // 查询课程信息详情
  getCourseInfo: async (id: number) => {
    return await request.get({ url: `/nmt/course-info/get?id=` + id })
  },

  // 新增课程信息
  createCourseInfo: async (data: CourseInfo) => {
    return await request.post({ url: `/nmt/course-info/create`, data })
  },

  // 修改课程信息
  updateCourseInfo: async (data: CourseInfo) => {
    return await request.put({ url: `/nmt/course-info/update`, data })
  },

  // 删除课程信息
  deleteCourseInfo: async (id: number) => {
    return await request.delete({ url: `/nmt/course-info/delete?id=` + id })
  },

  /** 批量删除课程信息 */
  deleteCourseInfoList: async (ids: number[]) => {
    return await request.delete({ url: `/nmt/course-info/delete-list?ids=${ids.join(',')}` })
  },

  // 导出课程信息 Excel
  exportCourseInfo: async (params) => {
    return await request.download({ url: `/nmt/course-info/export-excel`, params })
  },
}
