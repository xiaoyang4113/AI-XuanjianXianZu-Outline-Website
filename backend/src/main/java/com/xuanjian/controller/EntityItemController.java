package com.xuanjian.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuanjian.common.Result;
import com.xuanjian.dto.ItemDetailDTO;
import com.xuanjian.entity.EntityItem;
import com.xuanjian.entity.ItemTag;
import com.xuanjian.mapper.ItemTagMapper;
import com.xuanjian.service.EntityItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "条目接口")
@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class EntityItemController {

    private final EntityItemService entityItemService;
    private final ItemTagMapper itemTagMapper;

    @Operation(summary = "获取条目详情")
    @GetMapping("/{id}")
    public Result<ItemDetailDTO> detail(@PathVariable Long id) {
        ItemDetailDTO dto = entityItemService.getItemDetail(id);
        if (dto == null) return Result.error("条目不存在");
        return Result.ok(dto);
    }

    @Operation(summary = "分页获取条目")
    @GetMapping
    public Result<Page<EntityItem>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long projectId) {
        LambdaQueryWrapper<EntityItem> wrapper = new LambdaQueryWrapper<>();
        if (projectId != null) {
            wrapper.eq(EntityItem::getProjectId, projectId);
        }
        wrapper.orderByAsc(EntityItem::getSortOrder);
        return Result.ok(entityItemService.page(new Page<>(page, size), wrapper));
    }

    @Operation(summary = "获取条目的标签")
    @GetMapping("/{id}/tags")
    public Result<List<ItemTag>> tags(@PathVariable Long id) {
        return Result.ok(itemTagMapper.selectList(
                new LambdaQueryWrapper<ItemTag>()
                        .eq(ItemTag::getEntityItemId, id)
                        .orderByAsc(ItemTag::getSortOrder)));
    }
}