package com.xuanjian.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuanjian.entity.CharacterProfile;
import com.xuanjian.entity.Faction;
import com.xuanjian.mapper.CharacterProfileMapper;
import com.xuanjian.mapper.FactionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CharacterService extends ServiceImpl<CharacterProfileMapper, CharacterProfile> {

    private final FactionMapper factionMapper;
    private final JdbcTemplate jdbcTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    public List<Map<String, Object>> getCharacters(Long projectId, String status, String lineage) {
        String cacheKey = "chars:" + projectId + ":" + (status != null ? status : "all") + ":" + (lineage != null ? lineage : "all");
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached instanceof List) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> result = (List<Map<String, Object>>) cached;
            return result;
        }

        LambdaQueryWrapper<CharacterProfile> wrapper = new LambdaQueryWrapper<CharacterProfile>()
                .eq(CharacterProfile::getProjectId, projectId);
        if (status != null && !status.isEmpty()) {
            wrapper.eq(CharacterProfile::getStatus, status);
        }
        if (lineage != null && !lineage.isEmpty()) {
            wrapper.eq(CharacterProfile::getLineage, lineage);
        }
        wrapper.orderByAsc(CharacterProfile::getSortOrder);

        List<CharacterProfile> characters = list(wrapper);
        List<Long> factionIds = characters.stream()
                .map(CharacterProfile::getFactionId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, Faction> factionMap = new HashMap<>();
        if (!factionIds.isEmpty()) {
            List<Faction> factions = factionMapper.selectBatchIds(factionIds);
            factionMap = factions.stream().collect(Collectors.toMap(Faction::getId, f -> f));
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (CharacterProfile c : characters) {
            result.add(buildCharacterMap(c, factionMap));
        }

        redisTemplate.opsForValue().set(cacheKey, result, 15, TimeUnit.MINUTES);
        return result;
    }

    private Map<String, Object> buildCharacterMap(CharacterProfile c, Map<Long, Faction> factionMap) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", c.getId());
        item.put("name", c.getName());
        item.put("title", c.getTitle());
        item.put("realm", c.getRealm());
        item.put("daoTradition", c.getDaoTradition());
        item.put("status", c.getStatus());
        item.put("lineage", c.getLineage());
        item.put("branch", c.getBranch());
        item.put("causeOfDeath", c.getCauseOfDeath());
        item.put("description", c.getDescription());
        item.put("notableEvents", c.getNotableEvents());
        item.put("spouse", c.getSpouse());
        item.put("isKeyFigure", c.getIsKeyFigure());
        item.put("avatarUrl", "/api/file/image/avatar/thumb/" + c.getId() + ".png");

        if (c.getFactionId() != null && factionMap.containsKey(c.getFactionId())) {
            Faction f = factionMap.get(c.getFactionId());
            item.put("factionId", f.getId());
            item.put("factionName", f.getName());
            item.put("factionColor", f.getColor());
        }

        List<Map<String, Object>> relations = jdbcTemplate.queryForList(
                "SELECT cr.*, cp.name as target_name, cp.status as target_status, cp.title as target_title " +
                        "FROM character_relation cr " +
                        "JOIN character_profile cp ON cr.target_char_id = cp.id " +
                        "WHERE cr.source_char_id = ?", c.getId());
        item.put("relations", relations.stream().map(r -> {
            Map<String, Object> rel = new LinkedHashMap<>();
            rel.put("targetId", r.get("target_char_id"));
            rel.put("targetName", r.get("target_name"));
            rel.put("targetTitle", r.get("target_title"));
            rel.put("targetStatus", r.get("target_status"));
            rel.put("relationType", r.get("relation_type"));
            rel.put("description", r.get("description"));
            return rel;
        }).collect(Collectors.toList()));

        return item;
    }
}