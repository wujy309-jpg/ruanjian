package com.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 知识点前置依赖关系实体类
 * 表示知识点之间的依赖关系，形成有向无环图（DAG）
 */
@Data
@TableName("knowledge_edge")
@Schema(description = "知识点前置依赖关系")
public class KnowledgeEdge {

    @Schema(description = "关系ID，唯一标识", example = "1")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "前置知识点（必须先掌握）", example = "1")
    private Long fromKpId;

    @Schema(description = "目标知识点（依赖前置）", example = "2")
    private Long toKpId;

    @Schema(description = "关系类型：requires-必须，suggests-建议", example = "requires")
    private String edgeType;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    // ========== 非数据库字段 ==========

    @Schema(description = "前置知识点编码")
    @TableField(exist = false)
    private String fromKpCode;

    @Schema(description = "前置知识点名称")
    @TableField(exist = false)
    private String fromKpName;

    @Schema(description = "目标知识点编码")
    @TableField(exist = false)
    private String toKpCode;

    @Schema(description = "目标知识点名称")
    @TableField(exist = false)
    private String toKpName;
}
