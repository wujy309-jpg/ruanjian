# 成员 C 任务书：前端 + 可视化 + 交互

> 先读《团队项目总览》，再读本文档。

---

## 你的角色

你是这个项目的 **"脸"**。用户看到的一切——对话体验、画像可视化、学习路径时间线、资源内容渲染——都在你手里。你的目标是让评委眼前一亮。

---

## 技术栈

- Vue 3.3 + Composition API（已有）
- Element Plus 2.3（已有）
- Vite 4.4（已有）
- Axios（已有）
- Pinia（状态管理，已有）

**可能新增**：
- Mind Elixir 或 VueFlow — 思维导图渲染（任选一个轻量的）
- ECharts — 画像雷达图
- Markdown 渲染库（markdown-it 或 vue-markdown）

---

## 你需要交付的模块

```
src/
├── views/
│   ├── AgentChat.vue           ← 对话页面（核心）
│   └── Home.vue                ← 首页改造（增加入口）
├── components/
│   ├── agent/
│   │   ├── ChatMessage.vue     ← 消息气泡
│   │   ├── ChatInput.vue       ← 输入框 + 发送按钮
│   │   ├── PhaseIndicator.vue  ← 阶段状态指示器
│   │   ├── ProfilePanel.vue    ← 画像可视化面板（右侧）
│   │   ├── ProfileRadar.vue    ← 知识掌握度雷达图
│   │   ├── ProfileWeakpoints.vue ← 薄弱点标签云
│   │   ├── PathTimeline.vue    ← 学习路径时间线
│   │   ├── ResourceDoc.vue     ← Markdown 文档阅读器
│   │   ├── ResourceQuiz.vue    ← 答题界面（复用现有刷题组件）
│   │   └── ResourceMindmap.vue ← 思维导图渲染
│   └── ...
├── api/
│   └── agent.js               ← 对话 + SSE 对接
└── stores/
    └── agent.js               ← Agent 对话状态管理（Pinia）
```

---

## 详细任务清单（按顺序执行）

### C1：对话页面 — 基础聊天 UI（2 天）

**目标**：做一个像 ChatGPT 的对话界面。

**页面布局**：

```
┌─────────────────────────────────────────────────┐
│  导航栏（复用现有）                               │
├─────────────────────────────────────────────────┤
│                                    ┌──────────┐│
│                                    │ 画像面板  ││
│  消息区域（滚动）                    │ (右侧)   ││
│  ┌─────────────────────────┐      │ 知识掌握度││
│  │ 用户：我想学Spring Boot  │      │ 雷达图    ││
│  └─────────────────────────┘      │          ││
│       ┌─────────────────────────┐ │ 薄弱点    ││
│       │ AI：好的，我先了解你的.. │ │ 标签云    ││
│       └─────────────────────────┘ │          ││
│                                    └──────────┘│
├─────────────────────────────────────────────────┤
│  输入框                              [发送]     │
└─────────────────────────────────────────────────┘
```

**消息气泡**：
- 用户消息：靠右，蓝色背景
- AI 消息：靠左，浅灰背景，支持 Markdown 渲染
- AI 思考中：左侧显示 `...` 动画

**右侧面板**：默认折叠，当画像数据到达时自动展开并渲染。

---

### C2：对话页面 — SSE 对接（1 天）

**目标**：对接 A 的 `POST /api/agent/chat` SSE 流式接口。

**核心代码示例**：

```javascript
// api/agent.js
import { useAgentStore } from '@/stores/agent'

export async function sendMessage(userMessage, sessionId) {
  const store = useAgentStore()

  const response = await fetch('/api/agent/chat', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      sessionId: sessionId,
      message: userMessage,
      userId: store.currentUserId
    })
  })

  const reader = response.body.getReader()
  const decoder = new TextDecoder()

  while (true) {
    const { done, value } = await reader.read()
    if (done) break

    const text = decoder.decode(value)
    // 解析 SSE 事件
    const lines = text.split('\n')
    for (const line of lines) {
      if (line.startsWith('event:')) {
        store.currentPhase = line.replace('event:', '').trim()
      }
      if (line.startsWith('data:')) {
        const data = JSON.parse(line.replace('data:', '').trim())
        store.handlePhaseData(store.currentPhase, data)
      }
    }
  }
}
```

**Pinia Store 关键状态**：

