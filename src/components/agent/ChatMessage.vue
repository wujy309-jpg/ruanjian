<template>
  <div class="chat-message" :class="[role]">
    <div class="message-avatar">
      <el-avatar :size="32" v-if="role === 'user'">
        <el-icon><UserFilled /></el-icon>
      </el-avatar>
      <el-avatar :size="32" v-else-if="role === 'assistant'" style="background: #409EFF">
        <el-icon><Cpu /></el-icon>
      </el-avatar>
      <el-avatar :size="32" v-else style="background: #67C23A">
        <el-icon><InfoFilled /></el-icon>
      </el-avatar>
    </div>
    <div class="message-body">
      <div class="message-content" v-if="content">
        <div v-if="role === 'user'" class="user-text">{{ content }}</div>
        <div v-else class="ai-text" v-html="renderedContent"></div>
      </div>
      <div class="message-options" v-if="options && options.length > 0">
        <div class="options-hint">快捷选择（也可以自由输入）：</div>
        <div class="options-list">
          <el-button
            v-for="(opt, idx) in options"
            :key="idx"
            size="small"
            type="primary"
            plain
            @click="$emit('select-option', opt)"
          >
            {{ opt }}
          </el-button>
        </div>
      </div>
      <div class="message-meta">
        <el-tag v-if="phase" size="small" type="info">{{ phaseLabel }}</el-tag>
        <span class="time">{{ formatTime(timestamp) }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { renderMarkdown } from '@/utils/markdown'

const props = defineProps({
  role: { type: String, default: 'user' },
  content: { type: String, default: '' },
  options: { type: Array, default: null },
  phase: { type: String, default: '' },
  timestamp: { type: Date, default: null }
})

defineEmits(['select-option'])

const renderedContent = computed(() => {
  if (!props.content) return ''
  return renderMarkdown(props.content)
})

const phaseLabel = computed(() => {
  const map = {
    profiling: '画像构建',
    planning: '路径规划',
    generating: '资源生成',
    done: '已完成'
  }
  return map[props.phase] || props.phase
})

function formatTime(date) {
  if (!date) return ''
  const d = new Date(date)
  return `${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}`
}
</script>

<style scoped>
.chat-message {
  display: flex;
  gap: 12px;
  padding: 12px 20px;
  max-width: 85%;
}
.chat-message.user {
  margin-left: auto;
  flex-direction: row-reverse;
}
.chat-message.assistant,
.chat-message.system {
  margin-right: auto;
}
.message-body {
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-width: 0;
}
.message-content {
  border-radius: 12px;
  padding: 10px 16px;
  line-height: 1.6;
  font-size: 14px;
}
.user .message-content {
  background: #409EFF;
  color: #fff;
}
.assistant .message-content {
  background: #f0f2f5;
  color: #303133;
}
.system .message-content {
  background: #ecf5ff;
  color: #606266;
  font-size: 13px;
}
.ai-text :deep(p) { margin: 0 0 8px; }
.ai-text :deep(p:last-child) { margin-bottom: 0; }
.ai-text :deep(pre) {
  background: #282c34;
  color: #abb2bf;
  border-radius: 8px;
  padding: 12px 16px;
  overflow-x: auto;
  margin: 8px 0;
}
.ai-text :deep(code) {
  font-family: 'Fira Code', monospace;
  font-size: 13px;
}
.ai-text :deep(table) {
  border-collapse: collapse;
  margin: 8px 0;
  width: 100%;
}
.ai-text :deep(th),
.ai-text :deep(td) {
  border: 1px solid #dcdfe6;
  padding: 6px 12px;
  text-align: left;
}
.ai-text :deep(th) {
  background: #f5f7fa;
}
.message-options {
  padding: 8px 16px;
}

.options-hint {
  font-size: 12px;
  color: #909399;
  margin-bottom: 8px;
}

.options-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.message-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 16px;
}
.time {
  font-size: 12px;
  color: #c0c4cc;
}
</style>
