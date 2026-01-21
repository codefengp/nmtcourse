<template>
  <Dialog v-model="dialogVisible" title="教师导入" width="400">
    <el-upload
      ref="uploadRef"
      v-model:file-list="fileList"
      :action="validateUrl"
      :auto-upload="false"
      :disabled="validateLoading"
      :headers="uploadHeaders"
      :limit="1"
      :on-error="submitFormError"
      :on-exceed="handleExceed"
      :on-success="submitFormSuccess"
      accept=".xlsx, .xls"
      drag
    >
      <Icon icon="ep:upload" />
      <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
      <template #tip>
        <div class="el-upload__tip text-center">
          <span  style="font-size: 16px">仅允许导入 xls、xlsx 格式文件。</span>
          <el-link
            :underline="false"
            style="font-size: 14px; vertical-align: baseline"
            type="primary"
            @click="importTemplate"
          >
            下载模板
          </el-link>
        </div>
        <div class="el-upload__tip text-center" >
          <span v-if="validateShow == true && successData.length >= 0 && failData.length == 0"  style="font-size: 16px; color: green">
             验证成功: {{ successData.length }} 条, 验证失败数据: {{ failData.length }} 条
             
          </span>
          <span v-if="validateShow == true && successData.length >= 0 && failData.length > 0"  style="font-size: 16px; color: red">
            验证成功: {{ successData.length }} 条, 验证失败数据: {{ failData.length }} 条
              <el-link
              :underline="false"
              style="font-size: 14px; vertical-align: baseline"
              type="primary"
              @click="outFail(JSON.stringify(failData),JSON.stringify(successData))"
            >
              下载错误信息
            </el-link>
         </span>
        </div>
      </template>
    </el-upload>
    <template #footer>
      <el-button :disabled="validateLoading" type="warning" @click="validateForm">验证数据</el-button>
      <el-button :disabled="submitLoading" type="primary" @click="submitForm">确认导入</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script lang="ts" setup>
import {ClassStudentApi} from '@/api/nmt/classstudent'
import { getAccessToken, getTenantId } from '@/utils/auth'
import download from '@/utils/download'

defineOptions({ name: 'ClassStudentImportForm' })

const message = useMessage() // 消息弹窗
const dialogVisible = ref(false) // 弹窗的是否展示
const validateLoading = ref(false) // 验证数据的加载中
const submitLoading = ref(true) // 确定按钮加载中
const uploadRef = ref()
const validateUrl =
  import.meta.env.VITE_BASE_URL + import.meta.env.VITE_API_URL + '/nmt/class-student/import-validate'
const uploadHeaders = ref() // 上传 Header 头
const fileList = ref([]) // 文件列表

const validateShow = ref(false) // 验证显示
const validateFlag = ref(false) // 验证标识
const params = ref('') // 参数
let successData = []//验证成功数据
let failData = []//验证失败数据

/** 打开弹窗 */
const open = (paramTemp: string) => {
  dialogVisible.value = true
  fileList.value = []
  params.value = paramTemp
  resetForm()
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交表单 */
const submitForm = async () => {
  if(!validateFlag.value){
    message.error('请先验证数据')
    return
  }
  const data = JSON.stringify({ "params": params.value, "successData": successData })
  await ClassStudentApi.importData(data)
  dialogVisible.value = false
  // 发送操作成功的事件
  emits('success')
}


/** 验证数据 */
const validateForm = async () => {
  if (fileList.value.length == 0) {
    message.error('请上传文件')
    return
  }
  // 提交请求
  uploadHeaders.value = {
    Authorization: 'Bearer ' + getAccessToken(),
    'tenant-id': getTenantId()
  }
  uploadRef.value!.submit()
}

/** 提交校验 */
const emits = defineEmits(['success'])
const submitFormSuccess = (response: any) => {
  if (response.code !== 0) {
    message.error(response.msg)
    return
  }
  
  validateShow.value = true //已验证
  successData = response.data.successData
  failData = response.data.failData
  if(failData.length == 0){ //验证成功
    validateFlag.value = true
    submitLoading.value = false//导入按钮显示
  }

}

/** 上传错误提示 */
const submitFormError = (): void => {
  message.error('上传失败，请您重新上传！')
}

/** 重置表单 */
const resetForm = async (): Promise<void> => {
  successData = []//验证成功数据
  failData = []//验证失败数据
  validateShow.value = false //验证显示
  validateFlag.value = false //验证标识
  // 重置上传状态和文件
  await nextTick()
  uploadRef.value?.clearFiles()
}

/** 文件数超出提示 */
const handleExceed = (): void => {
  message.error('最多只能上传一个文件！')
}

/** 下载模板操作 */
const importTemplate = async () => {
  const res = await ClassStudentApi.importTemplate()
  download.excel(res, '用户导入模版.xls')
}

/** 导出错误信息*/
const outFail = async (failData: string,successData: string) => {
    const params = JSON.stringify({
        "failData": failData,
        "successData": successData
    })
  const res = await ClassStudentApi.outFail(params)
  download.excel(res, '用户导入错误信息.xls')
}

</script>
