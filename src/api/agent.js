import { useAgentStore } from '@/stores/agent'

export async function sendMessage(userMessage) {
  const store = useAgentStore()

  store.isStreaming = true
  store.addMessage({ role: 'user', content: userMessage })

  const response = await fetch('/api/agent/chat', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      sessionId: store.sessionId,
      message: userMessage,
      userId: store.userId
    })
  })

  if (!response.ok) {
    store.isStreaming = false
    store.addMessage({ role: 'assistant', content: '请求失败，请稍后重试' })
    return
  }

  const reader = response.body.getReader()
  const decoder = new TextDecoder()
  let buffer = ''
  let currentEvent = ''

  while (true) {
    const { done, value } = await reader.read()
    if (done) break

    buffer += decoder.decode(value, { stream: true })
    const lines = buffer.split('\n')
    buffer = lines.pop() || ''

    for (const line of lines) {
      if (line.startsWith('event:')) {
        currentEvent = line.replace('event:', '').trim()
      } else if (line.startsWith('data:')) {
        try {
          const data = JSON.parse(line.replace('data:', '').trim())
          handleSSEEvent(currentEvent, data)
        } catch (e) {
          console.warn('SSE parse error:', e)
        }
      }
    }
  }

  store.isStreaming = false
}

function handleSSEEvent(event, data) {
  const store = useAgentStore()

  if (data.sessionId && !store.sessionId) {
    store.sessionId = data.sessionId
  }

  if (event === 'phase') {
    const phase = data.phase
    store.setPhase(phase, data)

    if (data.content?.text) {
      const role = phase === 'profiling' ? 'assistant' : 'system'
      store.addMessage({
        role,
        content: data.content.text,
        phase,
        options: data.content.options || null,
        type: data.content.type || 'text'
      })
    }

    if (phase === 'done') {
      store.addMessage({
        role: 'system',
        content: '学习计划已生成，请在右侧面板查看详情',
        phase: 'done'
      })
    }
  }
}

export async function fetchProfile(userId) {
  const res = await fetch(`/api/user/profile/${userId}`)
  if (!res.ok) return null
  const json = await res.json()
  return json.code === 200 ? json.data : null
}

export async function fetchLearningPath(pathId) {
  const res = await fetch(`/api/learning-path/${pathId}`)
  if (!res.ok) return null
  const json = await res.json()
  return json.code === 200 ? json.data : null
}

export async function fetchLearningPathsByUser(userId) {
  const res = await fetch(`/api/learning-path/user/${userId}`)
  if (!res.ok) return []
  const json = await res.json()
  return json.code === 200 ? json.data : []
}

export async function fetchNodeResources(pathNodeId) {
  const res = await fetch(`/api/generated-resources?pathNodeId=${pathNodeId}`)
  if (!res.ok) return []
  const json = await res.json()
  if (json.code === 200) {
    const data = json.data
    if (Array.isArray(data)) return data
    if (data?.resources) return data.resources
    return []
  }
  return []
}

export async function fetchSessions(userId) {
  const res = await fetch(`/api/agent/sessions?userId=${userId}`)
  if (!res.ok) return []
  const json = await res.json()
  return json.code === 200 ? json.data : []
}

export async function fetchSessionMessages(sessionId) {
  const res = await fetch(`/api/agent/sessions/${sessionId}/messages`)
  if (!res.ok) return []
  const json = await res.json()
  return json.code === 200 ? json.data : []
}

export async function updateNodeStatus(nodeId, status) {
  await fetch(`/api/learning-path/nodes/${nodeId}/status?status=${status}`, {
    method: 'PUT'
  })
}
