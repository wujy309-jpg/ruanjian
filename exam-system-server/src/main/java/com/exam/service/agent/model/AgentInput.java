package com.exam.service.agent.model;

import java.util.List;
import java.util.Map;

/**
 * Agent输入
 */
public class AgentInput {

    private String userMessage;
    private List<Map<String, String>> conversationHistory;
    private AgentContext context;
    private String additionalData;

    public AgentInput() {
    }

    public AgentInput(String userMessage, List<Map<String, String>> conversationHistory, AgentContext context) {
        this.userMessage = userMessage;
        this.conversationHistory = conversationHistory;
        this.context = context;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public List<Map<String, String>> getConversationHistory() {
        return conversationHistory;
    }

    public void setConversationHistory(List<Map<String, String>> conversationHistory) {
        this.conversationHistory = conversationHistory;
    }

    public AgentContext getContext() {
        return context;
    }

    public void setContext(AgentContext context) {
        this.context = context;
    }

    public String getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(String additionalData) {
        this.additionalData = additionalData;
    }
}
