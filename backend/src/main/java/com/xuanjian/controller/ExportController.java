package com.xuanjian.controller;

import com.xuanjian.common.Result;
import com.xuanjian.service.ExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;

@Tag(name = "导出接口")
@RestController
@RequestMapping("/api/export")
@RequiredArgsConstructor
public class ExportController {

    private final ExportService exportService;

    @Operation(summary = "导出为Markdown")
    @GetMapping("/markdown/{projectId}")
    public void markdown(@PathVariable Long projectId, HttpServletResponse response) throws Exception {
        response.setContentType("text/markdown; charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=export.md");
        PrintWriter writer = response.getWriter();
        writer.write(exportService.exportToMarkdown(projectId));
        writer.flush();
    }

    @Operation(summary = "导出为JSON")
    @GetMapping("/json/{projectId}")
    public Result<Map<String, Object>> json(@PathVariable Long projectId) {
        return Result.ok(exportService.exportToJson(projectId));
    }
}