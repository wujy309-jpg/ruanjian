package com.exam.service.agent;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.exam.entity.GeneratedResource;
import com.exam.service.GeneratedResourceService;
import com.exam.service.LlmClient;
import com.exam.service.agent.model.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 资源生成Agent
 * 负责根据知识点生成学习资源（文档、练习题、思维导图）
 * 优先使用预存的资源，没有再调LLM生成
 */
@Component
public class GenAgent implements LearningAgent {

    private static final Logger log = LoggerFactory.getLogger(GenAgent.class);

    @Autowired
    private LlmClient llmClient;

    @Autowired
    private GeneratedResourceService generatedResourceService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String SYSTEM_PROMPT = "你是学习资源生成助手。根据要求生成学习资料。\n" +
            "文档：{\"type\":\"document\",\"title\":\"标题\",\"content\":\"# 标题\\n\\nMarkdown内容\"}\n" +
            "练习题：{\"type\":\"quiz\",\"title\":\"标题\",\"content\":{\"questions\":[{\"content\":\"题目\",\"type\":\"CHOICE\",\"difficulty\":\"EASY\",\"options\":[{\"content\":\"A\",\"isCorrect\":true}],\"analysis\":\"解析\"}]}}\n" +
            "思维导图：{\"type\":\"mindmap\",\"title\":\"标题\",\"content\":{\"topic\":\"主题\",\"children\":[{\"topic\":\"子主题\"}]}}";

    @Override
    public String getRole() {
        return "GenAgent";
    }

    @Override
    public String buildSystemPrompt(AgentContext ctx) {
        return SYSTEM_PROMPT;
    }

    @Override
    public AgentOutput execute(AgentInput input) {
        try {
            String systemPrompt = buildSystemPrompt(input.getContext());
            String userPrompt = input.getUserMessage();

            String response = llmClient.chat(systemPrompt, List.of(Map.of("role", "user", "content", userPrompt)));

            String jsonStr = extractJson(response);
            JSONObject parsed = JSON.parseObject(jsonStr);

            JsonNode structuredData = objectMapper.readTree(jsonStr);

            AgentOutput output = new AgentOutput();
            output.setAgentRole(getRole());
            output.setStatus("success");
            output.setStructuredData(structuredData);
            output.setRawResponse(response);

            return output;
        } catch (Exception e) {
            log.error("GenAgent execution failed", e);
            AgentOutput output = new AgentOutput();
            output.setAgentRole(getRole());
            output.setStatus("error");
            output.setRawResponse("资源生成失败: " + e.getMessage());
            return output;
        }
    }

    /**
     * 为指定节点生成单个资源
     * 优先使用预存资源，没有再调LLM
     */
    public AgentOutput generateResource(String nodeTitle, String resourceType, String kpInfo, Long pathNodeId) {
        // 1. 先检查是否有预存资源（根据标题匹配）
        GeneratedResource existing = findExistingResourceByTitle(nodeTitle, resourceType);
        if (existing != null) {
            log.info("Using pre-existing resource for node: {}, type: {}", nodeTitle, resourceType);
            AgentOutput output = new AgentOutput();
            output.setAgentRole(getRole());
            output.setStatus("success");
            output.setStructuredData(existing.getContentJson());
            output.setRawResponse("使用预存资源");
            return output;
        }

        // 2. 没有预存资源，调LLM生成
        log.info("Generating new resource for node: {}, type: {}", nodeTitle, resourceType);
        String typeDesc = switch (resourceType) {
            case "document" -> "Markdown格式的讲解文档";
            case "quiz" -> "3道练习题";
            case "mindmap" -> "思维导图树形JSON";
            default -> resourceType;
        };
        String prompt = "请为以下节点生成" + typeDesc + "：\n节点：" + nodeTitle + "\n知识点：" + (kpInfo.isEmpty() ? "无" : kpInfo);

        AgentInput input = new AgentInput();
        input.setUserMessage(prompt);
        return execute(input);
    }

    /**
     * 根据标题查找已存在的资源
     */
    private GeneratedResource findExistingResourceByTitle(String nodeTitle, String resourceType) {
        try {
            // 使用模糊匹配标题
            List<GeneratedResource> resources = generatedResourceService.getResourcesByTitle(nodeTitle);
            return resources.stream()
                    .filter(r -> resourceType.equals(r.getResourceType()))
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            log.warn("Failed to find existing resource by title", e);
            return null;
        }
    }

    /**
     * 解析资源报告
     */
    public ResourceReport parseResourceReport(AgentOutput output, Long pathNodeId, String resourceType) {
        try {
            JsonNode data = output.getStructuredData();
            ResourceReport report = new ResourceReport();
            report.setPathNodeId(pathNodeId);
            report.setResourceType(resourceType);
            report.setTitle(data.has("title") ? data.get("title").asText() : "");
            report.setContent(data.has("content") ? data.get("content") : data);
            report.setDifficulty("L1");
            return report;
        } catch (Exception e) {
            log.error("Failed to parse resource report", e);
            return null;
        }
    }

    /**
     * 从响应中提取JSON
     */
    private String extractJson(String text) {
        int s = text.indexOf("```json"), e = text.lastIndexOf("```");
        if (s != -1 && e > s) return text.substring(s + 7, e).trim();
        s = text.indexOf("{");
        e = text.lastIndexOf("}");
        if (s != -1 && e > s) return text.substring(s, e + 1).trim();
        return text;
    }
}
