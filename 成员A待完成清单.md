# 成员 A 待完成清单：多智能体核心引擎

> 基于《成员A-智能体核心引擎任务书》与当前代码现状的对比分析
> 
> 生成时间：2026-06-10

---

## 当前完成度：约 40%

已完成：AgentController、LLMClient（基础）、ProfileAgent（基础）、PlanAgent、GenAgent（基础）、profiling多轮对话

---

## 一、必做项（核心架构，缺了系统就不完整）

### 1. Agent 基类接口 `A2` ⏱ 预计 0.5 天

**现状**：没有独立的 Agent 接口定义，逻辑全部散在 `AgentOrchestratorService` 里

**需要新建的类**：

```
src/main/java/com/exam/service/agent/
├── LearningAgent.java          ← 所有Agent的统一接口
├── ProfileAgent.java           ← 画像构建Agent（从Orchestrator中拆出）
├── PlanAgent.java              ← 路径规划Agent（从Orchestrator中拆出）
├── GenAgent.java               ← 资源生成Agent（从Orchestrator中拆出）
└── model/
    ├── AgentContext.java       ← Agent上下文对象
    ├── AgentInput.java         ← Agent输入
    ├── AgentOutput.java        ← Agent输出
    ├── OrchestrationPlan.java  ← 调度计划
    ├── ProfileReport.java      ← 画像报告
    ├── PlanReport.java         ← 路径报告
    ├── ResourceReport.java     ← 资源报告
    └── ConflictResult.java     ← 冲突检测结果
```

**LearningAgent 接口定义**：

```java
public interface LearningAgent {
    String getRole();                              // Agent角色名，如 "ProfileAgent"
    String buildSystemPrompt(AgentContext ctx);    // 动态构建 system prompt
    AgentOutput execute(AgentInput input);         // 调用LLM + 解析输出
}
```

**AgentOutput 结构**：

```json
{
  "agent_role": "ProfileAgent",
  "status": "success",
  "structured_data": { "..." },
  "raw_response": "...",
  "tokens_used": 1250
}
```

**AgentContext 结构**：

```json
{
  "sessionId": "xxx",
  "userId": "xxx",
  "globalContext": {
    "userProfileSummary": "...",
    "knowledgeDomain": "Java后端",
    "sessionGoal": "从零学习Spring Boot"
  },
  "localContext": {
    "specificInstruction": "生成第3章学习资源",
    "scope": ["spring-boot-03-01", "spring-boot-03-02"]
  },
  "conversationHistory": [...]
}
```

---

### 2. Orchestrator 意图分类 `A3.1` ⏱ 预计 1 天

**现状**：没有意图分类，所有请求都走固定的 profiling → planning → generating 流程

**需要实现**：

用大模型判断用户输入的任务复杂度，决定走单 Agent 还是多 Agent：

```
简单任务（single_agent）：
  "出10道Java题目" → 只需要 GenAgent
  "分析我的薄弱点" → 只需要 ProfileAgent

复杂任务（multi_agent）：
  "帮我制定Java学习计划" → ProfileAgent → PlanAgent
  "从零教我Spring Boot" → ProfileAgent → PlanAgent → GenAgent
```

**实现方式**：在 Orchestrator 中新增 `classifyIntent()` 方法，用 prompt 让大模型输出 JSON：

```json
{
  "complexity": "complex",
  "required_agents": ["profile", "plan", "generate"]
}
```

---

### 3. Orchestrator 执行计划生成 `A3.2` ⏱ 预计 1 天

**现状**：硬编码了 profiling → planning → generating 的固定顺序

**需要实现**：

大模型输出动态执行计划：

```json
{
  "complexity": "complex",
  "plan": [
    {
      "step": 1,
      "agent": "profile",
      "task": "分析用户Java基础水平",
      "depends_on": [],
      "local_context": { "mode": "initial_profiling" }
    },
    {
      "step": 2,
      "agent": "plan",
      "task": "基于画像生成学习路径",
      "depends_on": [1],
      "local_context": { "courseId": 1, "targetLevel": "intermediate" }
    },
    {
      "step": 3,
      "agent": "generate",
      "task": "为路径各节点生成学习资源",
      "depends_on": [2],
      "local_context": { "resourceTypes": ["document", "quiz", "mindmap"] },
      "parallel": true
    }
  ]
}
```

---

### 4. Orchestrator 并行调度 `A3.3` ⏱ 预计 1 天

**现状**：顺序执行，不能并行调用多个 Agent

**需要实现**：

- 解析 plan，按 `depends_on` 决定执行顺序
- 同一批次内、依赖已满足的 Agent → **并行调用**（用 `CompletableFuture` 或线程池）
- Agent 完成后收集 AgentOutput
- 通知前端当前阶段（SSE 推送 phase 变更）

