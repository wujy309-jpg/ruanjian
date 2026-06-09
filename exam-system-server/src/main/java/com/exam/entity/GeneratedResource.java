package com.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI生成的个性化学习资源实体类
 * 存储AI生成的各种类型的学习资源，包括文档、测验、思维导图等
 */
@Data
@TableName(value = "generated_resource", autoResultMap = true)
@Schema(description = "AI生成的个性化学习资源")
public class GeneratedResource {

    @Schema(description = "资源ID，唯一标识", example = "1")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "关联的路径节点", example = "1")
    private Long pathNodeId;

    @Schema(description = "关联的知识点", example = "1")
    private Long knowledgePointId;

    @Schema(description = "资源类型：document-文档，quiz-测验，mindmap-思维导图，reading-阅读材料，video_script-视频脚本", example = "document")
    private String resourceType;

    @Schema(description = "资源标题", example = "类与对象详解")
    private String title;

    @Schema(description = "资源内容（JSON格式）")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private JsonNode contentJson;

    @Schema(description = "如果存MinIO，放外部URL", example = "http://localhost:9000/resources/xxx.md")
    private String contentUrl;

    @Schema(description = "难度层级：L1-基础，L2-进阶，L3-高级", example = "L1")
    private String difficulty;

    @Schema(description = "生成资源的对话会话ID", example = "1")
    private Long generatedBySessionId;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    // ========== 非数据库字段 ==========

    @Schema(description = "知识点名称")
    @TableField(exist = false)
    private String knowledgePointName;

    @Schema(description = "知识点编码")
    @TableField(exist = false)
    private String knowledgePointCode;

    @Schema(description = "路径节点标题")
    @TableField(exist = false)
    private String pathNodeTitle;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPathNodeId() { return pathNodeId; }
    public void setPathNodeId(Long pathNodeId) { this.pathNodeId = pathNodeId; }
    public Long getKnowledgePointId() { return knowledgePointId; }
    public void setKnowledgePointId(Long knowledgePointId) { this.knowledgePointId = knowledgePointId; }
    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public com.fasterxml.jackson.databind.JsonNode getContentJson() { return contentJson; }
    public void setContentJson(com.fasterxml.jackson.databind.JsonNode contentJson) { this.contentJson = contentJson; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public java.time.LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.time.LocalDateTime createdAt) { this.createdAt = createdAt; }

}