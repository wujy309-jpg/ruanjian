<template>
  <div class="resources-page">
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
        <h1>生成资源</h1>
        <p>AI 自动生成的个性化学习资料</p>
      </div>

      <div class="filter-bar">
        <el-select v-model="filterType" placeholder="资源类型" clearable size="default" style="width: 150px">
          <el-option label="全部" value="" />
          <el-option label="文档" value="document" />
          <el-option label="练习题" value="quiz" />
          <el-option label="思维导图" value="mindmap" />
        </el-select>
      </div>

      <div class="resource-list" v-if="filteredResources.length > 0">
        <div
          class="resource-card"
          v-for="res in filteredResources"
          :key="res.id"
          @click="openResource(res)"
        >
          <div class="resource-icon">
            <el-icon :size="32" :color="typeColor(res.resourceType || res.type)">
              <Document v-if="(res.resourceType || res.type) === 'document'" />
              <EditPen v-if="(res.resourceType || res.type) === 'quiz'" />
              <Share v-if="(res.resourceType || res.type) === 'mindmap'" />
            </el-icon>
          </div>
          <div class="resource-info">
            <h3>{{ res.title }}</h3>
            <div class="resource-meta">
              <el-tag size="small" :type="typeTagColor(res.resourceType || res.type)">
                {{ typeLabel(res.resourceType || res.type) }}
              </el-tag>
              <span class="resource-date">{{ formatDate(res.createdAt) }}</span>
            </div>
          </div>
        </div>
      </div>

      <el-empty v-else description="暂无生成资源，去 AI 学习助手中生成吧">
        <el-button type="primary" @click="$router.push('/agent-chat')">开始对话</el-button>
      </el-empty>

      <el-dialog
        v-model="showDialog"
        :title="activeResource?.title || '资源详情'"
        width="80%"
        top="5vh"
        destroy-on-close
      >
        <ResourceDoc
          v-if="(activeResource?.resourceType || activeResource?.type) === 'document'"
          :content="activeResource?.contentJson || ''"
        />
        <ResourceQuiz
          v-else-if="(activeResource?.resourceType || activeResource?.type) === 'quiz'"
          :content="activeResource?.contentJson || ''"
        />
        <ResourceMindmap
          v-else-if="(activeResource?.resourceType || activeResource?.type) === 'mindmap'"
          :content="activeResource?.contentJson || ''"
        />
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Document, EditPen, Share } from '@element-plus/icons-vue'
import { useAgentStore } from '@/stores/agent'
import ResourceDoc from '@/components/agent/ResourceDoc.vue'
import ResourceQuiz from '@/components/agent/ResourceQuiz.vue'
import ResourceMindmap from '@/components/agent/ResourceMindmap.vue'

const store = useAgentStore()
const resources = ref([])
const filterType = ref('')
const showDialog = ref(false)
const activeResource = ref(null)

const filteredResources = computed(() => {
  if (!filterType.value) return resources.value
  return resources.value.filter(r => (r.resourceType || r.type) === filterType.value)
})

async function loadResources() {
  try {
    const res = await fetch(`/api/generated-resources?userId=${store.userId}`)
    if (!res.ok) return
    const json = await res.json()
    if (json.code === 200) {
      const data = json.data
      resources.value = Array.isArray(data) ? data : (data?.resources || [])
    }
  } catch (e) {
    console.error('加载资源失败:', e)
  }
}

function openResource(res) {
  activeResource.value = res
  showDialog.value = true
}

function typeLabel(type) {
  const map = { document: '文档', quiz: '练习题', mindmap: '思维导图', reading: '拓展阅读', video_script: '视频脚本' }
  return map[type] || type
}

function typeColor(type) {
  const map = { document: '#409EFF', quiz: '#67C23A', mindmap: '#E6A23C' }
  return map[type] || '#909399'
}

function typeTagColor(type) {
  const map = { document: 'primary', quiz: 'success', mindmap: 'warning' }
  return map[type] || 'info'
}

function formatDate(date) {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN')
}

onMounted(loadResources)
</script>

<style scoped>
.resources-page {
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
  margin-bottom: 20px;
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
.filter-bar {
  margin-bottom: 20px;
}
.resource-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.resource-card {
  display: flex;
  align-items: center;
  gap: 16px;
  background: #fff;
  border-radius: 10px;
  padding: 16px 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}
.resource-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0,0,0,0.08);
}
.resource-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.resource-info {
  flex: 1;
  min-width: 0;
}
.resource-info h3 {
  font-size: 15px;
  color: #303133;
  margin-bottom: 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.resource-meta {
  display: flex;
  align-items: center;
  gap: 10px;
}
.resource-date {
  font-size: 12px;
  color: #c0c4cc;
}
</style>
