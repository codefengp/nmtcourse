<template>
    <div class="report-view" v-loading="loading">
        <div class="report-main">
            <div class="side-toolbar no-print">
                <div class="sticky-box">
                    <el-button circle class="prime-btn" @click="emit('back')" title="返回">
                        <el-icon><Back /></el-icon>
                    </el-button>

                    <el-button
                            circle
                            class="prime-btn"
                            :type="isEdit ? 'success' : 'primary'"
                            @click="toggleEdit"
                            :title="isEdit ? '保存' : '编辑'"
                    >
                        <el-icon v-if="!isEdit"><Edit /></el-icon>
                        <el-icon v-else><Check /></el-icon>
                    </el-button>

                    <el-button circle class="prime-btn" @click="handlePrint" title="打印">
                        <el-icon><Printer /></el-icon>
                    </el-button>
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

            <div class="section">
                <div class="hd"><i class="tag"></i>课程目标达成评价结果</div>
                <div class="bd no-padding">
                    <el-table :data="achievementList" :span-method="objectSpanMethod" border header-cell-class-name="table-header">
                        <el-table-column label="课程目标" min-width="200">
                            <template #default="scope">{{ scope.row.objectiveName }}: {{ scope.row.content }}</template>
                        </el-table-column>
                        <el-table-column label="评价依据及方式" align="center" width="220">
                            <template #default="scope">{{ scope.row.modeName }}(占{{ formatDecimal(scope.row.weight) }}%)</template>
                        </el-table-column>
                        <el-table-column label="评价内容的目标分值" align="center" width="160">
                            <template #default="scope">{{ formatDecimal(scope.row.omscore) }}</template>
                        </el-table-column>
                        <el-table-column label="评价内容的平均成绩" align="center" width="160">
                            <template #default="scope">{{ formatDecimal(scope.row.avscore) }}</template>
                        </el-table-column>
                        <el-table-column label="课程目标达成度" align="center" width="140">
                            <template #default="scope">{{ formatDecimal(scope.row.totalObjRate) }}</template>
                        </el-table-column>
                    </el-table>
                </div>
            </div>

            <div class="section">
                <div class="hd"><i class="tag"></i>学生总体达成情况评价</div>
                <div class="bd">
                    <Echart ref="overallChartRef" :options="overallChartOption" height="450px" v-if="overallChartOption.xAxis" />
                    <el-input
                            type="textarea"
                            v-model="overallComment"
                            :rows="3"
                            :disabled="!isEdit"
                            placeholder="请输入总体达成情况分析"
                            class="mt15"
                    />
                </div>
            </div>

            <div class="section">
                <div class="hd">
                    <i class="tag"></i>学生个体达成情况评价
                    <el-switch
                            v-model="onlyShowUnreached"
                            active-text="只显示未达标学生"
                            style="margin-left: 20px"
                            @change="handleSwitchChange"
                    />
                </div>
                <div v-for="obj in uniqueObjectives" :key="obj.id" class="bd" style="margin-bottom: 25px">
                    <div class="chart-title">{{ obj.name }}学生个体达成情况分布图</div>
                    <div :ref="el => { if (el) scatterRefs[obj.id] = el }" style="width: 100%; height: 420px;"></div>
                    <el-input
                            type="textarea"
                            v-model="individualAnalysis[obj.id]"
                            :rows="3"
                            :disabled="!isEdit"
                            class="mt15"
                    />
                </div>
            </div>

            <div class="section">
                <div class="hd"><i class="tag"></i>存在问题</div>
                <div class="simple-bd">
                    <el-input
                            type="textarea"
                            v-model="problemAnalysis"
                            :rows="4"
                            :disabled="!isEdit"
                            placeholder="尚未填写存在问题"
                    />
                </div>
            </div>

            <div class="section">
                <div class="hd"><i class="tag"></i>课程教学质量的持续改进</div>
                <div class="simple-bd">
                    <el-input
                            type="textarea"
                            v-model="improvementPlan"
                            :rows="4"
                            :disabled="!isEdit"
                            placeholder="尚未填写持续改进计划"
                    />
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive, nextTick, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'
import { Back, Edit, Printer, Check } from '@element-plus/icons-vue'
import { CourseInfoApi } from "@/api/nmt/courseinfo"
import { TeachClassApi } from '@/api/nmt/teachclass'
import {AchievementEvaluationApi, AchievementEvaluationEx} from '@/api/nmt/achievementevaluation'
import { getDictLabel, DICT_TYPE } from "@/utils/dict"
import { ElMessage } from 'element-plus'
import {ObjectiveEvaluationApi} from "@/api/nmt/objectiveevaluation";
import download from "@/utils/download";

