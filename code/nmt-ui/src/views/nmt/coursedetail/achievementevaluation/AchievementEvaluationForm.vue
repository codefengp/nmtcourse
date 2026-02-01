<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="课程ID" prop="courseId">
        <el-input v-model="formData.courseId" placeholder="请输入课程ID" />
      </el-form-item>
      <el-form-item label="教学班级ID" prop="classId">
        <el-input v-model="formData.classId" placeholder="请输入教学班级ID" />
      </el-form-item>
      <el-form-item label="学生总体评价" prop="overallComment">
        <el-input v-model="formData.overallComment" placeholder="请输入学生总体评价" />
      </el-form-item>
      <el-form-item label="存在问题" prop="problemAnalysis">
        <el-input v-model="formData.problemAnalysis" placeholder="请输入存在问题" />
      </el-form-item>
      <el-form-item label="课程改进" prop="improvementPlan">
        <el-input v-model="formData.improvementPlan" placeholder="请输入课程改进" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script setup lang="ts">
import { AchievementEvaluationApi, AchievementEvaluation } from '@/api/nmt/achievementevaluation'

/** 达成度评价 表单 */
defineOptions({ name: 'AchievementEvaluationForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  courseId: undefined,
  classId: undefined,
  overallComment: undefined,
  problemAnalysis: undefined,
  improvementPlan: undefined,
})
const formRules = reactive({
  courseId: [{ required: true, message: '课程ID不能为空', trigger: 'blur' }],
  classId: [{ required: true, message: '教学班级ID不能为空', trigger: 'blur' }],
})
const formRef = ref() // 表单 Ref

/** 打开弹窗 */
const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      formData.value = await AchievementEvaluationApi.getAchievementEvaluation(id)
    } finally {
      formLoading.value = false
    }
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交表单 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
const submitForm = async () => {
  // 校验表单
  await formRef.value.validate()
  // 提交请求
  formLoading.value = true
  try {
    const data = formData.value as unknown as AchievementEvaluation
    if (formType.value === 'create') {
      await AchievementEvaluationApi.createAchievementEvaluation(data)
      message.success(t('common.createSuccess'))
    } else {
      await AchievementEvaluationApi.updateAchievementEvaluation(data)
      message.success(t('common.updateSuccess'))
    }
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
const resetForm = () => {
  formData.value = {
    id: undefined,
    courseId: undefined,
    classId: undefined,
    overallComment: undefined,
    problemAnalysis: undefined,
    improvementPlan: undefined,
  }
  formRef.value?.resetFields()
}
</script>