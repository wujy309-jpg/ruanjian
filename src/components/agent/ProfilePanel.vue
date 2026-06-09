<template>
  <div class="profile-panel" :class="{ collapsed: !visible }">
    <div class="panel-toggle" @click="visible = !visible">
      <el-icon><component :is="visible ? 'DArrowRight' : 'DArrowLeft'" /></el-icon>
      <span v-if="!visible">画像</span>
    </div>
    <div class="panel-content" v-if="visible">
      <div class="panel-header">
        <h3>学习画像</h3>
        <el-button text size="small" @click="$emit('close')">
          <el-icon><Close /></el-icon>
        </el-button>
      </div>

      <div class="panel-section" v-if="profile">
        <div class="info-card" v-if="profile.dimensions?.cognitive_style">
          <div class="info-item">
            <span class="label">学习风格</span>
            <span class="value">{{ styleLabel }}</span>
          </div>
          <div class="info-item">
            <span class="label">偏好时长</span>
            <span class="value">{{ profile.dimensions.cognitive_style.avg_session_min || '-' }} 分钟</span>
          </div>
          <div class="info-item">
            <span class="label">最佳时段</span>
            <span class="value">{{ timeLabel }}</span>
          </div>
        </div>

        <div class="info-card" v-if="profile.dimensions?.learning_goal">
          <div class="info-item">
            <span class="label">学习目标</span>
            <span class="value">{{ profile.dimensions.learning_goal.target || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="label">每周学时</span>
            <span class="value">{{ profile.dimensions.learning_goal.weekly_hours || '-' }} 小时</span>
          </div>
        </div>
      </div>

      <div class="panel-section">
        <ProfileRadar :knowledge-map="profile?.dimensions?.knowledge_map || {}" />
      </div>

      <div class="panel-section">
        <ProfileWeakpoints
          :weak-points="profile?.dimensions?.weak_points || []"
          @focus-topic="$emit('focus-topic', $event)"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { DArrowRight, DArrowLeft, Close } from '@element-plus/icons-vue'
import ProfileRadar from './ProfileRadar.vue'
import ProfileWeakpoints from './ProfileWeakpoints.vue'

const props = defineProps({
  profile: { type: Object, default: null }
})

defineEmits(['close', 'focus-topic'])

const visible = ref(false)

watch(() => props.profile, (val) => {
  if (val) visible.value = true
})

const styleLabel = computed(() => {
  const type = props.profile?.dimensions?.cognitive_style?.type
  if (type === 'depth_first') return '深度优先型'
  if (type === 'breadth_first') return '广度优先型'
  return type || '-'
})

const timeLabel = computed(() => {
  const t = props.profile?.dimensions?.cognitive_style?.best_time
  const map = { morning: '上午', afternoon: '下午', evening: '晚上' }
  return map[t] || t || '-'
})
</script>

<style scoped>
.profile-panel {
  position: relative;
  width: 340px;
  background: #fff;
  border-left: 1px solid #e4e7ed;
  display: flex;
  flex-direction: column;
  transition: width 0.3s;
  flex-shrink: 0;
}
.profile-panel.collapsed {
  width: 40px;
}
.panel-toggle {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  padding: 12px 0;
  cursor: pointer;
  color: #909399;
  font-size: 13px;
  border-bottom: 1px solid #e4e7ed;
}
.collapsed .panel-toggle {
  writing-mode: vertical-lr;
  height: 100%;
  border-bottom: none;
}
.panel-content {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}
.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.panel-header h3 {
  font-size: 16px;
  color: #303133;
  margin: 0;
}
.panel-section {
  margin-bottom: 20px;
}
.info-card {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 12px 16px;
  margin-bottom: 12px;
}
.info-item {
  display: flex;
  justify-content: space-between;
  padding: 4px 0;
  font-size: 13px;
}
.info-item .label {
  color: #909399;
}
.info-item .value {
  color: #303133;
  font-weight: 500;
}
</style>
