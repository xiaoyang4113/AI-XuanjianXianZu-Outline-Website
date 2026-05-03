package com.xuanjian.controller;

import com.xuanjian.common.Result;
import com.xuanjian.dto.CategoryWithItemsDTO;
import com.xuanjian.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "分类接口")
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "获取项目的所有分类及条目")
    @GetMapping
    public Result<List<CategoryWithItemsDTO>> list(@RequestParam Long projectId) {
        return Result.ok(categoryService.getCategoriesWithItems(projectId));
    }

    @Operation(summary = "获取单个分类详情")
    @GetMapping("/{id}")
    public Result<List<CategoryWithItemsDTO>> detail(@PathVariable Long id) {
        List<CategoryWithItemsDTO> result = categoryService.getCategoryDetail(id);
        return Result.ok(result);
    }
}