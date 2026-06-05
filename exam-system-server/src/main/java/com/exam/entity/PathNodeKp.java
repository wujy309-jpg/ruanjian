package com.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 路径节点知识点关联实体类
 */
@Data
@TableName("path_node_kp")
@Schema(description = "路径节点知识点关联")
public class PathNodeKp {

    @Schema(description = "关联ID，唯一标识", example = "1")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "路径节点ID", example = "1")
    private Long pathNodeId;

    @Schema(description = "知识点ID", example = "1")
    private Long knowledgePointId;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
