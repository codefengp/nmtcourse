import request from '@/config/axios'

/** 学生成绩信息 */
export interface StudentAchievement {
          id: number; // 主键
          studentId?: number; // 学生ID
          planId?: number; // 考核计划ID
          objectiveId?: number; // 课程目标ID
          modeId?: number; // 考核方式ID
          score?: number; // 得分
  }

// 学生成绩 API
export const StudentAchievementApi = {
  // 查询学生成绩分页
  getStudentAchievementPage: async (params: any) => {
    return await request.get({ url: `/nmt/student-achievement/page`, params })
  },

  // 查询学生成绩详情
  getStudentAchievement: async (id: number) => {
    return await request.get({ url: `/nmt/student-achievement/get?id=` + id })
  },

  // 新增学生成绩
  createStudentAchievement: async (data: StudentAchievement) => {
    return await request.post({ url: `/nmt/student-achievement/create`, data })
  },

  // 修改学生成绩
  updateStudentAchievement: async (data: StudentAchievement) => {
    return await request.put({ url: `/nmt/student-achievement/update`, data })
  },

  // 删除学生成绩
  deleteStudentAchievement: async (id: number) => {
    return await request.delete({ url: `/nmt/student-achievement/delete?id=` + id })
  },

  /** 批量删除学生成绩 */
  deleteStudentAchievementList: async (ids: number[]) => {
    return await request.delete({ url: `/nmt/student-achievement/delete-list?ids=${ids.join(',')}` })
  },

  // 导出学生成绩 Excel
  exportStudentAchievement: async (params) => {
    return await request.download({ url: `/nmt/student-achievement/export-excel`, params })
  },
}
