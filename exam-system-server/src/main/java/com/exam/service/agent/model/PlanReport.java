package com.exam.service.agent.model;

import java.util.List;

/**
 * 路径报告 - PlanAgent的输出
 */
public class PlanReport {

    private String title;
    private Long courseId;
    private List<PathNodeInfo> nodes;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public List<PathNodeInfo> getNodes() {
        return nodes;
    }

    public void setNodes(List<PathNodeInfo> nodes) {
        this.nodes = nodes;
    }

    /**
     * 获取所有知识点ID
     */
    public List<Long> getAllKnowledgePointIds() {
        return nodes.stream()
                .flatMap(n -> n.getKnowledgePointIds().stream())
                .distinct()
                .toList();
    }

    /**
     * 路径节点信息
     */
    public static class PathNodeInfo {
        private Integer order;
        private String title;
        private String type;
        private Integer estimatedMinutes;
        private String reason;
        private List<Long> knowledgePointIds;

        public Integer getOrder() {
            return order;
        }

        public void setOrder(Integer order) {
            this.order = order;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Integer getEstimatedMinutes() {
            return estimatedMinutes;
        }

        public void setEstimatedMinutes(Integer estimatedMinutes) {
            this.estimatedMinutes = estimatedMinutes;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public List<Long> getKnowledgePointIds() {
            return knowledgePointIds;
        }

        public void setKnowledgePointIds(List<Long> knowledgePointIds) {
            this.knowledgePointIds = knowledgePointIds;
        }
    }
}
