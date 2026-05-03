package com.xuanjian.controller;

import com.xuanjian.common.Result;
import com.xuanjian.dto.ProjectStatsDTO;
import com.xuanjian.entity.Project;
import com.xuanjian.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "项目接口")
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @Operation(summary = "获取所有项目")
    @GetMapping
    public Result<List<Project>> list() {
        return Result.ok(projectService.list());
    }

    @Operation(summary = "获取项目详情")
    @GetMapping("/{id}")
    public Result<Project> detail(@PathVariable Long id) {
        return Result.ok(projectService.getById(id));
    }

    @Operation(summary = "获取项目统计")
    @GetMapping("/{id}/stats")
    public Result<ProjectStatsDTO> stats(@PathVariable Long id) {
        ProjectStatsDTO stats = projectService.getStats(id);
        if (stats == null) return Result.error("项目不存在");
        return Result.ok(stats);
    }
}