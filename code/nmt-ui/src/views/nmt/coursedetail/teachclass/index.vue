<template>
  <el-row :gutter="20">
    <!-- 左侧：课程基本信息，占1份 -->
    <el-col :span="5">
      <el-card class="course-card">
        <template #header>
          <span class="card-title">课程基本信息</span>
        </template>

        <div class="info-item" v-for="(label, key) in labels" :key="key">
          <span class="label">{{ label }}：</span>
          <span class="value">{{ courseDetail[key] || '—' }}</span>
        </div>
      </el-card>
    </el-col>

    <!-- 右侧：表格 + 新增按钮，占9份 -->
    <el-col :span="19">
      <el-card class="table-card">
        <template #header>
          <span class="card-title">教学班级信息</span>
        </template>
        <div class="table-header">
          <el-button
            type="primary"
            @click="openForm('create')"
            v-hasPermi="['nmt:teach-class:create']"
          >
          新增班级
          </el-button>
        </div>

        <el-table
          row-key="id"
          v-loading="loading"
          :data="list"
          :stripe="true"
          :show-overflow-tooltip="true"
        >
          <el-table-column label="班级编号" align="center" prop="number" />
          <el-table-column label="班级名称" align="center" prop="name" />
          <el-table-column label="班级人数" align="center" prop="totalNumber" />
          <el-table-column label="负责教师" align="center" prop="teacherName" />
          <el-table-column label="操作" align="center" min-width="120px">
            <template #default="scope">
              <el-button
                link
                type="primary"
                @click="handleStudent(scope.row.id)"
                v-hasPermi="['nmt:class-student:query']"
              >
                学生管理
              </el-button>
              <el-button
                link
                type="primary"
                @click="handleAchievement(scope.row.id)"
                v-hasPermi="['nmt:class-student:query']"
              >
                成绩管理
              </el-button>
              <el-button
                link
                type="primary"
                @click="openForm('update', scope.row.id)"
                v-hasPermi="['nmt:teach-class:update']"
              >
                编辑
              </el-button>
              <el-button
                link
                type="danger"
                @click="handleDelete(scope.row.id)"
                v-hasPermi="['nmt:teach-class:delete']"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <Pagination
          :total="total"
          v-model:page="queryParams.pageNo"
          v-model:limit="queryParams.pageSize"
          @pagination="getList"
        />
      </el-card>
    </el-col>
  </el-row>

  <!-- 表单弹窗：添加/修改 -->
  <TeachClassForm ref="formRef" @success="getList" />
</template>

<script setup lang="ts">
import { TeachClassApi, TeachClass } from '@/api/nmt/teachclass'
import TeachClassForm from './TeachClassForm.vue'
import { CourseInfoApi } from "@/api/nmt/courseinfo";
import { getDictLabel } from "@/utils/dict";

defineOptions({ name: 'TeachClass' })

const message = useMessage()
const { t } = useI18n()
const { push } = useRouter()

const loading = ref(true)
const list = ref<TeachClass[]>([])
const total = ref(0)
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  number: undefined,
  name: undefined,
  totalNumber: undefined,
  teacherName: undefined,
})
const formRef = ref()

// 课程id
const courseId = Number(useRoute().query.id)
const courseDetail = ref({
  number: undefined,
  name: undefined,
  majorType: undefined,
  courseType: undefined,
  courseProperty: undefined,
  courseHour: undefined,
  courseScore: undefined,
  grade: undefined,
  term: undefined,
  teacherName: undefined
})

/** 获取字段中文名 */
const labels: Record<string, string> = {
  number: '课程编码',
  name: '课程名称',
  courseType: '课程类别',
  courseProperty: '课程性质',
  courseHour: '课程学时',
  courseScore: '课程学分',
  grade: '授课年级',
  term: '授课学期',
  majorType: '适用专业',
  teacherName: '课程负责人',
}

/** 课程详情 */
const getDetail = async (id: number) => {
  const data = await CourseInfoApi.getCourseInfo(id)
  data.majorType = getDictLabel(DICT_TYPE.NMT_MAJOR_TYPE, data.majorType)
  data.courseType = getDictLabel(DICT_TYPE.NMT_COURSE_TYPE, data.courseType)
  data.courseProperty = getDictLabel(DICT_TYPE.NMT_COURSE_PROPERTY, data.courseProperty)
  data.term = getDictLabel(DICT_TYPE.NMT_TERM, data.term)
  courseDetail.value = data
}

/** 查询列表 */
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

/** 添加/修改操作 */
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, courseId,id)
}

/** 学生管理 */
const handleStudent = (id: number) => {
  // 跳转页面并设置请求参数，使用 `query` 属性
  push('/course/course-detail/class-student?id=' + id)
}

/** 成绩管理 */
const handleAchievement = (id: number) => {
  // 跳转页面并设置请求参数，使用 `query` 属性
  push('/course/course-detail/student-achievement?classId=' + id + '&courseId=' + courseId)
}

/** 删除操作 */
const handleDelete = async (id: number) => {
  try {
    await message.delConfirm()
    await TeachClassApi.deleteTeachClass(id)
    message.success(t('common.delSuccess'))
    await getList()
  } catch {}
}

/** 初始化 */
onMounted(async () => {
  await getDetail(courseId)
  await getList()
})
</script>

<style scoped>
.course-card {
  width: 100%;
}

.table-card {
  width: 100%;
}

.card-title {
  font-weight: 600;
  font-size: 16px;
}

/* 左侧信息 label 和内容同一行 */
.info-item {
  display: flex;
  gap: 4px; /* label 和 value 之间的间距，可调 */
  padding: 6px 0;
  align-items: center; /* 垂直居中对齐 */
}

.info-item .label {
  font-weight: 500;
  color: #606266;
}

.info-item .value {
  font-weight: 400;
  color: #333;
}

/* 表格上方按钮右上角 */
.table-header {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 12px;
}
</style>
