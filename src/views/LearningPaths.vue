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
          <!-- 生成中状态 -->
          <div class="path-generating" v-if="path.status === 'active' && !path.nodes?.length">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>资源生成中...</span>
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

        <div class="detail-layout">
          <!-- 左侧：路径时间线 -->
          <div class="detail-left">
            <PathTimeline
              :path="selectedPath"
              :selected-node-id="selectedNodeId"
              @select-node="handleSelectNode"
            />
            <!-- 路径生成中提示 -->
            <div class="path-generating-tip" v-if="selectedPath && !selectedPath.nodes?.length">
              <el-icon class="is-loading"><Loading /></el-icon>
              <div>
                <p><strong>学习路径生成中...</strong></p>
                <p>AI正在为您规划个性化学习路径，请稍候</p>
              </div>
            </div>
          </div>

          <!-- 右侧：资源内容 -->
          <div class="detail-right" v-if="selectedNodeId">
            <div class="resource-panel">
              <div class="resource-header">
                <h3>学习资源</h3>
                <el-tag v-if="currentNode" type="info">{{ currentNode.title }}</el-tag>
              </div>

              <!-- 资源标签页 -->
              <el-tabs v-model="activeResourceTab" type="border-card">
                <!-- 文档 -->
                <el-tab-pane label="文档" name="document" v-if="hasResource('document')">
                  <div class="resource-content" v-html="renderMarkdown(getResourceContent('document'))"></div>
                </el-tab-pane>

                <!-- 练习题 -->
                <el-tab-pane label="练习题" name="quiz" v-if="hasResource('quiz')">
                  <div class="quiz-content">
                    <div
                      v-for="(q, idx) in getQuizQuestions()"
                      :key="idx"
                      class="quiz-item"
                    >
                      <div class="quiz-question">
                        <span class="quiz-num">{{ idx + 1 }}.</span>
                        {{ q.content }}
                      </div>
                      <div class="quiz-options">
                        <div
                          v-for="(opt, oidx) in q.options"
                          :key="oidx"
                          class="quiz-option"
                          :class="{
                            selected: selectedAnswers[idx] === oidx,
                            correct: showAnswers && opt.isCorrect,
                            wrong: showAnswers && selectedAnswers[idx] === oidx && !opt.isCorrect
                          }"
                          @click="selectAnswer(idx, oidx)"
                        >
                          <span class="option-letter">{{ String.fromCharCode(65 + oidx) }}.</span>
                          {{ opt.content }}
                        </div>
                      </div>
                      <div class="quiz-analysis" v-if="showAnswers && q.analysis">
                        <strong>解析：</strong>{{ q.analysis }}
                      </div>
                    </div>
                    <div class="quiz-actions">
                      <el-button type="primary" @click="showAnswers = true" v-if="!showAnswers">
                        查看答案
                      </el-button>
                      <el-button @click="resetQuiz" v-if="showAnswers">
                        重新作答
                      </el-button>
                    </div>
                  </div>
                </el-tab-pane>

                <!-- 思维导图 -->
                <el-tab-pane label="思维导图" name="mindmap" v-if="hasResource('mindmap')">
                  <div class="mindmap-content">
                    <MindmapNode v-if="getMindmapData()" :node="getMindmapData()" :depth="0" />
                  </div>
                </el-tab-pane>
              </el-tabs>

              <!-- 无资源提示 -->
              <el-empty
                v-if="!hasResource('document') && !hasResource('quiz') && !hasResource('mindmap')"
                description="该节点暂无学习资源"
                :image-size="60"
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { List, Clock, Loading } from '@element-plus/icons-vue'
import { fetchLearningPathsByUser, fetchLearningPath, fetchNodeResources } from '@/api/agent'
import { useAgentStore } from '@/stores/agent'
import { renderMarkdown } from '@/utils/markdown'
import PathTimeline from '@/components/agent/PathTimeline.vue'
import MindmapNode from '@/components/agent/MindmapNode.vue'

const store = useAgentStore()
const paths = ref([])
const selectedPath = ref(null)
const selectedNodeId = ref(null)
const nodeResources = ref([])
const activeResourceTab = ref('document')
const selectedAnswers = ref({})
const showAnswers = ref(false)

// 当前选中的节点
const currentNode = computed(() => {
  if (!selectedPath.value?.nodes || !selectedNodeId.value) return null
  return selectedPath.value.nodes.find(n => n.id === selectedNodeId.value)
})

// 检查是否有某种资源
function hasResource(type) {
  return nodeResources.value.some(r => r.resourceType === type || r.type === type)
}

// 获取资源内容
function getResourceContent(type) {
  const resource = nodeResources.value.find(r => r.resourceType === type || r.type === type)
  if (!resource) return ''
  const content = resource.contentJson
  if (typeof content === 'string') return content
  if (content?.content) return content.content
  return JSON.stringify(content, null, 2)
}

