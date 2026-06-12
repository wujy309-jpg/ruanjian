package com.exam.service.agent;

import com.exam.entity.LearningPath;
import com.exam.entity.PathNode;
import com.exam.service.agent.model.PlanReport;
import com.exam.service.agent.model.ResourceReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 合成引擎
 * 负责聚合路径节点和生成的资源，去重并格式化为前端友好的结构
 */
@Component
public class SynthesisEngine {

    private static final Logger log = LoggerFactory.getLogger(SynthesisEngine.class);

    /**
     * 合成学习包
     * @param path 学习路径
     * @param resources 生成的资源列表
     * @return 合成后的学习包
     */
    public LearningPackage synthesize(LearningPath path, List<ResourceReport> resources) {
        LearningPackage pkg = new LearningPackage();
        pkg.setPathId(path.getId());
        pkg.setTitle("学习计划");
        pkg.setCourseId(path.getCourseId());

        // 按路径节点分组资源
        Map<Long, List<ResourceReport>> resourcesByNode = resources.stream()
                .collect(Collectors.groupingBy(ResourceReport::getPathNodeId));

        // 构建节点包
        List<NodePackage> nodePackages = new ArrayList<>();
        if (path.getNodes() != null) {
            for (PathNode node : path.getNodes()) {
                NodePackage nodePkg = new NodePackage();
                nodePkg.setNodeId(node.getId());
                nodePkg.setTitle(node.getTitle());
                nodePkg.setNodeType(node.getNodeType());
                nodePkg.setEstimatedMinutes(node.getEstimatedMinutes());
                nodePkg.setReason(node.getReason());
                nodePkg.setStatus(node.getStatus());

                // 获取该节点的资源并去重
                List<ResourceReport> nodeResources = resourcesByNode.getOrDefault(node.getId(), List.of());
                List<ResourceReport> deduplicatedResources = deduplicateResources(nodeResources);
                nodePkg.setResources(deduplicatedResources);

                nodePackages.add(nodePkg);
            }
        }

        pkg.setNodes(nodePackages);
        pkg.setTotalResources(resources.size());
        pkg.setDeduplicatedResources(countDeduplicatedResources(resources));

        return pkg;
    }

    /**
     * 资源去重
     * 同一知识点可能被多次生成资源，只保留最新的
     */
    private List<ResourceReport> deduplicateResources(List<ResourceReport> resources) {
        Map<String, ResourceReport> uniqueResources = new LinkedHashMap<>();

        for (ResourceReport resource : resources) {
            String key = resource.getResourceType() + "_" +
                    (resource.getKnowledgePointId() != null ? resource.getKnowledgePointId() : resource.getTitle());
            uniqueResources.put(key, resource);
        }

        return new ArrayList<>(uniqueResources.values());
    }

    /**
     * 统计去重后的资源数量
     */
    private int countDeduplicatedResources(List<ResourceReport> resources) {
        Set<String> uniqueKeys = new HashSet<>();
        for (ResourceReport resource : resources) {
            String key = resource.getResourceType() + "_" +
                    (resource.getKnowledgePointId() != null ? resource.getKnowledgePointId() : resource.getTitle());
            uniqueKeys.add(key);
        }
        return uniqueKeys.size();
    }

    /**
     * 学习包
     */
    public static class LearningPackage {
        private Long pathId;
        private String title;
        private Long courseId;
        private List<NodePackage> nodes;
        private int totalResources;
        private int deduplicatedResources;

        public Long getPathId() {
            return pathId;
        }

        public void setPathId(Long pathId) {
            this.pathId = pathId;
        }

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

        public List<NodePackage> getNodes() {
            return nodes;
        }

        public void setNodes(List<NodePackage> nodes) {
            this.nodes = nodes;
        }

        public int getTotalResources() {
            return totalResources;
        }

        public void setTotalResources(int totalResources) {
            this.totalResources = totalResources;
        }

        public int getDeduplicatedResources() {
            return deduplicatedResources;
        }

        public void setDeduplicatedResources(int deduplicatedResources) {
            this.deduplicatedResources = deduplicatedResources;
        }
    }

    /**
     * 节点包
     */
    public static class NodePackage {
        private Long nodeId;
        private String title;
        private String nodeType;
        private Integer estimatedMinutes;
        private String reason;
        private String status;
        private List<ResourceReport> resources;

        public Long getNodeId() {
            return nodeId;
        }

        public void setNodeId(Long nodeId) {
            this.nodeId = nodeId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getNodeType() {
            return nodeType;
        }

        public void setNodeType(String nodeType) {
            this.nodeType = nodeType;
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

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public List<ResourceReport> getResources() {
            return resources;
        }

        public void setResources(List<ResourceReport> resources) {
            this.resources = resources;
        }
    }
}
