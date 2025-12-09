<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="编号" prop="number">
        <el-input v-model="formData.number" placeholder="请输入编号" />
      </el-form-item>
      <el-form-item label="名称" prop="name">
        <el-input v-model="formData.name" placeholder="请输入名称" />
      </el-form-item>
      <el-form-item label="所属专业" prop="majorType">
        <el-select v-model="formData.majorType" placeholder="请选择所属专业">
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.NMT_MAJOR_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="课程类型" prop="courseType">
        <el-select v-model="formData.courseType" placeholder="请选择课程类型">
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.NMT_COURSE_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="课程属性" prop="courseProperty">
        <el-select v-model="formData.courseProperty" placeholder="请选择课程属性">
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.NMT_COURSE_PROPERTY)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="总学时" prop="courseHour">
        <el-input v-model="formData.courseHour" placeholder="请输入总学时" />
      </el-form-item>
      <el-form-item label="总学分" prop="courseScore">
        <el-input v-model="formData.courseScore" placeholder="请输入总学分" />
      </el-form-item>
      <el-form-item label="授课年级" prop="grade">
        <el-input v-model="formData.grade" placeholder="请输入授课年级" />
      </el-form-item>
      <el-form-item label="授课学期" prop="term">
        <el-select v-model="formData.term" placeholder="请选择授课学期">
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.NMT_TERM)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="授课教师" prop="teacherName">
        <el-input v-model="formData.teacherName" placeholder="请输入授课教师" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script setup lang="ts">
import { getIntDictOptions, DICT_TYPE } from '@/utils/dict'
import { CourseInfoApi, CourseInfo } from '@/api/nmt/courseinfo'

/** 课程信息 表单 */
defineOptions({ name: 'CourseInfoForm' })

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
  majorType: undefined,
  courseType: undefined,
  courseProperty: undefined,
  courseHour: undefined,
  courseScore: undefined,
  grade: undefined,
  term: undefined,
  teacherName: undefined,
})
const formRules = reactive({
  number: [{ required: true, message: '编号不能为空', trigger: 'blur' }],
  name: [{ required: true, message: '名称不能为空', trigger: 'blur' }],
  majorType: [{ required: true, message: '所属专业不能为空', trigger: 'change' }],
  courseType: [{ required: true, message: '课程类型不能为空', trigger: 'change' }],
  courseProperty: [{ required: true, message: '课程属性不能为空', trigger: 'change' }],
  courseHour: [{ required: true, message: '总学时不能为空', trigger: 'blur' }],
  courseScore: [{ required: true, message: '总学分不能为空', trigger: 'blur' }],
  grade: [{ required: true, message: '授课年级不能为空', trigger: 'blur' }],
  term: [{ required: true, message: '授课学期不能为空', trigger: 'change' }],
  teacherName: [{ required: true, message: '授课教师不能为空', trigger: 'blur' }]
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
      formData.value = await CourseInfoApi.getCourseInfo(id)
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
    const data = formData.value as unknown as CourseInfo
    if (formType.value === 'create') {
      await CourseInfoApi.createCourseInfo(data)
      message.success(t('common.createSuccess'))
    } else {
      await CourseInfoApi.updateCourseInfo(data)
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
    majorType: undefined,
    courseType: undefined,
    courseProperty: undefined,
    courseHour: undefined,
    courseScore: undefined,
    grade: undefined,
    term: undefined,
    teacherName: undefined,
  }
  formRef.value?.resetFields()
}
</script>
