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
 * 学习路径实体类
 * 学习路径是AI根据用户画像和知识图谱生成的个性化学习计划
 */
@Data
@TableName("learning_path")
@Schema(description = "学习路径")
public class LearningPath {

    @Schema(description = "路径ID，唯一标识", example = "1")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "课程ID", example = "1")
    private Long courseId;

    @Schema(description = "生成路径的对话会话ID", example = "1")
    private Long sessionId;

    @Schema(description = "状态：active-进行中，completed-已完成，abandoned-已放弃", example = "active")
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

    @Schema(description = "课程名称")
    @TableField(exist = false)
    private String courseName;

    @Schema(description = "路径节点列表")
    @TableField(exist = false)
    private List<PathNode> nodes;

    @Schema(description = "节点总数")
    @TableField(exist = false)
    private Integer nodeCount;

    @Schema(description = "已完成节点数")
    @TableField(exist = false)
    private Integer completedNodeCount;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public java.time.LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.time.LocalDateTime createdAt) { this.createdAt = createdAt; }
    public java.time.LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(java.time.LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public java.util.List<PathNode> getNodes() { return nodes; }
    public void setNodes(java.util.List<PathNode> nodes) { this.nodes = nodes; }
    public Integer getNodeCount() { return nodeCount; }
    public void setNodeCount(Integer nodeCount) { this.nodeCount = nodeCount; }

}