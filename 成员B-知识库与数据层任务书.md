# 成员 B 任务书：知识库 + 数据层 + 业务 API

> 先读《团队项目总览》，再读本文档。

---

## 你的角色

你是这个项目的 **"骨架"**。所有数据库表设计、知识图谱数据填充、REST API 接口、与现有系统的对接都在你手里。A 的所有 Agent 产出通过你的 API 落库，C 的所有前端页面通过你的 API 查数据。

---

## 技术栈

- Java 17 + Spring Boot 3.0
- MyBatis Plus 3.5（ORM，已有）
- MySQL 8.0（已有）
- MinIO 8.5（对象存储，已有）
- Knife4j（API 文档，已有）

---

## 你需要交付的模块

```
现有项目 + 以下新增：

src/main/java/com/platform/
├── entity/
│   ├── KnowledgePoint.java      ← 知识点实体
│   ├── KnowledgeEdge.java       ← 前置依赖关系
│   ├── UserProfile.java         ← 学习画像
│   ├── LearningPath.java        ← 学习路径
│   ├── PathNode.java            ← 路径节点
│   ├── GeneratedResource.java   ← 生成资源
│   ├── AgentSession.java        ← 对话会话
│   └── AgentMessage.java        ← 对话消息
├── mapper/                       ← 对应 MyBatis Mapper
├── service/                      ← 对应 Service
└── controller/
    ├── KnowledgeController.java  ← 知识图谱 API
    ├── ProfileController.java    ← 画像 API
    ├── LearningPathController.java← 路径 API
    ├── ResourceController.java   ← 资源 API
    └── SessionController.java    ← 会话 API

resources/
└── db/
    ├── V2__knowledge_base.sql    ← 新增表 DDL
    └── V3__seed_data.sql         ← 知识图谱种子数据
```

---

## 详细任务清单（按顺序执行）

### B0：环境搭建与现有项目摸底（1 天）

**目标**：把现有项目跑起来，搞清楚代码结构，为三人后续开发扫平障碍。

**具体步骤**：
1. 从代码仓库克隆现有"AI 云学智训平台"项目
2. 搭建本地开发环境：MySQL 8.0、Redis、MinIO、JDK 17、Maven
3. 导入数据库初始脚本，确认所有表正常
4. `mvn clean compile` 确认编译通过
5. 启动项目，确认能访问 Swagger（Knife4j）文档页面
6. 搞清楚以下信息并写成 1 页 `README.md` 放到项目根目录：
   - 项目包结构（controller/service/mapper/entity 在哪个包下）
   - 数据库表名前缀规范（如 `t_` 前缀？无前缀？）
   - 现有鉴权方式（token 校验拦截器在哪？怎么加新接口或绕过？）
   - 统一的返回格式（`Result<T>` 类路径）
   - 已有的题目表、试卷表、用户表的字段清单

**产出**：`README.md` + A 和 C 能直接 clone 即跑的代码仓库

---

### B1：知识图谱表设计 + DDL（1 天）

**三张核心表**：

```sql
-- 课程表
CREATE TABLE course (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL COMMENT '课程名称',
    description TEXT COMMENT '课程描述',
    status ENUM('draft','published') DEFAULT 'draft' COMMENT '状态',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '课程表';

-- 知识点表
CREATE TABLE knowledge_point (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT NOT NULL COMMENT '所属课程ID',
    parent_id BIGINT DEFAULT NULL COMMENT '父知识点ID（分级结构）',
    name VARCHAR(200) NOT NULL COMMENT '知识点名称',
    code VARCHAR(50) NOT NULL COMMENT '知识点编码，如 java-oop-01',
    difficulty ENUM('L1','L2','L3') NOT NULL DEFAULT 'L1' COMMENT '难度层级',
    description TEXT COMMENT '知识点描述（给大模型看的，要详细）',
    sort_order INT DEFAULT 0 COMMENT '排序',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_course (course_id),
    KEY idx_code (code)
) COMMENT '知识点表';

-- 前置依赖关系表
CREATE TABLE knowledge_edge (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    from_kp_id BIGINT NOT NULL COMMENT '前置知识点',
    to_kp_id BIGINT NOT NULL COMMENT '依赖知识点（必须先学from才能学to）',
    UNIQUE KEY uk_edge (from_kp_id, to_kp_id)
) COMMENT '知识点前置依赖关系';
```

