package com.xuanjian.controller;

import com.xuanjian.common.Result;
import com.xuanjian.service.FactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Tag(name = "势力接口")
@RestController
@RequestMapping("/api/factions")
@RequiredArgsConstructor
public class FactionController {

    private final FactionService factionService;

    @Operation(summary = "获取势力列表")
    @GetMapping
    public Result<List<Map<String, Object>>> list(@RequestParam Long projectId) {
        return Result.ok(factionService.getFactions(projectId));
    }

    @Operation(summary = "获取所有势力关系")
    @GetMapping("/relations")
    public Result<List<Map<String, Object>>> relations(@RequestParam Long projectId) {
        return Result.ok(factionService.getAllRelations(projectId));
    }
}