<template>
  <div class="phase-indicator" v-if="phase && phase !== 'done'">
    <div class="phase-bar">
      <el-progress
        :percentage="Math.round(progress * 100)"
        :stroke-width="4"
        :show-text="false"
        color="#409EFF"
      />
    </div>
    <div class="phase-info">
      <el-icon class="is-loading" v-if="phase !== 'done'"><Loading /></el-icon>
      <span class="phase-text">{{ label }}</span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { Loading } from '@element-plus/icons-vue'

const props = defineProps({
  phase: { type: String, default: '' },
  progress: { type: Number, default: 0 }
})

const label = computed(() => {
  const map = {
    orchestrating: '正在分析您的需求...',
    profiling: '正在了解您的学习情况...',
    planning: '正在为您规划学习路径...',
    generating: '正在生成学习资料...',
    synthesizing: '正在整理学习计划...'
  }
  return map[props.phase] || ''
})
</script>

<style scoped>
.phase-indicator {
  padding: 12px 20px;
  background: #f0f7ff;
  border-bottom: 1px solid #d9ecff;
}
.phase-bar {
  margin-bottom: 6px;
}
.phase-info {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #409EFF;
}
</style>
