package com.xuanjian.controller;

import com.xuanjian.common.Result;
import com.xuanjian.service.AIService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "AI辅助")
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {

    private final AIService aiService;

    @Operation(summary = "AI灵感生成")
    @PostMapping("/generate")
    public Result<Map<String, String>> generate(@RequestBody Map<String, String> body) {
        String prompt = body.getOrDefault("prompt", "");
        String result = aiService.generate(prompt);
        return Result.ok(Map.of("content", result));
    }
}