<template>
  <div class="resource-mindmap">
    <div v-if="mindmapData" class="mindmap-container">
      <div class="mindmap-node root">
        <div class="node-box root-box" @click="toggleRoot">
          <span class="toggle-icon" v-if="mindmapData.children?.length">{{ rootExpanded ? '−' : '+' }}</span>
          {{ mindmapData.topic }}
        </div>
        <div class="node-children" v-if="rootExpanded && mindmapData.children && mindmapData.children.length">
          <div class="mindmap-branch" v-for="(child, idx) in mindmapData.children" :key="idx">
            <div class="branch-line"></div>
            <MindmapNode :node="child" :depth="1" />
          </div>
        </div>
      </div>
    </div>
    <el-empty v-else description="暂无思维导图数据" :image-size="80" />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import MindmapNode from './MindmapNode.vue'

const props = defineProps({
  content: { type: String, default: '' }
})

const rootExpanded = ref(true)

const mindmapData = computed(() => {
  if (!props.content) return null
  try {
    return JSON.parse(props.content)
  } catch {
    return null
  }
})

function toggleRoot() {
  rootExpanded.value = !rootExpanded.value
}
</script>

<style scoped>
.resource-mindmap {
  padding: 20px;
  overflow-x: auto;
}
.mindmap-container {
  display: flex;
  justify-content: center;
  min-width: max-content;
}
.mindmap-node.root {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 24px;
}
.node-box {
  padding: 10px 20px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  text-align: center;
  white-space: nowrap;
}
.root-box {
  background: #409EFF;
  color: #fff;
  font-size: 16px;
  padding: 12px 28px;
  border-radius: 12px;
  cursor: pointer;
  user-select: none;
}
.toggle-icon {
  margin-right: 4px;
  font-weight: bold;
}
.node-children {
  display: flex;
  gap: 32px;
  position: relative;
}
.node-children::before {
  content: '';
  position: absolute;
  top: -12px;
  left: 0;
  right: 0;
  height: 2px;
  background: #c0c4cc;
}
.mindmap-branch {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
}
.branch-line {
  width: 2px;
  height: 12px;
  background: #c0c4cc;
}
</style>
