USE exam_system;

-- =====================================================
-- B1: 知识图谱核心表
-- =====================================================

-- 课程表
CREATE TABLE IF NOT EXISTS `course` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '课程ID',
    `name` VARCHAR(200) NOT NULL COMMENT '课程名称',
    `description` TEXT COMMENT '课程描述',
    `status` ENUM('draft','published') DEFAULT 'draft' COMMENT '状态：draft-草稿，published-已发布',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

-- 知识点表
CREATE TABLE IF NOT EXISTS `knowledge_point` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '知识点ID',
    `course_id` BIGINT NOT NULL COMMENT '所属课程ID',
    `parent_id` BIGINT DEFAULT NULL COMMENT '父知识点ID（树形分级，可空）',
    `name` VARCHAR(200) NOT NULL COMMENT '知识点名称（给人看的）',
    `code` VARCHAR(50) NOT NULL COMMENT '知识点编码（给程序用的，唯一）',
    `difficulty` ENUM('L1','L2','L3') NOT NULL DEFAULT 'L1' COMMENT '难度层级：L1-基础，L2-进阶，L3-高级',
    `description` TEXT COMMENT '知识点详细描述（给大模型看的，这部分最重要）',
    `keywords` VARCHAR(500) COMMENT '关键词，逗号分隔，用于检索',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY `idx_course` (`course_id`),
    KEY `idx_parent` (`parent_id`),
    UNIQUE KEY `uk_code` (`code`),
    CONSTRAINT `fk_kp_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识点表';

-- 前置依赖关系表
CREATE TABLE IF NOT EXISTS `knowledge_edge` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '关系ID',
    `from_kp_id` BIGINT NOT NULL COMMENT '前置知识点（必须先掌握）',
    `to_kp_id` BIGINT NOT NULL COMMENT '目标知识点（依赖前置）',
    `edge_type` VARCHAR(20) DEFAULT 'requires' COMMENT '关系类型：requires-必须，suggests-建议',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_edge` (`from_kp_id`, `to_kp_id`),
    KEY `idx_from` (`from_kp_id`),
    KEY `idx_to` (`to_kp_id`),
    CONSTRAINT `fk_edge_from` FOREIGN KEY (`from_kp_id`) REFERENCES `knowledge_point` (`id`),
    CONSTRAINT `fk_edge_to` FOREIGN KEY (`to_kp_id`) REFERENCES `knowledge_point` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识点前置依赖关系';

-- =====================================================
-- B3: 用户学习画像表
-- =====================================================

CREATE TABLE IF NOT EXISTS `user_profile` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '画像ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `dimensions` JSON NOT NULL COMMENT '多维画像数据（JSON格式）',
    `updated_by_session_id` BIGINT COMMENT '最近更新的对话会话ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_user` (`user_id`),
    CONSTRAINT `fk_profile_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户学习画像';

-- =====================================================
-- B4: 学习路径 + 生成资源表
-- =====================================================

-- 学习路径表
CREATE TABLE IF NOT EXISTS `learning_path` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '路径ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `session_id` BIGINT COMMENT '生成路径的对话会话ID',
    `status` ENUM('active','completed','abandoned') DEFAULT 'active' COMMENT '状态：active-进行中，completed-已完成，abandoned-已放弃',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_user` (`user_id`),
    INDEX `idx_course` (`course_id`),
    CONSTRAINT `fk_path_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
    CONSTRAINT `fk_path_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习路径';