// 获取练习题数据
function getQuizQuestions() {
  const resource = nodeResources.value.find(r => r.resourceType === 'quiz' || r.type === 'quiz')
  if (!resource) return []
  const content = resource.contentJson
  if (typeof content === 'string') {
    try {
      const parsed = JSON.parse(content)
      return parsed.questions || []
    } catch { return [] }
  }
  return content?.questions || []
}

// 获取思维导图数据
function getMindmapData() {
  const resource = nodeResources.value.find(r => r.resourceType === 'mindmap' || r.type === 'mindmap')
  if (!resource) return null
  const content = resource.contentJson
  if (typeof content === 'string') {
    try { return JSON.parse(content) } catch { return null }
  }
  return content
}

// 选择答案
function selectAnswer(questionIdx, optionIdx) {
  if (showAnswers.value) return
  selectedAnswers.value[questionIdx] = optionIdx
}

// 重置练习
function resetQuiz() {
  selectedAnswers.value = {}
  showAnswers.value = false
}

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
      await handleSelectNode(detail.nodes[0])
    } else {
      // 没有节点，清空资源
      nodeResources.value = []
      selectedNodeId.value = null
    }
  } catch (e) {
    console.error('加载路径详情失败:', e)
  }
}

async function handleSelectNode(node) {
  selectedNodeId.value = node.id
  selectedAnswers.value = {}
  showAnswers.value = false
  activeResourceTab.value = 'document'
  try {
    const resources = await fetchNodeResources(node.id)
    nodeResources.value = resources || []
    store.setNodeResources(resources || [])
    store.selectNode(node.id)
    store.setPath(selectedPath.value)
  } catch (e) {
    console.error('加载节点资源失败:', e)
    nodeResources.value = []
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

.path-generating {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 12px;
  padding: 8px 12px;
  background: #ecf5ff;
  border-radius: 6px;
  color: #409EFF;
  font-size: 13px;
}

.path-generating .is-loading {
  animation: rotating 1s linear infinite;
}

@keyframes rotating {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
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

.detail-layout {
  display: flex;
  gap: 24px;
}

.detail-left {
  width: 350px;
  flex-shrink: 0;
}

.detail-right {
  flex: 1;
  min-width: 0;
}

.resource-panel {
  background: #fff;
  border-radius: 8px;
}

.resource-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.resource-header h3 {
  font-size: 16px;
  color: #303133;
  margin: 0;
}

.resource-content {
  line-height: 1.8;
  color: #303133;
}

.resource-content :deep(h1),
.resource-content :deep(h2),
.resource-content :deep(h3) {
  margin-top: 16px;
  margin-bottom: 8px;
}

.resource-content :deep(pre) {
  background: #f5f7fa;
  padding: 12px;
  border-radius: 4px;
  overflow-x: auto;
}

.resource-content :deep(code) {
  background: #f5f7fa;
  padding: 2px 6px;
  border-radius: 3px;
  font-family: monospace;
}

.resource-content :deep(table) {
  border-collapse: collapse;
  width: 100%;
  margin: 12px 0;
}

.resource-content :deep(th),
.resource-content :deep(td) {
  border: 1px solid #e4e7ed;
  padding: 8px 12px;
  text-align: left;
}

.resource-content :deep(th) {
  background: #f5f7fa;
}

/* 练习题样式 */
.quiz-content {
  padding: 8px 0;
}

.quiz-item {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.quiz-item:last-child {
  border-bottom: none;
}

.quiz-question {
  font-size: 15px;
  color: #303133;
  margin-bottom: 12px;
  line-height: 1.6;
}

.quiz-num {
  font-weight: 600;
  color: #409EFF;
}

.quiz-options {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.quiz-option {
  padding: 10px 14px;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 14px;
}

.quiz-option:hover {
  border-color: #409EFF;
  background: #ecf5ff;
}

.quiz-option.selected {
  border-color: #409EFF;
  background: #ecf5ff;
}

.quiz-option.correct {
  border-color: #67C23A;
  background: #f0f9eb;
  color: #67C23A;
}

.quiz-option.wrong {
  border-color: #F56C6C;
  background: #fef0f0;
  color: #F56C6C;
}

.option-letter {
  font-weight: 600;
  margin-right: 8px;
}

.quiz-analysis {
  margin-top: 12px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 6px;
  font-size: 13px;
  color: #606266;
  line-height: 1.6;
}

.quiz-actions {
  margin-top: 20px;
  display: flex;
  gap: 12px;
}

/* 思维导图样式 */
.mindmap-content {
  padding: 8px 0;
}

/* 路径生成中提示 */
.path-generating-tip {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px;
  background: #ecf5ff;
  border-radius: 8px;
  margin-top: 16px;
}

.path-generating-tip .is-loading {
  font-size: 24px;
  color: #409EFF;
  animation: rotating 1s linear infinite;
}

.path-generating-tip p {
  margin: 4px 0;
  color: #606266;
  font-size: 13px;
}

.path-generating-tip p strong {
  color: #303133;
  font-size: 14px;
}
</style>
