package com.exam.service.agent.model;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * 画像报告 - ProfileAgent的输出
 */
public class ProfileReport {

    private String level;
    private JsonNode knowledgeMap;
    private JsonNode cognitiveStyle;
    private JsonNode weakPoints;
    private JsonNode learningGoal;
    private boolean complete;

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public JsonNode getKnowledgeMap() {
        return knowledgeMap;
    }

    public void setKnowledgeMap(JsonNode knowledgeMap) {
        this.knowledgeMap = knowledgeMap;
    }

    public JsonNode getCognitiveStyle() {
        return cognitiveStyle;
    }

    public void setCognitiveStyle(JsonNode cognitiveStyle) {
        this.cognitiveStyle = cognitiveStyle;
    }

    public JsonNode getWeakPoints() {
        return weakPoints;
    }

    public void setWeakPoints(JsonNode weakPoints) {
        this.weakPoints = weakPoints;
    }

    public JsonNode getLearningGoal() {
        return learningGoal;
    }

    public void setLearningGoal(JsonNode learningGoal) {
        this.learningGoal = learningGoal;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}
