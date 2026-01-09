<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      class="-mb-15px"
      :model="queryParams"
      ref="queryFormRef"
      :inline="true"
      label-width="68px"
    >
      <el-form-item label="编号" prop="number">
        <el-input
          v-model="queryParams.number"
          placeholder="请输入编号"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入名称"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="所属专业" prop="majorType">
        <el-select
          v-model="queryParams.majorType"
          placeholder="请选择所属专业"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.NMT_MAJOR_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="课程类型" prop="courseType">
        <el-select
          v-model="queryParams.courseType"
          placeholder="请选择课程类型"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.NMT_COURSE_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="课程属性" prop="courseProperty">
        <el-select
          v-model="queryParams.courseProperty"
          placeholder="请选择课程属性"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.NMT_COURSE_PROPERTY)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="授课年级" prop="grade">
        <el-input
          v-model="queryParams.grade"
          placeholder="请输入授课年级"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="授课学期" prop="term">
        <el-select
          v-model="queryParams.term"
          placeholder="请选择授课学期"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.NMT_TERM)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="授课教师" prop="teacherName">
        <el-input
          v-model="queryParams.teacherName"
          placeholder="请输入授课教师"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker
          v-model="queryParams.createTime"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="daterange"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          class="!w-220px"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
        <el-button
          type="primary"
          plain
          @click="openForm('create')"
          v-hasPermi="['nmt:course-info:create']"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
        <el-button
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
          v-hasPermi="['nmt:course-info:export']"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>
        <el-button
            type="danger"
            plain
            :disabled="isEmpty(checkedIds)"
            @click="handleDeleteBatch"
            v-hasPermi="['nmt:course-info:delete']"
        >
          <Icon icon="ep:delete" class="mr-5px" /> 批量删除
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table
        row-key="id"
        v-loading="loading"
        :data="list"
        :stripe="true"
        :show-overflow-tooltip="true"
        @selection-change="handleRowCheckboxChange"
    >
    <el-table-column type="selection" width="55" />
      <el-table-column label="主键ID" align="center" prop="id" />
      <el-table-column label="编号" align="center" prop="number" />
      <el-table-column label="名称" align="center" prop="name" />
      <el-table-column label="所属专业" align="center" prop="majorType">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.NMT_MAJOR_TYPE" :value="scope.row.majorType" />
        </template>
      </el-table-column>
      <el-table-column label="课程类型" align="center" prop="courseType">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.NMT_COURSE_TYPE" :value="scope.row.courseType" />
        </template>
      </el-table-column>
      <el-table-column label="总学时" align="center" prop="courseHour" />
      <el-table-column label="总学分" align="center" prop="courseScore" />
      <el-table-column label="授课年级" align="center" prop="grade" />
      <el-table-column label="授课学期" align="center" prop="term">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.NMT_TERM" :value="scope.row.term" />
        </template>
      </el-table-column>
      <el-table-column label="授课教师" align="center" prop="teacherName" />
      <el-table-column
        label="创建时间"
        align="center"
        prop="createTime"
        :formatter="dateFormatter"
        width="180px"
      />
      <el-table-column label="操作" align="center" min-width="150px">
        <template #default="scope">
         <div class="flex items-center justify-center">
          <el-button
              type="primary"
              link
              @click="openForm('update', scope.row.id)"
              v-hasPermi="['system:user:update']">
            <Icon icon="ep:edit" />修改
          </el-button>

          <el-dropdown
            @command="(command) => handleCommand(command, scope.row)"
            v-hasPermi="[
                    'nmt:course-info:update',
                    'nmt:course-info:update',
                    'nmt:course-info:delete'
                  ]"
          >
          <el-button type="primary" link><Icon icon="ep:d-arrow-right" /> 更多</el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item
                  command="courseOutline"
                  v-if="checkPermi(['nmt:course-info:update'])"
                >
                  <Icon icon="ep:grid" />课程大纲
                </el-dropdown-item>
                <el-dropdown-item
                        command="evaluatePlan"
                        v-if="checkPermi(['nmt:course-info:update'])"
                >
                    <Icon icon="ep:document" />考核计划
                </el-dropdown-item>
                <el-dropdown-item
                  command="teachClass"
                  v-if="checkPermi(['nmt:course-info:query'])"
                >
                  <Icon icon="ep:avatar" />班级成绩
                </el-dropdown-item>
                <el-dropdown-item
                  command="handleDelete"
                  v-if="checkPermi(['nmt:course-info:delete'])"
                >
                  <Icon icon="ep:delete" />删除
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
         </div>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <Pagination
      :total="total"
      v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </ContentWrap>

  <!-- 表单弹窗：添加/修改 -->
  <CourseInfoForm ref="formRef" @success="getList" />
</template>

<script setup lang="ts">
import { getIntDictOptions, DICT_TYPE } from '@/utils/dict'
import { isEmpty } from '@/utils/is'
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import { CourseInfoApi, CourseInfo } from '@/api/nmt/courseinfo'
import CourseInfoForm from './CourseInfoForm.vue'
import {checkPermi} from "@/utils/permission";

/** 课程信息 列表 */
defineOptions({ name: 'CourseInfo' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化
const { push } = useRouter()

const loading = ref(true) // 列表的加载中
const list = ref<CourseInfo[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  number: undefined,
  name: undefined,
  majorType: undefined,
  courseType: undefined,
  courseProperty: undefined,
  grade: undefined,
  term: undefined,
  teacherName: undefined,
  createTime: [],
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await CourseInfoApi.getCourseInfoPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value.resetFields()
  handleQuery()
}

/** 添加/修改操作 */
const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await CourseInfoApi.deleteCourseInfo(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 批量删除课程信息 */
const handleDeleteBatch = async () => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    await CourseInfoApi.deleteCourseInfoList(checkedIds.value);
    checkedIds.value = [];
    message.success(t('common.delSuccess'))
    await getList();
  } catch {}
}

const checkedIds = ref<number[]>([])
const handleRowCheckboxChange = (records: CourseInfo[]) => {
  checkedIds.value = records.map((item) => item.id!);
}

/** 导出按钮操作 */
const handleExport = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    exportLoading.value = true
    const data = await CourseInfoApi.exportCourseInfo(queryParams)
    download.excel(data, '课程信息.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 操作分发 */
const handleCommand = (command: string, row: CourseInfo) => {
    switch (command) {
        case 'handleDelete':
            handleDelete(row.id)
            break
        case 'courseOutline':
            courseOutline(row.id)
            break
        case 'evaluatePlan':
            evaluatePlan(row.id)
            break
        case 'teachClass':
          teachClass(row.id)
          break
        default:
            break
    }
}

/**
 * 课程大纲
 */
const courseOutline = (id: number) => {
  // 跳转页面并设置请求参数，使用 `query` 属性
  push('/course/course-detail/outline?id=' + id)
}

/**
 * 考核计划
 */
const evaluatePlan = (id: number) => {
    // 跳转页面并设置请求参数，使用 `query` 属性
    push('/course/course-detail/evaluate-plan?id=' + id)
}

/**
 * 教学班级
 */
const teachClass = (id: number) => {
  // 跳转页面并设置请求参数，使用 `query` 属性
  push('/course/course-detail/teach-class?id=' + id)
}

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>
