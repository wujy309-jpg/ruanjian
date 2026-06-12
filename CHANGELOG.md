# 更新日志

## 2026-06-12 (最新)

### 🏗️ 多Agent架构重构

**新增文件：**
- `exam-system-server/src/main/java/com/exam/service/agent/LearningAgent.java` - Agent统一接口
- `exam-system-server/src/main/java/com/exam/service/agent/ProfileAgent.java` - 画像构建Agent
- `exam-system-server/src/main/java/com/exam/service/agent/PlanAgent.java` - 路径规划Agent（支持拓扑排序+LLM）
- `exam-system-server/src/main/java/com/exam/service/agent/GenAgent.java` - 资源生成Agent（支持预存资源）
- `exam-system-server/src/main/java/com/exam/service/agent/AgentOrchestrator.java` - 多Agent调度器
- `exam-system-server/src/main/java/com/exam/service/agent/SynthesisEngine.java` - 合成引擎
- `exam-system-server/src/main/java/com/exam/service/agent/ConflictDetector.java` - 冲突检测器
- `exam-system-server/src/main/java/com/exam/service/agent/model/AgentContext.java` - Agent上下文
- `exam-system-server/src/main/java/com/exam/service/agent/model/AgentInput.java` - Agent输入
- `exam-system-server/src/main/java/com/exam/service/agent/model/AgentOutput.java` - Agent输出
- `exam-system-server/src/main/java/com/exam/service/agent/model/OrchestrationPlan.java` - 执行计划
- `exam-system-server/src/main/java/com/exam/service/agent/model/PlanReport.java` - 路径报告
- `exam-system-server/src/main/java/com/exam/service/agent/model/ProfileReport.java` - 画像报告
- `exam-system-server/src/main/java/com/exam/service/agent/model/ResourceReport.java` - 资源报告
- `exam-system-server/src/main/java/com/exam/service/agent/model/ConflictResult.java` - 冲突检测结果

**核心功能：**
- ✅ 意图分类：用LLM判断用户需要哪些Agent
- ✅ 执行计划：动态生成Agent执行顺序
- ✅ 并行调度：使用CompletableFuture并行执行资源生成
- ✅ 拓扑排序：PlanAgent使用算法生成基础路径
- ✅ 预存资源：GenAgent优先使用数据库中的预存资源

---

### 🎨 前端优化

**修改文件：**
- `src/views/AgentChat.vue` - 新增历史对话列表
- `src/views/LearningPaths.vue` - 新增资源展示（文档/练习题/思维导图）
- `src/views/VideoDetail.vue` - 支持B站视频跳转播放
- `src/components/agent/ChatMessage.vue` - 优化选项显示样式

**新增功能：**
- ✅ 历史对话列表：左侧显示所有对话记录，可点击切换
- ✅ 对话内容保持：刷新页面不丢失对话内容
- ✅ 学习资源展示：点击节点可查看文档/练习题/思维导图
- ✅ 练习题交互：可作答、查看答案和解析
- ✅ 思维导图展示：树形结构，可折叠展开
- ✅ 生成状态显示：路径生成中显示loading状态

---

### 🎬 视频系统

**新增文件：**
- `exam-system-server/src/main/resources/db/migration/V5.0.0__sample_videos.sql` - 示例视频数据

**功能说明：**
- 使用B站视频链接
- 点击视频封面跳转到B站播放
- 支持5个示例视频

---

### 📚 预存资源

**新增文件：**
- `exam-system-server/src/main/resources/db/migration/V6.0.0__preloaded_resources.sql` - 预存学习资源

**预存内容（7个知识点 × 3种资源 = 21条）：**
- Java简介：文档 + 练习题 + 思维导图
- 变量与数据类型：文档 + 练习题 + 思维导图
- 运算符与表达式：文档 + 练习题 + 思维导图
- 控制流：文档 + 练习题 + 思维导图
- 方法：文档 + 练习题 + 思维导图
- 数组：文档 + 练习题 + 思维导图
- 面向对象编程基础：文档 + 练习题 + 思维导图

**优化效果：**
- 之前：每个节点调LLM生成资源 → 21次调用 → ~10分钟
- 现在：优先使用预存资源 → 0次调用 → 秒级响应

---

### 🐛 Bug修复

1. **修复JSON直接显示问题**
   - 原因：保存完整JSON到数据库
   - 修复：只保存显示文本（提取text字段）

2. **修复资源不显示问题**
   - 原因：path_node_id不匹配（预存的是1-7，实际是14-20）
   - 修复：改为根据标题模糊匹配

3. **修复对话内容丢失问题**
   - 原因：刷新页面调用store.reset()
   - 修复：改为loadRecentSession()加载历史会话

---

### ⚙️ 配置变更

**修改文件：**
- `exam-system-server/src/main/resources/application.yml`
  - 新增 `agent.mode: multi` 配置项
  - 支持 `legacy`（旧单体模式）和 `multi`（新多Agent模式）

**修改文件：**
- `exam-system-server/src/main/java/com/exam/controller/AgentChatController.java`
  - 默认使用多Agent架构
  - 支持通过配置切换模式

---

## 之前版本

### 2026-06-10
- 初始项目搭建
- 基础功能实现（对话、画像、路径、资源）
- 视频系统（列表、详情、播放）
- 管理后台（分类、视频管理）
