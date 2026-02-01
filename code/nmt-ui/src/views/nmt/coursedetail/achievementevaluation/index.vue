<template>
  <div>
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">课程评价报告</span>
        </div>
      </template>

      <el-table
          v-loading="loading"
          :data="list"
      >
        <el-table-column label="课程编码" align="center">
          <template #default>{{ courseDetail.number || '-' }}</template>
        </el-table-column>

        <el-table-column label="课程名称" align="center">
          <template #default>{{ courseDetail.name || '-' }}</template>
        </el-table-column>

        <el-table-column label="上课班级" align="center" prop="name" />

        <el-table-column label="学年" align="center">
          <template #default>
            {{ courseDetail.grade ? courseDetail.grade + '-' + (Number(courseDetail.grade) + 1) : '-' }}
          </template>
        </el-table-column>

        <el-table-column label="学期" align="center">
          <template #default>{{ courseDetail.term || '-' }}</template>
        </el-table-column>

        <el-table-column label="考核人数" align="center" prop="totalNumber" />
        <el-table-column label="负责教师" align="center" prop="teacherName" />

        <el-table-column label="操作" align="center" fixed="right" width="100">
          <template #default="scope">
            <el-button
                link
                type="primary"
                @click="handleAchievement(scope.row.id)"
            >
              查看
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <Pagination
          v-show="total > 0"
          :total="total"
          v-model:page="queryParams.pageNo"
          v-model:limit="queryParams.pageSize"
          @pagination="getList"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { TeachClassApi, TeachClass } from '@/api/nmt/teachclass'
import { CourseInfoApi } from "@/api/nmt/courseinfo"
import { getDictLabel, DICT_TYPE } from "@/utils/dict"

defineOptions({ name: 'CourseEvaluationReport' })

const { push } = useRouter()
const route = useRoute()

const loading = ref(true)
const list = ref<TeachClass[]>([])
const total = ref(0)

// 课程 ID 和 详情
const courseId = Number(route.query.id)
const courseDetail = ref<any>({})

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  courseId: courseId // 确保只查当前课程下的班级
})

/** 获取课程详情（用于填充固定列内容） */
const getDetail = async (id: number) => {
  try {
    const data = await CourseInfoApi.getCourseInfo(id)
    data.term = getDictLabel(DICT_TYPE.NMT_TERM, data.term)
    courseDetail.value = data
  } catch (error) {
    console.error('获取课程详情失败', error)
  }
}

/** 查询班级列表 */
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

/** 查看操作：跳转至成绩汇总 */
const handleAchievement = (classId: number) => {
  push(`/course/course-detail/student-achievement?classId=${classId}&courseId=${courseId}`)
}

/** 初始化 */
onMounted(async () => {
  await getDetail(courseId)
  await getList()
})
</script>

<style scoped>
.card-header {
  display: flex;
  align-items: center;
}

.card-title {
  font-weight: 600;
  font-size: 16px;
}
</style>
