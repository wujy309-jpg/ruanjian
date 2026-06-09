package com.exam.controller;

import com.exam.service.impl.AgentOrchestratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/agent")
@Tag(name = "Agent对话", description = "AI Agent对话接口")
public class AgentChatController {

    private static final Logger log = LoggerFactory.getLogger(AgentChatController.class);

    @Autowired
    private AgentOrchestratorService orchestratorService;

    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "AI对话（SSE流式）", description = "与AI Agent进行流式对话")
    public SseEmitter chat(@RequestBody ChatRequest request) {
        SseEmitter emitter = new SseEmitter(300000L);

        Long sessionId = request.getSessionId();
        String message = request.getMessage();
        Long userId = request.getUserId() != null ? request.getUserId() : 1L;

        new Thread(() -> {
            try {
                orchestratorService.handleChat(sessionId, message, userId, emitter);
            } catch (Exception e) {
                log.error("Chat处理失败", e);
                try {
                    emitter.send(SseEmitter.event().name("error").data("{\"message\":\"处理失败\"}"));
                } catch (Exception ex) { /* ignore */ }
                emitter.complete();
            }
        }).start();

        return emitter;
    }

    public static class ChatRequest {
        private Long sessionId;
        private String message;
        private Long userId;

        public Long getSessionId() { return sessionId; }
        public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
    }
}
