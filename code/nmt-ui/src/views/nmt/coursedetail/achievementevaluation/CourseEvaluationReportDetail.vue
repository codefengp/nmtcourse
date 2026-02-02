<template>
  <div class="report-view" v-loading="loading">
    <div class="report-main">
      <div class="side-toolbar no-print">
        <div class="sticky-box">
          <el-button circle class="prime-btn" @click="emit('back')"><el-icon><Back /></el-icon></el-button>
          <el-button circle class="prime-btn"><el-icon><Edit /></el-icon></el-button>
          <el-button circle class="prime-btn" @click="handlePrint"><el-icon><Printer /></el-icon></el-button>
        </div>
      </div>

      <h2 class="title">{{ courseDetail.name }}课程达成评价报告</h2>

      <div class="section">
        <div class="hd"><i class="tag"></i>课程基本信息</div>
        <div class="bd grid5">
          <span>课程编码：{{ courseDetail.number || '-' }}</span>
          <span>课程名称：{{ courseDetail.name || '-' }}</span>
          <span>课程类别：{{ courseDetail.courseType || '-' }}</span>
          <span>课程性质：{{ courseDetail.courseProperty || '-' }}</span>
          <span>学时/学分：{{ courseDetail.courseHour }}学时/{{ courseDetail.courseScore }}学分</span>
          <span>开课学年：{{ courseDetail.grade || '-' }}</span>
          <span>开课学期：{{ courseDetail.term || '-' }}</span>
          <span>上课班级：{{ teachClassDetail.className || '-' }}</span>
          <span>考核人数：{{ teachClassDetail.totalNumber || 0 }}</span>
          <span>负责教师：{{ courseDetail.teacherName || '-' }}</span>
        </div>
      </div>

      <div class="section">
        <div class="hd"><i class="tag"></i>课程总评成绩</div>
        <div class="bd">
          <div class="score-row">
            <span>最高分：{{ scoreStats.maxScore }}</span>
            <span>最低分：{{ scoreStats.minScore }}</span>
            <span>平均分：{{ scoreStats.avgScore }}</span>
          </div>
          <div class="grid5 mt15">
            <span v-for="item in distribution" :key="item.range">
              {{ item.range }}成绩占比：{{ item.percent }}%
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { Back, Edit, Printer } from '@element-plus/icons-vue'
import { CourseInfoApi } from "@/api/nmt/courseinfo"
import { TeachClassApi } from '@/api/nmt/teachclass'
import { getDictLabel, DICT_TYPE } from "@/utils/dict"

const emit = defineEmits(['back'])
const props = defineProps<{ courseId: number; classId: number }>()
const loading = ref(false)

// 修正后的字段定义
const courseDetail = ref({
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
  teacherName: undefined
})

// 教学班信息（班级名称、人数）
const teachClassDetail = ref({
  className: '',
  totalNumber: 0
})

const scoreStats = reactive({ maxScore: '97.00', minScore: '53.12', avgScore: '80.30' })
const distribution = ref([
  { range: '90~100', percent: '6.6' }, { range: '80~89', percent: '51.2' },
  { range: '70~79', percent: '36.4' }, { range: '60~69', percent: '2.5' },
  { range: '≤59', percent: '3.3' }
])

const initData = async () => {
  loading.value = true
  try {
    // 1. 获取课程详情
    const data = await CourseInfoApi.getCourseInfo(props.courseId)
    data.majorType = getDictLabel(DICT_TYPE.NMT_MAJOR_TYPE, data.majorType)
    data.courseType = getDictLabel(DICT_TYPE.NMT_COURSE_TYPE, data.courseType)
    data.courseProperty = getDictLabel(DICT_TYPE.NMT_COURSE_PROPERTY, data.courseProperty)
    data.term = getDictLabel(DICT_TYPE.NMT_TERM, data.term)
    courseDetail.value = data

    // 2. 获取教学班信息
    const classData = await TeachClassApi.getTeachClass(props.classId)
    teachClassDetail.value = {
      className: classData.name,
      totalNumber: classData.totalNumber
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handlePrint = () => window.print()
onMounted(initData)
</script>

<style scoped>
/* 满屏布局 */
.report-view { padding: 15px 45px 15px 15px; background: #fff; min-height: 100vh; }
.report-main { width: 100%; position: relative; }

/* 侧边操作栏：绝对定位 + Sticky 解决不滚动且不重叠 */
.side-toolbar { position: absolute; right: -35px; top: 45px; height: calc(100% - 45px); }
.sticky-box { position: sticky; top: 70px; display: flex; flex-direction: column; gap: 12px; }

.prime-btn {
  margin: 0 !important; border: 1.5px solid #409eff !important; color: #409eff !important;
  box-shadow: 0 4px 10px rgba(64,158,255,0.15); background: #fff;
}

.title { text-align: center; margin-bottom: 30px; font-size: 22px; font-weight: bold; }
.section { margin-bottom: 20px; }
.hd { display: flex; align-items: center; font-weight: bold; margin-bottom: 10px; font-size: 15px; }
.tag { width: 12px; height: 12px; background: #409eff; margin-right: 8px; border-radius: 2px; }

/* 文本框样式 */
.bd {
  border: 1px solid #ebeef5; padding: 20px 25px; border-radius: 4px;
  line-height: 2; color: #444; font-size: 14px; width: 100%; box-sizing: border-box;
}

.grid5 { display: grid; grid-template-columns: repeat(5, 1fr); gap: 10px; }
.score-row { display: flex; gap: 80px; padding-bottom: 10px; border-bottom: 1px dashed #f0f2f5; margin-bottom: 10px; font-weight: bold; }
.mt15 { margin-top: 15px; }

@media print { .no-print { display: none; } .report-view { padding: 0; } }
</style>
