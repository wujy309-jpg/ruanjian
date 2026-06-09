<template>
  <div class="learning-paths-page">
    <div class="navbar">
      <div class="logo" @click="$router.push('/home')">
        <img src="../assets/logo.svg" alt="logo" class="logo-img" />
        <span class="title">AI云学智训平台</span>
      </div>
      <div class="nav-actions">
        <el-button text @click="$router.push('/home')">首页</el-button>
        <el-button text @click="$router.push('/agent-chat')">AI 学习助手</el-button>
      </div>
    </div>

    <div class="main-container">
      <div class="page-header">
        <h1>学习路径</h1>
        <p>多智能体协同规划的个性化学习路径</p>
      </div>

      <div class="path-list" v-if="paths.length > 0">
        <div
          class="path-card"
          v-for="path in paths"
          :key="path.id"
          @click="goToPath(path)"
        >
          <div class="path-card-header">
            <el-tag :type="statusColor(path.status)">{{ statusLabel(path.status) }}</el-tag>
            <span class="path-date">{{ formatDate(path.createdAt) }}</span>
          </div>
          <h3>{{ path.title || '学习路径 #' + path.id }}</h3>
          <p class="path-desc" v-if="path.courseName">课程：{{ path.courseName }}</p>
          <div class="path-stats">
            <span><el-icon><List /></el-icon> {{ path.nodeCount || path.nodes?.length || 0 }} 个节点</span>
            <span><el-icon><Clock /></el-icon> {{ calcTotalMinutes(path) }} 分钟</span>
          </div>
        </div>
      </div>

      <el-empty v-else description="暂无学习路径，去 AI 学习助手中生成吧">
        <el-button type="primary" @click="$router.push('/agent-chat')">开始对话</el-button>
      </el-empty>

      <div class="path-detail" v-if="selectedPath">
        <div class="detail-header">
          <h2>{{ selectedPath.title || '学习路径详情' }}</h2>
          <el-button text @click="selectedPath = null">返回列表</el-button>
        </div>
        <PathTimeline
          :path="selectedPath"
          :selected-node-id="selectedNodeId"
          @select-node="handleSelectNode"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { List, Clock } from '@element-plus/icons-vue'
import { fetchLearningPathsByUser, fetchLearningPath, fetchNodeResources } from '@/api/agent'
import { useAgentStore } from '@/stores/agent'
import PathTimeline from '@/components/agent/PathTimeline.vue'

const store = useAgentStore()
const paths = ref([])
const selectedPath = ref(null)
const selectedNodeId = ref(null)

async function loadPaths() {
  try {
    const data = await fetchLearningPathsByUser(store.userId)
    paths.value = data || []
  } catch (e) {
    console.error('加载学习路径失败:', e)
  }
}

async function goToPath(path) {
  try {
    const detail = await fetchLearningPath(path.id)
    selectedPath.value = detail
    if (detail?.nodes?.length) {
      selectedNodeId.value = detail.nodes[0].id
    }
  } catch (e) {
    console.error('加载路径详情失败:', e)
  }
}

async function handleSelectNode(node) {
  selectedNodeId.value = node.id
  try {
    const resources = await fetchNodeResources(node.id)
    store.setNodeResources(resources || [])
    store.selectNode(node.id)
    store.setPath(selectedPath.value)
  } catch (e) {
    console.error('加载节点资源失败:', e)
  }
}

function statusLabel(status) {
  const map = { active: '进行中', completed: '已完成', abandoned: '已放弃' }
  return map[status] || status || '未知'
}

function statusColor(status) {
  const map = { active: 'primary', completed: 'success', abandoned: 'info' }
  return map[status] || 'info'
}

function formatDate(date) {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN')
}

function calcTotalMinutes(path) {
  if (!path.nodes?.length) return 0
  return path.nodes.reduce((sum, n) => sum + (n.estimatedMinutes || n.estimated_minutes || 0), 0)
}

onMounted(loadPaths)
</script>

<style scoped>
.learning-paths-page {
  min-height: 100vh;
  background: #f5f7fa;
}
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  height: 56px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
}
.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}
.logo-img {
  width: 30px;
  height: 30px;
}
.title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}
.nav-actions {
  display: flex;
  gap: 8px;
}
.main-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
}
.page-header {
  margin-bottom: 30px;
}
.page-header h1 {
  font-size: 24px;
  color: #303133;
  margin-bottom: 8px;
}
.page-header p {
  font-size: 14px;
  color: #909399;
}
.path-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}
.path-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}
.path-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
}
.path-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.path-date {
  font-size: 12px;
  color: #c0c4cc;
}
.path-card h3 {
  font-size: 16px;
  color: #303133;
  margin-bottom: 8px;
}
.path-desc {
  font-size: 13px;
  color: #909399;
  margin-bottom: 12px;
}
.path-stats {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #606266;
}
.path-stats span {
  display: flex;
  align-items: center;
  gap: 4px;
}
.path-detail {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
}
.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.detail-header h2 {
  font-size: 18px;
  color: #303133;
  margin: 0;
}
</style>
