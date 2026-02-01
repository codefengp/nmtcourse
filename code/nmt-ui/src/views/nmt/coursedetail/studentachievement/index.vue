<template>
  <div class="score-container">
    <div class="operation-wrapper">
      <el-button type="primary" @click="handleImport" v-hasPermi="['nmt:student-achievement:create']">
        <el-icon><Upload /></el-icon><span class="ml-5">导入成绩</span>
      </el-button>
      <el-button type="success" @click="handleExport" :loading="exportLoading">
        <el-icon v-if="!exportLoading"><Download /></el-icon><span class="ml-5">导出成绩</span>
      </el-button>
    </div>

    <h2 class="main-title">{{ courseDetail?.name }} 成绩汇总表</h2>

    <div class="info-bar">
      <div class="info-left">
        <span>授课年级：<b>{{ courseDetail?.grade ? courseDetail.grade + '级' : '-' }}</b></span>
        <span>授课学期：<b>{{ courseDetail?.term || '-' }}</b></span>
        <span>上课班级：<b>{{ classDetail?.name || '-' }}</b></span>
        <span>负责教师：<b>{{ classDetail?.teacherName || '-' }}</b></span>
        <el-divider direction="vertical" />
        <el-input v-model="queryParams.number" placeholder="学号查询" clearable class="search-input" prefix-icon="Search" />
        <el-input v-model="queryParams.name" placeholder="姓名查询" clearable class="search-input" prefix-icon="Search" />
      </div>
      <div class="info-right">学生人数：<b class="green-text">{{ classDetail?.totalNumber || 0 }}</b> 人</div>
    </div>

    <el-table
        ref="scoreTable"
        v-loading="loading"
        :data="filteredTableData"
        border
        class="dynamic-table"
        height="calc(100vh - 310px)"
        header-cell-class-name="common-header"
        :header-cell-style="handleHeaderStyle"
    >
      <el-table-column label="考核方式" align="center">
        <el-table-column label="考核内容" align="center">
          <el-table-column label="课程目标" align="center">
            <el-table-column label="总分值" align="center">
              <el-table-column prop="number" label="学号" align="center" min-width="90" />
              <el-table-column prop="name" label="姓名" align="center" min-width="70" />
            </el-table-column>
          </el-table-column>
        </el-table-column>
      </el-table-column>

      <el-table-column v-for="group in groupedPlans" :key="group.modeName" :label="group.modeName" align="center">
        <el-table-column v-for="plan in group.children" :key="plan.id" :label="String(plan.content || '')" align="center">
          <el-table-column :label="plan.objectiveName" align="center">
            <el-table-column :label="String(plan.score)" align="center">
              <el-table-column align="center" min-width="50">
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

  <ImportDialog ref="importFormRef" title="成绩导入" :validate-url="validateUrl" :submit-api="StudentAchievementApi.importExcelData" :template-api="StudentAchievementApi.downloadTemplate" :template-file-name="courseDetail?.name + '-' + (classDetail?.name || '-') + '-成绩导入模板.xlsx'" :fail-export-api="StudentAchievementApi.outFail" :fail-export-file-name="(courseDetail?.name || '') + '-' + (classDetail?.name || '-') + '-成绩导入错误信息.xlsx'" @success="init" />
</template>

