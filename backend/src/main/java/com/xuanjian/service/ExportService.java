package com.xuanjian.service;

import com.xuanjian.entity.*;
import com.xuanjian.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExportService {

    private final ProjectMapper projectMapper;
    private final CategoryMapper categoryMapper;
    private final EntityItemMapper entityItemMapper;
    private final ItemTagMapper itemTagMapper;
    private final CharacterProfileMapper characterMapper;
    private final FactionMapper factionMapper;
    private final RealmMapper realmMapper;
    private final TechniqueMapper techniqueMapper;
    private final ArtifactMapper artifactMapper;

    public String exportToMarkdown(Long projectId) {
        StringBuilder sb = new StringBuilder();
        Project project = projectMapper.selectById(projectId);
        sb.append("# ").append(project.getName()).append("\n\n");

        // 果位
        sb.append("## 道统图鉴\n\n");
        List<Category> categories = categoryMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Category>()
                        .eq(Category::getProjectId, projectId));
        for (Category cat : categories) {
            sb.append("### ").append(cat.getName()).append("\n\n");
            List<EntityItem> items = entityItemMapper.selectList(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<EntityItem>()
                            .eq(EntityItem::getCategoryId, cat.getId()));
            for (EntityItem item : items) {
                sb.append("- **").append(item.getName()).append("**");
                if (item.getBadge() != null) sb.append(" [").append(item.getBadge()).append("]");
                sb.append("\n");
                List<ItemTag> tags = itemTagMapper.selectList(
                        new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ItemTag>()
                                .eq(ItemTag::getEntityItemId, item.getId()));
                for (ItemTag tag : tags) {
                    sb.append("  - ").append(tag.getName()).append("\n");
                }
                if (item.getGoldenNature() != null) {
                    sb.append("  - 金性：").append(item.getGoldenNature()).append("\n");
                }
                sb.append("\n");
            }
        }

        // 人物
        sb.append("## 人物\n\n");
        List<CharacterProfile> chars = characterMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CharacterProfile>()
                        .eq(CharacterProfile::getProjectId, projectId));
        for (CharacterProfile c : chars) {
            sb.append("- **").append(c.getName()).append("**");
            if (c.getTitle() != null) sb.append("（").append(c.getTitle()).append("）");
            sb.append(" | ").append(c.getStatus());
            if (c.getRealm() != null) sb.append(" | ").append(c.getRealm());
            sb.append("\n");
            if (c.getDescription() != null) sb.append("  ").append(c.getDescription()).append("\n");
        }

        return sb.toString();
    }

    public Map<String, Object> exportToJson(Long projectId) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("project", projectMapper.selectById(projectId));
        data.put("categories", categoryMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Category>()
                        .eq(Category::getProjectId, projectId)));
        data.put("characters", characterMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CharacterProfile>()
                        .eq(CharacterProfile::getProjectId, projectId)));
        data.put("factions", factionMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Faction>()
                        .eq(Faction::getProjectId, projectId)));
        data.put("realms", realmMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Realm>()
                        .eq(Realm::getProjectId, projectId)));
        data.put("techniques", techniqueMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Technique>()
                        .eq(Technique::getProjectId, projectId)));
        data.put("artifacts", artifactMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Artifact>()
                        .eq(Artifact::getProjectId, projectId)));
        return data;
    }
}