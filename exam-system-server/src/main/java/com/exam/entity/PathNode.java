package com.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 路径节点实体类
 * 路径节点是学习路径中的一个学习单元，包含学习类型、时长和关联的知识点
 */
@Data
@TableName("path_node")
@Schema(description = "路径节点")
public class PathNode {

    @Schema(description = "节点ID，唯一标识", example = "1")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "所属路径ID", example = "1")
    private Long pathId;

    @Schema(description = "节点序号", example = "1")
    @JsonAlias("order")
    private Integer nodeOrder;

    @Schema(description = "节点标题", example = "复习类与对象")
    private String title;

    @Schema(description = "学习类型：review-复习，new_learn-新学，reinforce-强化", example = "review")
    @JsonAlias("type")
    private String nodeType;

    @Schema(description = "预估学习时长（分钟）", example = "30")
    private Integer estimatedMinutes;

    @Schema(description = "安排此节点的原因（AI生成）", example = "这是面向对象的基础，需要先掌握")
    private String reason;

    @Schema(description = "状态：pending-待开始，in_progress-进行中，completed-已完成", example = "pending")
    private String status;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    // ========== 非数据库字段 ==========

    @Schema(description = "关联的知识点列表")
    @TableField(exist = false)
    private List<KnowledgePoint> knowledgePoints;

    @Schema(description = "关联的知识点ID列表")
    @TableField(exist = false)
    private List<Long> knowledgePointIds;

    @Schema(description = "该节点的生成资源列表")
    @TableField(exist = false)
    private List<GeneratedResource> resources;
}
