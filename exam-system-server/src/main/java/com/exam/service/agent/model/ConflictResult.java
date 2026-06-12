package com.exam.service.agent.model;

import java.util.List;

/**
 * 冲突检测结果
 */
public class ConflictResult {

    private int score;
    private List<String> missingCoverage;
    private List<String> difficultyMismatch;
    private boolean needRegenerate;

    public ConflictResult() {
    }

    public ConflictResult(int score, List<String> missingCoverage, List<String> difficultyMismatch) {
        this.score = score;
        this.missingCoverage = missingCoverage;
        this.difficultyMismatch = difficultyMismatch;
        this.needRegenerate = score > 30;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public List<String> getMissingCoverage() {
        return missingCoverage;
    }

    public void setMissingCoverage(List<String> missingCoverage) {
        this.missingCoverage = missingCoverage;
    }

    public List<String> getDifficultyMismatch() {
        return difficultyMismatch;
    }

    public void setDifficultyMismatch(List<String> difficultyMismatch) {
        this.difficultyMismatch = difficultyMismatch;
    }

    public boolean isNeedRegenerate() {
        return needRegenerate;
    }

    public void setNeedRegenerate(boolean needRegenerate) {
        this.needRegenerate = needRegenerate;
    }
}
