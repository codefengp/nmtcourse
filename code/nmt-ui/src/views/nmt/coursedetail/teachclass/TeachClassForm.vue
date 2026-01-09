<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="班级编号" prop="number">
        <el-input v-model="formData.number" placeholder="请输入班级编号" />
      </el-form-item>
      <el-form-item label="班级名称" prop="name">
        <el-input v-model="formData.name" placeholder="请输入班级名称" />
      </el-form-item>
      <el-form-item label="课程ID" prop="courseId">
        <el-input v-model="formData.courseId" placeholder="请输入课程ID" />
      </el-form-item>
      <el-form-item label="班级人数" prop="totalNumber">
        <el-input v-model="formData.totalNumber" placeholder="请输入班级人数" />
      </el-form-item>
      <el-form-item label="负责教师" prop="teacherName">
        <el-input v-model="formData.teacherName" placeholder="请输入负责教师" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script setup lang="ts">
import { TeachClassApi, TeachClass } from '@/api/nmt/teachclass'

/** 教学班级 表单 */
defineOptions({ name: 'TeachClassForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  number: undefined,
  name: undefined,
  courseId: undefined,
  totalNumber: undefined,
  teacherName: undefined,
})
const formRules = reactive({
  number: [{ required: true, message: '班级编号不能为空', trigger: 'blur' }],
  name: [{ required: true, message: '班级名称不能为空', trigger: 'blur' }],
  courseId: [{ required: true, message: '课程ID不能为空', trigger: 'blur' }],
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
      formData.value = await TeachClassApi.getTeachClass(id)
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
    const data = formData.value as unknown as TeachClass
    if (formType.value === 'create') {
      await TeachClassApi.createTeachClass(data)
      message.success(t('common.createSuccess'))
    } else {
      await TeachClassApi.updateTeachClass(data)
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
    number: undefined,
    name: undefined,
    courseId: undefined,
    totalNumber: undefined,
    teacherName: undefined,
  }
  formRef.value?.resetFields()
}
</script>