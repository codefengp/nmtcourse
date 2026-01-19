import request from '@/config/axios'

/** 班级学生信息 */
export interface ClassStudent {
          id: number; // 主键
          name?: string; // 学生姓名
          number?: string; // 学号
          classId?: number; // 所属班级ID
          className?: string; // 所属班级
  }

// 班级学生 API
export const ClassStudentApi = {
  // 查询班级学生分页
  getClassStudentPage: async (params: any) => {
    return await request.get({ url: `/nmt/class-student/page`, params })
  },

  // 查询班级学生详情
  getClassStudent: async (id: number) => {
    return await request.get({ url: `/nmt/class-student/get?id=` + id })
  },

  // 新增班级学生
  createClassStudent: async (data: ClassStudent) => {
    return await request.post({ url: `/nmt/class-student/create`, data })
  },

  // 修改班级学生
  updateClassStudent: async (data: ClassStudent) => {
    return await request.put({ url: `/nmt/class-student/update`, data })
  },

  // 删除班级学生
  deleteClassStudent: async (id: number) => {
    return await request.delete({ url: `/nmt/class-student/delete?id=` + id })
  },

  /** 批量删除班级学生 */
  deleteClassStudentList: async (ids: number[]) => {
    return await request.delete({ url: `/nmt/class-student/delete-list?ids=${ids.join(',')}` })
  },

  // 导出班级学生 Excel
  exportClassStudent: async (params) => {
    return await request.download({ url: `/nmt/class-student/export-excel`, params })
  },

  // 下载导入模板
  importTemplate : async () => {
      return await request.download({ url: '/nmt/class-student/get-import-template' })
  },

  // 导出错误
  outFail : async (data: string) => {
      return await request.downloadFile({ url: `/nmt/class-student/out-fail`, data })
  },

  // 导入数据
  importData : async (data) => {
      return await request.post({ url: `/nmt/class-student/import`, data })
  }
}
