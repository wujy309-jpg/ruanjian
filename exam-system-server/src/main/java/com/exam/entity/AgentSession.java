package com.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Agent对话会话实体类
 * 记录用户与AI Agent的对话会话信息
 */
@Data
@TableName("agent_session")
@Schema(description = "Agent对话会话")
public class AgentSession {

    @Schema(description = "会话ID，唯一标识", example = "1")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "会话标题", example = "Java基础学习规划")
    private String title;

    @Schema(description = "状态：active-进行中，completed-已完成", example = "active")
    private String status;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    // ========== 非数据库字段 ==========

    @Schema(description = "用户名")
    @TableField(exist = false)
    private String username;

    @Schema(description = "真实姓名")
    @TableField(exist = false)
    private String realName;

    @Schema(description = "会话消息列表")
    @TableField(exist = false)
    private List<AgentMessage> messages;

    @Schema(description = "消息数量")
    @TableField(exist = false)
    private Integer messageCount;
}
