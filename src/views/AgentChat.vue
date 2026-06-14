<template>
  <div class="agent-chat-page">
    <div class="navbar">
      <div class="logo" @click="$router.push('/home')">
        <img src="../assets/logo.svg" alt="logo" class="logo-img" />
        <span class="title">AI云学智训平台</span>
      </div>
      <div class="nav-actions">
        <el-button text @click="$router.push('/home')">首页</el-button>
        <el-button text @click="$router.push('/videos')">视频学习</el-button>
      </div>
    </div>

    <div class="chat-layout">
      <!-- 左侧：历史对话列表 -->
      <div class="sidebar">
        <div class="sidebar-header">
          <h3>历史对话</h3>
          <el-button text size="small" @click="handleNewChat" :disabled="store.isStreaming">
            <el-icon><Plus /></el-icon> 新对话
          </el-button>
        </div>
        <div class="session-list">
          <div
            v-for="session in sessions"
            :key="session.id"
            class="session-item"
            :class="{ active: store.sessionId === session.id }"
            @click="loadSession(session)"
          >
            <div class="session-title">{{ session.title || '对话 #' + session.id }}</div>
            <div class="session-meta">
              <el-tag size="small" :type="session.status === 'completed' ? 'success' : 'primary'">
                {{ session.status === 'completed' ? '已完成' : '进行中' }}
              </el-tag>
              <span class="session-time">{{ formatTime(session.createdAt) }}</span>
            </div>
          </div>
          <el-empty v-if="sessions.length === 0" description="暂无历史对话" :image-size="40" />
        </div>
      </div>

      <!-- 右侧：聊天区域 -->
      <div class="chat-main">
        <div class="chat-header">
          <h2>AI 学习助手</h2>
        </div>

        <PhaseIndicator :phase="store.currentPhase" :progress="store.progress" />

        <div class="messages-container" ref="messagesRef">
          <div class="welcome-message" v-if="store.messages.length === 0">
            <div class="welcome-icon">
              <el-icon :size="48" color="#409EFF"><Cpu /></el-icon>
            </div>
            <h3>你好，我是 AI 学习助手</h3>
            <p>告诉我你想学什么，我会通过对话了解你的水平，为你规划个性化学习路径，并生成专属学习资料。</p>
            <div class="quick-prompts">
              <el-button
                v-for="prompt in quickPrompts"
                :key="prompt"
                size="small"
                plain
                @click="handleQuickPrompt(prompt)"
              >
                {{ prompt }}
              </el-button>
            </div>
          </div>

          <ChatMessage
            v-for="msg in store.messages"
            :key="msg.id"
            :role="msg.role"
            :content="msg.content"
            :options="msg.options"
            :phase="msg.phase"
            :timestamp="msg.timestamp"
            @select-option="handleSelectOption"
          />

          <div class="streaming-indicator" v-if="store.isStreaming && store.currentPhase !== 'profiling'">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>{{ store.phaseLabel }}</span>
          </div>
        </div>

        <ChatInput
          :disabled="store.isStreaming && store.currentPhase !== 'profiling'"
          :loading="store.isStreaming"
          @send="handleSend"
        />
      </div>

      <ProfilePanel
        :profile="store.profile"
        @close="store.setProfile(null)"
        @focus-topic="handleFocusTopic"
      />

      <div class="resource-panel" v-if="store.selectedNodeId">
        <div class="resource-panel-header">
          <h3>学习资源</h3>
          <el-button text size="small" @click="store.selectNode(null)">
            <el-icon><Close /></el-icon>
          </el-button>
        </div>
        <div class="resource-tabs">
          <el-tabs v-model="activeResourceTab" type="border-card">
            <el-tab-pane label="路径" name="path">
              <PathTimeline
                :path="store.path"
                :selected-node-id="store.selectedNodeId"
                @select-node="handleSelectPathNode"
              />
            </el-tab-pane>
            <el-tab-pane label="文档" name="doc" v-if="hasResourceType('document')">
              <ResourceDoc :content="getResourceContent('document')" />
            </el-tab-pane>
            <el-tab-pane label="练习题" name="quiz" v-if="hasResourceType('quiz')">
              <ResourceQuiz :content="getResourceContent('quiz')" />
            </el-tab-pane>
            <el-tab-pane label="思维导图" name="mindmap" v-if="hasResourceType('mindmap')">
              <ResourceMindmap :content="getResourceContent('mindmap')" />
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, nextTick, onMounted } from 'vue'
import { Plus, Cpu, Loading, Close } from '@element-plus/icons-vue'
import { useAgentStore } from '@/stores/agent'
import { sendMessage, fetchProfile, fetchLearningPathsByUser, fetchNodeResources, fetchSessions, fetchSessionMessages } from '@/api/agent'
import { formatTime } from '@/utils/format'
import ChatMessage from '@/components/agent/ChatMessage.vue'
import ChatInput from '@/components/agent/ChatInput.vue'
import PhaseIndicator from '@/components/agent/PhaseIndicator.vue'
import ProfilePanel from '@/components/agent/ProfilePanel.vue'
import PathTimeline from '@/components/agent/PathTimeline.vue'
import ResourceDoc from '@/components/agent/ResourceDoc.vue'
import ResourceQuiz from '@/components/agent/ResourceQuiz.vue'
import ResourceMindmap from '@/components/agent/ResourceMindmap.vue'

