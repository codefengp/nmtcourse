<template>
  <div style="padding: 20px; background: #fff; color: #333;">
    <h2 style="text-align: center; margin: 0 0 20px 0; font-size: 24px; letter-spacing: 1px;">
      {{ courseDetail?.name }} 成绩汇总表
    </h2>

    <div class="info-wrapper">
      <div class="info-left">
        <span>授课年级：<b>{{ courseDetail?.grade ? courseDetail.grade + '级' : '-' }}</b></span>
        <span>授课学期：<b>{{ courseDetail?.term || '-' }}</b></span>
        <span>上课班级：<b style="color: #409eff;">{{ classDetail?.name || '-' }}</b></span>
        <span>负责教师：<b>{{ classDetail?.teacherName || '-' }}</b></span>
      </div>
      <div class="info-right">
        学生人数：<b style="color: #67c23a; font-size: 20px;">{{ classDetail?.totalNumber || 0 }}</b> 人
      </div>
    </div>

    <el-table
        v-loading="loading"
        :data="tableData"
        border
        stripe
        style="width: 100%"
        header-cell-class-name="table-header"
    >
      <el-table-column label="考核方式" align="center">
        <el-table-column label="考核内容" align="center">
          <el-table-column label="课程目标" align="center">
            <el-table-column prop="number" label="学号" min-width="120" align="center" />
            <el-table-column prop="name" label="姓名" min-width="90" align="center" />
          </el-table-column>
        </el-table-column>
      </el-table-column>

      <el-table-column
          v-for="group in groupedPlans"
          :key="group.modeName"
          :label="group.modeName"
          align="center"
      >
        <el-table-column
            v-for="plan in group.children"
            :key="plan.id"
            :label="String(plan.content || '')"
            align="center"
        >
          <el-table-column :label="plan.objectiveName" align="center">
            <el-table-column align="center" min-width="70">
              <template #header>
                <div style="line-height: 1.2;">
                  <div style="font-size: 11px; color: #999; font-weight: normal;">满分</div>
                  <div style="color: #409eff; font-weight: bold;">{{ plan.score }}</div>
                </div>
              </template>
              <template #default="scope">
                <span style="font-weight: 500;">{{ scope.row['plan_' + plan.id] }}</span>
              </template>
            </el-table-column>
          </el-table-column>
        </el-table-column>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { StudentAchievementApi, StudentAchievement } from '@/api/nmt/studentachievement'
import { EvaluatePlanApi, RowVO } from '@/api/nmt/evaluateplan'
import { ClassStudentApi, ClassStudent } from '@/api/nmt/classstudent'
import { CourseInfoApi, CourseInfo } from '@/api/nmt/courseinfo'
import { TeachClassApi, TeachClass } from '@/api/nmt/teachclass'
import { getDictLabel, DICT_TYPE } from "@/utils/dict"

const loading = ref(false)
const route = useRoute()
const classId = Number(route.query.classId)
const courseId = Number(route.query.courseId)

const courseDetail = ref<Partial<CourseInfo>>({})
const classDetail = ref<Partial<TeachClass>>({})
const rawPlans = ref<RowVO[]>([])
const tableData = ref<any[]>([])

const groupedPlans = computed(() => {
  const groups: { modeName: string; children: RowVO[] }[] = []
  rawPlans.value.forEach(plan => {
    let group = groups.find(g => g.modeName === plan.modeName)
    if (!group) {
      group = { modeName: plan.modeName || '未知', children: [] }
      groups.push(group)
    }
    group.children.push(plan)
  })
  return groups
})

const initData = async () => {
  try {
    const [c, t] = await Promise.all([
      CourseInfoApi.getCourseInfo(courseId),
      TeachClassApi.getTeachClass(classId)
    ])
    courseDetail.value = { name: c.name, grade: c.grade, term: getDictLabel(DICT_TYPE.NMT_TERM, c.term) }
    classDetail.value = { name: t.name, totalNumber: t.totalNumber, teacherName: t.teacherName }
  } catch (e) { console.error(e) }
}

async function getList() {
  loading.value = true
  try {
    const [plans, students, achievements] = await Promise.all([
      EvaluatePlanApi.listEvaluatePlan(courseId),
      ClassStudentApi.listClassStudent(classId),
      StudentAchievementApi.listStudentAchievement(classId)
    ])
    rawPlans.value = plans as RowVO[]
    tableData.value = students.map((stu: ClassStudent) => {
      const row: any = { id: stu.id, name: stu.name, number: stu.number }
      rawPlans.value.forEach(p => {
        const match = (achievements as StudentAchievement[]).find(a => a.studentId === stu.id && a.planId === p.id)
        row[`plan_${p.id}`] = match ? match.score : ''
      })
      return row
    })
  } finally { loading.value = false }
}

onMounted(() => { initData(); getList(); })
</script>

<style scoped>
/* 容器去掉装饰，仅保留底部分割线 */
.info-wrapper {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0 20px 0;
  border-bottom: 1px solid #ebeef5; /* 使用 Element 默认的浅灰色细线 */
  margin-bottom: 20px;
  font-size: 16px;
}

.info-left {
  display: flex;
  gap: 30px;
}

.info-left span {
  color: #606266;
}

.info-left b {
  color: #303133;
}

/* 表头样式微调 */
:deep(.table-header) {
  background-color: #f5f7fa !important;
  color: #333 !important;
  font-weight: bold !important;
}

/* 单元格内容自动挤压 */
:deep(.el-table .cell) {
  padding: 0 4px !important;
}
</style>