---

## 二、建议做项（系统完整度加分）

### 5. SynthesisEngine `A7` ⏱ 预计 1 天

**现状**：没有独立类，聚合逻辑散在 Orchestrator 中

**需要实现**：

- 路径节点（PlanAgent 输出）× 资源列表（GenAgent 输出）= 完整学习包
- 去重：同一知识点可能被多次生成资源，只保留最新
- 格式化为前端友好的结构

**新建文件**：`src/main/java/com/exam/service/agent/SynthesisEngine.java`

---

### 6. ConflictDetector `A8` ⏱ 预计 1 天

**现状**：完全没有

**需要实现**：

| 检测项 | 规则 | 严重度 |
|--------|------|--------|
| 覆盖缺失 | PlanAgent 规划的路径节点，GenAgent 是否每个都生成了资源？ | 高 |
| 难度不匹配 | 画像说用户是 L1 水平，生成的题是否标了 L3？ | 中 |

**伪代码**：

```java
ConflictResult detect(PlanReport plan, List<ResourceReport> resources, ProfileReport profile) {
    // 检测1：覆盖缺失
    List<String> missing = plan.getAllKnowledgePointIds() - resources.getAllKnowledgePointIds();
    
    // 检测2：难度不匹配
    List<String> mismatched = resources.stream()
        .filter(r -> r.difficulty > profile.maxRecommendedDifficulty())
        .toList();
    
    int score = missing.size() * 10 + mismatched.size() * 5;
    // score > 30 → 让 Orchestrator 重新调度 GenAgent 补生成
    return new ConflictResult(score, missing, mismatched);
}
```

**新建文件**：`src/main/java/com/exam/service/agent/ConflictDetector.java`

---

### 7. LLMClient 增强 `A1` ⏱ 预计 1 天

**现状**：只有同步调用，没有流式调用和 token 记录

**需要增强**：

| 功能 | 现状 | 目标 |
|------|------|------|
| 同步调用 | ✅ 有 | - |
| 流式调用 SSE | ❌ 没有 | 对话场景用户看到实时打字 |
| 超时重试 | ✅ 有（3次） | - |
| token 消耗记录 | ❌ 没有 | 记录每次调用的 token 用量 |

**需要修改的文件**：`src/main/java/com/exam/service/LlmClient.java`

---

## 三、开发顺序建议

```
第1步：A2（Agent基类接口）         → 0.5天
第2步：A3.1（意图分类）            → 1天
第3步：A3.2（执行计划生成）        → 1天
第4步：A3.3（并行调度）            → 1天
第5步：A7（SynthesisEngine）      → 1天
第6步：A8（ConflictDetector）     → 1天
第7步：A1（LLMClient流式+token）  → 1天
```

**总计：约 6.5 天**

---

## 四、需要依赖的 API（B 已完成）

| API | 用途 | 状态 |
|-----|------|------|
| `GET /api/knowledge/graph/{courseId}` | PlanAgent 需要知识图谱 | ✅ 已有 |
| `POST /api/questions/batch` | GenAgent 生成题目入库 | ✅ 已有 |
| `GET /api/user/profile/{userId}` | ProfileAgent 读取画像 | ✅ 已有 |
| `PUT /api/user/profile/{userId}` | ProfileAgent 写回画像 | ✅ 已有 |
| `POST /api/learning-path` | PlanAgent 输出落库 | ✅ 已有 |
| `POST /api/generated-resources` | GenAgent 输出落库 | ✅ 已有 |

B 的所有 API 已经就绪，A 可以直接调用。

---

## 五、关键代码位置

| 文件 | 说明 |
|------|------|
| `AgentOrchestratorService.java` | 当前的核心调度逻辑，需要重构 |
| `AgentChatController.java` | SSE 接口入口，不需要改 |
| `LlmClient.java` | 大模型调用封装，需要增强 |
| `AgentSessionMapper.java` / `AgentMessageMapper.java` | 会话/消息 Mapper，A 直接注入使用 |

---

## 六、注意事项

1. **不要删除现有 Orchestrator 逻辑**，先在新文件中实现 Agent 接口，再逐步迁移
2. **profiling 多轮对话已修复**（3轮限制 + sessionId 返回），重构时保留这个逻辑
3. **MiMo API 已配置好**（application.yml 中的 kimi.api），直接用 `LlmClient.chat()` 即可
4. **所有新 API 统一前缀 `/api/`**，返回格式 JSON，沿用现有项目规范
5. B 的所有 Mapper 都是 public 的，A 可以直接 `@Autowired` 注入使用
