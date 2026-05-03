package com.xuanjian.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuanjian.common.Result;
import com.xuanjian.entity.*;
import com.xuanjian.mapper.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Tag(name = "搜索接口")
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final EntityItemMapper entityItemMapper;
    private final ItemTagMapper itemTagMapper;
    private final CharacterProfileMapper characterMapper;
    private final FactionMapper factionMapper;
    private final TechniqueMapper techniqueMapper;
    private final ArtifactMapper artifactMapper;

    @Operation(summary = "全局搜索")
    @GetMapping
    public Result<List<Map<String, Object>>> search(
            @RequestParam Long projectId,
            @RequestParam String q) {
        if (q == null || q.trim().isEmpty()) return Result.ok(Collections.emptyList());
        String keyword = q.trim();
        List<Map<String, Object>> results = new ArrayList<>();

        // 搜果位
        List<EntityItem> items = entityItemMapper.selectList(new LambdaQueryWrapper<EntityItem>()
                .eq(EntityItem::getProjectId, projectId).like(EntityItem::getName, keyword));
        for (EntityItem item : items) {
            Map<String, Object> e = new LinkedHashMap<>();
            e.put("type", "果位");
            e.put("id", item.getId());
            e.put("name", item.getName());
            e.put("badge", item.getBadge());
            e.put("detail", item.getGoldenNature());
            results.add(e);
        }

        // 搜人物
        List<CharacterProfile> chars = characterMapper.selectList(new LambdaQueryWrapper<CharacterProfile>()
                .eq(CharacterProfile::getProjectId, projectId)
                .and(w -> w.like(CharacterProfile::getName, keyword)
                        .or().like(CharacterProfile::getTitle, keyword)
                        .or().like(CharacterProfile::getDescription, keyword)));
        for (CharacterProfile c : chars) {
            Map<String, Object> e = new LinkedHashMap<>();
            e.put("type", "人物");
            e.put("id", c.getId());
            e.put("name", c.getName());
            e.put("badge", c.getRealm());
            e.put("detail", c.getTitle());
            results.add(e);
        }

        // 搜势力
        List<Faction> fcts = factionMapper.selectList(new LambdaQueryWrapper<Faction>()
                .eq(Faction::getProjectId, projectId)
                .and(w -> w.like(Faction::getName, keyword).or().like(Faction::getDescription, keyword)));
        for (Faction f : fcts) {
            Map<String, Object> e = new LinkedHashMap<>();
            e.put("type", "势力");
            e.put("id", f.getId());
            e.put("name", f.getName());
            e.put("badge", f.getHighestRealm());
            e.put("detail", f.getTerritory());
            results.add(e);
        }

        // 搜功法
        List<Technique> techs = techniqueMapper.selectList(new LambdaQueryWrapper<Technique>()
                .eq(Technique::getProjectId, projectId)
                .and(w -> w.like(Technique::getName, keyword).or().like(Technique::getDescription, keyword)));
        for (Technique t : techs) {
            Map<String, Object> e = new LinkedHashMap<>();
            e.put("type", "功法");
            e.put("id", t.getId());
            e.put("name", t.getName());
            e.put("badge", t.getGrade());
            e.put("detail", t.getCategory());
            results.add(e);
        }

        // 搜灵物
        List<Artifact> arts = artifactMapper.selectList(new LambdaQueryWrapper<Artifact>()
                .eq(Artifact::getProjectId, projectId)
                .and(w -> w.like(Artifact::getName, keyword).or().like(Artifact::getDescription, keyword)));
        for (Artifact a : arts) {
            Map<String, Object> e = new LinkedHashMap<>();
            e.put("type", "灵物");
            e.put("id", a.getId());
            e.put("name", a.getName());
            e.put("badge", a.getType());
            e.put("detail", a.getEffect());
            results.add(e);
        }

        return Result.ok(results);
    }
}