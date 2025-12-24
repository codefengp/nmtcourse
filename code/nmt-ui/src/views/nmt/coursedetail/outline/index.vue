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
          <span class="card-title">课程教学目标</span>
      </template>
        <el-button
            type="primary"
            round
            class="button-add"
            @click="addObject"
        >
          新增课程目标
        </el-button>
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
              @blur="saveObject(item)"
            />
          </el-form-item>
        </el-col>

        <!-- 目标内容 -->
        <el-col :span="16">
          <el-form-item
            label="目标内容"
            label-width="100px"
            :prop="`objectives.${index}.content`"
            :rules="[{ required: true, message: '请输入目标内容', trigger: 'blur' }]"
          >
            <el-input
              v-model="item.content"
              placeholder="请输入课程目标内容描述"
              @blur="saveObject(item)"
            />
          </el-form-item>
        </el-col>

        <!-- 期望值 + 删除 -->
        <el-col :span="4">
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
              @change="saveObject(item)"
            />

            <el-button
              type="danger"
              round
              style="margin-left: 20px"
              @click="removeObject(item.id)"
            >
              删除
            </el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </el-card>


</template>

<script setup lang="ts">
import {DICT_TYPE, getDictLabel} from '@/utils/dict'
import { CourseInfoApi } from '@/api/nmt/courseinfo'
import { CourseObjectiveApi,CourseObjective } from '@/api/nmt/courseobjective'

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
const addObject = async () => {
  await CourseObjectiveApi.createCourseObjective({
    courseId,
    name: '课程目标',
    content: '',
    expectValue: 0
  } as CourseObjective)

  await reloadObjectList()
}

/** 目标列表数据 */
const reloadObjectList = async () => {
  const res = await CourseObjectiveApi.getCourseObjectivePage({
      courseId,
      pageNo: 1,
      pageSize: 100

  })
    objectForm.objectives = res.list || []
}

/** 自动保存 */
const saveObject = async (row: CourseObjective) => {
  if (!row.name || !row.content) return
  await CourseObjectiveApi.updateCourseObjective(row)
}

/** 删除 */
const removeObject = async (id: number) => {
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
    })
    .catch(() => {
    })
}

/** 初始化 **/
onMounted(async () => {
    //1.课程详情
    await getDetail(courseId)
    //2.课程目标
    await reloadObjectList()
})
</script>

<style scoped>
.course-card {
    width: 100%;
    margin-bottom: 2%;
}

.card-title {
    font-weight: 600;
    font-size: 16px;
}

.button-add {
    margin-top: 10px;
    margin-bottom: 16px;
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

</style>

