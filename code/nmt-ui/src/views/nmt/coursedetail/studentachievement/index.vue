<template>
  <div class="score-container">
    <div class="operation-wrapper">
      <el-button type="primary" @click="handleImport" v-hasPermi="['nmt:student-achievement:create']">
        <el-icon><Upload /></el-icon><span class="ml-5">导入成绩</span>
      </el-button>
      <el-button type="success" @click="handleExport">
        <el-icon><Download /></el-icon><span class="ml-5">导出报表</span>
      </el-button>
    </div>

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
      height="calc(100vh - 220px)"
      header-cell-class-name="common-header"
      :header-cell-style="handleHeaderStyle"
    >
      <el-table-column label="考核方式" align="center" fixed="left">
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
              <el-table-column label="" min-width="80" align="center">
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

  <ImportDialog
    ref="importFormRef"
    title="成绩导入"
    :validate-url="validateUrl"
    :submit-api="StudentAchievementApi.importExcelData"
    :template-api="StudentAchievementApi.downloadTemplate"
    :template-file-name="courseDetail?.name + '-' + (classDetail?.name || '-') + '-成绩导入模板.xlsx'"
    :fail-export-api="StudentAchievementApi.outFail"
    :fail-export-file-name="(courseDetail?.name || '') + '-' + (classDetail?.name || '-') + '-成绩导入错误信息.xlsx'"
    @success="init"
  />
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

const validateUrl = import.meta.env.VITE_BASE_URL + import.meta.env.VITE_API_URL + '/nmt/student-achievement/validate-import'

const scoreTable = ref()
const loading = ref(false)
const route = useRoute()
const classId = Number(route.query.classId)
const courseId = Number(route.query.courseId)

const courseDetail = ref<any>({})
const classDetail = ref<any>({})
const rawPlans = ref<any[]>([])
const tableData = ref<any[]>([])

const handleHeaderStyle = ({ rowIndex, columnIndex }: any) => {
  const baseStyle = { backgroundColor: '#f5f7fa', color: '#333' }
  if (rowIndex === 3 && columnIndex > 0) {
    return { ...baseStyle, color: '#409eff', fontWeight: 'bold' }
  }
  return baseStyle
}

const groupedPlans = computed(() => {
  const groups: any[] = []
  rawPlans.value.forEach(p => {
    let g = groups.find(x => x.modeName === p.modeName)
    if (!g) {
      groups.push({ modeName: p.modeName || '未知', children: [p] })
    } else {
      g.children.push(p)
    }
  })
  return groups
})

const applyMerge = () => {
  const tableEl = scoreTable.value?.$el
  if (!tableEl) return

  const allHeaders = tableEl.querySelectorAll('.el-table__header')
  allHeaders.forEach((header: HTMLElement) => {
    const rows = header.querySelectorAll('tr')
    if (rows.length < 5) return
    const ths = Array.from(rows[4].querySelectorAll('th')) as HTMLElement[]

    if (ths.length > 1) {
      ths[1].setAttribute('colspan', '100')
      ths[1].innerHTML = `
        <div class="merge-wrapper">
          <div class="name-box">姓名</div>
          <div class="remark-box">说明:点击左上方 导入成绩 按钮，下载模板后，填入学生成绩，若有错误数据，请根据提示信息修改，最后导入成绩数据！</div>
        </div>`
      ths.slice(2).forEach(th => th.style.display = 'none')
    }
  })
}

const importFormRef = ref()
const handleImport = () => {
  importFormRef.value.open({classId, courseId})
}

const handleExport = () => {
  console.log('执行导出逻辑')
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

    nextTick(() => {
      setTimeout(applyMerge, 150)
    })
  } catch (error) {
    console.error('加载失败', error)
  } finally {
    loading.value = false
  }
}

onMounted(init)
</script>

<style scoped>
.score-container {
  padding: 20px;
  background: #fff;
  position: relative;
}

.operation-wrapper {
  position: absolute;
  top: 20px;
  right: 20px;
  display: flex;
  gap: 12px;
  z-index: 20;
}

.ml-5 { margin-left: 5px; }

.main-title {
  text-align: center;
  margin-bottom: 20px;
  font-size: 24px;
  color: #333;
}

.info-bar {
  display: flex;
  justify-content: space-between;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
  margin-bottom: 15px;
}

.info-left { display: flex; gap: 30px; font-size: 15px; }
.blue-text { color: #409eff; }
.green-text { color: #67c23a; font-size: 20px; }

:deep(.common-header) { font-weight: bold !important; }

/* 合并行样式修正 */
:deep(.merge-wrapper) {
  display: flex;
  align-items: center;
  height: 44px;
  /* 确保在固定列模式下也有足够的展示空间 */
  min-width: 1000px;
}

:deep(.name-box) {
  width: 100px;
  border-right: 1px solid #ebeef5;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  background-color: #f5f7fa;
}

:deep(.remark-box) {
  flex: 1;
  padding: 0 20px;
  text-align: center; /* 恢复居中 */
  color: rgba(0, 0, 0, 0.98); /* 恢复原来的灰色 */
  font-weight: normal;
  font-size: 14px !important;
  white-space: nowrap;
}

:deep(.el-table__header tr:nth-child(5) th) { padding: 0 !important; }

.score-num { font-weight: 500; color: #333; }
</style>
