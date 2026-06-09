<template>
  <div class="mindmap-node" :class="'depth-' + depth">
    <div class="node-box" :class="'box-depth-' + Math.min(depth, 3)" @click="toggle">
      <span class="toggle-icon" v-if="node.children?.length">{{ expanded ? '−' : '+' }}</span>
      {{ node.topic }}
    </div>
    <div class="node-children" v-if="expanded && node.children && node.children.length">
      <div class="mindmap-branch" v-for="(child, idx) in node.children" :key="idx">
        <div class="branch-line"></div>
        <MindmapNode :node="child" :depth="depth + 1" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

defineProps({
  node: { type: Object, required: true },
  depth: { type: Number, default: 1 }
})

const expanded = ref(true)

function toggle() {
  expanded.value = !expanded.value
}
</script>

<style scoped>
.mindmap-node {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}
.node-box {
  padding: 6px 14px;
  border-radius: 6px;
  font-size: 13px;
  text-align: center;
  white-space: nowrap;
  cursor: pointer;
  user-select: none;
}
.toggle-icon {
  margin-right: 2px;
  font-weight: bold;
}
.box-depth-1 {
  background: #ecf5ff;
  color: #409EFF;
  border: 1px solid #d9ecff;
}
.box-depth-2 {
  background: #f0f9eb;
  color: #67C23A;
  border: 1px solid #e1f3d8;
}
.box-depth-3 {
  background: #fdf6ec;
  color: #E6A23C;
  border: 1px solid #faecd8;
}
.node-children {
  display: flex;
  gap: 16px;
  position: relative;
}
.node-children::before {
  content: '';
  position: absolute;
  top: -4px;
  left: 0;
  right: 0;
  height: 1px;
  background: #dcdfe6;
}
.mindmap-branch {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
}
.branch-line {
  width: 1px;
  height: 4px;
  background: #dcdfe6;
}
</style>
