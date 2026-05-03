package com.xuanjian.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuanjian.common.Result;
import com.xuanjian.entity.*;
import com.xuanjian.mapper.*;
import com.xuanjian.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@Tag(name = "管理后台")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final CharacterProfileMapper characterMapper;
    private final FactionMapper factionMapper;
    private final TechniqueMapper techniqueMapper;
    private final ArtifactMapper artifactMapper;
    private final EntityItemMapper entityItemMapper;
    private final CategoryMapper categoryMapper;
    private final ItemTagMapper itemTagMapper;
    private final FactionRelationMapper factionRelationMapper;
    private final FileService fileService;
    private final RedisTemplate<String, Object> redisTemplate;

    // ==================== 安全的缓存清除（SCAN 替代 KEYS） ====================

    private void clearCache(String modulePattern, Long projectId) {
        try {
            redisTemplate.execute((RedisCallback<Void>) connection -> {
                ScanOptions options = ScanOptions.scanOptions().match(modulePattern + ":*").count(100).build();
                Cursor<byte[]> cursor = connection.scan(options);
                while (cursor.hasNext()) {
                    connection.del(cursor.next());
                }
                return null;
            });
        } catch (Exception e) {
            // SCAN 失败则回退到删除单个 key
            redisTemplate.delete(modulePattern);
        }
        if (projectId != null) {
            redisTemplate.delete("stats:" + projectId);
        }
    }

    // ==================== 人物 CRUD ====================

    @Operation(summary = "人物列表（分页）")
    @GetMapping("/characters")
    public Result<?> listCharacters(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<CharacterProfile> w = new LambdaQueryWrapper<>();
        if (projectId != null) w.eq(CharacterProfile::getProjectId, projectId);
        if (keyword != null && !keyword.isEmpty()) {
            w.and(q -> q.like(CharacterProfile::getName, keyword)
                    .or().like(CharacterProfile::getTitle, keyword)
                    .or().like(CharacterProfile::getDaoTradition, keyword));
        }
        w.orderByAsc(CharacterProfile::getSortOrder);
        return Result.ok(characterMapper.selectPage(new Page<>(page, size), w));
    }

    @Operation(summary = "人物详情")
    @GetMapping("/characters/{id}")
    public Result<?> getCharacter(@PathVariable Long id) {
        return Result.ok(characterMapper.selectById(id));
    }

    @Operation(summary = "新增人物")
    @PostMapping("/characters")
    public Result<?> createCharacter(@RequestBody CharacterProfile entity) {
        characterMapper.insert(entity);
        clearCache("chars", entity.getProjectId());
        return Result.ok(entity);
    }

    @Operation(summary = "修改人物")
    @PutMapping("/characters/{id}")
    public Result<?> updateCharacter(@PathVariable Long id, @RequestBody CharacterProfile entity) {
        entity.setId(id);
        characterMapper.updateById(entity);
        clearCache("chars", entity.getProjectId());
        return Result.ok(entity);
    }

    @Operation(summary = "删除人物")
    @DeleteMapping("/characters/{id}")
    public Result<?> deleteCharacter(@PathVariable Long id) {
        CharacterProfile e = characterMapper.selectById(id);
        if (e != null) clearCache("chars", e.getProjectId());
        characterMapper.deleteById(id);
        return Result.ok();
    }

    // ==================== 势力 CRUD ====================

    @GetMapping("/factions")
    public Result<?> listFactions(@RequestParam Long projectId) {
        return Result.ok(factionMapper.selectList(
                new LambdaQueryWrapper<Faction>().eq(Faction::getProjectId, projectId)));
    }

    @PostMapping("/factions")
    public Result<?> createFaction(@RequestBody Faction e) {
        factionMapper.insert(e);
        clearCache("factions", e.getProjectId());
        return Result.ok(e);
    }

    @PutMapping("/factions/{id}")
    public Result<?> updateFaction(@PathVariable Long id, @RequestBody Faction e) {
        e.setId(id);
        factionMapper.updateById(e);
        clearCache("factions", e.getProjectId());
        return Result.ok(e);
    }

    @DeleteMapping("/factions/{id}")
    public Result<?> deleteFaction(@PathVariable Long id) {
        Faction e = factionMapper.selectById(id);
        if (e != null) clearCache("factions", e.getProjectId());
        factionMapper.deleteById(id);
        return Result.ok();
    }

    // ==================== 功法 CRUD ====================

    @GetMapping("/techniques")
    public Result<?> listTechniques(@RequestParam Long projectId) {
        return Result.ok(techniqueMapper.selectList(new LambdaQueryWrapper<Technique>().eq(Technique::getProjectId, projectId)));
    }

    @PostMapping("/techniques")
    public Result<?> createTechnique(@RequestBody Technique e) {
        techniqueMapper.insert(e);
        clearCache("techs", e.getProjectId());
        return Result.ok(e);
    }

    @PutMapping("/techniques/{id}")
    public Result<?> updateTechnique(@PathVariable Long id, @RequestBody Technique e) {
        e.setId(id);
        techniqueMapper.updateById(e);
        clearCache("techs", e.getProjectId());
        return Result.ok(e);
    }

    @DeleteMapping("/techniques/{id}")
    public Result<?> deleteTechnique(@PathVariable Long id) {
        Technique e = techniqueMapper.selectById(id);
        if (e != null) clearCache("techs", e.getProjectId());
        techniqueMapper.deleteById(id);
        return Result.ok();
    }

    // ==================== 灵物 CRUD ====================

    @GetMapping("/artifacts")
    public Result<?> listArtifacts(@RequestParam Long projectId) {
        return Result.ok(artifactMapper.selectList(new LambdaQueryWrapper<Artifact>().eq(Artifact::getProjectId, projectId)));
    }

    @PostMapping("/artifacts")
    public Result<?> createArtifact(@RequestBody Artifact e) {
        artifactMapper.insert(e);
        clearCache("arts", e.getProjectId());
        return Result.ok(e);
    }

    @PutMapping("/artifacts/{id}")
    public Result<?> updateArtifact(@PathVariable Long id, @RequestBody Artifact e) {
        e.setId(id);
        artifactMapper.updateById(e);
        clearCache("arts", e.getProjectId());
        return Result.ok(e);
    }

    @DeleteMapping("/artifacts/{id}")
    public Result<?> deleteArtifact(@PathVariable Long id) {
        Artifact e = artifactMapper.selectById(id);
        if (e != null) clearCache("arts", e.getProjectId());
        artifactMapper.deleteById(id);
        return Result.ok();
    }

    // ==================== 果位 CRUD（@Transactional 保证数据一致性） ====================
    @GetMapping("/entity-items")
    public Result<?> listEntityItems(@RequestParam Long projectId) {
        return Result.ok(entityItemMapper.selectList(
                new LambdaQueryWrapper<EntityItem>().eq(EntityItem::getProjectId, projectId)));
    }

    @Transactional
    @PostMapping("/entity-items")
    public Result<?> createEntityItem(@RequestBody Map<String, Object> body) {
        EntityItem entity = buildEntityItem(body);
        entityItemMapper.insert(entity);

        String tagNames = (String) body.getOrDefault("tagNames", "");
        if (!tagNames.trim().isEmpty()) {
            String[] names = tagNames.split("[,，\\n]+");
            int sort = 1;
            for (String n : names) {
                String tag = n.trim();
                if (!tag.isEmpty()) {
                    ItemTag t = new ItemTag();
                    t.setEntityItemId(entity.getId());
                    t.setName(tag);
                    t.setIsSubstitute(false);
                    t.setSortOrder(sort++);
                    itemTagMapper.insert(t);
                }
            }
        }

        clearCache("categories", entity.getProjectId());
        clearCache("item", entity.getProjectId());
        clearCache("stats", entity.getProjectId());
        return Result.ok(entity);
    }

    @Transactional
    @PutMapping("/entity-items/{id}")
    public Result<?> updateEntityItem(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        EntityItem entity = buildEntityItem(body);
        entity.setId(id);
        entityItemMapper.updateById(entity);

        String tagNames = (String) body.getOrDefault("tagNames", "");
        if (body.containsKey("tagNames")) {
            itemTagMapper.delete(new LambdaQueryWrapper<ItemTag>().eq(ItemTag::getEntityItemId, id));
            if (!tagNames.trim().isEmpty()) {
                String[] names = tagNames.split("[,，\\n]+");
                int sort = 1;
                for (String n : names) {
                    String tag = n.trim();
                    if (!tag.isEmpty()) {
                        ItemTag t = new ItemTag();
                        t.setEntityItemId(id);
                        t.setName(tag);
                        t.setIsSubstitute(false);
                        t.setSortOrder(sort++);
                        itemTagMapper.insert(t);
                    }
                }
            }
        }

        clearCache("categories", entity.getProjectId());
        clearCache("item", entity.getProjectId());
        clearCache("stats", entity.getProjectId());
        return Result.ok(entity);
    }

    @Transactional
    @DeleteMapping("/entity-items/{id}")
    public Result<?> deleteEntityItem(@PathVariable Long id) {
        EntityItem e = entityItemMapper.selectById(id);
        if (e != null) {
            itemTagMapper.delete(new LambdaQueryWrapper<ItemTag>().eq(ItemTag::getEntityItemId, id));
            clearCache("categories", e.getProjectId());
            clearCache("item", e.getProjectId());
            clearCache("stats", e.getProjectId());
        }
        entityItemMapper.deleteById(id);
        return Result.ok();
    }

    // ==================== 统计 ====================

    @GetMapping("/stats/{projectId}")
    public Result<?> getStats(@PathVariable Long projectId) {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("人物", characterMapper.selectCount(
                new LambdaQueryWrapper<CharacterProfile>().eq(CharacterProfile::getProjectId, projectId)));
        stats.put("势力", factionMapper.selectCount(
                new LambdaQueryWrapper<Faction>().eq(Faction::getProjectId, projectId)));
        stats.put("功法", techniqueMapper.selectCount(
                new LambdaQueryWrapper<Technique>().eq(Technique::getProjectId, projectId)));
        stats.put("灵物", artifactMapper.selectCount(
                new LambdaQueryWrapper<Artifact>().eq(Artifact::getProjectId, projectId)));
        stats.put("果位", entityItemMapper.selectCount(
                new LambdaQueryWrapper<EntityItem>().eq(EntityItem::getProjectId, projectId)));
        stats.put("神通标签", itemTagMapper.selectCount(new LambdaQueryWrapper<ItemTag>()));
        return Result.ok(stats);
    }

    // ==================== 工具方法 ====================

    private EntityItem buildEntityItem(Map<String, Object> body) {
        EntityItem entity = new EntityItem();
        entity.setProjectId(toLong(body.get("projectId")));
        entity.setCategoryId(toLong(body.get("categoryId")));
        entity.setSubCategoryId(toLong(body.get("subCategoryId")));
        entity.setName((String) body.get("name"));
        entity.setBadge((String) body.get("badge"));
        entity.setGoldenNature((String) body.get("goldenNature"));
        entity.setNotes((String) body.get("notes"));
        entity.setSortOrder(toInt(body.get("sortOrder"), 0));
        Object emptyCert = body.get("isEmptyCertification");
        if (emptyCert instanceof Boolean) {
            entity.setIsEmptyCertification((Boolean) emptyCert);
        } else if (emptyCert != null) {
            entity.setIsEmptyCertification(Boolean.parseBoolean(emptyCert.toString()));
        }
        return entity;
    }

    private Long toLong(Object v) {
        if (v == null) return null;
        if (v instanceof Number) return ((Number) v).longValue();
        try {
            return Long.parseLong(v.toString());
        } catch (Exception e) {
            return null;
        }
    }

    private int toInt(Object v, int def) {
        if (v == null) return def;
        if (v instanceof Number) return ((Number) v).intValue();
        try {
            return Integer.parseInt(v.toString());
        } catch (Exception e) {
            return def;
        }
    }
}