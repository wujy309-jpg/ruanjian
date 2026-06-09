<template>
  <div class="profile-weakpoints">
    <h4 class="section-title">薄弱知识点</h4>
    <div class="tags" v-if="weakPoints.length > 0">
      <el-tag
        v-for="(item, idx) in weakPoints"
        :key="idx"
        :color="getColor(item.level)"
        effect="dark"
        class="weak-tag"
        @click="$emit('focus-topic', item.topic)"
      >
        {{ item.topic }}
      </el-tag>
    </div>
    <el-empty v-else description="暂无薄弱点" :image-size="60" />
  </div>
</template>

<script setup>
defineProps({
  weakPoints: { type: Array, default: () => [] }
})

defineEmits(['focus-topic'])

function getColor(level) {
  if (level <= 0.2) return '#F56C6C'
  if (level <= 0.4) return '#E6A23C'
  if (level <= 0.6) return '#E6A23C'
  return '#409EFF'
}
</script>

<style scoped>
.section-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
}
.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.weak-tag {
  cursor: pointer;
  transition: transform 0.2s;
}
.weak-tag:hover {
  transform: scale(1.05);
}
</style>
