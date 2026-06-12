package com.exam.service.agent.model;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * 资源报告 - GenAgent的输出
 */
public class ResourceReport {

    private Long pathNodeId;
    private Long knowledgePointId;
    private String resourceType;
    private String title;
    private JsonNode content;
    private String difficulty;

    public Long getPathNodeId() {
        return pathNodeId;
    }

    public void setPathNodeId(Long pathNodeId) {
        this.pathNodeId = pathNodeId;
    }

    public Long getKnowledgePointId() {
        return knowledgePointId;
    }

    public void setKnowledgePointId(Long knowledgePointId) {
        this.knowledgePointId = knowledgePointId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public JsonNode getContent() {
        return content;
    }

    public void setContent(JsonNode content) {
        this.content = content;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
}
