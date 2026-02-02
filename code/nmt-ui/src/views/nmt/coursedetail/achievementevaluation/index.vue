<template>
  <!-- 列表 -->
  <CourseEvaluationReportList
    v-if="mode === 'list'"
    ref="listRef"
    @view="openDetail"
  />

  <!-- 详情 -->
  <CourseEvaluationReportDetail
    v-else
    :course-id="courseId"
    :class-id="classId"
    @back="backToList"
  />
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue'
import CourseEvaluationReportList from './CourseEvaluationReportList.vue'
import CourseEvaluationReportDetail from './CourseEvaluationReportDetail.vue'

const mode = ref<'list' | 'detail'>('list') // 当前显示模式，list 或 detail
const courseId = ref<number>() // 当前选中的课程ID
const classId = ref<number>()  // 当前选中的班级ID
const listRef = ref()          // 列表组件引用，用于返回刷新

// 打开详情
function openDetail(payload: { courseId: number; classId: number }) {
  courseId.value = payload.courseId
  classId.value = payload.classId
  mode.value = 'detail'
}

// 返回列表
function backToList() {
  mode.value = 'list'
  // 返回列表时，主动刷新接口
  nextTick(() => {
    listRef.value?.getList()
  })
}
</script>
