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
 * 用户学习画像实体类
 * 存储用户的多维学习画像数据，使用JSON字段存储灵活的维度数据
 */
@Data
@TableName(value = "user_profile", autoResultMap = true)
@Schema(description = "用户学习画像")
public class UserProfile {

    @Schema(description = "画像ID，唯一标识", example = "1")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "多维画像数据（JSON格式）")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private JsonNode dimensions;

    @Schema(description = "最近更新的对话会话ID", example = "1")
    private Long updatedBySessionId;

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
}
