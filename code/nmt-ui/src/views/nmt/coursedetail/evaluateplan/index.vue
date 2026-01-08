<template>
  <!--每列之间有 16px 的间距-->
  <el-row :gutter="16">
    <!-- 左侧说明 -->
    <el-col :span="3">
      <el-card>
        <template #header>
          <span class="desc-title">考核计划说明:</span>
        </template>
        <div class="desc-content">
          <p>
            1、各课程目标下不同考核方式的评价总分为课程大纲中达成度评价总分;
          </p>
          <p>
            2、您可以在右侧表格“考核内容”-列中维护考查分课程目标的考核内容（题目)，多项考核内容（题目）之间可以用“、”(顿号）隔开;
          </p>
          <p>
            3、考核分数之和等于评价总分，分数显示绿色，反之显示红色;
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
                  type="primary"
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
                  :class="isScoreValid(row) ? 'score-valid' : 'score-invalid'"
              />
              <span
                v-else
                :class="isScoreValid(row) ? 'score-valid' : 'score-invalid'"
              >
              {{ row.score ?? '' }}
            </span>
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
import {EvaluatePlanApi,RowVO} from "@/api/nmt/evaluateplan";
//课程id
const courseId = Number(useRoute().query.id)

/**
 * 保存用对象
 */
interface EvaluatePlan {
  id: number
  objectiveModeId?: number
  objectiveId?: number
  modeId?: number
  score?: number
  content?: string
}

const editable = ref(false)
const tableData = ref<RowVO[]>([])

/* ================= 数据加载 ================= */
const loadData = async () => {
  //清空数据
  tableData.value = []
  const data = await EvaluatePlanApi.listEvaluatePlan(courseId)
  tableData.value = data
}

/** 初始化 **/
onMounted(async () => {
  //1.列表数据
  await loadData()
})

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

// 判断当前行的分数是否有效
const isScoreValid = (row: RowVO) => {
  // 找到同一组（同一课程目标 + 同一考核方式）的所有行
  const groupRows = tableData.value.filter(item =>
    item.objectiveId === row.objectiveId &&
    item.modeId === row.modeId
  )

  // 计算该组所有分数之和
  const sum = groupRows.reduce((total, item) => total + (item.score || 0), 0)

  // 比较和与总分（允许0.01的误差）
  return Math.abs(sum - row.totalScore) < 0.01
}

/* ================= 保存 / 取消 ================= */

const save = async () => {
  const plans: EvaluatePlan[] = []
  tableData.value.forEach(row => {
    //没有id,并且考核分数或者考核内容任一为空，不保存数据
    if(!row.id && (!row.score || !row.content)){
      return
    }
    plans.push({
      id: row.id,
      objectiveModeId: row.objectiveModeId,
      objectiveId: row.objectiveId,
      modeId: row.modeId,
      score: row.score ?? 0,
      content: row.content
    })
  })
  if (plans.length) {
    //保存数据
    await EvaluatePlanApi.saveEvaluatePlanList(plans)
    ElMessage.success('保存成功')
  }
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

.desc-title {
  font-weight: 600;
}

.desc-content {
  font-size: 14px;
  line-height: 1.8;
}

.score-valid {
  color: #58d719; /* 绿色 */
  font-weight: bold;
}

.score-invalid {
  color: #ea1717; /* 红色 */
  font-weight: bold;
}
</style>
