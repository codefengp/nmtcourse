<template>
  <el-card class="course-card">
    <template #header>
      <span class="card-title">课程基本信息</span>
    </template>

    <el-row :gutter="20">
      <el-col :span="8">
        <div class="info-item">
          <span class="label">课程编码：</span>
          <span class="value">{{ courseDetail.number }}</span>
        </div>
      </el-col>

      <el-col :span="8">
        <div class="info-item">
          <span class="label">课程名称：</span>
          <span class="value">{{ courseDetail.name }}</span>
        </div>
      </el-col>

      <el-col :span="8">
        <div class="info-item">
          <span class="label">课程类别：</span>
          <span class="value">{{ courseDetail.courseType }}</span>
        </div>
      </el-col>

      <el-col :span="8">
        <div class="info-item">
          <span class="label">课程性质：</span>
          <span class="value">{{ courseDetail.courseProperty }}</span>
        </div>
      </el-col>

      <el-col :span="8">
        <div class="info-item">
          <span class="label">课程学时：</span>
          <span class="value">{{ courseDetail.courseHour }}</span>
        </div>
      </el-col>

      <el-col :span="8">
        <div class="info-item">
          <span class="label">课程学分：</span>
          <span class="value">{{ courseDetail.courseScore }}</span>
        </div>
      </el-col>

      <el-col :span="8">
        <div class="info-item">
          <span class="label">授课年级：</span>
          <span class="value">{{ courseDetail.grade }}</span>
        </div>
      </el-col>

      <el-col :span="8">
        <div class="info-item">
          <span class="label">授课学期：</span>
          <span class="value">{{ courseDetail.term }}</span>
        </div>
      </el-col>

      <el-col :span="8">
        <div class="info-item">
          <span class="label">适用专业：</span>
          <span class="value">{{ courseDetail.majorType }}</span>
        </div>
      </el-col>

      <el-col :span="8">
        <div class="info-item">
          <span class="label">课程负责人：</span>
          <span class="value">
            {{ courseDetail.teacherName || '—' }}
          </span>
        </div>
      </el-col>

    </el-row>
  </el-card>

  <el-card class="course-card">
      <template #header>
        <div class="card-header">
            <span class="card-title">课程教学目标</span>
            <el-button
              type="primary"
              class="button-add"
              @click="addObjective"
            >
              新增课程目标
            </el-button>
        </div>
      </template>
    <el-form ref="formRef" :model="objectForm">
      <el-row
        v-for="(item, index) in objectForm.objectives"
        :key="item.id"
        :gutter="20"
        style="margin-bottom: 12px"
      >
        <!-- 目标名 -->
        <el-col :span="4">
          <el-form-item
            label="目标名称"
            label-width="100px"
            :prop="`objectives.${index}.name`"
            :rules="[{ required: true, message: '请输入目标名', trigger: 'blur' }]"
          >
            <el-input
              v-model="item.name"
              placeholder="请输入课程目标名称"
              @blur="saveObjective(item)"
            />
          </el-form-item>
        </el-col>

        <!-- 目标内容 -->
        <el-col :span="14">
          <el-form-item
            label="目标内容"
            label-width="100px"
            :prop="`objectives.${index}.content`"
            :rules="[{ required: true, message: '请输入目标内容', trigger: 'blur' }]"
          >
            <el-input
              v-model="item.content"
              placeholder="请输入课程目标内容描述"
              @blur="saveObjective(item)"
            />
          </el-form-item>
        </el-col>

        <!-- 期望值 + 删除 -->
        <el-col :span="6">
          <el-form-item
            label="目标值"
            label-width="100px"
            :prop="`objectives.${index}.expectValue`"
            :rules="[{ required: true, message: '请输入目标值', trigger: 'change' }]"
          >
            <el-input-number
              v-model="item.expectValue"
              :min="0"
              :max="1"
              :step="0.01"
              controls-position="right"
              @change="saveObjective(item)"
            />

            <el-button
              type="danger"
              style="margin-left: 20px"
              @click="removeObjective(item.id)"
            >
              删除
            </el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </el-card>

  <el-card class="course-card">
    <template #header>
      <div class="card-header">
        <span class="card-title">课程考核方式</span>
        <el-button
          type="primary"
          class="button-add"
          @click="addMode"
        >
          新增考核方式
        </el-button>
      </div>
    </template>
    <el-form ref="formRef" :model="modeForm">
      <el-row
          v-for="(item, index) in modeForm.modes"
          :key="item.id"
          :gutter="20"
          style="margin-bottom: 12px"
      >
        <!-- 目标名 -->
        <el-col :span="18">
          <el-form-item
              label="考核名称"
              label-width="100px"
              :prop="`modes.${index}.name`"
              :rules="[{ required: true, message: '请输入考核名', trigger: 'blur' }]"
          >
            <el-input
                v-model="item.name"
                placeholder="请输入考核评价名称"
                @blur="saveMode(item)"
            />
          </el-form-item>
        </el-col>

        <!-- 期望值 + 删除 -->
        <el-col :span="6">
          <el-form-item
              label="权重(%)"
              label-width="100px"
              :prop="`modes.${index}.weight`"
              :rules="[{ required: true, message: '请输入权重', trigger: 'change' }]"
          >
            <el-input-number
                v-model="item.weight"
                :min="0"
                :max="100"
                :step="1"
                controls-position="right"
                @change="saveMode(item)"
            />

            <el-button
                type="danger"
                style="margin-left: 20px"
                @click="removeMode(item.id)"
            >
              删除
            </el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </el-card>

  <el-card class="course-card">
    <template #header>
      <div class="card-header">
        <span class="card-title">课程达成度评价</span>
        <!-- 操作按钮 -->
        <div class="button-add" v-if="tableData && tableData.length > 0">
          <el-button v-if="!editable" type="primary" @click="editable = true">编辑</el-button>
          <el-button v-else type="primary" @click="saveObjectiveMode">保存</el-button>
        </div>
      </div>
    </template>
      <!-- 表格 -->
    <el-table
      v-if="tableData && tableData.length > 0"
      :data="tableData"
      border
      style="width: 100%"
      row-key="id"
      :row-class-name="rowClassName"
    >
      <!-- 课程目标 -->
      <el-table-column
        prop="objectiveName"
        label="课程目标"
        width="160"
        align="center"
      />

      <!-- 考核方式及成绩总分（父表头） -->
      <el-table-column label="考核方式及成绩总分" align="center">
        <!-- 子表头 -->
        <el-table-column
          v-for="mode in modes"
          :key="mode.modeId"
          :label="mode.modeName"
          align="center"
        >
          <template #default="{ row }">
            <template v-if="!row.isTotal">
              <el-input-number
                v-if="editable"
                v-model="row[mode.modeId]"
                :min="0"
                :max="100"
              />
              <span v-else>{{ row[mode.modeId] ?? 0 }}</span>
            </template>

            <span v-else>{{ row[mode.modeId] }}</span>
          </template>
        </el-table-column>
      </el-table-column>
    </el-table>
  </el-card>


