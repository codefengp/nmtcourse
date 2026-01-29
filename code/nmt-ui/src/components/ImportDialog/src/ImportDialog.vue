<template>
    <Dialog v-model="dialogVisible" :title="title" width="400">
        <el-upload
                ref="uploadRef"
                v-model:file-list="fileList"
                :action="validateUrl"
                :auto-upload="false"
                :headers="uploadHeaders"
                :data="{ bodyParams: JSON.stringify(buildBodyParams()) }"
                :limit="1"
                :on-error="submitFormError"
                :on-exceed="handleExceed"
                :on-success="submitFormSuccess"
                accept=".xlsx,.xls"
                drag
        >
            <Icon icon="ep:upload" />
            <div class="el-upload__text">
                将文件拖到此处，或<em>点击上传</em>
            </div>

            <template #tip>
                <div class="el-upload__tip text-center">
          <span style="font-size: 16px">
            仅允许导入 xls、xlsx 格式文件。
          </span>

                    <el-link
                            v-if="templateApi"
                            :underline="false"
                            style="font-size: 14px; vertical-align: baseline"
                            type="primary"
                            @click="importTemplate"
                    >
                        下载模板
                    </el-link>
                </div>

                <div class="el-upload__tip text-center">
          <span
                  v-if="validateShow && failData.length === 0"
                  style="font-size: 16px; color: green"
          >
            验证成功：{{ successData.length }} 条，
            验证失败：{{ failData.length }} 条
          </span>

                    <span
                            v-if="validateShow && failData.length > 0"
                            style="font-size: 16px; color: red"
                    >
            验证成功：{{ successData.length }} 条，
            验证失败：{{ failData.length }} 条

            <el-link
                    v-if="failExportApi"
                    :underline="false"
                    style="font-size: 14px; vertical-align: baseline"
                    type="primary"
                    @click="outFail"
            >
              下载错误信息
            </el-link>
          </span>
                </div>
            </template>
        </el-upload>

        <template #footer>
            <el-button type="warning" @click="validateForm">
                验证数据
            </el-button>

            <el-button
                    type="primary"
                    :disabled="submitLoading"
                    @click="submitForm"
            >
                确认导入
            </el-button>

            <el-button @click="dialogVisible = false">
                取 消
            </el-button>
        </template>
    </Dialog>
</template>

<script lang="ts" setup>
import { getAccessToken, getTenantId } from '@/utils/auth'
import download from '@/utils/download'

defineOptions({ name: 'ImportDialog' })

/* ======================= Props ======================= */
const props = defineProps<{
    title: string
    validateUrl: string
    submitApi: (data: any) => Promise<any>

    templateApi?: (data: any) => Promise<any>
    templateFileName?: string

    failExportApi?: (data: any) => Promise<any>
    failExportFileName?: string
}>()

/* ======================= Emits ======================= */
const emits = defineEmits<{
    (e: 'success'): void
}>()

/* ======================= State ======================= */
const message = useMessage()
const dialogVisible = ref(false)
const submitLoading = ref(true)

const uploadRef = ref()
const fileList = ref<any[]>([])
const uploadHeaders = ref<Record<string, string>>({})

const validateShow = ref(false)
const validateFlag = ref(false)

const extraParams = ref<any>(null)

let successData: any[] = []
let failData: any[] = []

/* ======================= 核心：统一参数结构 ======================= */
/**
 * 后端统一接收：
 * {
 *   bizParams: {
 *     params,
 *     successData,
 *     failData
 *   }
 * }
 */
const buildBodyParams = () => {
    return JSON.stringify({
            bizParams: extraParams.value,
            successData,
            failData
        })
}

/* ======================= Methods ======================= */

/** 打开弹窗 */
const open = (params?: any) => {
    dialogVisible.value = true
    fileList.value = []
    extraParams.value = params || null
    resetForm()
}
defineExpose({ open })

/** 验证数据 */
const validateForm = () => {
    if (fileList.value.length === 0) {
        message.error('请上传文件')
        return
    }

    uploadHeaders.value = {
        Authorization: 'Bearer ' + getAccessToken(),
        'tenant-id': getTenantId()
    }

    uploadRef.value?.submit()
}

/** 校验成功 */
const submitFormSuccess = (response: any) => {
    if (response.code !== 0) {
        message.error(response.msg || '校验失败')
        return
    }

    validateShow.value = true
    successData = response.data?.successData || []
    failData = response.data?.failData || []

    if (failData.length === 0) {
        validateFlag.value = true
        submitLoading.value = false
    }
}

/** 确认导入 */
const submitForm = async () => {
    if (!validateFlag.value) {
        message.error('请先验证数据')
        return
    }

    await props.submitApi(buildBodyParams())

    dialogVisible.value = false
    emits('success')
}

/** 下载模板 */
const importTemplate = async () => {
    if (!props.templateApi) return
    const res = await props.templateApi(buildBodyParams())
    download.excel(res, props.templateFileName || '导入模板.xls')
}

/** 导出错误 */
const outFail = async () => {
    if (!props.failExportApi) return
    const res = await props.failExportApi(buildBodyParams())
    download.excel(res, props.failExportFileName || '导入错误信息.xls')
}

/** 上传失败 */
const submitFormError = () => {
    message.error('上传失败，请重新上传')
}

/** 超出文件数 */
const handleExceed = () => {
    message.error('最多只能上传一个文件')
}

/** 重置 */
const resetForm = async () => {
    successData = []
    failData = []
    validateShow.value = false
    validateFlag.value = false
    submitLoading.value = true
    await nextTick()
    uploadRef.value?.clearFiles()
}
</script>
