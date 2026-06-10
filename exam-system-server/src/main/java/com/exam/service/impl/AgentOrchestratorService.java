package com.exam.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.exam.entity.*;
import com.exam.mapper.AgentMessageMapper;
import com.exam.mapper.AgentSessionMapper;
import com.exam.service.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AgentOrchestratorService {

    private static final Logger log = LoggerFactory.getLogger(AgentOrchestratorService.class);

    @Autowired private LlmClient llmClient;
    @Autowired private AgentSessionMapper agentSessionMapper;
    @Autowired private AgentMessageMapper agentMessageMapper;
    @Autowired private KnowledgeService knowledgeService;
    @Autowired private UserProfileService userProfileService;
    @Autowired private LearningPathService learningPathService;
    @Autowired private GeneratedResourceService generatedResourceService;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    private static final String PROFILING_PROMPT = "你是一个学习画像构建助手。通过对话了解用户的学习水平和偏好。\n" +
            "规则（必须严格遵守）：\n" +
            "1. 每次只问一个问题\n" +
            "2. 总共只问恰好3个问题，不能多也不能少\n" +
            "3. 前2个问题返回状态profiling，第3个问题用户回答后，第4次调用时必须返回status:complete\n" +
            "4. 需要了解：编程基础、学习目标、每周学习时长\n" +
            "还在提问时返回：{\"status\":\"profiling\",\"type\":\"question\",\"text\":\"问题\",\"options\":[\"选项1\",\"选项2\",\"选项3\",\"选项4\"]}\n" +
            "画像完成时（第3个问题回答后）必须返回：{\"status\":\"complete\",\"profile\":{\"level\":\"初级|中级|高级\",\"knowledge_map\":{\"java_basics\":{\"level\":0.5,\"label\":\"Java基础\"}},\"cognitive_style\":{\"type\":\"depth_first\",\"avg_session_min\":30},\"weak_points\":[{\"topic\":\"薄弱点\",\"level\":0.3}],\"learning_goal\":{\"target\":\"学习目标\"}}>";

    private static final String PLANNING_PROMPT = "你是学习路径规划助手。根据用户画像和知识图谱生成学习路径。\n" +
            "返回格式：{\"title\":\"路径标题\",\"courseId\":1,\"nodes\":[{\"order\":1,\"title\":\"节点标题\",\"type\":\"review|new_learn|reinforce\",\"estimatedMinutes\":30,\"reason\":\"原因\",\"knowledgePointIds\":[1,2]}]}";

    private static final String RESOURCE_PROMPT = "你是学习资源生成助手。根据要求生成学习资料。\n" +
            "文档：{\"type\":\"document\",\"title\":\"标题\",\"content\":\"# 标题\\n\\nMarkdown内容\"}\n" +
            "练习题：{\"type\":\"quiz\",\"title\":\"标题\",\"content\":{\"questions\":[{\"content\":\"题目\",\"type\":\"CHOICE\",\"difficulty\":\"EASY\",\"options\":[{\"content\":\"A\",\"isCorrect\":true}],\"analysis\":\"解析\"}]}}\n" +
            "思维导图：{\"type\":\"mindmap\",\"title\":\"标题\",\"content\":{\"topic\":\"主题\",\"children\":[{\"topic\":\"子主题\"}]}}";

    public void handleChat(Long sessionId, String message, Long userId, SseEmitter emitter) {
        try {
            // 获取或创建会话
            AgentSession session;
            if (sessionId == null) {
                session = new AgentSession();
                session.setUserId(userId);
                String title = message.length() > 50 ? message.substring(0, 50) + "..." : message;
                session.setTitle(title);
                session.setStatus("active");
                session.setCreatedAt(LocalDateTime.now());
                agentSessionMapper.insert(session);
            } else {
                session = agentSessionMapper.selectById(sessionId);
                if (session == null) {
                    sendError(emitter, "会话不存在");
                    emitter.complete();
                    return;
                }
            }

            // 保存用户消息
            AgentMessage userMsg = new AgentMessage();
            userMsg.setSessionId(session.getId());
            userMsg.setRole("user");
            userMsg.setContent(message);
            userMsg.setCreatedAt(LocalDateTime.now());
            agentMessageMapper.insert(userMsg);

            // 获取历史消息
            List<AgentMessage> history = agentMessageMapper.selectBySessionId(session.getId());
            String phase = determinePhase(history);
            log.info("Session {} in phase: {}", session.getId(), phase);

            switch (phase) {
                case "profiling" -> handleProfiling(session, history, emitter);
                case "planning" -> handlePlanning(session, userId, emitter);
                case "generating" -> handleGenerating(session, userId, emitter);
                default -> sendDone(emitter, session.getId());
            }
        } catch (Exception e) {
            log.error("Agent chat error", e);
            sendError(emitter, "处理失败: " + e.getMessage());
        } finally {
            emitter.complete();
        }
    }

    private String determinePhase(List<AgentMessage> history) {
        boolean hasDone = history.stream().anyMatch(m -> "done".equals(m.getPhase()));
        if (hasDone) return "done";

        boolean hasGenerating = history.stream().anyMatch(m -> "generating".equals(m.getPhase()));
        if (hasGenerating) return "done";

        boolean hasPlanning = history.stream().anyMatch(m -> "planning".equals(m.getPhase()));
        if (hasPlanning) return "generating";

        boolean profileComplete = history.stream()
                .filter(m -> "assistant".equals(m.getRole()))
                .anyMatch(m -> m.getContent() != null && m.getContent().contains("\"status\":\"complete\""));
        if (profileComplete) return "planning";

        return "profiling";
    }

    private void handleProfiling(AgentSession session, List<AgentMessage> history, SseEmitter emitter) throws Exception {
        sendPhase(emitter, "profiling", 0.15, null, session.getId());

        // 硬性限制：用户消息超过3条就强制完成画像
        long userMsgCount = history.stream().filter(m -> "user".equals(m.getRole())).count();
        if (userMsgCount >= 3) {
            log.info("Session {} profiling forced complete after {} user messages", session.getId(), userMsgCount);
            // 构建一个简单的画像直接完成
            JSONObject forcedProfile = new JSONObject();
            forcedProfile.put("status", "complete");
            JSONObject profileData = new JSONObject();
            profileData.put("level", "初级");
            JSONObject knowledgeMap = new JSONObject();
            JSONObject javaBasics = new JSONObject();
            javaBasics.put("level", 0.5);
            javaBasics.put("label", "Java基础");
            knowledgeMap.put("java_basics", javaBasics);
            profileData.put("knowledge_map", knowledgeMap);
            JSONObject cognitiveStyle = new JSONObject();
            cognitiveStyle.put("type", "depth_first");
            cognitiveStyle.put("avg_session_min", 30);
            profileData.put("cognitive_style", cognitiveStyle);
            profileData.put("weak_points", new JSONArray());
            JSONObject learningGoal = new JSONObject();
            learningGoal.put("target", "学习Java");
            profileData.put("learning_goal", learningGoal);
            forcedProfile.put("profile", profileData);

            saveProfile(session.getUserId(), profileData, session.getId());
            saveMessage(session.getId(), forcedProfile.toJSONString(), "profiling");

            sendPhase(emitter, "profiling", 0.30,
                    Map.of("type", "text", "text", "已完成学习画像构建！正在为您规划学习路径..."), session.getId());
            Thread.sleep(500);
            handlePlanning(session, session.getUserId(), emitter);
            return;
        }

        List<Map<String, String>> messages = history.stream()
                .filter(m -> !"system".equals(m.getRole()))
                .map(m -> Map.of("role", m.getRole(), "content", m.getContent()))
                .collect(Collectors.toList());

        String response = llmClient.chat(PROFILING_PROMPT, messages);
        String jsonStr = extractJson(response);

        if (response.contains("\"status\":\"complete\"")) {
            JSONObject parsed = JSON.parseObject(jsonStr);
            JSONObject profileData = parsed.getJSONObject("profile");

            saveProfile(session.getUserId(), profileData, session.getId());
            saveMessage(session.getId(), response, "profiling");

            sendPhase(emitter, "profiling", 0.30,
                    Map.of("type", "text", "text", "已完成学习画像构建！正在为您规划学习路径..."), session.getId());
            Thread.sleep(500);
            handlePlanning(session, session.getUserId(), emitter);
        } else {
            JSONObject parsed = JSON.parseObject(jsonStr);
            saveMessage(session.getId(), response, "profiling");

            Map<String, Object> content = new HashMap<>();
            content.put("type", parsed.getString("type"));
            content.put("text", parsed.getString("text"));
            if (parsed.containsKey("options")) {
                content.put("options", parsed.getJSONArray("options"));
            }
            sendPhase(emitter, "profiling", 0.20, content, session.getId());
        }
    }

    private void handlePlanning(AgentSession session, Long userId, SseEmitter emitter) throws Exception {
        sendPhase(emitter, "planning", 0.40, Map.of("summary", "正在为您规划学习路径..."), session.getId());

        Map<String, Object> graph = knowledgeService.getKnowledgeGraph(1L);
        String graphJson = objectMapper.writeValueAsString(graph);

        UserProfile profile = userProfileService.getUserProfile(userId);
        String profileJson = profile != null ? objectMapper.writeValueAsString(profile) : "{}";

        String prompt = "用户画像：\n" + profileJson + "\n\n知识图谱：\n" + graphJson;
        String response = llmClient.chat(PLANNING_PROMPT, List.of(Map.of("role", "user", "content", prompt)));

        String jsonStr = extractJson(response);
        JSONObject parsed = JSON.parseObject(jsonStr);

        LearningPath path = new LearningPath();
        path.setUserId(userId);
        path.setCourseId(parsed.getLong("courseId"));
        path.setSessionId(session.getId());
        path.setStatus("active");

        List<PathNode> nodes = new ArrayList<>();
        JSONArray nodesArray = parsed.getJSONArray("nodes");
        for (int i = 0; i < nodesArray.size(); i++) {
            JSONObject nj = nodesArray.getJSONObject(i);
            PathNode node = new PathNode();
            node.setNodeOrder(nj.getIntValue("order"));
            node.setTitle(nj.getString("title"));
            node.setNodeType(nj.getString("type"));
            node.setEstimatedMinutes(nj.getIntValue("estimatedMinutes"));
            node.setReason(nj.getString("reason"));
            node.setStatus("pending");
            nodes.add(node);
        }
        path.setNodes(nodes);
        learningPathService.createLearningPath(path);

        saveMessage(session.getId(), response, "planning");
        sendPhase(emitter, "planning", 0.50, Map.of("summary", "已生成学习路径，共" + nodes.size() + "个节点"), session.getId());
        Thread.sleep(500);
        handleGenerating(session, userId, emitter);
    }

    private void handleGenerating(AgentSession session, Long userId, SseEmitter emitter) throws Exception {
        List<LearningPath> paths = learningPathService.getLearningPathsByUser(userId);
        if (paths.isEmpty()) {
            sendPhase(emitter, "generating", 0.60, Map.of("chapter_title", "暂无学习路径", "current", 0, "total", 0), session.getId());
            return;
        }

        LearningPath latestPath = paths.get(0);
        LearningPath detail = learningPathService.getLearningPathDetail(latestPath.getId());
        List<PathNode> nodes = detail.getNodes();

        sendPhase(emitter, "generating", 0.55, Map.of("chapter_title", "开始生成学习资料", "current", 0, "total", nodes.size()), session.getId());

        for (int i = 0; i < nodes.size(); i++) {
            PathNode node = nodes.get(i);
            double progress = 0.55 + (0.40 * (i + 1) / nodes.size());
            sendPhase(emitter, "generating", progress,
                    Map.of("chapter_title", "第" + (i + 1) + "章：" + node.getTitle(), "current", i + 1, "total", nodes.size()), session.getId());

            String kpInfo = "";
            if (node.getKnowledgePoints() != null) {
                kpInfo = node.getKnowledgePoints().stream()
                        .map(kp -> kp.getName() + "：" + kp.getDescription())
                        .collect(Collectors.joining("\n"));
            }

            genResource(session.getId(), node, "document", kpInfo);
            genResource(session.getId(), node, "quiz", kpInfo);
            genResource(session.getId(), node, "mindmap", kpInfo);
        }

        saveMessage(session.getId(), "学习资料生成完成", "done");
        learningPathService.updatePathStatus(latestPath.getId(), "completed");
        session.setStatus("completed");
        agentSessionMapper.updateById(session);
        sendDone(emitter, session.getId());
    }

    private void genResource(Long sessionId, PathNode node, String type, String kpInfo) {
        try {
            String typeDesc = switch (type) {
                case "document" -> "Markdown格式的讲解文档";
                case "quiz" -> "3道练习题";
                case "mindmap" -> "思维导图树形JSON";
                default -> type;
            };
            String prompt = "请为以下节点生成" + typeDesc + "：\n节点：" + node.getTitle() + "\n知识点：" + (kpInfo.isEmpty() ? "无" : kpInfo);
            String response = llmClient.chat(RESOURCE_PROMPT, List.of(Map.of("role", "user", "content", prompt)));

            String jsonStr = extractJson(response);
            JSONObject parsed = JSON.parseObject(jsonStr);

            GeneratedResource resource = new GeneratedResource();
            resource.setPathNodeId(node.getId());
            resource.setTitle(parsed.getString("title"));
            resource.setResourceType(type);
            resource.setDifficulty("L1");

            Object contentObj = parsed.get("content");
            JsonNode contentNode = (contentObj instanceof String s) ? objectMapper.readTree(s) : objectMapper.valueToTree(contentObj);
            resource.setContentJson(contentNode);

            generatedResourceService.batchCreateResources(List.of(resource));
        } catch (Exception e) {
            log.error("生成资源失败: node={}, type={}", node.getId(), type, e);
        }
    }

    private void saveProfile(Long userId, JSONObject data, Long sessionId) {
        try {
            UserProfile profile = userProfileService.getUserProfile(userId);
            JsonNode dims = objectMapper.readTree(data.toJSONString());
            if (profile == null) {
                profile = new UserProfile();
                profile.setUserId(userId);
                profile.setDimensions(dims);
                profile.setUpdatedBySessionId(sessionId);
                userProfileService.createUserProfile(profile);
            } else {
                profile.setDimensions(dims);
                profile.setUpdatedBySessionId(sessionId);
                userProfileService.updateUserProfile(userId, profile);
            }
        } catch (Exception e) {
            log.error("保存画像失败", e);
        }
    }

    private void saveMessage(Long sessionId, String content, String phase) {
        AgentMessage msg = new AgentMessage();
        msg.setSessionId(sessionId);
        msg.setRole("assistant");
        msg.setContent(content);
        msg.setPhase(phase);
        msg.setCreatedAt(LocalDateTime.now());
        agentMessageMapper.insert(msg);
    }

    private void sendPhase(SseEmitter emitter, String phase, double progress, Object content, Long sessionId) throws IOException {
        Map<String, Object> data = new HashMap<>();
        data.put("phase", phase);
        data.put("progress", progress);
        if (sessionId != null) data.put("sessionId", sessionId);
        if (content != null) data.put("content", content);
        emitter.send(SseEmitter.event().name("phase").data(objectMapper.writeValueAsString(data)));
    }

    private void sendDone(SseEmitter emitter, Long sessionId) throws IOException {
        Map<String, Object> data = Map.of("phase", "done", "progress", 1.0, "sessionId", sessionId,
                "content", Map.of("profileSummary", Map.of("level", "已完成"), "pathPreview", "学习计划已生成"));
        emitter.send(SseEmitter.event().name("phase").data(objectMapper.writeValueAsString(data)));
    }

    private void sendError(SseEmitter emitter, String msg) {
        try { emitter.send(SseEmitter.event().name("error").data(Map.of("message", msg))); } catch (IOException e) { log.error("send error", e); }
    }

    private String extractJson(String text) {
        int s = text.indexOf("```json"), e = text.lastIndexOf("```");
        if (s != -1 && e > s) return text.substring(s + 7, e).trim();
        s = text.indexOf("{"); e = text.lastIndexOf("}");
        if (s != -1 && e > s) return text.substring(s, e + 1).trim();
        return text;
    }
}
