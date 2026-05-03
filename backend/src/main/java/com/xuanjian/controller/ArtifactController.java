package com.xuanjian.controller;

import com.xuanjian.common.Result;
import com.xuanjian.entity.Artifact;
import com.xuanjian.service.ArtifactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "灵物接口")
@RestController
@RequestMapping("/api/artifacts")
@RequiredArgsConstructor
public class ArtifactController {

    private final ArtifactService artifactService;

    @Operation(summary = "获取灵物列表")
    @GetMapping
    public Result<List<Artifact>> list(@RequestParam Long projectId) {
        return Result.ok(artifactService.getByProject(projectId));
    }
}