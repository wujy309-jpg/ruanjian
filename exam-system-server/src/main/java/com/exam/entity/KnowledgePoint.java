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
 * 知识点实体类
 * 知识点是知识图谱的最小学习单元，通过parent_id实现树形分级
 * 通过knowledge_edge表实现知识点之间的依赖关系
 */
@Data
@TableName("knowledge_point")
@Schema(description = "知识点信息")
public class KnowledgePoint {

    @Schema(description = "知识点ID，唯一标识", example = "1")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "所属课程ID", example = "1")
    private Long courseId;

    @Schema(description = "父知识点ID（树形分级，可空）", example = "null")
    private Long parentId;

    @Schema(description = "知识点名称（给人看的）", example = "类与对象")
    private String name;

    @Schema(description = "知识点编码（给程序用的，唯一）", example = "java-oop-01")
    private String code;

    @Schema(description = "难度层级：L1-基础，L2-进阶，L3-高级", example = "L1")
    private String difficulty;

    @Schema(description = "知识点详细描述（给大模型看的，这部分最重要）", example = "## 核心概念\n类是Java中对象的模板...")
    private String description;

    @Schema(description = "关键词，逗号分隔，用于检索", example = "类,对象,实例化,成员变量")
    private String keywords;

    @Schema(description = "排序", example = "0")
    private Integer sortOrder;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    // ========== 非数据库字段 ==========

    @Schema(description = "父知识点名称")
    @TableField(exist = false)
    private String parentName;

    @Schema(description = "子知识点列表")
    @TableField(exist = false)
    private List<KnowledgePoint> children;

    @Schema(description = "前置知识点列表")
    @TableField(exist = false)
    private List<KnowledgePoint> prerequisites;

    @Schema(description = "前置知识点编码列表")
    @TableField(exist = false)
    private List<String> prerequisiteCodes;

    @Schema(description = "课程名称")
    @TableField(exist = false)
    private String courseName;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public java.util.List<KnowledgePoint> getChildren() { return children; }
    public void setChildren(java.util.List<KnowledgePoint> children) { this.children = children; }
    public java.util.List<String> getPrerequisiteCodes() { return prerequisiteCodes; }
    public void setPrerequisiteCodes(java.util.List<String> prerequisiteCodes) { this.prerequisiteCodes = prerequisiteCodes; }

}