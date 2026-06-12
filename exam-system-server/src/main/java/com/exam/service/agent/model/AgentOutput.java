package com.exam.service.agent.model;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Agent输出
 */
public class AgentOutput {

    private String agentRole;
    private String status;
    private JsonNode structuredData;
    private String rawResponse;
    private Integer tokensUsed;

    public AgentOutput() {
    }

    public AgentOutput(String agentRole, String status, JsonNode structuredData, String rawResponse) {
        this.agentRole = agentRole;
        this.status = status;
        this.structuredData = structuredData;
        this.rawResponse = rawResponse;
    }

    public String getAgentRole() {
        return agentRole;
    }

    public void setAgentRole(String agentRole) {
        this.agentRole = agentRole;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public JsonNode getStructuredData() {
        return structuredData;
    }

    public void setStructuredData(JsonNode structuredData) {
        this.structuredData = structuredData;
    }

    public String getRawResponse() {
        return rawResponse;
    }

    public void setRawResponse(String rawResponse) {
        this.rawResponse = rawResponse;
    }

    public Integer getTokensUsed() {
        return tokensUsed;
    }

    public void setTokensUsed(Integer tokensUsed) {
        this.tokensUsed = tokensUsed;
    }
}
