<template>
  <div class="score-container">
    <h2 class="main-title">{{ courseDetail?.name }} 成绩汇总表</h2>

    <div class="info-bar">
      <div class="info-left">
        <span>授课年级：<b>{{ courseDetail?.grade ? courseDetail.grade + '级' : '-' }}</b></span>
        <span>授课学期：<b>{{ courseDetail?.term || '-' }}</b></span>
        <span>上课班级：<b class="blue-text">{{ classDetail?.name || '-' }}</b></span>
        <span>负责教师：<b>{{ classDetail?.teacherName || '-' }}</b></span>
      </div>
      <div class="info-right">
        学生人数：<b class="green-text">{{ classDetail?.totalNumber || 0 }}</b> 人
      </div>
    </div>

    <el-table
      ref="scoreTable"
      v-loading="loading"
      :data="tableData"
      border
      style="width: 100%"
      header-cell-class-name="common-header"
      :header-cell-style="handleHeaderStyle"
    >
      <el-table-column label="考核方式" align="center">
        <el-table-column label="考核内容" align="center">
          <el-table-column label="课程目标" align="center">
            <el-table-column label="总分值" align="center">
              <el-table-column prop="number" label="学号" width="120" align="center" />
              <el-table-column prop="name" label="姓名" width="100" align="center" />
            </el-table-column>
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
            <el-table-column :label="String(plan.score)" align="center">
              <el-table-column label="" min-width="70" align="center">
                <template #default="{ row }">
                  <span class="score-num">{{ row['plan_' + plan.id] }}</span>
                </template>
              </el-table-column>
            </el-table-column>
          </el-table-column>
        </el-table-column>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { StudentAchievementApi } from '@/api/nmt/studentachievement'
import { EvaluatePlanApi } from '@/api/nmt/evaluateplan'
import { ClassStudentApi } from '@/api/nmt/classstudent'
import { CourseInfoApi } from '@/api/nmt/courseinfo'
import { TeachClassApi } from '@/api/nmt/teachclass'
import { getDictLabel, DICT_TYPE } from "@/utils/dict"

const scoreTable = ref()
const loading = ref(false)
const route = useRoute()
const classId = Number(route.query.classId)
const courseId = Number(route.query.courseId)

const courseDetail = ref<any>({})
const classDetail = ref<any>({})
const rawPlans = ref<any[]>([])
const tableData = ref<any[]>([])

// 样式控制：总分值数字仅改字体颜色，背景恢复灰色
const handleHeaderStyle = ({ rowIndex, columnIndex }: any) => {
  const baseStyle = { backgroundColor: '#f5f7fa', color: '#333' }
  // 第四行 (rowIndex 3) 且非首列（即分值数字格）
  if (rowIndex === 3 && columnIndex > 0) {
    return { ...baseStyle, color: '#409eff', fontWeight: 'bold' }
  }
  return baseStyle
}

const groupedPlans = computed(() => {
  const groups: any[] = []
  rawPlans.value.forEach(p => {
    let g = groups.find(x => x.modeName === p.modeName)
    if (!g) { groups.push({ modeName: p.modeName || '未知', children: [p] }) }
    else { g.children.push(p) }
  })
  return groups
})

const applyMerge = () => {
  const rows = scoreTable.value?.$el.querySelectorAll('.el-table__header tr')
  if (!rows || rows.length < 5) return
  const ths = Array.from(rows[4].querySelectorAll('th')) as HTMLElement[]
  if (ths.length > 1) {
    ths[1].setAttribute('colspan', (ths.length - 1).toString())
    ths[1].innerHTML = `
      <div class="merge-wrapper">
        <div class="name-box">姓名</div>
        <div class="remark-box">说明:本表格为在线操作表格，您可直接将学生学号、姓名对应考核内容的成绩复制进表格后点击右上角保存即可!</div>
      </div>`
    ths.slice(2).forEach(th => th.style.display = 'none')
  }
}

const init = async () => {
  loading.value = true
  try {
    const [c, t, plans, students, achs] = await Promise.all([
      CourseInfoApi.getCourseInfo(courseId),
      TeachClassApi.getTeachClass(classId),
      EvaluatePlanApi.listEvaluatePlan(courseId),
      ClassStudentApi.listClassStudent(classId),
      StudentAchievementApi.listStudentAchievement(classId)
    ])
    courseDetail.value = { ...c, term: getDictLabel(DICT_TYPE.NMT_TERM, c.term) }
    classDetail.value = t
    rawPlans.value = plans
    tableData.value = students.map((s: any) => {
      const row: any = { id: s.id, name: s.name, number: s.number }
      plans.forEach((p: any) => {
        const a = achs.find((x: any) => x.studentId === s.id && x.planId === p.id)
        row[`plan_${p.id}`] = a ? a.score : ''
      })
      return row
    })
    nextTick(applyMerge)
  } finally {
    loading.value = false
  }
}

onMounted(init)
</script>

<style scoped>
.score-container { padding: 20px; background: #fff; }
.main-title { text-align: center; margin-bottom: 20px; font-size: 24px; color: #333; }
.info-bar { display: flex; justify-content: space-between; padding-bottom: 15px; border-bottom: 1px solid #eee; margin-bottom: 15px; }
.info-left { display: flex; gap: 30px; font-size: 15px; }
.blue-text { color: #409eff; }
.green-text { color: #67c23a; font-size: 20px; }

:deep(.common-header) { font-weight: bold !important; }

/* 合并说明行样式 */
:deep(.merge-wrapper) { display: flex; align-items: center; height: 44px; width: 100%; }
:deep(.name-box) {
  width: 100px; border-right: 1px solid #ebeef5; height: 100%;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}
:deep(.remark-box) {
  flex: 1; text-align: center; color: rgba(0, 0, 0, 0.98);
  font-weight: normal; /* 取消加粗 */
  font-size: 14px;
}

:deep(.el-table__header tr:nth-child(5) th) { padding: 0 !important; }
.score-num { font-weight: 500; color: #333; }
</style>
