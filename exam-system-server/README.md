# AI云学智训平台 - 项目结构说明

> 本文档供团队成员（A、B、C）快速了解项目结构和开发规范。

---

## 一、项目概述

- **项目名称**：AI云学智训平台（smart-learning-platform）
- **技术栈**：Java 17 + Spring Boot 3.0 + MyBatis Plus 3.5 + MySQL 8.0 + Vue 3 + Element Plus
- **项目结构**：前后端分离（前端Vue + 后端Spring Boot）

---

## 二、后端项目结构

```
exam-system-server/src/main/java/com/exam/
├── common/                    # 公共类
│   ├── Result.java           # 统一响应封装类
│   └── CacheConstants.java   # 缓存常量
├── config/                    # 配置类
│   ├── AppConfig.java        # 应用配置
│   ├── CorsConfig.java       # 跨域配置
│   ├── Knife4jConfig.java    # API文档配置
│   ├── MinioConfig.java      # MinIO对象存储配置
│   ├── MybatisPlusConfig.java # MyBatis Plus配置
│   └── RedisConfig.java      # Redis缓存配置
├── controller/                # 控制器层（REST API）
│   ├── KnowledgeController.java    # 知识图谱API（新增）
│   ├── ProfileController.java      # 用户画像API（新增）
│   ├── LearningPathController.java # 学习路径API（新增）
│   ├── ResourceController.java     # 生成资源API（新增）
│   ├── SessionController.java      # 对话会话API（新增）
│   ├── QuestionController.java     # 题目管理API
│   ├── QuestionBatchController.java # 题目批量操作API
│   ├── VideoController.java        # 视频管理API
│   ├── VideoAdminController.java   # 视频管理后台API
│   ├── VideoCategoryController.java # 视频分类API
│   ├── CategoryController.java     # 题目分类API
│   ├── FileController.java         # 文件上传API
│   └── UserController.java         # 用户管理API
├── dto/                       # 数据传输对象
├── entity/                    # 实体类
│   ├── KnowledgePoint.java   # 知识点（新增）
│   ├── KnowledgeEdge.java    # 知识点依赖关系（新增）
│   ├── Course.java           # 课程（新增）
│   ├── UserProfile.java      # 用户学习画像（新增）
│   ├── LearningPath.java     # 学习路径（新增）
│   ├── PathNode.java         # 路径节点（新增）
│   ├── PathNodeKp.java       # 路径节点知识点关联（新增）
│   ├── GeneratedResource.java # 生成资源（新增）
│   ├── AgentSession.java     # 对话会话（新增）
│   ├── AgentMessage.java     # 对话消息（新增）
│   ├── Question.java         # 题目
│   ├── QuestionChoice.java   # 题目选项
│   ├── QuestionAnswer.java   # 题目答案
│   ├── Category.java         # 分类
│   ├── User.java             # 用户
│   ├── Video.java            # 视频
│   ├── VideoCategory.java    # 视频分类
│   ├── VideoLike.java        # 视频点赞
│   └── VideoView.java        # 视频观看记录
├── mapper/                    # MyBatis Mapper接口
├── service/                   # Service接口
│   └── impl/                 # Service实现类
├── utils/                     # 工具类
│   ├── ExcelUtil.java        # Excel工具
│   ├── IpUtils.java          # IP工具
│   └── RedisUtils.java       # Redis工具
└── vo/                        # 视图对象
```

---

## 三、数据库规范

### 3.1 表名规范
- **无前缀**：直接使用表名（如 `users`, `questions`, `exams`）
- **命名风格**：小写下划线（snake_case）

### 3.2 现有表清单

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| `users` | 用户表 | id, username, password, real_name, role |
| `questions` | 题目表 | id, title, type, category_id, knowledge_point_id, difficulty, score |
| `question_choices` | 选择题选项 | id, question_id, content, is_correct, sort |
| `question_answers` | 题目答案 | id, question_id, answer, keywords |
| `categories` | 分类表 | id, name, parent_id, sort |
| `exams` | 试卷表 | id, name, description, duration, pass_score, total_score |
| `exam_questions` | 试卷题目关联 | id, exam_id, question_id, question_score |
| `exam_records` | 考试记录 | id, exam_id, student_name, student_number, total_score |
| `videos` | 视频表 | id, title, description, category_id, file_url, status |
| `video_categories` | 视频分类 | id, name, parent_id, sort_order |
| `video_likes` | 视频点赞 | id, video_id, user_ip |
| `video_views` | 视频观看 | id, video_id, user_ip, view_duration |

