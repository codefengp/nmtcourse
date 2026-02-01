<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="课程评价主表ID" prop="evaluationId">
        <el-input v-model="formData.evaluationId" placeholder="请输入课程评价主表ID" />
      </el-form-item>
      <el-form-item label="课程目标ID" prop="objectiveId">
        <el-input v-model="formData.objectiveId" placeholder="请输入课程目标ID" />
      </el-form-item>
      <el-form-item label="目标名称" prop="objectiveName">
        <el-input v-model="formData.objectiveName" placeholder="请输入目标名称" />
      </el-form-item>
      <el-form-item label="目标内容" prop="objectiveContent">
        <Editor v-model="formData.objectiveContent" height="150px" />
      </el-form-item>
      <el-form-item label="评价内容" prop="comment">
        <el-input v-model="formData.comment" placeholder="请输入评价内容" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script setup lang="ts">
import { ObjectiveEvaluationApi, ObjectiveEvaluation } from '@/api/nmt/objectiveevaluation'

/** 课程目标达成度评价 表单 */
defineOptions({ name: 'ObjectiveEvaluationForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  evaluationId: undefined,
  objectiveId: undefined,
  objectiveName: undefined,
  objectiveContent: undefined,
  comment: undefined,
})
const formRules = reactive({
  evaluationId: [{ required: true, message: '课程评价主表ID不能为空', trigger: 'blur' }],
  objectiveId: [{ required: true, message: '课程目标ID不能为空', trigger: 'blur' }],
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
      formData.value = await ObjectiveEvaluationApi.getObjectiveEvaluation(id)
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
    const data = formData.value as unknown as ObjectiveEvaluation
    if (formType.value === 'create') {
      await ObjectiveEvaluationApi.createObjectiveEvaluation(data)
      message.success(t('common.createSuccess'))
    } else {
      await ObjectiveEvaluationApi.updateObjectiveEvaluation(data)
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
    evaluationId: undefined,
    objectiveId: undefined,
    objectiveName: undefined,
    objectiveContent: undefined,
    comment: undefined,
  }
  formRef.value?.resetFields()
}
</script>