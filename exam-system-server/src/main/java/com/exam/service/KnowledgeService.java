package com.exam.service;

import com.exam.entity.Course;
import com.exam.entity.KnowledgeEdge;
import com.exam.entity.KnowledgePoint;

import java.util.List;
import java.util.Map;

/**
 * 知识图谱Service接口
 */
public interface KnowledgeService {

    /**
     * 获取课程完整知识图谱（树形 + 依赖关系）
     */
    Map<String, Object> getKnowledgeGraph(Long courseId);

    /**
     * 获取课程列表
     */
    List<Course> getAllCourses();

    /**
     * 根据ID获取课程详情
     */
    Course getCourseById(Long id);

    /**
     * 创建课程
     */
    void createCourse(Course course);

    /**
     * 更新课程
     */
    void updateCourse(Course course);

    /**
     * 删除课程
     */
    void deleteCourse(Long id);

    /**
     * 获取知识点列表（按课程）
     */
    List<KnowledgePoint> getKnowledgePointsByCourse(Long courseId);

    /**
     * 根据ID获取知识点详情
     */
    KnowledgePoint getKnowledgePointById(Long id);

    /**
     * 根据编码获取知识点详情
     */
    KnowledgePoint getKnowledgePointByCode(String code);

    /**
     * 获取知识点树形结构（前端用）
     */
    Map<String, Object> getKnowledgeTree(Long courseId);

    /**
     * 获取某知识点的前置依赖链
     */
    List<KnowledgePoint> getPrerequisiteChain(Long kpId);

    /**
     * 新增知识点
     */
    void createKnowledgePoint(KnowledgePoint knowledgePoint);

    /**
     * 修改知识点
     */
    void updateKnowledgePoint(KnowledgePoint knowledgePoint);

    /**
     * 删除知识点
     */
    void deleteKnowledgePoint(Long id);

    /**
     * 新增依赖关系
     */
    void createEdge(KnowledgeEdge edge);

    /**
     * 删除依赖关系
     */
    void deleteEdge(Long fromKpId, Long toKpId);

    /**
     * 获取某个知识点的所有前置依赖
     */
    List<KnowledgeEdge> getPrerequisites(Long kpId);

    /**
     * 获取某个知识点的所有后置依赖
     */
    List<KnowledgeEdge> getDependents(Long kpId);
}