const emit = defineEmits(['back'])
const props = defineProps<{ courseId: number; classId: number }>()
const loading = ref(false)
const isEdit = ref(false)

// 业务变量
const reportId = ref<number>()
const courseDetail = ref<any>({})
const teachClassDetail = ref<any>({ className: '', totalNumber: 0 })
const scoreStats = reactive({ maxScore: '0.00', minScore: '0.00', avgScore: '0.00' })
const distribution = ref<any[]>([])
const achievementList = ref<any[]>([])
const spanMap = ref<number[]>([])
const rawStuObjRateList = ref<any[]>([])
const uniqueObjectives = ref<any[]>([])
const overallChartOption = ref<any>({})

// 输入框对应字段
const overallComment = ref('')
const problemAnalysis = ref('')
const improvementPlan = ref('')
const individualAnalysis = reactive<Record<string, string>>({})

const onlyShowUnreached = ref(false)
const scatterRefs = reactive<Record<string, any>>({})

// --- 关键修改：定义引用变量 ---
const overallChartRef = ref<any>(null) // 总体图组件引用
let scatterInstances: echarts.ECharts[] = [] // 个体图实例数组

const formatDecimal = (val: any) => (val !== null && val !== undefined) ? Number(val).toFixed(2) : '0.00'

/** 获取课程基本信息 */
const getBaseInfo = async () => {
  const data = await CourseInfoApi.getCourseInfo(props.courseId)
  data.majorType = getDictLabel(DICT_TYPE.NMT_MAJOR_TYPE, data.majorType)
  data.courseType = getDictLabel(DICT_TYPE.NMT_COURSE_TYPE, data.courseType)
  data.courseProperty = getDictLabel(DICT_TYPE.NMT_COURSE_PROPERTY, data.courseProperty)
  data.term = getDictLabel(DICT_TYPE.NMT_TERM, data.term)
  courseDetail.value = data
  const classData = await TeachClassApi.getTeachClass(props.classId)
  teachClassDetail.value = { className: classData.name, totalNumber: classData.totalNumber }
}

/** 获取总评成绩统计 */
const getScoreInfo = async () => {
  const data = await AchievementEvaluationApi.getCourseOverallScore(props.courseId, props.classId)
  scoreStats.maxScore = formatDecimal(data.max)
  scoreStats.minScore = formatDecimal(data.min)
  scoreStats.avgScore = formatDecimal(data.avg)
  distribution.value = ['90-100', '80-89', '70-79', '60-69', '≤59'].map(range => ({
    range, percent: data[range] || '0.0'
  }))
}

/** 获取核心达成度数据 */
const getAchievementData = async () => {
  const achRes = await AchievementEvaluationApi.getObjectiveAchievementEvaluation(props.courseId, props.classId)
  achievementList.value = achRes.courseObjRateList || []
  rawStuObjRateList.value = achRes.stuObjRateList || []

  if(achRes.achiEval){
    var achiEval = achRes.achiEval;
    reportId.value = achiEval.id
    problemAnalysis.value = achiEval.problemAnalysis || ''
    improvementPlan.value = achiEval.improvementPlan || ''
    overallComment.value = achiEval.overallComment || ''
    if (reportId.value) {
      const evaluationRes = await ObjectiveEvaluationApi.getByAchievementEvaluation(reportId.value)
      if (Array.isArray(evaluationRes) && evaluationRes.length > 0) {
        evaluationRes.forEach(item => {
          if (item.objectiveId && item.comment) {
            individualAnalysis[item.objectiveId] = item.comment
          }
        })
      }
    }
  }

  const spans: number[] = []
  let pos = 0
  achievementList.value.forEach((item, i) => {
    if (i === 0) { spans.push(1); pos = 0 }
    else if (item.objectiveId === achievementList.value[i - 1].objectiveId) {
      spans[pos] += 1; spans.push(0)
    } else { spans.push(1); pos = i }
  })
  spanMap.value = spans

  uniqueObjectives.value = achievementList.value.filter((item, index, self) =>
    index === self.findIndex((t) => t.objectiveId === item.objectiveId)
  ).map(i => ({
    id: i.objectiveId,
    name: i.objectiveName,
    expect: Number(i.expectValue || 0),
    totalRate: Number(i.totalObjRate || 0)
  }))

  initOverallChart()

  if (!overallComment.value) {
    const failed = uniqueObjectives.value.find(o => o.totalRate < o.expect)
    overallComment.value = failed ? `分析显示，${failed.name}达成度为${failed.totalRate.toFixed(2)}，未达到预期。` : '各指标达成度均符合预期要求。'
  }

  initScatterCharts()
}