**关键设计**：`description` 字段是你最重要的字段——A 的 PlanAgent 和 GenAgent 靠这个字段理解知识点内容，所以描述要足够详细（至少 100 字）。

---

### B2：知识图谱数据填充 — Java 基础课程（4 天）

**目标**：手动构建 1 门课程的知识图谱，约 30-50 个知识点。

选 **Java 程序设计基础** 这门课，因为它结构清晰、知识点边界明确。

**工作方式**：
1. 先整理知识点清单（建议用 Excel 或 YAML）
2. 标注每个知识点的难度和前置依赖
3. 为每个知识点写一段 100-200 字的描述（这对 Agent 生成质量至关重要）
4. 写成 SQL INSERT 脚本或 YAML → 解析导入

**示例 YAML**（你可以直接从这里开始改）：

```yaml
course: Java程序设计基础
knowledge_points:
  - code: java-oop-01
    name: 类与对象
    difficulty: L1
    prerequisites: []
    description: >
      类是Java中对象的模板，定义了对象的属性（成员变量）和行为（方法）。
      对象是类的实例，通过new关键字创建。理解类与对象的关系是面向对象编程的第一步。
      关键概念：封装、成员变量、成员方法、构造方法。
      常见误区：混淆类和对象、不理解static的含义。

  - code: java-oop-02
    name: 构造方法
    difficulty: L2
    prerequisites: [java-oop-01]
    description: >
      构造方法是创建对象时自动调用的特殊方法，方法名与类名相同，无返回值。
      关键概念：默认构造方法、有参构造方法、构造方法重载、this关键字。
      常见误区：误以为构造方法有返回值、不理解this()调用另一个构造方法的机制。

  - code: java-oop-03
    name: 继承与多态
    difficulty: L2
    prerequisites: [java-oop-01, java-oop-02]
    description: >
      继承是子类获得父类属性和方法的机制（extends关键字），Java单继承。
      多态是同一行为在不同对象上表现出不同形态（方法重写 + 父类引用指向子类对象）。
      关键概念：super关键字、方法重写@Override、向上转型、向下转型、instanceof。
      常见误区：混淆重载与重写、以为多态就是重载、不理解动态绑定。

  # ...继续写 30-50 个

  - code: java-exception-01
    name: 异常的概念与分类
    difficulty: L2
    prerequisites: [java-oop-03]
    description: >
      Java异常是程序运行中发生的非正常事件。异常分类：Error（系统级，不可处理）、
      Exception（可处理）及其子类RuntimeException（运行时异常，编译器不检查）。
      关键概念：try-catch-finally、throw和throws的区别、异常链。
      常见误区：空catch块、捕获过于宽泛的异常类型、finally中return导致异常丢失。

  - code: java-exception-02
    name: 自定义异常与异常处理最佳实践
    difficulty: L3
    prerequisites: [java-exception-01]
    description: >
      自定义异常需继承Exception或RuntimeException，通常提供多个构造方法。
      最佳实践：异常信息要具体、不要用异常控制业务流程。
      关键概念：自定义异常构造方法、异常转译（包装底层异常再抛出）。
```

---

### B3：学习画像表（0.5 天）

```sql
CREATE TABLE user_profile (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE COMMENT '用户ID',
    dimensions JSON NOT NULL COMMENT '多维画像数据（JSON格式）',
    updated_by_session_id BIGINT COMMENT '最近更新的对话会话ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '用户学习画像';
```

**为什么用 JSON 字段**：画像维度可能随时间扩展（从 3 维到 5 维），JSON 字段比固定列灵活，MyBatis Plus 支持 JSON 类型处理器。画像的 JSON 结构见《接口契约文档》。

---

### B4：学习路径表 + 生成资源表（0.5 天）

