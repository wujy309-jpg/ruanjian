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
 * 课程实体类
 * 课程是知识图谱的顶层容器，一个课程对应一个完整的知识图谱
 */
@Data
@TableName("course")
@Schema(description = "课程信息")
public class Course {

    @Schema(description = "课程ID，唯一标识", example = "1")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "课程名称", example = "Java程序设计基础")
    private String name;

    @Schema(description = "课程描述", example = "本课程涵盖Java编程语言的基础知识...")
    private String description;

    @Schema(description = "状态：draft-草稿，published-已发布", example = "published")
    private String status;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    // ========== 非数据库字段 ==========

    @Schema(description = "课程下的知识点列表")
    @TableField(exist = false)
    private List<KnowledgePoint> knowledgePoints;

    @Schema(description = "知识点数量")
    @TableField(exist = false)
    private Long knowledgePointCount;
}
