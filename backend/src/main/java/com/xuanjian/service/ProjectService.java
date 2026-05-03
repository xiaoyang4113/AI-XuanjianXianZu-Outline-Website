package com.xuanjian.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuanjian.dto.ProjectStatsDTO;
import com.xuanjian.entity.Category;
import com.xuanjian.entity.EntityItem;
import com.xuanjian.entity.ItemTag;
import com.xuanjian.entity.Project;
import com.xuanjian.mapper.CategoryMapper;
import com.xuanjian.mapper.EntityItemMapper;
import com.xuanjian.mapper.ItemTagMapper;
import com.xuanjian.mapper.ProjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService extends ServiceImpl<ProjectMapper, Project> {

    private final CategoryMapper categoryMapper;
    private final EntityItemMapper entityItemMapper;
    private final ItemTagMapper itemTagMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    public ProjectStatsDTO getStats(Long projectId) {
        String cacheKey = "stats:" + projectId;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached instanceof ProjectStatsDTO) {
            return (ProjectStatsDTO) cached;
        }

        Project project = getById(projectId);
        if (project == null) return null;

        ProjectStatsDTO stats = new ProjectStatsDTO();
        stats.setProjectId(projectId);
        stats.setProjectName(project.getName());

        // 总条目数
        long totalItems = entityItemMapper.selectCount(
                new LambdaQueryWrapper<EntityItem>().eq(EntityItem::getProjectId, projectId));
        stats.setTotalItems((int) totalItems);

        // 分类数
        long totalCategories = categoryMapper.selectCount(
                new LambdaQueryWrapper<Category>().eq(Category::getProjectId, projectId));
        stats.setTotalCategories((int) totalCategories);

        // 总标签数
        List<EntityItem> items = entityItemMapper.selectList(
                new LambdaQueryWrapper<EntityItem>().eq(EntityItem::getProjectId, projectId));
        List<Long> itemIds = items.stream().map(EntityItem::getId).collect(Collectors.toList());
        int totalTags = 0;
        if (!itemIds.isEmpty()) {
            Long count = itemTagMapper.selectCount(
                    new LambdaQueryWrapper<ItemTag>().in(ItemTag::getEntityItemId, itemIds));
            totalTags = count != null ? Math.toIntExact(count) : 0;

        }
        stats.setTotalTags(totalTags);

        // 金性已录数
        int goldenCount = (int) items.stream()
                .filter(i -> i.getGoldenNature() != null && !i.getGoldenNature().isEmpty())
                .count();
        stats.setGoldenNaturesCount(goldenCount);

        // 各分类条目数
        List<Map<String, Object>> catCounts = categoryMapper.selectCategoriesWithCount(projectId);
        Map<String, Integer> categoryCounts = new LinkedHashMap<>();
        for (Map<String, Object> row : catCounts) {
            categoryCounts.put((String) row.get("name"), ((Number) row.get("item_count")).intValue());
        }
        stats.setCategoryCounts(categoryCounts);

        redisTemplate.opsForValue().set(cacheKey, stats, 30, TimeUnit.MINUTES);
        return stats;
    }
}