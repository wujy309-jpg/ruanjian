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
 * 对话消息记录实体类
 * 记录用户与AI Agent的每一条对话消息
 */
@Data
@TableName(value = "agent_message", autoResultMap = true)
@Schema(description = "对话消息记录")
public class AgentMessage {

    @Schema(description = "消息ID，唯一标识", example = "1")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "会话ID", example = "1")
    private Long sessionId;

    @Schema(description = "角色：user-用户，assistant-助手，system-系统", example = "user")
    private String role;

    @Schema(description = "消息内容", example = "我想学习Java面向对象编程")
    private String content;

    @Schema(description = "消息所属阶段（profiling/planning/generating等）", example = "profiling")
    private String phase;

    @Schema(description = "附加元数据")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private JsonNode metadata;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    // ========== 非数据库字段 ==========

    @Schema(description = "会话标题")
    @TableField(exist = false)
    private String sessionTitle;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getPhase() { return phase; }
    public void setPhase(String phase) { this.phase = phase; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
