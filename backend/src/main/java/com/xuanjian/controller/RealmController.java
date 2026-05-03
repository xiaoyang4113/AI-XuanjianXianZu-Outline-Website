package com.xuanjian.controller;

import com.xuanjian.common.Result;
import com.xuanjian.entity.CultivationPath;
import com.xuanjian.service.RealmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "修炼体系接口")
@RestController
@RequestMapping("/api/realms")
@RequiredArgsConstructor
public class RealmController {

    private final RealmService realmService;

    @Operation(summary = "获取所有境界（含子阶段）")
    @GetMapping
    public Result<List<Map<String, Object>>> list(@RequestParam Long projectId) {
        return Result.ok(realmService.getRealms(projectId));
    }

    @Operation(summary = "获取所有修炼道统")
    @GetMapping("/paths")
    public Result<List<CultivationPath>> paths(@RequestParam Long projectId) {
        return Result.ok(realmService.getPaths(projectId));
    }

    @Operation(summary = "获取道统在各境界的表现")
    @GetMapping("/paths/{pathId}/stages")
    public Result<List<Map<String, Object>>> pathStages(@PathVariable Long pathId) {
        return Result.ok(realmService.getPathStages(pathId));
    }
}