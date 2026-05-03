package com.xuanjian.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AIService {

    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${ai.deepseek.api-url}")
    private String apiUrl;
    @Value("${ai.deepseek.api-key}")
    private String apiKey;

    public String generate(String prompt) {
        if (apiKey == null || apiKey.isEmpty()) {
            return "AI 服务未配置 API Key";
        }
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> body = new LinkedHashMap<>();
            body.put("model", "deepseek-chat");
            body.put("messages", List.of(
                    Map.of("role", "system", "content", "你是《玄鉴仙族》小说的创作助手，精通修仙世界设定。请用中文回答。"),
                    Map.of("role", "user", "content", prompt)
            ));
            body.put("temperature", 0.8);
            body.put("max_tokens", 1000);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.POST, request, Map.class);

            if (response.getBody() != null) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> msg = (Map<String, Object>) choices.get(0).get("message");
                    return (String) msg.get("content");
                }
            }
            return "AI 返回为空";
        } catch (Exception e) {
            log.error("AI 调用失败", e);
            return "AI 调用失败，请稍后重试";
        }
    }
}