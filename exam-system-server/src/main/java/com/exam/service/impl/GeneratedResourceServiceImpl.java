package com.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.exam.entity.GeneratedResource;
import com.exam.mapper.GeneratedResourceMapper;
import com.exam.service.GeneratedResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * AI生成的个性化学习资源Service实现类
 */
@Service
public class GeneratedResourceServiceImpl implements GeneratedResourceService {

    @Autowired
    private GeneratedResourceMapper generatedResourceMapper;

    @Override
    @Transactional
    public void batchCreateResources(List<GeneratedResource> resources) {
        for (GeneratedResource resource : resources) {
            generatedResourceMapper.insert(resource);
        }
    }

    @Override
    public List<GeneratedResource> getResourcesByPathNodeId(Long pathNodeId) {
        return generatedResourceMapper.selectByPathNodeId(pathNodeId);
    }

    @Override
    public List<GeneratedResource> getResourcesByKnowledgePointId(Long kpId) {
        return generatedResourceMapper.selectByKnowledgePointId(kpId);
    }

    @Override
    public GeneratedResource getResourceById(Long id) {
        return generatedResourceMapper.selectById(id);
    }

    @Override
    public List<GeneratedResource> getResourcesByTitle(String title) {
        // 模糊匹配标题
        LambdaQueryWrapper<GeneratedResource> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(GeneratedResource::getTitle, title);
        return generatedResourceMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public void deleteResource(Long id) {
        generatedResourceMapper.deleteById(id);
    }
}
