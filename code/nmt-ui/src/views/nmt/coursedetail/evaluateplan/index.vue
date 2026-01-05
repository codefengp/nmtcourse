<template>
  <!--每列之间有 16px 的间距-->
  <el-row :gutter="16">
    <!-- 左侧说明 -->
    <el-col :span="3">
      <el-card class="desc-card">
        <template #header>
          <span class="desc-title">考核计划说明</span>
        </template>
        <div class="desc-content">
          <p>
            1. 各课程目标下不同考核方式的评价总分为课程目标达成度的重要依据；
          </p>
          <p>
            2. 每种考核方式下可配置多个考核内容，多个内容请使用“、”（顿号）分隔；
          </p>
        </div>
      </el-card>
    </el-col>

    <!-- 右侧表格 -->
    <el-col :span="21">
      <el-card>
        <template #header>
          <div class="header">
            <span class="desc-title">课程考核计划</span>
            <div>
              <el-button
                  type="primary"
                  v-if="!editable"
                  @click="editable = true"
              >
                编辑
              </el-button>
              <el-button
                  type="success"
                  v-if="editable"
                  @click="save"
              >
                保存
              </el-button>
            </div>
          </div>
        </template>

        <el-table
            :data="tableData"
            :span-method="spanMethod"
            border
            style="width: 100%"
        >
          <!-- 课程目标 -->
          <el-table-column
              prop="objectiveName"
              label="课程目标"
              min-width="140"
          />

          <!-- 考核方式 -->
          <el-table-column
              prop="modeName"
              label="考核方式"
              min-width="140"
          />

          <!-- 评价总分 -->
          <el-table-column
              prop="totalScore"
              label="评价总分"
              min-width="100"
          />

          <!-- 考核分数 -->
          <el-table-column
              label="考核分数"
              min-width="100"
          >
            <template #default="{ row }">
              <el-input-number
                  v-if="editable"
                  v-model="row.score"
                  :min="0"
                  :controls="false"
              />
              <span v-else>{{ row.score ?? '' }}</span>
            </template>
          </el-table-column>

          <!-- 考核内容 -->
          <el-table-column label="考核内容" min-width="140">
            <template #default="{ row }">
              <el-input
                  v-if="editable"
                  v-model="row.content"
                  placeholder="多个内容用顿号（、）分隔"
                  clearable
              />
              <span v-else>{{ row.content ?? '' }}</span>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </el-col>
  </el-row>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

/**
 * 页面 VO（与你给出的一致）
 */
export interface RowVO {
  objectiveModeId: number
  objectiveId: number
  objectiveName: string

  modeId: number
  modeName: string

  totalScore: number

  // EvaluatePlan
  id?: number
  score?: number
  content?: string
}

/**
 * 保存用对象
 */
interface EvaluatePlan {
  id?: number
  objectiveModeId: number
  objectiveId: number
  modeId: number
  score: number
  content: string
}

const editable = ref(false)
const tableData = ref<RowVO[]>([])

/* ================= 模拟接口（替换成真实接口） ================= */

const getEvaluatePlanList = async (): Promise<RowVO[]> => {
  return [
    {
      objectiveModeId: 1,
      objectiveId: 1,
      objectiveName: '课程目标1',
      modeId: 1,
      modeName: '平时考核',
      totalScore: 0.3,
      id: 101,
      score: 10,
      content: '作业'
    },
    {
      objectiveModeId: 1,
      objectiveId: 1,
      objectiveName: '课程目标1',
      modeId: 1,
      modeName: '平时考核',
      totalScore: 0.3,
      id: 102,
      score: 10,
      content: '测验'
    },
    {
      objectiveModeId: 2,
      objectiveId: 1,
      objectiveName: '课程目标1',
      modeId: 2,
      modeName: '期末考试',
      totalScore: 0.7,
      id: 103,
      score: 70,
      content: '期末试卷'
    }
  ]
}

const saveEvaluatePlans = async (plans: EvaluatePlan[]) => {
  console.log('保存数据：', plans)
}

/* ================= 数据加载 ================= */
const loadData = async () => {
  const res = await getEvaluatePlanList()
  tableData.value = res
}

/**
 * 初始化加载数据 */
onMounted(loadData)

/* ================= 合并单元格逻辑 ================= */
const spanMethod = ({
                      row,
                      column,
                      rowIndex
                    }: {
  row: RowVO
  column: any
  rowIndex: number
}) => {
  const field = column.property

  // 课程目标合并
  if (field === 'objectiveName') {
    if (
        rowIndex > 0 &&
        tableData.value[rowIndex - 1].objectiveId === row.objectiveId
    ) {
      return [0, 0]
    }

    let rowspan = 1
    for (let i = rowIndex + 1; i < tableData.value.length; i++) {
      if (tableData.value[i].objectiveId === row.objectiveId) {
        rowspan++
      } else {
        break
      }
    }
    return [rowspan, 1]
  }

  // 考核方式 & 评价总分合并
  if (field === 'modeName' || field === 'totalScore') {
    if (
        rowIndex > 0 &&
        tableData.value[rowIndex - 1].objectiveId === row.objectiveId &&
        tableData.value[rowIndex - 1].modeId === row.modeId
    ) {
      return [0, 0]
    }

    let rowspan = 1
    for (let i = rowIndex + 1; i < tableData.value.length; i++) {
      if (
          tableData.value[i].objectiveId === row.objectiveId &&
          tableData.value[i].modeId === row.modeId
      ) {
        rowspan++
      } else {
        break
      }
    }
    return [rowspan, 1]
  }

  return [1, 1]
}

/* ================= 保存 / 取消 ================= */

const save = async () => {
  const plans: EvaluatePlan[] = []

  tableData.value.forEach(row => {
    if (!row.content) return

    const contents = row.content
        .split('、')
        .map(c => c.trim())
        .filter(Boolean)

    contents.forEach(content => {
      plans.push({
        id: row.id,
        objectiveModeId: row.objectiveModeId,
        objectiveId: row.objectiveId,
        modeId: row.modeId,
        score: row.score ?? 0,
        content
      })
    })
  })

  if (!plans.length) {
    ElMessage.warning('没有可保存的数据')
    return
  }

  await saveEvaluatePlans(plans)

  ElMessage.success('保存成功')
  editable.value = false
  await loadData()
}
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.desc-card {
  height: 100%;
}

.desc-title {
  font-weight: 600;
}

.desc-content {
  font-size: 14px;
  line-height: 1.8;
}
</style>