### 3.3 新增表清单（知识库相关）

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| `course` | 课程表 | id, name, description, status |
| `knowledge_point` | 知识点表 | id, course_id, parent_id, name, code, difficulty, description |
| `knowledge_edge` | 知识点依赖关系 | id, from_kp_id, to_kp_id, edge_type |
| `user_profile` | 用户学习画像 | id, user_id, dimensions(JSON) |
| `learning_path` | 学习路径 | id, user_id, course_id, status |
| `path_node` | 路径节点 | id, path_id, node_order, title, node_type, estimated_minutes |
| `path_node_kp` | 节点知识点关联 | id, path_node_id, knowledge_point_id |
| `generated_resource` | 生成资源 | id, path_node_id, resource_type, title, content_json |
| `agent_session` | 对话会话 | id, user_id, title, status |
| `agent_message` | 对话消息 | id, session_id, role, content, phase |

---

## 四、统一返回格式

### 4.1 Result类路径
```
com.exam.common.Result
```

### 4.2 返回格式示例
```json
{
  "code": 200,
  "message": "操作成功",
  "data": { ... }
}
```

### 4.3 使用方式
```java
import com.exam.common.Result;

// 成功返回
return Result.success(data);

// 带消息的成功返回
return Result.success(data, "操作成功");

// 错误返回
return Result.error("错误信息");
```

---

## 五、鉴权方式

### 5.1 当前状态
**无鉴权机制**：当前系统未实现token校验拦截器，所有API均可直接访问。

### 5.2 后续接入建议
如需接入鉴权，建议：
1. 创建 `AuthInterceptor` 类实现 `HandlerInterceptor`
2. 在 `WebMvcConfig` 中注册拦截器
3. 配置白名单路径（如 `/api/user/login`）

---

## 六、开发环境配置

### 6.1 数据库配置
- **地址**：localhost:3306
- **数据库**：exam_system
- **用户名**：root
- **密码**：132200

### 6.2 Redis配置
- **地址**：localhost:6379
- **数据库**：0

### 6.3 MinIO配置
- **地址**：http://localhost:9000
- **用户名**：minioadmin
- **密码**：minioadmin
- **存储桶**：java1229

### 6.4 启动命令
```bash
# 后端
cd exam-system-server
mvn clean compile
mvn spring-boot:run

# 前端
npm install
npm run dev
```

---

## 七、API文档

启动后端后访问Knife4j API文档：
- **地址**：http://localhost:8080/doc.html

---

## 八、新增API快速参考

### 8.1 知识图谱API
```
GET    /api/knowledge/graph/{courseId}     # 获取课程知识图谱
GET    /api/knowledge/tree/{courseId}      # 获取树形结构
GET    /api/knowledge/points/{id}          # 获取知识点详情
GET    /api/knowledge/points/code/{code}   # 根据编码获取知识点
POST   /api/knowledge/points               # 新增知识点
PUT    /api/knowledge/points/{id}          # 修改知识点
DELETE /api/knowledge/points/{id}          # 删除知识点
POST   /api/knowledge/edges                # 新增依赖关系
```

### 8.2 用户画像API
```
GET    /api/user/profile/{userId}          # 获取用户画像
PUT    /api/user/profile/{userId}          # 更新用户画像
POST   /api/user/profile                   # 创建用户画像
```

### 8.3 学习路径API
```
POST   /api/learning-path                  # 创建学习路径
GET    /api/learning-path/{pathId}         # 获取路径详情
GET    /api/learning-path/user/{userId}    # 获取用户路径列表
```

### 8.4 生成资源API
```
POST   /api/generated-resources            # 批量写入资源
GET    /api/generated-resources?pathNodeId=xxx  # 获取节点资源
```

### 8.5 对话会话API
```
GET    /api/agent/sessions?userId=xxx      # 获取会话列表
GET    /api/agent/sessions/{id}/messages   # 获取消息列表
DELETE /api/agent/sessions/{id}            # 删除会话
```

---

## 九、注意事项

1. **Mapper公开**：所有新增Mapper均为public接口，A模块可直接注入使用
2. **JSON字段**：`user_profile.dimensions` 和 `agent_message.metadata` 使用JSON类型处理器
3. **外键约束**：表之间存在外键关系，删除数据时需注意顺序
4. **字符编码**：数据库和表均使用utf8mb4字符集

---

## 十、联系信息

如有问题请联系项目负责人。