const store = useAgentStore()
const messagesRef = ref(null)
const activeResourceTab = ref('path')
const sessions = ref([])

const quickPrompts = [
  '我想学Java',
  '帮我复习面向对象',
  '我要准备Java面试',
  '帮我规划数据结构学习路径'
]

// 加载会话列表
async function loadSessions() {
  try {
    const data = await fetchSessions(store.userId)
    sessions.value = data || []
  } catch (e) {
    console.error('加载会话列表失败:', e)
  }
}

// 加载指定会话
async function loadSession(session) {
  if (store.isStreaming) return
  if (store.sessionId === session.id) return

  try {
    store.reset()
    store.sessionId = session.id

    const messages = await fetchSessionMessages(session.id)
    if (messages && messages.length > 0) {
      messages.forEach(msg => {
        store.addMessage({
          role: msg.role,
          content: msg.content,
          phase: msg.phase
        })
      })

      // 检查是否已完成
      const hasDone = messages.some(m => m.phase === 'done')
      if (hasDone) {
        store.currentPhase = 'done'
        await loadDoneData()
      }
    }
  } catch (e) {
    console.error('加载会话失败:', e)
  }
}

function scrollToBottom() {
  nextTick(() => {
    if (messagesRef.value) {
      messagesRef.value.scrollTop = messagesRef.value.scrollHeight
    }
  })
}

watch(() => store.messages.length, scrollToBottom)
watch(() => store.isStreaming, scrollToBottom)

async function handleSend(text) {
  await sendMessage(text)
  scrollToBottom()

  // 刷新会话列表
  await loadSessions()

  if (store.currentPhase === 'done') {
    await loadDoneData()
  }
}

function handleSelectOption(option) {
  handleSend(option)
}

function handleQuickPrompt(prompt) {
  handleSend(prompt)
}

function handleFocusTopic(topic) {
  handleSend(`帮我强化${topic}`)
}

function handleSelectPathNode(node) {
  store.selectNode(node.id)
}

async function loadDoneData() {
  try {
    const profile = await fetchProfile(store.userId)
    if (profile) {
      store.setProfile(profile)
    }

    const paths = await fetchLearningPathsByUser(store.userId)
    if (paths && paths.length > 0) {
      const latestPath = paths[0]
      store.setPath(latestPath)
      if (latestPath.nodes && latestPath.nodes.length > 0) {
        store.selectNode(latestPath.nodes[0].id)
        await loadNodeResources(latestPath.nodes[0].id)
      }
    }
  } catch (e) {
    console.error('加载完成数据失败:', e)
  }
}

async function loadNodeResources(nodeId) {
  try {
    const resources = await fetchNodeResources(nodeId)
    store.setNodeResources(resources || [])
  } catch (e) {
    console.error('加载节点资源失败:', e)
  }
}

