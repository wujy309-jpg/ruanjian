package com.exam.controller;

import com.exam.common.Result;
import com.exam.entity.LearningPath;
import com.exam.entity.PathNode;
import com.exam.service.LearningPathService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学习路径控制器
 * 处理学习路径相关的HTTP请求
 */
@RestController
@RequestMapping("/api/learning-path")
@Tag(name = "学习路径管理", description = "学习路径相关操作，包括路径的创建、查询和节点管理")
public class LearningPathController {

    @Autowired
    private LearningPathService learningPathService;

    @PostMapping
    @Operation(summary = "创建学习路径", description = "创建新的学习路径，A的Agent写入路径使用")
    public Result<Void> createLearningPath(@RequestBody LearningPath path) {
        learningPathService.createLearningPath(path);
        return Result.success(null);
    }

    @GetMapping("/{pathId}")
    @Operation(summary = "获取路径详情", description = "获取学习路径详情含节点，C的前端读取路径使用")
    public Result<LearningPath> getLearningPathDetail(
            @Parameter(description = "路径ID") @PathVariable Long pathId) {
        return Result.success(learningPathService.getLearningPathDetail(pathId));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户所有路径", description = "获取指定用户的所有学习路径列表")
    public Result<List<LearningPath>> getLearningPathsByUser(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        return Result.success(learningPathService.getLearningPathsByUser(userId));
    }

    @GetMapping("/user/{userId}/course/{courseId}")
    @Operation(summary = "获取用户在某课程的活跃路径", description = "获取用户在指定课程的活跃学习路径")
    public Result<LearningPath> getActiveLearningPath(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "课程ID") @PathVariable Long courseId) {
        return Result.success(learningPathService.getActiveLearningPath(userId, courseId));
    }

    @PutMapping("/{pathId}/status")
    @Operation(summary = "更新路径状态", description = "更新学习路径的状态")
    public Result<Void> updatePathStatus(
            @Parameter(description = "路径ID") @PathVariable Long pathId,
            @Parameter(description = "状态") @RequestParam String status) {
        learningPathService.updatePathStatus(pathId, status);
        return Result.success(null);
    }

    @PostMapping("/{pathId}/nodes")
    @Operation(summary = "添加路径节点", description = "向学习路径中添加新的节点")
    public Result<Void> addPathNode(
            @Parameter(description = "路径ID") @PathVariable Long pathId,
            @RequestBody PathNode node) {
        node.setPathId(pathId);
        learningPathService.addPathNode(node);
        return Result.success(null);
    }

    @PutMapping("/nodes/{nodeId}/status")
    @Operation(summary = "更新节点状态", description = "更新路径节点的状态")
    public Result<Void> updateNodeStatus(
            @Parameter(description = "节点ID") @PathVariable Long nodeId,
            @Parameter(description = "状态") @RequestParam String status) {
        learningPathService.updateNodeStatus(nodeId, status);
        return Result.success(null);
    }

    @DeleteMapping("/{pathId}")
    @Operation(summary = "删除学习路径", description = "删除指定的学习路径及其所有节点")
    public Result<Void> deleteLearningPath(
            @Parameter(description = "路径ID") @PathVariable Long pathId) {
        learningPathService.deleteLearningPath(pathId);
        return Result.success(null);
    }
}