```javascript
// stores/agent.js
export const useAgentStore = defineStore('agent', {
  state: () => ({
    messages: [],          // [{ role, content, phase }]
    currentPhase: '',      // orchestrating | profiling | planning | generating | done
    currentPhaseData: null,// 当前阶段的实时数据
    profile: null,         // 画像数据（phase=done 时拿到完整数据）
    path: null,            // 学习路径
    resources: [],         // 生成资源列表
    progress: 0            // 0-1
  })
})
```

---

### C3：对话页面 — 阶段状态展示（1 天）

**目标**：根据 A 推送的 `phase`，动态切换界面状态。

| Phase | 用户看到的 | UI 表现 |
|-------|-----------|---------|
| `orchestrating` | "正在分析您的需求..." | 消息左侧 loading 动画 |
| `profiling` | "正在了解您的学习情况..." + 对话气泡 | 多轮对话形式，AI 提问，用户回答 |
| `planning` | "正在为您规划学习路径..." | 进度条 + 文案提示 |
| `generating` | "正在生成第 X/Y 章学习资料..." | 进度条 + 实时章节名称 |
| `synthesizing` | "正在整理学习计划..." | 短暂 loading |
| `done` | 完整的学习计划展示 | 右侧面板展开，内容完整渲染 |

**实现**：监听 store 的 `currentPhase`，用 `v-if` 切换不同展示组件。

**profiling 多轮交互的关键逻辑**：

profiling 阶段采用短连接轮询——每次问答是一次完整的 HTTP 请求 → SSE 响应。C 的处理流程：

```
1. 用户输入 → sendMessage() → POST /api/agent/chat
2. SSE 返回 phase=profiling，content 里是一个问题
3. 把问题渲染为 AI 消息气泡（type=question 带选项列表，type=follow_up 纯文本）
4. 用户再次输入回答 → sendMessage() → POST /api/agent/chat (带 sessionId)
5. SSE 可能返回 phase=profiling（还有下一个问题）→ 回到步骤 3
6. 或者返回 phase=planning（画像完成，进入下一阶段）→ 切换 UI
```

**代码要点**：
- `sendMessage()` 每次对话是新的 fetch 请求，不要在 profiling 中间保持长连接
- Store 中维护 `sessionId`，第一次发请求时 sessionId=null，服务端返回后保存
- profiling 阶段如果 `content.options` 不为空，渲染为可点击选项（点击即发送）；如果为空，渲染为普通输入框

---

### C4：画像可视化面板（2 天）

**目标**：把画像 JSON 变成可视化图表。

**知识掌握度 → 雷达图（ECharts）**：

```javascript
// 画像数据
profile.dimensions.knowledge_map = {
  "java_basics": 0.7,
  "object_oriented": 0.6,
  "exception_handling": 0.2,
  ...
}

// → ECharts radar 配置
option = {
  radar: {
    indicator: Object.entries(knowledge_map).map(([name, data]) => ({
      name: data.label, max: 1
    }))
  },
  series: [{
    type: 'radar',
    data: [{ value: Object.values(knowledge_map).map(d => d.level) }]
  }]
}
```

**薄弱点 → 标签云**：
- 用 Element Plus 的 `el-tag` 组件
- 颜色越深 = 越薄弱（level 越低）
- 点击标签可以触发"帮我强化这个知识点"

**认知风格 → 信息卡片**：
- 小卡片展示：学习风格（深度优先/广度优先）、偏好时长、最佳时段

---

### C5：学习路径时间线（1.5 天）

**目标**：把路径 JSON 渲染为纵向时间线。

**视觉效果**：

```
┌──────────────────────────────────────────┐
│  ●  第1步：类与对象入门          30分钟  │  ← 当前
│  │  复习型 | 您已有基础，快速回顾          │
│  │                                      │
│  ○  第2步：异常处理机制          45分钟  │  ← 待完成
│  │  新学型 | 您标注的薄弱点，重点攻克       │
│  │                                      │
│  ○  第3步：集合框架              45分钟  │
│  │  新学型 |                              │
│  ┊                                      │
└──────────────────────────────────────────┘
```

**交互**：
- 点击节点 → 右侧加载该节点的资源（文档、题目、思维导图）
- 完成学习后点击"标记完成" → 调用 B 的 API 更新节点状态
- 用 Element Plus 的 `el-timeline` 组件实现

---

### C6：资源展示 — Markdown 文档阅读器（1 天）

**目标**：渲染 GenAgent 生成的 Markdown 讲解文档。

- 集成 `markdown-it` 或 `vue-markdown`
- 支持代码语法高亮（highlight.js）
- 支持目录导航（解析 h1-h3 标题生成侧边目录）
- 适配深色/浅色主题（Element Plus 自带主题变量）

