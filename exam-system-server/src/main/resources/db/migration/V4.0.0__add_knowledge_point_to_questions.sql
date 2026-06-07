USE exam_system;

-- 为 questions 表添加知识点关联字段，支持 GenAgent 批量为知识点生成题目
ALTER TABLE `questions`
ADD COLUMN `knowledge_point_id` BIGINT DEFAULT NULL COMMENT '关联的知识点ID（GenAgent生成题目时使用）' AFTER `category_id`,
ADD KEY `idx_knowledge_point` (`knowledge_point_id`),
ADD CONSTRAINT `fk_question_kp` FOREIGN KEY (`knowledge_point_id`) REFERENCES `knowledge_point` (`id`) ON DELETE SET NULL;

-- 放宽 category_id 约束，允许 AI 生成题目时仅关联知识点而不设分类
ALTER TABLE `questions`
MODIFY COLUMN `category_id` BIGINT DEFAULT NULL COMMENT '分类ID（AI生成题目时可为空）';