```sql
CREATE TABLE learning_path (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    session_id BIGINT COMMENT '生成路径的对话会话ID',
    status ENUM('active','completed','abandoned') DEFAULT 'active',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user (user_id)
) COMMENT '学习路径';

CREATE TABLE path_node (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    path_id BIGINT NOT NULL COMMENT '所属路径ID',
    node_order INT NOT NULL COMMENT '节点序号',
    title VARCHAR(200) NOT NULL COMMENT '节点标题',
    node_type ENUM('review','new_learn','reinforce') NOT NULL COMMENT '学习类型',
    estimated_minutes INT NOT NULL COMMENT '预估学习时长（分钟）',
    reason TEXT COMMENT '安排此节点的原因（AI生成）',
    status ENUM('pending','in_progress','completed') DEFAULT 'pending',
    FOREIGN KEY (path_id) REFERENCES learning_path(id)
) COMMENT '路径节点';

CREATE TABLE path_node_kp (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    path_node_id BIGINT NOT NULL,
    knowledge_point_id BIGINT NOT NULL,
    UNIQUE KEY uk_node_kp (path_node_id, knowledge_point_id)
) COMMENT '路径节点关联的知识点';
```

```sql
CREATE TABLE generated_resource (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    path_node_id BIGINT NOT NULL COMMENT '关联的路径节点',
    knowledge_point_id BIGINT COMMENT '关联的知识点',
    resource_type ENUM('document','quiz','mindmap','reading','video_script') NOT NULL,
    title VARCHAR(200) NOT NULL,
    content_json JSON COMMENT '资源内容（JSON格式）',
    content_url VARCHAR(500) COMMENT '如果存MinIO，放外部URL',
    difficulty ENUM('L1','L2','L3'),
    generated_by_session_id BIGINT COMMENT '生成资源的对话会话ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_node (path_node_id),
    INDEX idx_kp (knowledge_point_id)
) COMMENT 'AI生成的个性化学习资源';
```

---

### B5：对话会话表（0.5 天）

```sql
CREATE TABLE agent_session (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(200) COMMENT '会话标题',
    status ENUM('active','completed') DEFAULT 'active',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT 'Agent对话会话';

CREATE TABLE agent_message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id BIGINT NOT NULL,
    role ENUM('user','assistant','system') NOT NULL,
    content TEXT NOT NULL COMMENT '消息内容',
    phase VARCHAR(50) COMMENT '消息所属阶段（profiling/planning/generating等）',
    metadata JSON COMMENT '附加元数据',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES agent_session(id)
) COMMENT '对话消息记录';
```

---

### B6：现有题目表改造（1 天）

**目标**：`questions` 表增加 `knowledge_point_id` 字段，让已有题目挂到知识图谱上。

```sql
ALTER TABLE questions ADD COLUMN knowledge_point_id BIGINT;
ALTER TABLE questions ADD INDEX idx_kp (knowledge_point_id);
```

- 如果已有题目数据不多（< 500 题），建议人工标注知识点的题，写 SQL UPDATE
- 如果量大，写一个批量更新脚本，按现有 `category_id` 做映射

---

### B7：知识库相关 API（2 天）

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/knowledge/graph/{courseId}` | GET | 返回课程完整知识图谱（树形 + 依赖关系） |
| `/api/knowledge/points/{id}` | GET | 单个知识点详情 |
| `/api/knowledge/points` | POST | 新增知识点 |
| `/api/knowledge/points/{id}` | PUT | 修改知识点 |
| `/api/knowledge/edges` | POST | 新增依赖关系 |

**知识图谱查询返回格式**（A 的 PlanAgent 需要这个）：

```json
{
  "courseId": 1,
  "courseName": "Java程序设计基础",
  "points": [
    {
      "id": 1,
      "code": "java-oop-01",
      "name": "类与对象",
      "difficulty": "L1",
      "description": "类是Java中对象的模板...",
      "prerequisites": [],
      "children": []
    },
    {
      "id": 2,
      "code": "java-oop-02",
      "name": "构造方法",
      "difficulty": "L2",
      "description": "构造方法是创建对象时...",
      "prerequisites": ["java-oop-01"],
      "children": []
    }
  ],
  "edges": [
    {"from": "java-oop-01", "to": "java-oop-02"},
    {"from": "java-oop-01", "to": "java-oop-03"}
  ]
}
```

---

### B8：画像 / 路径 / 资源 API（2 天）

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/user/profile/{userId}` | GET | 获取用户学习画像（A 读取） |
| `/api/user/profile/{userId}` | PUT | 更新用户学习画像（A 写入） |
| `/api/learning-path` | POST | 创建学习路径（A 写入） |
| `/api/learning-path/{pathId}` | GET | 获取路径详情含节点（C 读取） |
| `/api/learning-path/user/{userId}` | GET | 获取用户所有路径列表 |
| `/api/generated-resources` | POST | 批量写入生成资源（A 写入） |
| `/api/generated-resources?pathNodeId=xxx` | GET | 获取某节点的资源列表（C 读取） |

