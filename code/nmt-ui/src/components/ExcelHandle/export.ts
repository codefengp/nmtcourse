import {ref} from 'vue'
import {ElMessage} from 'element-plus'
import download from "@/utils/download";
export function useExcelExport() {
    const exportLoading = ref(false)

    /**
     * @param apiFn 接口函数，例如 StudentAchievementApi.exportExcelData
     * @param bizParams 业务参数对象
     * @param fileName 文件名
     */
    const exportExcel = async (
        apiFn: (data: any) => Promise<any>,
        bizParams: Record<string, any>,
        fileName: string
    ) => {
        // 1. 开启 loading
        exportLoading.value = true

        try {
            // 2. 构造参数（对标 ImportDialog 逻辑）
            const requestPayload = {
                bizParams: bizParams,
                successData: [],
                failData: []
            }

            // 3. 调用接口并等待返回文件流
            // 注意：这里传的是对象还是字符串，取决于你后端 API 的定义
            // 按照你之前的逻辑，这里通常需要 JSON.stringify
            const res = await apiFn(JSON.stringify(requestPayload))

            if (res) {
                download.excel(res, fileName)
                ElMessage.success('导出成功')
            }
        } catch (error: any) {
            console.error('导出失败详情:', error)
            ElMessage.error('导出失败，请联系管理员')
        } finally {
            // 4. 无论成功失败，关闭 loading
            exportLoading.value = false
        }
    }

    return {
        exportLoading,
        exportExcel
    }
}
