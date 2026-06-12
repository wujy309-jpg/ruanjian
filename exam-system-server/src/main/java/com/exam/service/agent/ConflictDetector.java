package com.exam.service.agent;

import com.exam.service.agent.model.ConflictResult;
import com.exam.service.agent.model.PlanReport;
import com.exam.service.agent.model.ResourceReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 冲突检测器
 * 负责检测路径规划和资源生成之间的不一致
 */
@Component
public class ConflictDetector {

    private static final Logger log = LoggerFactory.getLogger(ConflictDetector.class);

    /**
     * 检测冲突
     * @param plan 路径规划报告
     * @param resources 生成的资源列表
     * @param userLevel 用户水平 (L1/L2/L3)
     * @return 冲突检测结果
     */
    public ConflictResult detect(PlanReport plan, List<ResourceReport> resources, String userLevel) {
        List<String> missingCoverage = detectMissingCoverage(plan, resources);
        List<String> difficultyMismatch = detectDifficultyMismatch(resources, userLevel);

        int score = missingCoverage.size() * 10 + difficultyMismatch.size() * 5;

        ConflictResult result = new ConflictResult(score, missingCoverage, difficultyMismatch);

        log.info("Conflict detection: score={}, missing={}, mismatched={}",
                score, missingCoverage.size(), difficultyMismatch.size());

        return result;
    }

    /**
     * 检测覆盖缺失
     * PlanAgent规划的路径节点，GenAgent是否每个都生成了资源
     */
    private List<String> detectMissingCoverage(PlanReport plan, List<ResourceReport> resources) {
        if (plan == null || plan.getNodes() == null) {
            return List.of();
        }

        // 获取所有规划的知识点ID
        Set<Long> plannedKpIds = plan.getAllKnowledgePointIds().stream()
                .collect(Collectors.toSet());

        // 获取已生成资源的知识点ID
        Set<Long> generatedKpIds = resources.stream()
                .filter(r -> r.getKnowledgePointId() != null)
                .map(ResourceReport::getKnowledgePointId)
                .collect(Collectors.toSet());

        // 找出缺失的知识点
        List<String> missing = new ArrayList<>();
        for (Long kpId : plannedKpIds) {
            if (!generatedKpIds.contains(kpId)) {
                missing.add("KP_" + kpId);
            }
        }

        return missing;
    }

    /**
     * 检测难度不匹配
     * 用户水平是L1，但生成的资源标了L3
     */
    private List<String> detectDifficultyMismatch(List<ResourceReport> resources, String userLevel) {
        if (userLevel == null || resources == null) {
            return List.of();
        }

        int userLevelNum = parseLevel(userLevel);
        List<String> mismatched = new ArrayList<>();

        for (ResourceReport resource : resources) {
            if (resource.getDifficulty() != null) {
                int resourceLevel = parseLevel(resource.getDifficulty());
                // 如果资源难度超过用户水平2级以上，认为不匹配
                if (resourceLevel - userLevelNum >= 2) {
                    mismatched.add(resource.getTitle() + " (难度:" + resource.getDifficulty() + ")");
                }
            }
        }

        return mismatched;
    }

    /**
     * 解析难度级别
     */
    private int parseLevel(String level) {
        if (level == null) return 1;
        return switch (level.toUpperCase()) {
            case "L1", "初级", "EASY" -> 1;
            case "L2", "中级", "MEDIUM" -> 2;
            case "L3", "高级", "HARD" -> 3;
            default -> 1;
        };
    }

    /**
     * 判断是否需要重新生成
     */
    public boolean needRegenerate(ConflictResult result) {
        return result.isNeedRegenerate();
    }
}