---

### B9：会话 API（0.5 天）

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/agent/sessions?userId=xxx` | GET | 获取用户对话历史列表 |
| `/api/agent/sessions/{sessionId}/messages` | GET | 获取某次对话的消息列表 |
| `/api/agent/sessions/{sessionId}` | DELETE | 删除对话记录 |

**注意**：B 只负责查询和删除 API。**消息写入由 A 的 Orchestrator 直接操作 `agent_message` mapper 完成**（单体应用内跨模块调 DAO 是合理的，避免 A 等你 API 导致的时序阻塞）。B 建表时确保 mapper 是 public 的，A 直接注入使用。

---

### B10：现有服务适配（1 天）

**确认以下现有接口能正常工作，必要时做兼容改造**：

- `POST /api/questions/batch` — A 的 GenAgent 会调用这个写入生成的题目
- `GET /api/questions/list` — C 的答题页面需要按知识点筛选题目
- `GET /api/exam-records/ranking` — 画像计算的成绩数据来源

**鉴权接入**：
- 搞清楚现有系统的 token 校验拦截器在哪里（通常是 `Interceptor` 或 `Filter`）
- 确保新增的 `/api/agent/**`、`/api/knowledge/**`、`/api/learning-path/**`、`/api/generated-resources/**` 接口正确接入鉴权
- 如果拦截器有白名单机制，先把 A 的 `/api/agent/chat` 加入（A 开发阶段可能需要绕过鉴权测试，后续再接入）

---

### B11：MinIO 资源存储对接（0.5 天）

**目标**：GenAgent 生成的文档、思维导图等较大内容，不直接存 MySQL，上传到 MinIO，存 URL。

- 已有 MinIO 客户端配置，再确认一次能正常上传/下载
- 生成的内容先序列化为 Markdown 文件或 JSON 文件 → 上传 → 把 URL 写入 `generated_resource.content_url`
- 文件访问通过已有 `/files/**` 接口

---

## 开发顺序建议

```
第1周：B0(环境摸底) → B1(DDL) → B2(数据填充开始)
       务必在第1周末完成 B0 + B1，开始 B2 的数据撰写

第2周：B2(完成) → B3(画像表) → B4(路径+资源表)
       第2周末前完成知识图谱核心 20+ 个知识点，让 A 能开始写 PlanAgent

第3周：B6(题目表改造) → B7(知识库API) → B8(画像/路径/资源API 开始)

第4周：B8(完成) → B9(会话API) → B5(会话表) → B10(现有服务适配) → B11(MinIO)

第5-6周：根据 A 和 C 联调反馈修 bug + 完善 API
```

---

## 你的 API 调用关系图

```
         A (调用B的API)                     C (调用B的API)
              │                                   │
    ┌─────────┼─────────┐               ┌─────────┼─────────┐
    ↓         ↓         ↓               ↓         ↓         ↓
知识图谱API  画像API   题目API        画像API   路径API   资源API
(B7)       (B8)      (已有)         (B8)      (B8)      (B8)
```

---

## 注意事项

- **P0 API（知识图谱查询 + 画像读写）必须在第1周末完成**，否则 A 无法开始写 Agent
- **知识图谱 description 字段写详细点**，这是大模型理解知识点的唯一依据，比名称更重要
- **所有新 API 统一前缀 `/api/`，返回格式 JSON**，沿用现有项目规范
- **用 Knife4j 生成 API 文档**，方便 A 和 C 查阅，不需要单独写文档
