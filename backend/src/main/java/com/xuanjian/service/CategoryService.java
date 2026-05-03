package com.xuanjian.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuanjian.dto.CategoryWithItemsDTO;
import com.xuanjian.dto.ItemBriefDTO;
import com.xuanjian.entity.*;
import com.xuanjian.mapper.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService extends ServiceImpl<CategoryMapper, Category> {

    private final SubCategoryMapper subCategoryMapper;
    private final EntityItemMapper entityItemMapper;
    private final ItemTagMapper itemTagMapper;
    private final TagRelationMapper tagRelationMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    public List<CategoryWithItemsDTO> getCategoriesWithItems(Long projectId) {
        String cacheKey = "categories:" + projectId;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached instanceof List) {
            @SuppressWarnings("unchecked")
            List<CategoryWithItemsDTO> result = (List<CategoryWithItemsDTO>) cached;
            return result;
        }

        List<Category> categories = list(new LambdaQueryWrapper<Category>()
                .eq(Category::getProjectId, projectId)
                .orderByAsc(Category::getSortOrder));

        List<CategoryWithItemsDTO> result = new ArrayList<>();

        for (Category cat : categories) {
            CategoryWithItemsDTO dto = new CategoryWithItemsDTO();
            dto.setId(cat.getId());
            dto.setName(cat.getName());
            dto.setElement(cat.getElement());

            // 检查是否有子分类
            List<SubCategory> subCats = subCategoryMapper.selectByCategoryId(cat.getId());

            if (!subCats.isEmpty()) {
                dto.setSubCategories(new ArrayList<>());
                dto.setItems(Collections.emptyList());
                for (SubCategory sub : subCats) {
                    CategoryWithItemsDTO.SubCategoryWithItemsDTO subDto =
                            new CategoryWithItemsDTO.SubCategoryWithItemsDTO();
                    subDto.setId(sub.getId());
                    subDto.setName(sub.getName());
                    subDto.setItems(getItemsBySubCategory(sub.getId()));
                    dto.getSubCategories().add(subDto);
                }
            } else {
                dto.setSubCategories(Collections.emptyList());
                dto.setItems(getItemsByCategory(cat.getId()));
            }

            dto.setItemCount(countItems(dto));
            result.add(dto);
        }

        redisTemplate.opsForValue().set(cacheKey, result, 10, TimeUnit.MINUTES);
        return result;
    }

    public List<CategoryWithItemsDTO> getCategoryDetail(Long categoryId) {
        Category cat = getById(categoryId);
        if (cat == null) return Collections.emptyList();
        return getCategoriesWithItems(cat.getProjectId()).stream()
                .filter(c -> c.getId().equals(categoryId))
                .collect(Collectors.toList());
    }

    private List<ItemBriefDTO> getItemsByCategory(Long categoryId) {
        List<EntityItem> items = entityItemMapper.selectList(
                new LambdaQueryWrapper<EntityItem>()
                        .eq(EntityItem::getCategoryId, categoryId)
                        .orderByAsc(EntityItem::getSortOrder));
        return items.stream().map(this::convertToBrief).collect(Collectors.toList());
    }

    private List<ItemBriefDTO> getItemsBySubCategory(Long subCategoryId) {
        List<EntityItem> items = entityItemMapper.selectList(
                new LambdaQueryWrapper<EntityItem>()
                        .eq(EntityItem::getSubCategoryId, subCategoryId)
                        .orderByAsc(EntityItem::getSortOrder));
        return items.stream().map(this::convertToBrief).collect(Collectors.toList());
    }

    private ItemBriefDTO convertToBrief(EntityItem item) {
        ItemBriefDTO dto = new ItemBriefDTO();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setBadge(item.getBadge());
        dto.setGoldenNature(item.getGoldenNature());
        dto.setNotes(item.getNotes());
        dto.setIsEmptyCertification(item.getIsEmptyCertification());

        // 标签
        List<ItemTag> tags = itemTagMapper.selectList(
                new LambdaQueryWrapper<ItemTag>()
                        .eq(ItemTag::getEntityItemId, item.getId())
                        .orderByAsc(ItemTag::getSortOrder));
        dto.setTags(tags.stream().map(t -> {
            ItemBriefDTO.TagDTO tagDto = new ItemBriefDTO.TagDTO();
            tagDto.setId(t.getId());
            tagDto.setName(t.getName());
            tagDto.setSubstitute(Boolean.TRUE.equals(t.getIsSubstitute()));
            return tagDto;
        }).collect(Collectors.toList()));

        // 替参关系
        List<ItemBriefDTO.SubstituteNoteDTO> subNotes = new ArrayList<>();
        for (ItemTag tag : tags) {
            List<TagRelation> relations = tagRelationMapper.selectList(
                    new LambdaQueryWrapper<TagRelation>()
                            .eq(TagRelation::getSourceTagId, tag.getId()));
            for (TagRelation rel : relations) {
                if (rel.getTargetName() != null) {
                    ItemBriefDTO.SubstituteNoteDTO note = new ItemBriefDTO.SubstituteNoteDTO();
                    note.setSource(tag.getName());
                    note.setTarget(rel.getTargetName());
                    subNotes.add(note);
                }
            }
        }
        dto.setSubstituteNotes(subNotes);

        return dto;
    }

    private int countItems(CategoryWithItemsDTO dto) {
        int count = dto.getItems() != null ? dto.getItems().size() : 0;
        if (dto.getSubCategories() != null) {
            for (CategoryWithItemsDTO.SubCategoryWithItemsDTO sub : dto.getSubCategories()) {
                count += sub.getItems() != null ? sub.getItems().size() : 0;
            }
        }
        return count;
    }
}