**示例渲染效果**：

```
# 异常处理机制

## 1. 异常的概念

在Java中，异常是程序运行中发生的非正常事件...

### 1.1 异常的分类

| 类型 | 说明 | 举例 |
|------|------|------|
| Error | 系统级错误 | OutOfMemoryError |
| Exception | 可处理异常 | IOException |

## 2. try-catch 语法

```java
try {
    // 可能抛出异常的代码
} catch (ExceptionType e) {
    // 处理异常
}
```
```

---

### C7：资源展示 — 答题界面（1 天）

**目标**：复用现有智能刷题模块的答题 UI。

- GenAgent 生成的题目 JSON 格式与现有题库一致，所以可以直接复用
- 如果格式有差异，写一个适配方法转换即可
- 答题完成后，调用 B 的 API 记录作答结果

**不要重新做一个答题页面**，复用是最快的。

---

### C8：资源展示 — 思维导图（1.5 天）

**目标**：把 GenAgent 输出的树形 JSON 渲染为可视化思维导图。

**推荐库**：**Mind Elixir**（轻量，中文友好）或 **VueFlow**（更灵活）

**数据格式**（GenAgent 给的）：

```json
{
  "topic": "异常处理",
  "children": [
    {
      "topic": "异常分类",
      "children": [
        { "topic": "Error（系统错误）" },
        { "topic": "Exception（可处理）" },
        { "topic": "RuntimeException（运行时）" }
      ]
    },
    {
      "topic": "处理机制",
      "children": [
        { "topic": "try-catch-finally" },
        { "topic": "throws声明" },
        { "topic": "throw抛出" }
      ]
    }
  ]
}
```

**交互**：支持节点展开/折叠、缩放、导出为图片。

---

### C9：首页改造（1 天）

**目标**：在现有学生端首页增加"智能学习助手"入口。

**改什么**：
- 在快捷功能区增加一个卡片："AI 学习助手"——"个性化学习画像 · 智能路径规划 · 多模态资源"
- 点击 → 跳转到 `AgentChat.vue` 对话页面
- 保持现有首页其他内容不变

---

### C10：整体联调 + UI 打磨（1.5 天）

**必须验证的交互流程**：

1. 用户输入"我想学 Java"
2. → AI 回复引导问题（画像构建中）
3. → 用户回答 3-4 个问题
4. → 右侧面板出现雷达图 + 薄弱点
5. → AI 显示学习路径时间线
6. → 点击第1个路径节点 → 显示文档 + 题目 + 思维导图
7. → 用户做完题目 → 查看答案

**打磨点**：
- 加载状态不要闪白屏
- 流式打字速度舒适（太快看不清，太慢焦虑）
- 移动端基本可用（不需要完美适配，但不要完全崩）
- 滚动到底部自动跟随

---

## 你和 A 的接口

你只需要调一个接口：`POST /api/agent/chat`

这是一个 SSE 流式接口，A 会在不同阶段推送不同事件。格式见《接口契约文档》。

---

## 你和 B 的接口

获取静态数据（非 SSE）：

| 接口 | 用途 | 调用时机 |
|------|------|----------|
| `GET /api/user/profile/{userId}` | 获取已有画像 | 进入对话页面时 |
| `GET /api/learning-path/{pathId}` | 获取学习路径详情 | done 阶段后 |
| `GET /api/generated-resources?pathNodeId=xxx` | 获取某节点的资源 | 点击路径节点时 |
| `GET /api/agent/sessions?userId=xxx` | 获取对话历史 | 侧边栏历史列表 |
| `GET /api/agent/sessions/{id}/messages` | 获取历史消息 | 点击历史对话时 |

---

## 开发顺序建议

```
第1周：C1（基础聊天UI）
       和 A 确认 SSE 格式后立即做

第2周：C2（SSE对接）→ C3（阶段展示）→ C4（画像面板 开始）

第3周：C4（完成）→ C5（路径时间线）→ C6（文档阅读器）

第4周：C7（答题界面）→ C8（思维导图）

第5周：C9（首页改造）→ C10（整体联调）

第6周：打磨 + 修复
```

---

## UI 设计原则（给评委看的东西）

- **不要花里胡哨**：用 Element Plus 默认主题色系即可，不需要自己调色
- **信息密度适中**：对话页面不要堆满，右侧面板按模块分区
- **加载状态要优雅**：骨架屏 > loading圈 > 白屏
- **对话感要自然**：AI 的回复不要一坨全出来，分段流式渲染
