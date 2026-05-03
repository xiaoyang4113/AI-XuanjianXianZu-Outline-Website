package com.xuanjian.controller;

import com.xuanjian.common.Result;
import com.xuanjian.entity.Technique;
import com.xuanjian.service.TechniqueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "功法接口")
@RestController
@RequestMapping("/api/techniques")
@RequiredArgsConstructor
public class TechniqueController {

    private final TechniqueService techniqueService;

    @Operation(summary = "获取功法列表")
    @GetMapping
    public Result<List<Technique>> list(@RequestParam Long projectId) {
        return Result.ok(techniqueService.getByProject(projectId));
    }
}