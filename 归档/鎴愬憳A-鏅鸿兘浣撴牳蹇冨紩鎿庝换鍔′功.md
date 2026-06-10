# 成员 A 任务书：多智能体核心引擎

> 先读《团队项目总览》，再读本文档。

---

## 你的角色

你是这个项目的 **"脑子"**。所有 Agent 的 prompt 设计、调度逻辑、大模型调用都在你手里。你不需要写 SQL，不需要写前端页面，只需要输出**结构化 JSON**，通过 B 的 API 落库，通过 C 的前端展示。

---

## 技术栈

- Java 17 + Spring Boot 3.0
- 大模型 HTTP 调用（OkHttp 或 RestTemplate 均可）
- JSON 解析（Jackson，已有）

---

## 你需要交付的模块

```
src/main/java/com/platform/
├── controller/
│   └── AgentController.java   ← Agent HTTP 入口（SSE + 请求分发）
└── service/agent/
    ├── LLMClient.java         ← 统一大模型调用封装
    ├── Orchestrator.java      ← 复杂度判断 + 任务路由 + 调度
    ├── ProfileAgent.java      ← 对话式画像构建
    ├── PlanAgent.java         ← 学习路径规划
    ├── GenAgent.java          ← 多模态资源生成
    ├── SynthesisEngine.java   ← 多报告聚合
    ├── ConflictDetector.java  ← 规则层冲突检测
    └── model/
        ├── AgentContext.java  ← 上下文对象
        ├── AgentInput.java    ← Agent 输入
        ├── AgentOutput.java   ← Agent 输出
        ├── OrchestrationPlan.java← 调度计划
        ├── ProfileReport.java ← 画像报告
        ├── PlanReport.java    ← 路径报告
        ├── ResourceReport.java← 资源报告
        └── ConflictResult.java← 冲突检测结果
```

---

## 详细任务清单（按顺序执行）

### A0.5：AgentController（0.5 天）

**目标**：写 `POST /api/agent/chat` 接口，这是 A 模块对外唯一的 HTTP 入口。

**职责**：
- 接收前端请求（sessionId + message + userId）
- 调用 Orchestrator 执行 Agent 管线
- 将 Orchestrator 的阶段性输出转为 SSE 事件流推送给前端
- 处理异常（大模型超时、JSON 解析失败等），返回前端可读的错误信息

**注意**：
- SSE Content-Type 为 `text/event-stream`，不要用 `application/json`
- 鉴权：开发阶段可先绕过，等 B10 完成后再接入现有 token 校验

---

### A1：LLMClient（1 天）

**目标**：封装一次大模型调用，所有 Agent 复用。

```java
// 你最终给每个 Agent 提供的就是这个
String callLLM(String systemPrompt, String userPrompt, Tool[] tools) → String
```

**要求**：
- 支持同步调用（Agent 间有依赖时需要等结果）
- 支持流式调用 SSE（对话场景，用户需要看到实时打字）
- 统一错误处理：超时重试（最多 2 次）、token 超限截断
- 统一 token 消耗记录（用来算成本）

**测试用例**：先不接 Agent，直接发一句 "你好，请用 JSON 格式返回 {\"msg\": \"hello\"}"，验证能拿到合法 JSON。

---

### A2：Agent 基类接口（0.5 天）

**目标**：定义所有 Agent 的统一契约。

```java
// 所有 Agent 都实现这个接口
interface LearningAgent {
    String getRole();                           // Agent 角色名
    String buildSystemPrompt(AgentContext ctx); // 动态构建 system prompt
    AgentOutput execute(AgentInput input);       // 调用 LLM + 解析输出
}
```

**AgentOutput 结构**：

```json
{
  "agent_role": "ProfileAgent",
  "status": "success",       // success | partial | failed
  "structured_data": { ... }, // Agent 产出的具体数据（JSON Object）
  "raw_response": "...",      // 原始大模型返回（调试用）
  "tokens_used": 1250
}
```

**AgentContext 结构**：

```json
{
  "sessionId": "xxx",
  "userId": "xxx",
  "globalContext": {           // 每个 Agent 都收到
    "userProfileSummary": "...",
    "knowledgeDomain": "Java后端",
    "sessionGoal": "从零学习Spring Boot"
  },
  "localContext": {            // 只给这个 Agent
    "specificInstruction": "生成第3章学习资源",
    "scope": ["spring-boot-03-01", "spring-boot-03-02"]
  },
  "conversationHistory": [...]
}
```

---

### A3：Orchestrator（3 天）**最重要**

**目标**：这是整个系统的中枢，负责三件事

**3.1 意图分类（Intention Router）**

```
用户输入 → 判断任务复杂度 → 决定走单 Agent 还是多 Agent

判断逻辑（用 prompt 让大模型输出 JSON）：

简单任务（single_agent）：
  "出10道Java题目" → 只需要 GenAgent
  "分析我的薄弱点" → 只需要 ProfileAgent

复杂任务（multi_agent）：
  "帮我制定Java学习计划" → ProfileAgent → PlanAgent
  "从零教我Spring Boot" → ProfileAgent → PlanAgent → GenAgent
```

