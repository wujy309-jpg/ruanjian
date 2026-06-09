<template>
  <div class="path-timeline">
    <h4 class="section-title">学习路径</h4>
    <div class="path-meta" v-if="path">
      <span>共 {{ path.nodes?.length || 0 }} 个节点</span>
      <span class="status-tag">{{ statusLabel }}</span>
    </div>
    <div class="timeline" v-if="path?.nodes?.length">
      <div
        class="timeline-node"
        v-for="(node, idx) in path.nodes"
        :key="node.id"
        :class="{ active: selectedNodeId === node.id, completed: node.status === 'completed' }"
        @click="$emit('select-node', node)"
      >
        <div class="node-dot">
          <el-icon v-if="node.status === 'completed'"><Check /></el-icon>
          <span v-else>{{ idx + 1 }}</span>
        </div>
        <div class="node-content">
          <div class="node-title">{{ node.title }}</div>
          <div class="node-tags">
            <el-tag size="small" :type="nodeTypeColor(node.nodeType || node.type)">
              {{ nodeTypeLabel(node.nodeType || node.type) }}
            </el-tag>
            <span class="node-duration">{{ node.estimatedMinutes || node.estimated_minutes }} 分钟</span>
          </div>
          <div class="node-reason" v-if="node.reason">{{ node.reason }}</div>
          <div class="node-kps" v-if="node.knowledgePoints?.length">
            <el-tag
              v-for="kp in node.knowledgePoints"
              :key="kp.id"
              size="small"
              type="info"
              effect="plain"
            >
              {{ kp.name }}
            </el-tag>
          </div>
        </div>
      </div>
    </div>
    <el-empty v-else description="暂无学习路径" :image-size="80" />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { Check } from '@element-plus/icons-vue'

const props = defineProps({
  path: { type: Object, default: null },
  selectedNodeId: { type: [Number, String], default: null }
})

defineEmits(['select-node'])

const statusLabel = computed(() => {
  const map = { active: '进行中', completed: '已完成', abandoned: '已放弃' }
  return map[props.path?.status] || props.path?.status || ''
})

function nodeTypeLabel(type) {
  const map = { review: '复习', new_learn: '新学', reinforce: '强化' }
  return map[type] || type
}

function nodeTypeColor(type) {
  const map = { review: 'warning', new_learn: 'primary', reinforce: 'danger' }
  return map[type] || 'info'
}
</script>

<style scoped>
.section-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
}
.path-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  color: #909399;
  margin-bottom: 16px;
}
.status-tag {
  color: #409EFF;
}
.timeline {
  position: relative;
  padding-left: 24px;
}
.timeline::before {
  content: '';
  position: absolute;
  left: 11px;
  top: 0;
  bottom: 0;
  width: 2px;
  background: #e4e7ed;
}
.timeline-node {
  position: relative;
  padding: 8px 0 16px;
  cursor: pointer;
  transition: opacity 0.2s;
}
.timeline-node:hover {
  opacity: 0.8;
}
.timeline-node.completed .node-dot {
  background: #67C23A;
  border-color: #67C23A;
  color: #fff;
}
.timeline-node.active .node-dot {
  background: #409EFF;
  border-color: #409EFF;
  color: #fff;
}
.node-dot {
  position: absolute;
  left: -24px;
  top: 8px;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: #fff;
  border: 2px solid #dcdfe6;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: #909399;
  z-index: 1;
}
.node-content {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 10px 14px;
}
.timeline-node.active .node-content {
  background: #ecf5ff;
  border: 1px solid #d9ecff;
}
.node-title {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 6px;
}
.node-tags {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}
.node-duration {
  font-size: 12px;
  color: #909399;
}
.node-reason {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}
.node-kps {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  margin-top: 6px;
}
</style>
