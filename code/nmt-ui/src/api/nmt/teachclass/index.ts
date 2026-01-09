import request from '@/config/axios'
import type { Dayjs } from 'dayjs';

/** 教学班级信息 */
export interface TeachClass {
          id: number; // 主键
          number?: string; // 班级编号
          name?: string; // 班级名称
          courseId?: number; // 课程ID
          totalNumber: number; // 班级人数
          teacherName: string; // 负责教师
  }

// 教学班级 API
export const TeachClassApi = {
  // 查询教学班级分页
  getTeachClassPage: async (params: any) => {
    return await request.get({ url: `/nmt/teach-class/page`, params })
  },

  // 查询教学班级详情
  getTeachClass: async (id: number) => {
    return await request.get({ url: `/nmt/teach-class/get?id=` + id })
  },

  // 新增教学班级
  createTeachClass: async (data: TeachClass) => {
    return await request.post({ url: `/nmt/teach-class/create`, data })
  },

  // 修改教学班级
  updateTeachClass: async (data: TeachClass) => {
    return await request.put({ url: `/nmt/teach-class/update`, data })
  },

  // 删除教学班级
  deleteTeachClass: async (id: number) => {
    return await request.delete({ url: `/nmt/teach-class/delete?id=` + id })
  },

  /** 批量删除教学班级 */
  deleteTeachClassList: async (ids: number[]) => {
    return await request.delete({ url: `/nmt/teach-class/delete-list?ids=${ids.join(',')}` })
  },

  // 导出教学班级 Excel
  exportTeachClass: async (params) => {
    return await request.download({ url: `/nmt/teach-class/export-excel`, params })
  },
}