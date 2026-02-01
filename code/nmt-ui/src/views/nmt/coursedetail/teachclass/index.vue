<template>
  <div class="app-container">
    <div class="header-info">
      <div class="title-row">
        <span class="c-name">{{ courseDetail.name }}</span>
        <span class="course-tag">{{ courseDetail.number }}</span>
      </div>
      <div class="meta-row">
        <span v-for="(label, key) in labels" :key="key" class="meta-item">
          <span class="m-label">{{ label }}：</span>
          <span class="m-value">{{ courseDetail[key] || '—' }}</span>
        </span>
      </div>
    </div>

    <hr class="divider" />

    <div class="table-section">
      <div class="table-tool" style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px;">
        <span class="table-title">教学班级管理</span>
        <el-button type="primary" @click="openForm('create')">新增班级</el-button>
      </div>

      <el-table
          v-loading="loading"
          :data="list"
      >
        <el-table-column label="班级编号" prop="number" width="120" align="center" />
        <el-table-column label="班级名称" prop="name" min-width="150" show-overflow-tooltip />
        <el-table-column label="人数" prop="totalNumber" width="100" align="center" />
        <el-table-column label="负责教师" prop="teacherName" width="120" align="center" />
        <el-table-column label="操作" width="280" align="center" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="handleStudent(scope.row.id)">学生管理</el-button>
            <el-button link type="primary" @click="handleAchievement(scope.row.id)">成绩管理</el-button>
            <el-button link type="primary" @click="openForm('update', scope.row.id)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-footer">
        <Pagination
            :total="total"
            v-model:page="queryParams.pageNo"
            v-model:limit="queryParams.pageSize"
            @pagination="getList"
        />
      </div>
    </div>

    <TeachClassForm ref="formRef" @success="getList" />
  </div>
</template>

<script setup lang="ts">
/* 脚本部分完全没动 */
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { TeachClassApi } from '@/api/nmt/teachclass'
import TeachClassForm from './TeachClassForm.vue'
import { CourseInfoApi } from "@/api/nmt/courseinfo"
import { getDictLabel, DICT_TYPE } from "@/utils/dict"

const { push } = useRouter()
const route = useRoute()
const message = useMessage()
const { t } = useI18n()

const loading = ref(true)
const list = ref([])
const total = ref(0)
const formRef = ref()
const courseId = Number(route.query.id)
const courseDetail = ref<any>({})
const queryParams = reactive({ pageNo: 1, pageSize: 10, courseId })

const labels = {
  courseProperty: '性质',
  courseHour: '学时',
  courseScore: '学分',
  grade: '年级',
  term: '学期',
  teacherName: '负责人'
}

const getDetail = async (id: number) => {
  const data = await CourseInfoApi.getCourseInfo(id)
  data.courseProperty = getDictLabel(DICT_TYPE.NMT_COURSE_PROPERTY, data.courseProperty)
  data.term = getDictLabel(DICT_TYPE.NMT_TERM, data.term)
  courseDetail.value = data
}

const getList = async () => {
  loading.value = true
  try {
    const data = await TeachClassApi.getTeachClassPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

const openForm = (type, id) => formRef.value.open(type, courseId, id)
const handleStudent = (id) => push(`/course/course-detail/class-student?id=${id}`)
const handleAchievement = (id) => push(`/course/course-detail/student-achievement?classId=${id}&courseId=${courseId}`)

const handleDelete = async (id: number) => {
  try {
    await message.delConfirm()
    await TeachClassApi.deleteTeachClass(id)
    message.success(t('common.delSuccess'))
    getList()
  } catch {}
}

onMounted(() => { getDetail(courseId); getList() })
</script>

<style scoped>
/* 样式部分完全保留你提供的原始代码 */
.app-container { padding: 20px; background: #fff; }
.title-row { margin-bottom: 15px; display: flex; align-items: center; gap: 12px; }
.c-name { font-size: 24px; font-weight: bold; color: #333; }
.course-tag {
  font-size: 14px;
  padding: 2px 10px;
  background-color: #ecf5ff;
  color: #409eff;
  border: 1px solid #d9ecff;
  border-radius: 4px;
  font-weight: 500;
}
.meta-row { display: flex; flex-wrap: wrap; gap: 15px 40px; }
.meta-item { font-size: 16px; }
.m-label { color: #888; }
.m-value { color: #333; font-weight: 500; }
.divider { border: 0; border-top: 1px solid #eee; margin: 20px 0; }
.table-title { font-size: 18px; font-weight: bold; border-left: 4px solid #409eff; padding-left: 10px; }
.pagination-footer {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
