package com.exam.controller;

import com.exam.common.Result;
import com.exam.entity.GeneratedResource;
import com.exam.service.GeneratedResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 生成资源控制器
 * 处理AI生成的学习资源相关的HTTP请求
 */
@RestController
@RequestMapping("/api/generated-resources")
@Tag(name = "生成资源管理", description = "AI生成的学习资源相关操作，包括资源的写入和查询")
public class ResourceController {

    @Autowired
    private GeneratedResourceService generatedResourceService;

    @PostMapping
    @Operation(summary = "批量写入生成资源", description = "批量写入AI生成的学习资源，A的Agent写入资源使用。支持契约格式：{resources:[...]}")
    public Result<Void> batchCreateResources(@RequestBody ResourceBatchRequest body) {
        generatedResourceService.batchCreateResources(body.getResources());
        return Result.success(null);
    }

    @Data
    public static class ResourceBatchRequest {
        private List<GeneratedResource> resources;
    }

    @GetMapping
    @Operation(summary = "获取资源列表", description = "根据路径节点ID获取资源列表，C的前端读取资源使用")
    public Result<List<GeneratedResource>> getResources(
            @Parameter(description = "路径节点ID") @RequestParam(required = false) Long pathNodeId,
            @Parameter(description = "知识点ID") @RequestParam(required = false) Long knowledgePointId) {
        if (pathNodeId != null) {
            return Result.success(generatedResourceService.getResourcesByPathNodeId(pathNodeId));
        } else if (knowledgePointId != null) {
            return Result.success(generatedResourceService.getResourcesByKnowledgePointId(knowledgePointId));
        } else {
            return Result.error("请提供pathNodeId或knowledgePointId参数");
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取资源详情", description = "根据资源ID获取资源详细信息")
    public Result<GeneratedResource> getResourceById(
            @Parameter(description = "资源ID") @PathVariable Long id) {
        return Result.success(generatedResourceService.getResourceById(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除资源", description = "删除指定的生成资源")
    public Result<Void> deleteResource(
            @Parameter(description = "资源ID") @PathVariable Long id) {
        generatedResourceService.deleteResource(id);
        return Result.success(null);
    }
}
