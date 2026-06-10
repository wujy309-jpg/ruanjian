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
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;

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

        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                String response = webClient.post()
                        .uri(baseUrl + "/chat/completions")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                        .bodyValue(body.toJSONString())
                        .retrieve()
                        .bodyToMono(String.class)
                        .timeout(Duration.ofSeconds(120))
                        .block();

                JSONObject json = JSON.parseObject(response);
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
}