/** 编辑/保存逻辑 */
const toggleEdit = async () => {
  if (isEdit.value) {
    try {
      loading.value = true
      const payload: AchievementEvaluationEx = {
        id: reportId.value,
        courseId: props.courseId,
        classId: props.classId,
        overallComment: overallComment.value,
        problemAnalysis: problemAnalysis.value,
        improvementPlan: improvementPlan.value,
        objectiveEvaluations: JSON.stringify(individualAnalysis)
      }
      const achiEvalId = await AchievementEvaluationApi.saveAchievementEvaluation(payload)
      if (achiEvalId) reportId.value = achiEvalId
      ElMessage.success('保存成功')
      isEdit.value = false
    } catch (e) {
      console.error(e)
    } finally {
      loading.value = false
    }
  } else {
    isEdit.value = true
  }
}

/** 柱状图初始化 */
const initOverallChart = () => {
  overallChartOption.value = {
    tooltip: { trigger: 'axis' },
    legend: { bottom: 0 },
    grid: { left: '3%', right: '4%', bottom: '15%', top: '10%', containLabel: true },
    xAxis: { type: 'category', data: uniqueObjectives.value.map(o => o.name) },
    yAxis: { type: 'value', min: 0, max: 1 },
    series: [
      {
        name: '达成值', type: 'bar', barWidth: 35,
        data: uniqueObjectives.value.map(o => o.totalRate),
        itemStyle: { color: '#15c3c6' },
        label: { show: true, position: 'top', formatter: (params: any) => params.value.toFixed(2) }
      },
      {
        name: '期望值', type: 'bar', barWidth: 35,
        data: uniqueObjectives.value.map(o => o.expect),
        itemStyle: { color: '#b2a5e7' },
        label: { show: true, position: 'top', formatter: (params: any) => params.value.toFixed(2) }
      }
    ]
  }
}

/** 散点图初始化 */
const initScatterCharts = async () => {
  await nextTick()
  scatterInstances.forEach(ins => ins.dispose())
  scatterInstances = []
  uniqueObjectives.value.forEach(obj => {
    const dom = scatterRefs[obj.id]
    if (!dom) return
    const chart = echarts.init(dom)
    const allStu = rawStuObjRateList.value.filter(s => String(s.objectiveId) === String(obj.id))
    const displayList = onlyShowUnreached.value ? allStu.filter(s => Number(s.objRate) < obj.expect) : allStu
    chart.setOption({
      tooltip: { trigger: 'item', formatter: (p: any) => `<b>${p.data[0]}</b><br/>达成度: ${p.data[1].toFixed(2)}` },
      grid: { top: '15%', bottom: '25%', left: '5%', right: '5%', containLabel: true },
      xAxis: {
        type: 'category',
        data: displayList.map(s => s.studentName),
        axisLabel: { rotate: 45, interval: 0, fontSize: 11, color: '#15c3c6' },
        axisTick: { alignWithLabel: true }
      },
      yAxis: { type: 'value', min: 0, max: 1, splitLine: { lineStyle: { type: 'dashed', color: '#eee' } } },
      series: [{
        type: 'scatter', symbolSize: 12,
        data: displayList.map(s => [s.studentName, Number(s.objRate)]),
        itemStyle: { color: '#15c3c6', shadowBlur: 5, shadowColor: 'rgba(21, 195, 198, 0.3)' },
        markLine: {
          silent: true, symbol: 'none',
          label: { position: 'end', formatter: `期望值: ${obj.expect.toFixed(2)}`, color: '#F56C6C' },
          data: [{ yAxis: obj.expect, lineStyle: { color: '#F56C6C', type: 'dashed', width: 2 } }]
        }
      }]
    })
    scatterInstances.push(chart)

    if (!individualAnalysis[obj.id]) {
      const unreachedLen = allStu.filter(s => Number(s.objRate) < obj.expect).length
      const passRate = allStu.length ? ((allStu.length - unreachedLen) / allStu.length * 100).toFixed(2) : '0'
      individualAnalysis[obj.id] = `该目标总体达成率为${passRate}%；未达标人数：${unreachedLen}人。`
    }
  })
}

const objectSpanMethod = ({ rowIndex, columnIndex }: any) => {
  if (columnIndex === 0 || columnIndex === 4) {
    const _row = spanMap.value[rowIndex]; return _row > 0 ? { rowspan: _row, colspan: 1 } : { rowspan: 0, colspan: 0 }
  }
}
const handleSwitchChange = () => initScatterCharts()

/**
 * 核心修改：打印/导出逻辑
 */