<script setup lang="ts">
import { ref, onMounted, computed, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { Upload, Download } from '@element-plus/icons-vue'
import { StudentAchievementApi } from '@/api/nmt/studentachievement'
import { EvaluatePlanApi } from '@/api/nmt/evaluateplan'
import { ClassStudentApi } from '@/api/nmt/classstudent'
import { CourseInfoApi } from '@/api/nmt/courseinfo'
import { TeachClassApi } from '@/api/nmt/teachclass'
import { getDictLabel, DICT_TYPE } from "@/utils/dict"
import { useExcelExport } from "@/components/ExcelHandle/export"

const { exportLoading, exportExcel } = useExcelExport()
const validateUrl = import.meta.env.VITE_BASE_URL + import.meta.env.VITE_API_URL + '/nmt/student-achievement/validate-import'
const scoreTable = ref(), loading = ref(false), route = useRoute()
const classId = Number(route.query.classId), courseId = Number(route.query.courseId)
const courseDetail = ref<any>({}), classDetail = ref<any>({}), rawPlans = ref<any[]>([]), tableData = ref<any[]>([])
const queryParams = ref({ name: '', number: '' })

// 过滤列表逻辑
const filteredTableData = computed(() => {
  const { name, number } = queryParams.value
  return tableData.value.filter(item =>
      (!name || item.name?.includes(name)) && (!number || item.number?.includes(number))
  )
})

// 表头第四行蓝色加粗提示
const handleHeaderStyle = ({ rowIndex, columnIndex }: any) => {
  const style: any = { backgroundColor: '#f5f7fa', color: '#333' }
  if (rowIndex === 3 && columnIndex > 0) {
    Object.assign(style, { color: '#409eff', fontWeight: 'bold' })
  }
  return style
}

// 多级表头分组逻辑
const groupedPlans = computed(() => {
  const groups: any[] = []
  rawPlans.value.forEach(p => {
    let g = groups.find(x => x.modeName === p.modeName)
    if (!g) groups.push({ modeName: p.modeName || '未知', children: [p] })
    else g.children.push(p)
  })
  return groups
})

// 动态合并第五行表头并插入说明文字
const applyMerge = () => {
  const tableEl = scoreTable.value?.$el
  if (!tableEl) return
  const fifthRow = tableEl.querySelectorAll('.el-table__header tr')[4]
  if (!fifthRow) return
  const ths = Array.from(fifthRow.querySelectorAll('th')) as HTMLElement[]
  if (ths.length > 1) {
    ths[1].setAttribute('colspan', '100')
    ths[1].innerHTML = `
      <div class="merge-wrapper">
        <div class="name-box">姓名</div>
        <div class="remark-box">说明:点击左上方 导入成绩 按钮，下载模板后，填入学生成绩，若有错误数据，请根据提示信息修改，最后导入成绩数据！</div>
      </div>`
    ths.slice(2).forEach(th => th.style.display = 'none')
  }
}

// 初始化加载数据
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
    nextTick(() => { setTimeout(applyMerge, 200) })
  } finally { loading.value = false }
}

const importFormRef = ref()
const handleImport = () => { importFormRef.value.open({ classId, courseId }) }
const handleExport = () => exportExcel(StudentAchievementApi.exportExcelData, { classId, courseId }, `${courseDetail.value?.name}-成绩汇总表.xlsx`)

onMounted(init)
</script>

<style scoped>
/* 1. 核心修复：强制固定布局，防止撑开横向滚动条 */
.dynamic-table {
  width: 100% !important;
}
:deep(.el-table__header), :deep(.el-table__body) {
  table-layout: fixed !important; /* 核心：强制列宽动态向内挤压 */
  width: 100% !important;
}

/* 2. 垂直滚动修复：对应 F12 勾掉高度的效果 */
:deep(.el-table__inner-wrapper) {
  height: 100% !important;
}
:deep(.el-scrollbar__wrap) {
  overflow: hidden auto !important; /* 禁横向，开纵向 */
}

/* 3. 合并表头及说明样式 */
:deep(.merge-wrapper) { display: flex; align-items: center; height: 44px; }
:deep(.name-box) {
  width: 100px; border-right: 1px solid #ebeef5; height: 100%;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0; background-color: #f5f7fa; font-weight: bold;
}
:deep(.remark-box) {
  flex: 1; padding: 0 15px; color: #666; font-weight: normal !important;
  font-size: 15px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;
}

/* 4. 基础 UI 样式 */
.score-container { padding: 20px; background: #fff; position: relative; }
.operation-wrapper { position: absolute; top: 20px; right: 20px; display: flex; gap: 12px; z-index: 20; }
.main-title { text-align: center; margin-bottom: 20px; font-size: 24px; color: #333; }
.info-bar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; padding-bottom: 10px; border-bottom: 1px solid #eee; }
.info-left { display: flex; align-items: center; gap: 15px; }
.search-input { width: 140px; }
.green-text { color: #67c23a; font-size: 20px; }
:deep(.common-header) { font-weight: bold !important; }
</style>
