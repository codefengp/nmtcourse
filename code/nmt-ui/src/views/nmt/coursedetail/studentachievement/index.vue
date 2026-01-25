<template>
  <el-table
      :data="tableData"
      border
      style="width: 100%"
      v-loading="loading"
      :span-method="spanMethod"
  >
    <el-table-column
        v-for="(col, index) in tableColumns"
        :key="index"
        :label="col.label"
        :prop="col.prop"
        align="center"
        width="120"
    />
  </el-table>
</template>

<script setup lang="ts">
import { StudentAchievementApi } from '@/api/nmt/studentachievement'
import { EvaluatePlanApi } from '@/api/nmt/evaluateplan'
import { ClassStudentApi } from '@/api/nmt/classstudent'

const loading = ref(false)
const classId = Number(useRoute().query.classId)
const courseId = Number(useRoute().query.courseId)

const planList = ref<any[]>([])
const studentList = ref<any[]>([])
const achievementList = ref<any[]>([])
const tableData = ref<any[]>([])
const tableColumns = ref<any[]>([])

/** 初始化 */
onMounted(() => {
  getList()
})

/** 获取数据 */
async function getList() {
  loading.value = true
  try {
    planList.value = await EvaluatePlanApi.listEvaluatePlan(courseId)
    studentList.value = await ClassStudentApi.listClassStudent(classId)
    achievementList.value = await StudentAchievementApi.listStudentAchievement(classId)

    buildTable()
  } finally {
    loading.value = false
  }
}

/** 构建表格列和行 */
function buildTable() {
  const columns: any[] = []

  // 前两列在前四行表头左侧合并显示
  columns.push({ label: '', prop: 'col1' })
  columns.push({ label: '', prop: 'col2' })

  // 后面每个 plan 对应一列
  planList.value.forEach(plan => {
    columns.push({
      label: `plan_${plan.id}`,
      prop: `plan_${plan.id}`
    })
  })
  tableColumns.value = columns

  const rows: any[] = []

  // 第一行：考核方式
  const row1: any = {}
  row1.col1 = '考核方式'
  row1.col2 = ''
  planList.value.forEach(plan => {
    row1[`plan_${plan.id}`] = plan.modeName
  })
  rows.push(row1)

  // 第二行：考核内容
  const row2: any = {}
  row2.col1 = '考核内容'
  row2.col2 = ''
  planList.value.forEach(plan => {
    row2[`plan_${plan.id}`] = plan.content
  })
  rows.push(row2)

  // 第三行：课程目标
  const row3: any = {}
  row3.col1 = '课程目标'
  row3.col2 = ''
  planList.value.forEach(plan => {
    row3[`plan_${plan.id}`] = `课程目标${plan.objectiveId}`
  })
  rows.push(row3)

  // 第四行：总分值
  const row4: any = {}
  row4.col1 = '总分值'
  row4.col2 = ''
  planList.value.forEach(plan => {
    row4[`plan_${plan.id}`] = plan.score
  })
  rows.push(row4)

  // 第五行：说明
  const row5: any = {}
  row5.col1 = '学号'
  row5.col2 = '姓名'
  const mergedText = '说明：本表格为在线操作表格，您可直接将学生学号、姓名对应考核内容的成绩复制进表格后点击右上角保存即可！'
  planList.value.forEach(plan => {
    row5[`plan_${plan.id}`] = mergedText
  })
  rows.push(row5)

  // 学生行
  studentList.value.forEach(student => {
    const row: any = { col1: student.number, col2: student.name }
    planList.value.forEach(plan => {
      const ach = achievementList.value.find(
          a => a.studentId === student.id && a.planId === plan.id
      )
      row[`plan_${plan.id}`] = ach ? ach.score : ''
    })
    rows.push(row)
  })

  tableData.value = rows
}

/** 合并单元格 */
function spanMethod({ row, columnIndex }: any) {
  const planCount = planList.value.length

  const rowIndex = tableData.value.indexOf(row)

  // 前四行表头：左两列合并
  if (rowIndex >= 0 && rowIndex <= 3) {
    if (columnIndex === 0) return [1, 2]
    if (columnIndex === 1) return [0, 0]
  }

  // 第五行说明：后面列全部合并
  if (rowIndex === 4) {
    if (columnIndex < 2) return [1, 1]
    return [1, planCount]
  }

  // 学生行：不合并
  return [1, 1]
}
</script>