function hasResourceType(type) {
  return store.nodeResources.some(r => r.resourceType === type || r.type === type)
}

function getResourceContent(type) {
  const resource = store.nodeResources.find(r => r.resourceType === type || r.type === type)
  return resource?.contentJson || ''
}

watch(() => store.selectedNodeId, async (nodeId) => {
  if (nodeId) {
    await loadNodeResources(nodeId)
  }
})

// profiling 阶段完成后立即拉取画像数据
watch(() => store.currentPhase, async (newPhase, oldPhase) => {
  if (oldPhase === 'profiling' && newPhase !== 'profiling' && !store.profile) {
    const profile = await fetchProfile(store.userId)
    if (profile) {
      store.setProfile(profile)
    }
  }
})

// 加载最近的会话
async function loadRecentSession() {
  try {
    // 先加载会话列表
    await loadSessions()
    
    if (sessions.value.length > 0) {
      // 找到最近的活跃会话
      const recentSession = sessions.value.find(s => s.status === 'active') || sessions.value[0]
      store.sessionId = recentSession.id
      
      // 加载会话消息
      const messages = await fetchSessionMessages(recentSession.id)
      if (messages && messages.length > 0) {
        // 清空现有消息，加载历史消息
        store.messages = []
        messages.forEach(msg => {
          store.addMessage({
            role: msg.role,
            content: msg.content,
            phase: msg.phase
          })
        })
        
        // 检查是否已完成
        const hasDone = messages.some(m => m.phase === 'done')
        if (hasDone) {
          store.currentPhase = 'done'
          await loadDoneData()
        }
      }
    }
  } catch (e) {
    console.error('加载历史会话失败:', e)
  }
}

// 新对话
function handleNewChat() {
  store.reset()
  // 刷新会话列表
  loadSessions()
}

onMounted(async () => {
  await loadRecentSession()
})
</script>

<style scoped>
.agent-chat-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
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
  flex-shrink: 0;
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
.chat-layout {
  flex: 1;
  display: flex;
  overflow: hidden;
}

/* 侧边栏样式 */
.sidebar {
  width: 260px;
  background: #fff;
  border-right: 1px solid #e4e7ed;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
}

.sidebar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #e4e7ed;
}

.sidebar-header h3 {
  font-size: 15px;
  color: #303133;
  margin: 0;
}

.session-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}

.session-item {
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  margin-bottom: 4px;
  transition: background 0.2s;
}

.session-item:hover {
  background: #f5f7fa;
}

.session-item.active {
  background: #ecf5ff;
  border: 1px solid #d9ecff;
}

.session-title {
  font-size: 14px;
  color: #303133;
  margin-bottom: 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.session-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.session-time {
  font-size: 12px;
  color: #c0c4cc;
}

.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}
.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
}
.chat-header h2 {
  font-size: 16px;
  color: #303133;
  margin: 0;
}
.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 16px 0;
}
.welcome-message {
  text-align: center;
  padding: 60px 40px;
}
.welcome-icon {
  margin-bottom: 16px;
}
.welcome-message h3 {
  font-size: 20px;
  color: #303133;
  margin-bottom: 8px;
}
.welcome-message p {
  font-size: 14px;
  color: #909399;
  max-width: 480px;
  margin: 0 auto 24px;
  line-height: 1.6;
}
.quick-prompts {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 8px;
}
.streaming-indicator {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  color: #409EFF;
  font-size: 13px;
}
.resource-panel {
  width: 420px;
  background: #fff;
  border-left: 1px solid #e4e7ed;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
  overflow: hidden;
}
.resource-panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #e4e7ed;
}
.resource-panel-header h3 {
  font-size: 15px;
  color: #303133;
  margin: 0;
}
.resource-tabs {
  flex: 1;
  overflow-y: auto;
}
.resource-tabs :deep(.el-tabs) {
  height: 100%;
  display: flex;
  flex-direction: column;
}
.resource-tabs :deep(.el-tabs__content) {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}
</style>
