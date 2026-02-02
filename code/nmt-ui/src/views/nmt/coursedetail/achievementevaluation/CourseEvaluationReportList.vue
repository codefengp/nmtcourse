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
          <!-- 显示课程编码，如果为空显示 '-' -->
          <template #default>{{ courseDetail.number || '-' }}</template>
        </el-table-column>

        <el-table-column label="课程名称" align="center">
          <!-- 显示课程名称，如果为空显示 '-' -->
          <template #default>{{ courseDetail.name || '-' }}</template>
        </el-table-column>

        <el-table-column label="上课班级" align="center" prop="name" />

        <el-table-column label="学年" align="center">
          <!-- 显示学年，例如 2022-2023 -->
          <template #default>
            {{ courseDetail.grade ? courseDetail.grade + '-' + (Number(courseDetail.grade) + 1) : '-' }}
          </template>
        </el-table-column>

        <el-table-column label="学期" align="center">
          <!-- 显示学期，如果为空显示 '-' -->
          <template #default>{{ courseDetail.term || '-' }}</template>
        </el-table-column>

        <el-table-column label="考核人数" align="center" prop="totalNumber" />
        <el-table-column label="负责教师" align="center" prop="teacherName" />

        <el-table-column label="操作" align="center" fixed="right" width="100">
          <!-- 查看按钮 -->
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

      <!-- 分页组件 -->
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
import { useRoute } from 'vue-router'
import { TeachClassApi, TeachClass } from '@/api/nmt/teachclass'
import { CourseInfoApi } from "@/api/nmt/courseinfo"
import { getDictLabel, DICT_TYPE } from "@/utils/dict"

defineOptions({ name: 'CourseEvaluationReportList' })

const emit = defineEmits(['view']) // 点击查看事件
const route = useRoute()

const loading = ref(true)          // 表格加载状态
const list = ref<TeachClass[]>([]) // 班级列表
const total = ref(0)               // 列表总条数

// 课程ID和详情
const courseId = Number(route.query.id)
const courseDetail = ref<any>({}) // 当前课程信息

// 查询参数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  courseId // 确保只查当前课程下的班级
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

/** 查看操作：触发父组件事件，不再跳转路由 */
const handleAchievement = (classId: number) => {
  emit('view', {
    classId,
    courseId
  })
}

/** 初始化 */
onMounted(async () => {
  await getDetail(courseId)
  await getList()
})

// 暴露给父组件调用，用于返回时刷新列表
defineExpose({ getList })
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
