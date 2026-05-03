package com.xuanjian.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuanjian.dto.ItemBriefDTO;
import com.xuanjian.dto.ItemDetailDTO;
import com.xuanjian.entity.*;
import com.xuanjian.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EntityItemService extends ServiceImpl<EntityItemMapper, EntityItem> {

    private final ItemTagMapper itemTagMapper;
    private final TagRelationMapper tagRelationMapper;
    private final CategoryMapper categoryMapper;
    private final SubCategoryMapper subCategoryMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    public ItemDetailDTO getItemDetail(Long itemId) {
        String cacheKey = "item:" + itemId;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached instanceof ItemDetailDTO) {
            return (ItemDetailDTO) cached;
        }

        EntityItem item = getById(itemId);
        if (item == null) return null;

        ItemDetailDTO dto = new ItemDetailDTO();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setBadge(item.getBadge());
        dto.setGoldenNature(item.getGoldenNature());
        dto.setNotes(item.getNotes());
        dto.setIsEmptyCertification(item.getIsEmptyCertification());

        // 分类名
        if (item.getCategoryId() != null) {
            Category cat = categoryMapper.selectById(item.getCategoryId());
            if (cat != null) dto.setCategoryName(cat.getName());
        }
        if (item.getSubCategoryId() != null) {
            SubCategory sub = subCategoryMapper.selectById(item.getSubCategoryId());
            if (sub != null) dto.setSubCategoryName(sub.getName());
        }

        // 标签
        List<ItemTag> tags = itemTagMapper.selectList(
                new LambdaQueryWrapper<ItemTag>()
                        .eq(ItemTag::getEntityItemId, itemId)
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
                    new LambdaQueryWrapper<TagRelation>().eq(TagRelation::getSourceTagId, tag.getId()));
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

        redisTemplate.opsForValue().set(cacheKey, dto, 10, TimeUnit.MINUTES);
        return dto;
    }
}