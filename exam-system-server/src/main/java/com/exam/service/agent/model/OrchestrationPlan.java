package com.exam.service.agent.model;

import java.util.List;

/**
 * 调度计划 - Orchestrator生成的执行计划
 */
public class OrchestrationPlan {

    private String complexity;
    private List<PlanStep> plan;

    public String getComplexity() {
        return complexity;
    }

    public void setComplexity(String complexity) {
        this.complexity = complexity;
    }

    public List<PlanStep> getPlan() {
        return plan;
    }

    public void setPlan(List<PlanStep> plan) {
        this.plan = plan;
    }

    /**
     * 计划步骤
     */
    public static class PlanStep {
        private Integer step;
        private String agent;
        private String task;
        private List<Integer> dependsOn;
        private StepLocalContext localContext;
        private Boolean parallel;

        public Integer getStep() {
            return step;
        }

        public void setStep(Integer step) {
            this.step = step;
        }

        public String getAgent() {
            return agent;
        }

        public void setAgent(String agent) {
            this.agent = agent;
        }

        public String getTask() {
            return task;
        }

        public void setTask(String task) {
            this.task = task;
        }

        public List<Integer> getDependsOn() {
            return dependsOn;
        }

        public void setDependsOn(List<Integer> dependsOn) {
            this.dependsOn = dependsOn;
        }

        public StepLocalContext getLocalContext() {
            return localContext;
        }

        public void setLocalContext(StepLocalContext localContext) {
            this.localContext = localContext;
        }

        public Boolean getParallel() {
            return parallel;
        }

        public void setParallel(Boolean parallel) {
            this.parallel = parallel;
        }
    }

    /**
     * 步骤局部上下文
     */
    public static class StepLocalContext {
        private String mode;
        private Long courseId;
        private String targetLevel;
        private List<String> resourceTypes;

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public Long getCourseId() {
            return courseId;
        }

        public void setCourseId(Long courseId) {
            this.courseId = courseId;
        }

        public String getTargetLevel() {
            return targetLevel;
        }

        public void setTargetLevel(String targetLevel) {
            this.targetLevel = targetLevel;
        }

        public List<String> getResourceTypes() {
            return resourceTypes;
        }

        public void setResourceTypes(List<String> resourceTypes) {
            this.resourceTypes = resourceTypes;
        }
    }
}