</template>

<script setup lang="ts">
import {DICT_TYPE, getDictLabel} from '@/utils/dict'
import { CourseInfoApi } from '@/api/nmt/courseinfo'
import { CourseObjectiveApi,CourseObjective } from '@/api/nmt/courseobjective'
import { EvaluateModeApi,EvaluateMode} from "@/api/nmt/evaluatemode";
import { ObjectiveModeApi,ObjectiveMode} from "@/api/nmt/objectivemode";

/** 课程大纲 列表 */
defineOptions({ name: 'CourseOutline' })
const formRef = ref()
//课程id
const courseId = Number(useRoute().query.id)
const courseDetail = ref({
    id: undefined,
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
/** 1.课程详情*/
const getDetail = async (id: number) => {
      const data = await CourseInfoApi.getCourseInfo(id)
      //字典
      data.majorType = getDictLabel(DICT_TYPE.NMT_MAJOR_TYPE,data.majorType)
      data.courseType = getDictLabel(DICT_TYPE.NMT_COURSE_TYPE,data.courseType)
      data.courseProperty = getDictLabel(DICT_TYPE.NMT_COURSE_PROPERTY,data.courseProperty)
      data.term = getDictLabel(DICT_TYPE.NMT_TERM,data.term)
      courseDetail.value = data
}

/** 2.课程目标*/
const objectForm = reactive<{
    objectives: CourseObjective[]
}>({
    objectives: []
})

/** 新增：直接入库，name 为空让用户填 */
const addObjective = async () => {
  await CourseObjectiveApi.createCourseObjective({
    courseId,
    name: '课程目标',
    content: '课程目标内容',
    expectValue: 0
  } as CourseObjective)

  await reloadObjectiveList()
  //课程达成度评价(矩阵)
  await reloadObjectiveModeList()
}

/** 目标列表数据 */
const reloadObjectiveList = async () => {
  const res = await CourseObjectiveApi.getCourseObjectivePage({
      courseId,
      pageNo: 1,
      pageSize: 100

  })
    objectForm.objectives = res.list || []
}

/** 自动保存 */
const saveObjective = async (row: CourseObjective) => {
  if (!row.name || !row.content) return
  await CourseObjectiveApi.updateCourseObjective(row)
  //课程达成度评价(矩阵)
  await reloadObjectiveModeList()
}

/** 删除 */
const removeObjective = async (id: number) => {
    ElMessageBox.confirm(
        '确认删除该课程目标吗？',
        '删除确认',
        {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: 'warning'
        }
    )
    .then(async () => {
        await CourseObjectiveApi.deleteCourseObjective(id)
        objectForm.objectives = objectForm.objectives.filter(i => i.id !== id)
        ElMessage.success('删除课程目标成功')

      //课程达成度评价(矩阵)
      await reloadObjectiveModeList()
    })
    .catch(() => {
    })
}


/** 3.考核评价方式*/
const modeForm = reactive<{
  modes: EvaluateMode[]
}>({
  modes: []
})

/** 新增：直接入库，name 为空让用户填 */
const addMode = async () => {
  await EvaluateModeApi.createEvaluateMode({
    courseId,
    name: '考核方式',
    weight: 0
  } as EvaluateMode)

  await reloadModeList()
  //课程达成度评价(矩阵)
  await reloadObjectiveModeList()
}

/** 列表数据 */
const reloadModeList = async () => {
  const res = await EvaluateModeApi.getEvaluateModePage({
    courseId,
    pageNo: 1,
    pageSize: 100

  })
  modeForm.modes = res.list || []
}

/** 自动保存 */
const saveMode = async (row: EvaluateMode) => {
  if (!row.name) return
  await EvaluateModeApi.updateEvaluateMode(row)
  //课程达成度评价(矩阵)
  await reloadObjectiveModeList()
}

/** 删除 */
const removeMode = async (id: number) => {
  ElMessageBox.confirm(
      '确认删除该评价方式吗？',
      '删除确认',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }
  )
      .then(async () => {
        await EvaluateModeApi.deleteEvaluateMode(id)
        modeForm.modes = modeForm.modes.filter(i => i.id !== id)
        ElMessage.success('删除评价方式成功')
        //课程达成度评价(矩阵)
        await reloadObjectiveModeList()
      })
      .catch(() => {
      })
}


/** 4.课程达成度评价 **/
interface TableRow {
    id: number
    objectiveId: number
    objectiveName: string
    isTotal?: boolean
    [modeId: number]: number | boolean | string
}

const editable = ref(false)
const tableData = ref<TableRow[]>([])
const modes = ref<{ modeId: number; modeName: string }[]>([])

/** 根据接口数据生成矩阵 + 合计行 */
const renderMatrix = (list: ObjectiveMode[]) => {
    //清空数据
    tableData.value = []
    const rowMap = new Map<number, TableRow>()
    const modeMap = new Map<number, string>()

    list.forEach(item => {
        if (!item.objectiveId || !item.modeId) return
        if (!rowMap.has(item.objectiveId)) {
            rowMap.set(item.objectiveId, {
                id: item.objectiveId,
                objectiveId: item.objectiveId,
                objectiveName: item.objectiveName || ''
            })
        }
        // !是ts的非空断言,保证 rowMap.get(item.objectiveId)undefined
        // [item.modeId] 是 动态属性访问，意思是把对象的某个属性设置为某个值，属性名是 item.modeId(若属性不存在,自动创建属性)
        rowMap.get(item.objectiveId)![item.modeId] = item.score ?? 0
        //设置单元格的主键 key: item.objectiveId + '_' +item.modeId
        rowMap.get(item.objectiveId)![item.objectiveId + '_' +item.modeId] = item.id ?? 0
        // 收集所有列，形成 modes 数组,用于 <el-table-column v-for="mode in modes"> 动态渲染列
        if (!modeMap.has(item.modeId)) modeMap.set(item.modeId, item.modeName || '')
    })

    //普通行
    const rows = Array.from(rowMap.values())
    modes.value = Array.from(modeMap.entries()).map(([modeId, modeName]) => ({ modeId, modeName }))

    if(rows.length > 0){
      const totalRow: TableRow = { id: -1, objectiveId: -1, objectiveName: '合计', isTotal: true }
      modes.value.forEach(mode => {
        totalRow[mode.modeId] = rows.reduce((sum, r) => sum + Number(r[mode.modeId] ?? 0), 0)
      })
      //合并数组
      tableData.value = [...rows, totalRow]
    }
}

/** 刷新矩阵 */
const reloadObjectiveModeList = async () => {
    const list = await ObjectiveModeApi.listObjectiveMode()
    //矩阵数据更新
    renderMatrix(list)
    editable.value = false
}

/** 保存并自动刷新 */
const saveObjectiveMode = async () => {
    const rows = tableData.value.filter(r => !r.isTotal)
    const result: ObjectiveMode[] = []
    rows.forEach(row => {
        modes.value.forEach(mode => {
            //单元格的主键 key: objectiveId + '_' +modeId
            const key = `${row.objectiveId}_${mode.modeId}`
            result.push({
                id: row[key],
                objectiveId: row.objectiveId,
                objectiveName: row.objectiveName,
                modeId: mode.modeId,
                modeName: mode.modeName,
                score: Number(row[mode.modeId] ?? 0)
            })
        })
    })
    // 保存矩阵数据
    await ObjectiveModeApi.updateObjectiveModeList(result)
    // 自动刷新矩阵
    await reloadObjectiveModeList()
    ElMessage.success('保存成功')
}

/** 合计行字体加粗 */
const rowClassName = ({ row }) => {
  return row.isTotal ? 'total-row' : ''
}

/** 初始化 **/
onMounted(async () => {
    //1.课程详情
    await getDetail(courseId)
    //2.课程目标
    await reloadObjectiveList()
    //3.考核评价方式
    await reloadModeList()
    //4.课程达成度评价(矩阵)
    await reloadObjectiveModeList()
})
</script>

<style scoped>
.course-card {
    width: 100%;
    margin-bottom: 1%;
}

.card-title {
    font-weight: 600;
    font-size: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.button-add {
    display: flex;
    justify-content: flex-end; /* 右对齐 */
}

.info-item {
    margin-bottom: 8px;
    line-height: 22px;
}

.label {
    color: #606266;
}

.value {
    color: #303133;
}

:deep(.total-row td) {
  font-weight: 600;
  background-color: rgba(250, 250, 250, 0.99); /* 可选：浅灰 */
}

</style>