-- 路径节点表
CREATE TABLE IF NOT EXISTS `path_node` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '节点ID',
    `path_id` BIGINT NOT NULL COMMENT '所属路径ID',
    `node_order` INT NOT NULL COMMENT '节点序号',
    `title` VARCHAR(200) NOT NULL COMMENT '节点标题',
    `node_type` ENUM('review','new_learn','reinforce') NOT NULL COMMENT '学习类型：review-复习，new_learn-新学，reinforce-强化',
    `estimated_minutes` INT NOT NULL COMMENT '预估学习时长（分钟）',
    `reason` TEXT COMMENT '安排此节点的原因（AI生成）',
    `status` ENUM('pending','in_progress','completed') DEFAULT 'pending' COMMENT '状态',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_path` (`path_id`),
    CONSTRAINT `fk_node_path` FOREIGN KEY (`path_id`) REFERENCES `learning_path` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='路径节点';

-- 路径节点知识点关联表
CREATE TABLE IF NOT EXISTS `path_node_kp` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '关联ID',
    `path_node_id` BIGINT NOT NULL COMMENT '路径节点ID',
    `knowledge_point_id` BIGINT NOT NULL COMMENT '知识点ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_node_kp` (`path_node_id`, `knowledge_point_id`),
    KEY `idx_kp` (`knowledge_point_id`),
    CONSTRAINT `fk_nkp_node` FOREIGN KEY (`path_node_id`) REFERENCES `path_node` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_nkp_kp` FOREIGN KEY (`knowledge_point_id`) REFERENCES `knowledge_point` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='路径节点关联的知识点';

-- AI生成的个性化学习资源表
CREATE TABLE IF NOT EXISTS `generated_resource` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '资源ID',
    `path_node_id` BIGINT NOT NULL COMMENT '关联的路径节点',
    `knowledge_point_id` BIGINT COMMENT '关联的知识点',
    `resource_type` ENUM('document','quiz','mindmap','reading','video_script') NOT NULL COMMENT '资源类型',
    `title` VARCHAR(200) NOT NULL COMMENT '资源标题',
    `content_json` JSON COMMENT '资源内容（JSON格式）',
    `content_url` VARCHAR(500) COMMENT '如果存MinIO，放外部URL',
    `difficulty` ENUM('L1','L2','L3') COMMENT '难度层级',
    `generated_by_session_id` BIGINT COMMENT '生成资源的对话会话ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_node` (`path_node_id`),
    INDEX `idx_kp` (`knowledge_point_id`),
    CONSTRAINT `fk_resource_node` FOREIGN KEY (`path_node_id`) REFERENCES `path_node` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_resource_kp` FOREIGN KEY (`knowledge_point_id`) REFERENCES `knowledge_point` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI生成的个性化学习资源';

-- =====================================================
-- B5: 对话会话表
-- =====================================================

-- Agent对话会话表
CREATE TABLE IF NOT EXISTS `agent_session` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '会话ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `title` VARCHAR(200) COMMENT '会话标题',
    `status` ENUM('active','completed') DEFAULT 'active' COMMENT '状态：active-进行中，completed-已完成',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_user` (`user_id`),
    INDEX `idx_status` (`status`),
    CONSTRAINT `fk_session_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Agent对话会话';

-- 对话消息记录表
CREATE TABLE IF NOT EXISTS `agent_message` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '消息ID',
    `session_id` BIGINT NOT NULL COMMENT '会话ID',
    `role` ENUM('user','assistant','system') NOT NULL COMMENT '角色：user-用户，assistant-助手，system-系统',
    `content` TEXT NOT NULL COMMENT '消息内容',
    `phase` VARCHAR(50) COMMENT '消息所属阶段（profiling/planning/generating等）',
    `metadata` JSON COMMENT '附加元数据',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_session` (`session_id`),
    INDEX `idx_role` (`role`),
    CONSTRAINT `fk_message_session` FOREIGN KEY (`session_id`) REFERENCES `agent_session` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对话消息记录';

-- =====================================================
-- B6: 现有questions表增加knowledge_point_id字段
-- =====================================================

ALTER TABLE `questions` ADD COLUMN `knowledge_point_id` BIGINT COMMENT '关联的知识点ID' AFTER `category_id`;
ALTER TABLE `questions` ADD INDEX `idx_kp` (`knowledge_point_id`);
ALTER TABLE `questions` ADD CONSTRAINT `fk_question_kp` FOREIGN KEY (`knowledge_point_id`) REFERENCES `knowledge_point` (`id`);
