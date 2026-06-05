package com.exam.controller;

import com.exam.common.Result;
import com.exam.entity.Course;
import com.exam.entity.KnowledgeEdge;
import com.exam.entity.KnowledgePoint;
import com.exam.service.KnowledgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 知识图谱控制器
 * 处理知识图谱相关的HTTP请求，包括课程管理、知识点管理、依赖关系管理
 */
@RestController
@RequestMapping("/api/knowledge")
@Tag(name = "知识图谱管理", description = "知识图谱相关操作，包括课程管理、知识点CRUD、依赖关系管理等功能")
public class KnowledgeController {

    @Autowired
    private KnowledgeService knowledgeService;

    // ========== 课程管理 ==========

    @GetMapping("/courses")
    @Operation(summary = "获取课程列表", description = "获取所有课程列表")
    public Result<List<Course>> getAllCourses() {
        return Result.success(knowledgeService.getAllCourses());
    }

    @GetMapping("/courses/{id}")
    @Operation(summary = "获取课程详情", description = "根据课程ID获取课程详细信息")
    public Result<Course> getCourseById(
            @Parameter(description = "课程ID") @PathVariable Long id) {
        return Result.success(knowledgeService.getCourseById(id));
    }

    @PostMapping("/courses")
    @Operation(summary = "创建课程", description = "创建新的课程")
    public Result<Void> createCourse(@RequestBody Course course) {
        knowledgeService.createCourse(course);
        return Result.success(null);
    }

    @PutMapping("/courses/{id}")
    @Operation(summary = "更新课程", description = "修改课程信息")
    public Result<Void> updateCourse(
            @Parameter(description = "课程ID") @PathVariable Long id,
            @RequestBody Course course) {
        course.setId(id);
        knowledgeService.updateCourse(course);
        return Result.success(null);
    }

    @DeleteMapping("/courses/{id}")
    @Operation(summary = "删除课程", description = "删除指定的课程")
    public Result<Void> deleteCourse(
            @Parameter(description = "课程ID") @PathVariable Long id) {
        knowledgeService.deleteCourse(id);
        return Result.success(null);
    }

    // ========== 知识图谱查询 ==========

    @GetMapping("/graph/{courseId}")
    @Operation(summary = "获取课程完整知识图谱", description = "返回课程完整知识图谱（树形 + 依赖关系），PlanAgent需要这个")
    public Result<Map<String, Object>> getKnowledgeGraph(
            @Parameter(description = "课程ID") @PathVariable Long courseId) {
        return Result.success(knowledgeService.getKnowledgeGraph(courseId));
    }

    @GetMapping("/tree/{courseId}")
    @Operation(summary = "获取知识图谱树形结构", description = "获取知识点的层级导航，前端展示用")
    public Result<Map<String, Object>> getKnowledgeTree(
            @Parameter(description = "课程ID") @PathVariable Long courseId) {
        return Result.success(knowledgeService.getKnowledgeTree(courseId));
    }

    // ========== 知识点管理 ==========

    @GetMapping("/points/{id}")
    @Operation(summary = "获取知识点详情", description = "根据知识点ID获取详细信息，包含前置依赖和子知识点")
    public Result<KnowledgePoint> getKnowledgePointById(
            @Parameter(description = "知识点ID") @PathVariable Long id) {
        return Result.success(knowledgeService.getKnowledgePointById(id));
    }

    @GetMapping("/points/code/{code}")
    @Operation(summary = "根据编码获取知识点", description = "根据知识点编码获取详细信息，GenAgent生成题目时使用")
    public Result<KnowledgePoint> getKnowledgePointByCode(
            @Parameter(description = "知识点编码") @PathVariable String code) {
        return Result.success(knowledgeService.getKnowledgePointByCode(code));
    }

    @GetMapping("/points/course/{courseId}")
    @Operation(summary = "获取课程知识点列表", description = "获取指定课程下的所有知识点")
    public Result<List<KnowledgePoint>> getKnowledgePointsByCourse(
            @Parameter(description = "课程ID") @PathVariable Long courseId) {
        return Result.success(knowledgeService.getKnowledgePointsByCourse(courseId));
    }

    @GetMapping("/points/{id}/prerequisite-chain")
    @Operation(summary = "获取知识点前置依赖链", description = "获取某个知识点的所有前置依赖，PlanAgent验证用户画像使用")
    public Result<List<KnowledgePoint>> getPrerequisiteChain(
            @Parameter(description = "知识点ID") @PathVariable Long id) {
        return Result.success(knowledgeService.getPrerequisiteChain(id));
    }

    @PostMapping("/points")
    @Operation(summary = "新增知识点", description = "创建新的知识点")
    public Result<Void> createKnowledgePoint(@RequestBody KnowledgePoint knowledgePoint) {
        knowledgeService.createKnowledgePoint(knowledgePoint);
        return Result.success(null);
    }

    @PutMapping("/points/{id}")
    @Operation(summary = "修改知识点", description = "修改知识点信息")
    public Result<Void> updateKnowledgePoint(
            @Parameter(description = "知识点ID") @PathVariable Long id,
            @RequestBody KnowledgePoint knowledgePoint) {
        knowledgePoint.setId(id);
        knowledgeService.updateKnowledgePoint(knowledgePoint);
        return Result.success(null);
    }

    @DeleteMapping("/points/{id}")
    @Operation(summary = "删除知识点", description = "删除指定的知识点")
    public Result<Void> deleteKnowledgePoint(
            @Parameter(description = "知识点ID") @PathVariable Long id) {
        knowledgeService.deleteKnowledgePoint(id);
        return Result.success(null);
    }

    // ========== 依赖关系管理 ==========

    @GetMapping("/edges/course/{courseId}")
    @Operation(summary = "获取课程依赖关系", description = "获取指定课程的所有知识点依赖关系")
    public Result<List<KnowledgeEdge>> getEdgesByCourse(
            @Parameter(description = "课程ID") @PathVariable Long courseId) {
        return Result.success(knowledgeService.getPrerequisites(courseId));
    }

    @PostMapping("/edges")
    @Operation(summary = "新增依赖关系", description = "创建新的知识点依赖关系")
    public Result<Void> createEdge(@RequestBody KnowledgeEdge edge) {
        knowledgeService.createEdge(edge);
        return Result.success(null);
    }

    @DeleteMapping("/edges")
    @Operation(summary = "删除依赖关系", description = "删除指定的知识点依赖关系")
    public Result<Void> deleteEdge(
            @Parameter(description = "前置知识点ID") @RequestParam Long fromKpId,
            @Parameter(description = "目标知识点ID") @RequestParam Long toKpId) {
        knowledgeService.deleteEdge(fromKpId, toKpId);
        return Result.success(null);
    }
}
