package com.xuanjian.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuanjian.entity.EntityItem;
import com.xuanjian.entity.ItemTag;
import com.xuanjian.mapper.EntityItemMapper;
import com.xuanjian.mapper.ItemTagMapper;
import com.xuanjian.mapper.TagRelationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService extends ServiceImpl<EntityItemMapper, EntityItem> {

    private final ItemTagMapper itemTagMapper;
    private final TagRelationMapper tagRelationMapper;

    public List<Map<String, Object>> search(Long projectId, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return Collections.emptyList();
        String kw = keyword.trim().toLowerCase();

        // 搜索条目名称
        List<EntityItem> items = list(new LambdaQueryWrapper<EntityItem>()
                .eq(EntityItem::getProjectId, projectId)
                .and(w -> w
                        .like(EntityItem::getName, keyword)
                        .or().like(EntityItem::getGoldenNature, keyword)
                        .or().like(EntityItem::getNotes, keyword)));

        // 搜索标签名
        List<Long> allItemIds = items.stream().map(EntityItem::getId).collect(Collectors.toList());
        List<ItemTag> matchingTags = itemTagMapper.selectList(
                new LambdaQueryWrapper<ItemTag>()
                        .like(ItemTag::getName, keyword));
        Set<Long> tagItemIds = matchingTags.stream()
                .map(ItemTag::getEntityItemId)
                .collect(Collectors.toSet());

        // 合并去重
        List<Long> resultIds = new ArrayList<>(new LinkedHashSet<>(allItemIds));
        tagItemIds.forEach(id -> {
            if (!resultIds.contains(id)) resultIds.add(id);
        });

        List<Map<String, Object>> results = new ArrayList<>();
        for (Long id : resultIds) {
            EntityItem item = getById(id);
            if (item == null) continue;

            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("id", item.getId());
            entry.put("name", item.getName());
            entry.put("badge", item.getBadge());
            entry.put("goldenNature", item.getGoldenNature());

            List<ItemTag> tags = itemTagMapper.selectList(
                    new LambdaQueryWrapper<ItemTag>()
                            .eq(ItemTag::getEntityItemId, id)
                            .orderByAsc(ItemTag::getSortOrder));
            entry.put("tags", tags.stream().map(t -> {
                Map<String, Object> tagMap = new LinkedHashMap<>();
                tagMap.put("id", t.getId());
                tagMap.put("name", t.getName());
                tagMap.put("substitute", Boolean.TRUE.equals(t.getIsSubstitute()));
                return tagMap;
            }).collect(Collectors.toList()));

            // 高亮匹配
            entry.put("matchType", getMatchType(item, matchingTags, kw));
            results.add(entry);
        }
        return results;
    }

    private String getMatchType(EntityItem item, List<ItemTag> matchingTags, String kw) {
        if (item.getName() != null && item.getName().toLowerCase().contains(kw)) return "条目名称";
        if (item.getGoldenNature() != null && item.getGoldenNature().toLowerCase().contains(kw)) return "金性";
        if (item.getNotes() != null && item.getNotes().toLowerCase().contains(kw)) return "备注";
        boolean tagMatch = matchingTags.stream().anyMatch(t -> t.getEntityItemId().equals(item.getId()));
        if (tagMatch) return "仙基/神通";
        return "相关";
    }
}