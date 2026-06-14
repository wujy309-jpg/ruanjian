import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAgentStore = defineStore('agent', () => {
  const messages = ref([])
  const currentPhase = ref('')
  const progress = ref(0)
  const sessionId = ref(null)
  const userId = ref(1)

  const profile = ref(null)
  const path = ref(null)
  const resources = ref([])
  const selectedNodeId = ref(null)
  const nodeResources = ref([])
  const isStreaming = ref(false)

  const phaseLabel = computed(() => {
    const map = {
      orchestrating: '正在分析您的需求...',
      profiling: '正在了解您的学习情况...',
      planning: '正在为您规划学习路径...',
      generating: '正在生成学习资料...',
      synthesizing: '正在整理学习计划...',
      done: '学习计划已生成'
    }
    return map[currentPhase.value] || ''
  })

  const isDone = computed(() => currentPhase.value === 'done')

  let _msgSeq = 0

  function addMessage(msg) {
    messages.value.push({
      id: `${Date.now()}-${++_msgSeq}`,
      timestamp: new Date(),
      ...msg
    })
  }

  function setPhase(phase, data) {
    currentPhase.value = phase
    if (data?.progress !== undefined) {
      progress.value = data.progress
    }
  }

  function setProfile(data) {
    profile.value = data
  }

  function setPath(data) {
    path.value = data
  }

  function setResources(data) {
    resources.value = data
  }

  function selectNode(nodeId) {
    selectedNodeId.value = nodeId
  }

  function setNodeResources(data) {
    nodeResources.value = data
  }

  function reset() {
    messages.value = []
    currentPhase.value = ''
    progress.value = 0
    sessionId.value = null
    profile.value = null
    path.value = null
    resources.value = []
    selectedNodeId.value = null
    nodeResources.value = []
    isStreaming.value = false
  }

  return {
    messages,
    currentPhase,
    progress,
    sessionId,
    userId,
    profile,
    path,
    resources,
    selectedNodeId,
    nodeResources,
    isStreaming,
    phaseLabel,
    isDone,
    addMessage,
    setPhase,
    setProfile,
    setPath,
    setResources,
    selectNode,
    setNodeResources,
    reset
  }
})