const handlePrint = async () => {
  loading.value = true;
  try {
    const chartImages: Record<string, string> = {};

    // 1. 获取总体图 (使用 ref 获取组件内部 echarts 实例)
    // 注意：这里尝试获取组件内部的 DOM 并转换成实例
    const overallEl = overallChartRef.value?.$el || document.querySelector('.section .bd .echarts');
    if (overallEl) {
      const instance = echarts.getInstanceByDom(overallEl as HTMLElement)
        || echarts.getInstanceByDom(overallEl.querySelector('div') as HTMLElement);
      if (instance) {
        chartImages['overallChart'] = instance.getDataURL({
          type: 'jpeg', pixelRatio: 1.5, backgroundColor: '#fff'
        });
      }
    }

    // 2. 获取散点图 (利用已保存的实例数组)
    scatterInstances.forEach((instance, index) => {
      const objId = uniqueObjectives.value[index].id;
      chartImages[`objChart_${objId}`] = instance.getDataURL({
        type: 'jpeg', pixelRatio: 1.5, backgroundColor: '#fff'
      });
    });

    // 3. 发送请求
    const params = {
      courseId: props.courseId,
      classId: props.classId,
      chartImages: chartImages
    };

    // 调试打印：如果这里没东西，说明前端没抓到图
    console.log("准备发送的图片参数:", chartImages);

    const res = await AchievementEvaluationApi.exportReport(params);
    download.word(res, `${courseDetail.value.name}-${teachClassDetail.value.className}-评价报告`);
    ElMessage.success('导出成功');
  } catch (e) {
    console.error('导出失败:', e);
    ElMessage.error('导出失败');
  } finally {
    loading.value = false;
  }
};

onMounted(async () => {
  loading.value = true
  try { await getBaseInfo(); await getScoreInfo(); await getAchievementData(); }
  finally { loading.value = false }
  window.addEventListener('resize', () => {
    // 缩放时重置所有实例
    const overallEl = overallChartRef.value?.$el;
    if (overallEl) echarts.getInstanceByDom(overallEl)?.resize();
    scatterInstances.forEach(i => i.resize())
  })
})

onBeforeUnmount(() => {
  scatterInstances.forEach(ins => ins.dispose())
})
</script>

<style scoped>

.report-view { padding: 20px 0; background: #f0f2f5; min-height: 100vh; }
.report-main { width: 95%; max-width: 1400px; margin: 0 auto; position: relative; background: #fff; padding: 40px; box-shadow: 0 2px 20px rgba(0,0,0,0.05); border-radius: 8px; }

/* 需求3：右侧三个按钮竖着对齐且随下滑不动 (使用 absolute + sticky) */
.side-toolbar { position: absolute; right: 10px; top: 40px; height: 100%; }
.sticky-box { position: sticky; top: 100px; display: flex; flex-direction: column; align-items: center; gap: 15px; }

.prime-btn { border: 2px solid #409eff !important; color: #409eff !important; background: #fff; font-size: 20px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); transition: all 0.3s; }
.prime-btn:hover { transform: scale(1.1); background: #409eff !important; color: #fff !important; }

.title { text-align: center; margin-bottom: 40px; font-size: 26px; font-weight: bold; color: #1a1a1a; letter-spacing: 1px; }
.section { margin-bottom: 40px; }
.hd { display: flex; align-items: center; font-weight: bold; margin-bottom: 15px; font-size: 18px; color: #303133; }
.tag { width: 6px; height: 20px; background: #409eff; margin-right: 12px; border-radius: 3px; }

.bd { border: 1px solid #e4e7ed; padding: 30px; border-radius: 8px; line-height: 2; color: #606266; font-size: 15px; background: #fff; }

/* 需求：“只要一个框线条” */
.simple-bd { border: 1px solid #dcdfe6; border-radius: 4px; overflow: hidden; }
:deep(.simple-bd .el-textarea__inner) { border: none !important; box-shadow: none !important; background: transparent; padding: 15px; }

.no-padding { padding: 0; overflow: hidden; }
.grid5 { display: grid; grid-template-columns: repeat(5, 1fr); gap: 15px; }
.score-row { display: flex; gap: 100px; padding-bottom: 15px; border-bottom: 1px dashed #ebeef5; font-weight: bold; margin-bottom: 15px; color: #303133; }
.mt15 { margin-top: 20px; }
.chart-title { text-align: center; font-weight: bold; margin-bottom: 20px; color: #303133; font-size: 16px; }

/* 需求4：不可编辑时的样式 */
:deep(.el-textarea.is-disabled .el-textarea__inner) { color: #555; background-color: #fafafa; cursor: not-allowed; }
:deep(.table-header) { background-color: #f5f7fa !important; color: #303133; font-weight: bold; height: 50px; }

@media print {
    .no-print { display: none; }
    .report-view { padding: 0; background: #fff; }
    .report-main { width: 100%; max-width: 100%; box-shadow: none; padding: 0; }
    .side-toolbar { display: none; }
}
</style>
