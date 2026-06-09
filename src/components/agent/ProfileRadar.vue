<template>
  <div class="profile-radar" v-if="indicators.length > 0">
    <div ref="chartRef" class="chart"></div>
  </div>
  <div class="profile-empty" v-else>
    <el-empty description="暂无画像数据" :image-size="80" />
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({
  knowledgeMap: { type: Object, default: () => ({}) }
})

const chartRef = ref(null)
let chart = null

const indicators = computed(() => {
  return Object.entries(props.knowledgeMap || {}).map(([key, val]) => ({
    name: val.label || key,
    max: 1
  }))
})

const values = computed(() => {
  return Object.values(props.knowledgeMap || {}).map(v => v.level || 0)
})

function initChart() {
  if (!chartRef.value || indicators.value.length === 0) return
  if (chart) chart.dispose()

  chart = echarts.init(chartRef.value)
  chart.setOption({
    radar: {
      center: ['50%', '50%'],
      radius: '65%',
      indicator: indicators.value,
      axisName: { color: '#606266', fontSize: 11 }
    },
    series: [{
      type: 'radar',
      data: [{
        value: values.value,
        name: '知识掌握度',
        areaStyle: { color: 'rgba(64, 158, 255, 0.2)' },
        lineStyle: { color: '#409EFF', width: 2 },
        itemStyle: { color: '#409EFF' }
      }]
    }]
  })
}

watch(() => props.knowledgeMap, initChart, { deep: true })

onMounted(() => {
  setTimeout(initChart, 100)
})

onUnmounted(() => {
  if (chart) chart.dispose()
})
</script>

<style scoped>
.profile-radar {
  width: 100%;
}
.chart {
  width: 100%;
  height: 280px;
}
.profile-empty {
  padding: 20px 0;
}
</style>