**3.2 生成执行计划（Task Planner）**

大模型输出如下 JSON：

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

**3.3 调度执行（Executor）**

- 解析 plan，按 depends_on 决定执行顺序
- 同一批次内、依赖已满足的 Agent → **并行调用**
- Agent 完成后收集 AgentOutput
- 通知前端当前阶段（SSE 推送 phase 变更）

**3.4 profiling 阶段的多轮对话循环（关键设计）**

profiling 阶段和其他阶段不同——它需要和用户多轮交互。采用**短连接轮询方案**：

```
第1次 POST /api/agent/chat (sessionId=null, message="我想学Java")
  → Orchestrator 判断需要 profiling
  → 调 ProfileAgent，输出一个问题（如"你之前学过哪些语言？"）
  → SSE 推送 phase=profiling + 问题内容
  → profile_status: "in_progress"

第2次 POST /api/agent/chat (sessionId=123, message="学过Python")
  → Orchestrator 从 agent_message 表读取历史对话
  → 调 ProfileAgent（传入历史 + 新回答），输出第二个问题
  → SSE 推送 phase=profiling + 新问题

... 循环 3-5 轮 ...

第N次 POST /api/agent/chat (sessionId=123, message="每天能学1小时")
  → ProfileAgent 判断信息足够 → 输出完整画像 JSON
  → Orchestrator 写入画像到 DB，标记 profiling 完成
  → 自动进入下一阶段（planning）
```

**实现要点**：
- Orchestrator 维护一个会话状态机，记录当前 session 处于哪个 Agent 的哪个阶段
- ProfileAgent 的输出中必须包含 `status` 字段：`"in_progress"`（还需更多信息）或 `"complete"`（画像已完成）
- 会话状态和对话历史从 `agent_message` 表读写（你直接操作 mapper，不等 B 的 API）


### A4：ProfileAgent（2 天）

**目标**：通过对话了解学生水平，输出结构化画像。

**Prompt 设计要点**：
- 引导式提问，不要让人觉得像在填表
- 把用户的模糊描述量化（"我Java还行" → 掌握度 0.7）
- 至少收集 3 个维度：知识掌握度、认知风格、薄弱点
- 支持增量更新：已有画像时，只追问变化部分

**画像输出结构**（你和 B、C 约定这个格式）：

```json
{
  "dimensions": {
    "knowledge_map": {
      "java_basics":        { "level": 0.7, "label": "Java基础" },
      "object_oriented":    { "level": 0.6, "label": "面向对象" },
      "exception_handling": { "level": 0.2, "label": "异常处理" }
    },
    "cognitive_style": {
      "type": "depth_first",
      "avg_session_min": 45,
      "best_time": "evening"
    },
    "weak_points": [
      { "topic": "异常处理", "level": 0.2, "evidence": "用户标注'一直搞不懂'" }
    ]
  }
}
```

---

### A5：PlanAgent（2 天）

**目标**：基于画像 + 知识图谱，生成个性化学习路径。

**Prompt 关键约束**：
- 必须遵守知识图谱的前置依赖（没学"变量"就不能先学"方法"）
- 薄弱点优先安排，但需穿插巩固已有内容（间隔重复）
- 每节点预估学习时长匹配用户的 `avg_session_min`
- 路径节点 10-25 个，每个标注学习类型（新学/复习/强化）

**输出结构**：

```json
{
  "courseName": "Java程序设计基础",
  "nodes": [
    {
      "order": 1,
      "knowledgePoints": ["java-oop-01", "java-oop-02"],
      "title": "类与对象入门",
      "type": "review",
      "estimatedMinutes": 30,
      "reason": "用户已有基础，快速巩固"
    },
    {
      "order": 2,
      "knowledgePoints": ["java-exception-01", "java-exception-02"],
      "title": "异常处理机制",
      "type": "new_learn",
      "estimatedMinutes": 45,
      "reason": "用户薄弱点，优先攻克",
      "prerequisites": ["java-oop-01"]
    }
  ]
}
```

---

### A6：GenAgent（5 天）

**目标**：根据知识点 + 资源类型，生成具体学习内容。

**支持 3 类资源（第 1 阶段）**：

| 资源类型 | 输出格式 | 示例 |
|----------|----------|------|
| 讲解文档 | Markdown | 概念解释 + 代码示例 + 注意事项 |
| 练习题 | JSON（复用现有题库格式） | 选择题/判断题/简答题 + 答案 + 解析 |
| 思维导图 | JSON（树形结构） | 知识点 → 子知识点 → 关键概念 |

**重要**：GenAgent 需要 **Tool Calling** 能力。即大模型不是直接输出 JSON，而是"调用你注册的工具"：

```
大模型的思考：我需要把生成的题目存到数据库
→ 调用 save_questions([{...}])
→ 你的代码收到调用 → 调 B 的 API 写入 → 返回成功
→ 大模型继续：题目已保存，现在生成思维导图
→ 调用 generate_mindmap({...})
```

