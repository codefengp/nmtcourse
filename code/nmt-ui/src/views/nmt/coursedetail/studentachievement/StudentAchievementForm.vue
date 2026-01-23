<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="学生ID" prop="studentId">
        <el-input v-model="formData.studentId" placeholder="请输入学生ID" />
      </el-form-item>
      <el-form-item label="考核计划ID" prop="planId">
        <el-input v-model="formData.planId" placeholder="请输入考核计划ID" />
      </el-form-item>
      <el-form-item label="课程目标ID" prop="objectiveId">
        <el-input v-model="formData.objectiveId" placeholder="请输入课程目标ID" />
      </el-form-item>
      <el-form-item label="考核方式ID" prop="modeId">
        <el-input v-model="formData.modeId" placeholder="请输入考核方式ID" />
      </el-form-item>
      <el-form-item label="得分" prop="score">
        <el-input v-model="formData.score" placeholder="请输入得分" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script setup lang="ts">
import { StudentAchievementApi, StudentAchievement } from '@/api/nmt/studentachievement'

/** 学生成绩 表单 */
defineOptions({ name: 'StudentAchievementForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  studentId: undefined,
  planId: undefined,
  objectiveId: undefined,
  modeId: undefined,
  score: undefined,
})
const formRules = reactive({
  studentId: [{ required: true, message: '学生ID不能为空', trigger: 'blur' }],
  planId: [{ required: true, message: '考核计划ID不能为空', trigger: 'blur' }],
  objectiveId: [{ required: true, message: '课程目标ID不能为空', trigger: 'blur' }],
  modeId: [{ required: true, message: '考核方式ID不能为空', trigger: 'blur' }],
  score: [{ required: true, message: '得分不能为空', trigger: 'blur' }],
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
      formData.value = await StudentAchievementApi.getStudentAchievement(id)
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
    const data = formData.value as unknown as StudentAchievement
    if (formType.value === 'create') {
      await StudentAchievementApi.createStudentAchievement(data)
      message.success(t('common.createSuccess'))
    } else {
      await StudentAchievementApi.updateStudentAchievement(data)
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
    studentId: undefined,
    planId: undefined,
    objectiveId: undefined,
    modeId: undefined,
    score: undefined,
  }
  formRef.value?.resetFields()
}
</script>