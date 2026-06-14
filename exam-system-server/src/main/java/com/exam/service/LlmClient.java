package com.exam.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

@Slf4j
@Service
public class LlmClient {

    @Value("${kimi.api.api-key:}")
    private String apiKey;

    @Value("${kimi.api.base-url:https://api.xiaomimimo.com/v1}")
    private String baseUrl;

    @Value("${kimi.api.model:mimo-v2.5-pro}")
    private String model;

    private final WebClient webClient = WebClient.builder()
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    // Token 消耗统计
    private final AtomicLong totalPromptTokens = new AtomicLong(0);
    private final AtomicLong totalCompletionTokens = new AtomicLong(0);
    private final AtomicLong totalCalls = new AtomicLong(0);

    /**
     * Token 用量统计结果
     */
    public static class TokenUsage {
        private final long promptTokens;
        private final long completionTokens;
        private final long totalTokens;

        public TokenUsage(long promptTokens, long completionTokens) {
            this.promptTokens = promptTokens;
            this.completionTokens = completionTokens;
            this.totalTokens = promptTokens + completionTokens;
        }

        public long getPromptTokens() { return promptTokens; }
        public long getCompletionTokens() { return completionTokens; }
        public long getTotalTokens() { return totalTokens; }

        @Override
        public String toString() {
            return String.format("prompt=%d, completion=%d, total=%d", promptTokens, completionTokens, totalTokens);
        }
    }

    /**
     * 获取累计 Token 统计
     */
    public TokenUsage getCumulativeUsage() {
        return new TokenUsage(totalPromptTokens.get(), totalCompletionTokens.get());
    }

    /**
     * 获取总调用次数
     */
    public long getTotalCalls() {
        return totalCalls.get();
    }

    public String chat(String systemPrompt, List<Map<String, String>> messages) {
        if (apiKey == null || apiKey.isEmpty() || apiKey.startsWith("你的")) {
            throw new RuntimeException("未配置有效的 Kimi API 密钥，请在 application.yml 中设置 kimi.api.api-key");
        }

        JSONArray msgArray = new JSONArray();
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            JSONObject sys = new JSONObject();
            sys.put("role", "system");
            sys.put("content", systemPrompt);
            msgArray.add(sys);
        }
        for (Map<String, String> m : messages) {
            JSONObject msg = new JSONObject();
            msg.put("role", m.get("role"));
            msg.put("content", m.get("content"));
            msgArray.add(msg);
        }

        JSONObject body = new JSONObject();
        body.put("model", model);
        body.put("messages", msgArray);
        body.put("temperature", 0.7);
        body.put("max_tokens", 4000);

        // 计算prompt长度用于日志
        int promptLength = body.toJSONString().length();
        log.info("LLM request - prompt length: {} chars, attempt: {}", promptLength, 3);

        for (int attempt = 1; attempt <= 3; attempt++) {
            long startTime = System.currentTimeMillis();
            try {
                String response = webClient.post()
                        .uri(baseUrl + "/chat/completions")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                        .bodyValue(body.toJSONString())
                        .retrieve()
                        .bodyToMono(String.class)
                        .timeout(Duration.ofSeconds(60))
                        .block();

                long elapsed = System.currentTimeMillis() - startTime;
                log.info("LLM response received in {} ms", elapsed);

                JSONObject json = JSON.parseObject(response);

                // 记录 token 消耗
                recordTokenUsage(json);

                if (json.containsKey("error")) {
                    String errMsg = json.getJSONObject("error").getString("message");
                    if (errMsg.contains("rate limit") && attempt < 3) {
                        Thread.sleep(attempt * 5000);
                        continue;
                    }
                    throw new RuntimeException("LLM API 错误: " + errMsg);
                }

                JSONArray choices = json.getJSONArray("choices");
                if (choices != null && !choices.isEmpty()) {
                    return choices.getJSONObject(0).getJSONObject("message").getString("content");
                }
                throw new RuntimeException("LLM 返回空响应");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("调用被中断");
            } catch (Exception e) {
                if (attempt == 3) throw new RuntimeException("LLM 服务不可用: " + e.getMessage());
                try { Thread.sleep(2000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
            }
        }
        throw new RuntimeException("LLM 调用失败");
    }

    /**
     * 流式调用 LLM，通过 Consumer 回调实时推送文本块
     * 适用于对话场景，用户可以看到实时打字效果
     */
    public void chatStream(String systemPrompt, List<Map<String, String>> messages,
                           Consumer<String> onChunk, Consumer<TokenUsage> onComplete) {
        if (apiKey == null || apiKey.isEmpty() || apiKey.startsWith("你的")) {
            throw new RuntimeException("未配置有效的 API 密钥，请在 application.yml 中设置 kimi.api.api-key");
        }

        JSONArray msgArray = new JSONArray();
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            JSONObject sys = new JSONObject();
            sys.put("role", "system");
            sys.put("content", systemPrompt);
            msgArray.add(sys);
        }
        for (Map<String, String> m : messages) {
            JSONObject msg = new JSONObject();
            msg.put("role", m.get("role"));
            msg.put("content", m.get("content"));
            msgArray.add(msg);
        }

        JSONObject body = new JSONObject();
        body.put("model", model);
        body.put("messages", msgArray);
        body.put("temperature", 0.7);
        body.put("max_tokens", 4000);
        body.put("stream", true);

        log.info("LLM stream request - prompt length: {} chars", body.toJSONString().length());

        webClient.post()
                .uri(baseUrl + "/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .bodyValue(body.toJSONString())
                .retrieve()
                .bodyToFlux(String.class)
                .doOnNext(line -> {
                    if (line.startsWith("data: ")) {
                        String data = line.substring(6).trim();
                        if ("[DONE]".equals(data)) {
                            return;
                        }
                        try {
                            JSONObject json = JSON.parseObject(data);
                            JSONArray choices = json.getJSONArray("choices");
                            if (choices != null && !choices.isEmpty()) {
                                JSONObject delta = choices.getJSONObject(0).getJSONObject("delta");
                                if (delta != null) {
                                    String content = delta.getString("content");
                                    if (content != null && !content.isEmpty()) {
                                        onChunk.accept(content);
                                    }
                                }
                            }
                            // 记录最后一个chunk的token信息
                            if (json.containsKey("usage")) {
                                recordTokenUsage(json);
                            }
                        } catch (Exception e) {
                            log.warn("Failed to parse stream chunk: {}", data);
                        }
                    }
                })
                .doOnComplete(() -> {
                    TokenUsage usage = getCumulativeUsage();
                    onComplete.accept(usage);
                })
                .doOnError(error -> {
                    log.error("Stream error", error);
                })
                .subscribe();
    }

    /**
     * 记录 token 消耗
     */
    private void recordTokenUsage(JSONObject responseJson) {
        try {
            JSONObject usage = responseJson.getJSONObject("usage");
            if (usage != null) {
                long promptTokens = usage.getLongValue("prompt_tokens");
                long completionTokens = usage.getLongValue("completion_tokens");
                totalPromptTokens.addAndGet(promptTokens);
                totalCompletionTokens.addAndGet(completionTokens);
                totalCalls.incrementAndGet();
                log.debug("Token usage recorded: prompt={}, completion={}", promptTokens, completionTokens);
            }
        } catch (Exception e) {
            log.debug("Failed to record token usage (response may not include usage info)");
        }
    }
}
