package com.exam.service;

import com.exam.entity.GeneratedResource;

import java.util.List;

/**
 * AI生成的个性化学习资源Service接口
 */
public interface GeneratedResourceService {

    /**
     * 批量写入生成资源
     */
    void batchCreateResources(List<GeneratedResource> resources);

    /**
     * 获取某节点的资源列表
     */
    List<GeneratedResource> getResourcesByPathNodeId(Long pathNodeId);

    /**
     * 获取某知识点的资源列表
     */
    List<GeneratedResource> getResourcesByKnowledgePointId(Long kpId);

    /**
     * 根据ID获取资源详情
     */
    GeneratedResource getResourceById(Long id);

    /**
     * 根据标题模糊查找资源
     */
    List<GeneratedResource> getResourcesByTitle(String title);

    /**
     * 删除资源
     */
    void deleteResource(Long id);
}