**如果你用的模型不支持 Tool Calling**，改为两步法：让大模型输出 JSON → 你的代码解析 JSON 并执行操作。

**记忆技巧**：给 GenAgent 的 prompt 里明确写出它可用的工具及其参数格式。

---

### A7：SynthesisEngine（1 天）

**目标**：把多个 Agent 的报告合并成前端能直接用的一份结果。

**核心逻辑**：
- 路径节点（PlanAgent 输出）× 资源列表（GenAgent 输出）= 完整学习包
- 去重处理：同一个知识点可能被多次生成资源，只保留最新
- 格式化为前端友好的结构

---

### A8：ConflictDetector（1 天）

**目标**：纯规则检测，不调大模型。

**检测项**：

| 检测项 | 规则 | 严重度 |
|--------|------|--------|
| 覆盖缺失 | PlanAgent 规划的路径节点，GenAgent 是否每个都生成了资源？ | 高 |
| 难度不匹配 | 画像说用户是 L1 水平，生成的题是否标了 L3？ | 中 |

**伪代码**：

```java
ConflictResult detect(PlanReport plan, List<ResourceReport> resources, ProfileReport profile) {
    // 检测 1：覆盖缺失
    List<String> plannedPoints = plan.getAllKnowledgePointIds();
    List<String> coveredPoints = resources.getAllKnowledgePointIds();
    List<String> missing = plannedPoints - coveredPoints;

    // 检测 2：难度不匹配
    List<String> mismatched = resources.stream()
        .filter(r -> r.difficulty > profile.maxRecommendedDifficulty())
        .toList();

    int score = missing.size() * 10 + mismatched.size() * 5;
    return new ConflictResult(score, missing, mismatched);
}
```

冲突分 > 30 → 让 Orchestrator 重新调度 GenAgent 补生成。

---

### A9：Prompt 调优 + 联调（2 天）

**调优重点**：

1. **幻觉测试**：让 GenAgent 生成"不存在的 Java API"的题目，看它是否胡编
2. **格式稳定性**：100 次调用，统计 JSON 解析成功率，低于 95% 需要改 prompt
3. **边界情况**：用户说"我什么都不会"、"我已经是专家了"——agent 的降级策略是什么
4. **对话中断恢复**：用户在画像中间说"算了直接出题"，Orchestrator 怎么处理

---

## 你和 B 的依赖关系

**你需要的 API（B 提供）**：

| API | 用途 | 优先级 |
|-----|------|--------|
| `GET /api/knowledge/graph/{courseId}` | PlanAgent 需要知识图谱才能规划路径 | P0 |
| `POST /api/questions/batch` | GenAgent 生成的题目入库 | P0 |
| `GET /api/user/profile/{userId}` | ProfileAgent 读取已有画像做增量更新 | P0 |
| `PUT /api/user/profile/{userId}` | ProfileAgent 写回画像 | P0 |
| `POST /api/learning-path` | PlanAgent 输出落库 | P1 |
| `POST /api/generated-resources` | GenAgent 输出落库 | P1 |

**P0** = B 必须在你的 Agent 开发前完成。**P1** = 可以先用 mock 测试，B 完成后接入。

**你直接用的 DB 表（不等 B 的 API）**：

| 表 | 用途 | 原因 |
|----|------|------|
| `agent_message` | Orchestrator 读写对话历史 | profiling 多轮循环不能等 B 的 API |
| `agent_session` | Orchestrator 创建/更新会话 | 同上，写消息前必须有关联的 session_id |

B 建表后确保 mapper 是 public 的，你在 Orchestrator 里直接 `@Autowired` 注入使用。B 的 B9 只负责**查询**和**删除** API。

---

## 你和 C 的依赖关系

**你提供给 C 的接口**：

`POST /api/agent/chat`（SSE 流式）

这是 C 的对话页面唯一需要调的接口。每个阶段的推送格式见《接口契约文档》。

---

## 开发顺序建议

```
第1周：A0.5(AgentController) → A1(LLMClient) → A2(基类) → A3(Orchestrator 开始)
       同时催促 B 完成 B0（环境摸底），你才能知道现有项目的包结构

第2周：A3(完成，含 profiling 多轮循环) → A4(ProfileAgent)
       和 C 联调画像 + 可视化

第3周：A5(PlanAgent)
       和 B 的知识图谱 API（B7）联调

第4-5周：A6(GenAgent，含 3 类资源的 prompt 调优)
       第5周末前完成 Synthesis + ConflictDetector

第6周：A9(全链路调优) + 联调
```

---

## 你需要的知识储备

- **Prompt Engineering 基础**：Few-shot、Chain-of-Thought、结构化输出约束
- **大模型 Tool Calling 原理**（如果模型支持）
- **SSE 协议**：`text/event-stream` 格式

有问题先看《接口契约文档》和《团队项目总览》，再在群里问。
