package com.xuanjian.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuanjian.entity.CharacterProfile;
import com.xuanjian.entity.Faction;
import com.xuanjian.entity.FactionRelation;
import com.xuanjian.mapper.CharacterProfileMapper;
import com.xuanjian.mapper.FactionMapper;
import com.xuanjian.mapper.FactionRelationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FactionService extends ServiceImpl<FactionMapper, Faction> {

    private final FactionRelationMapper factionRelationMapper;
    private final CharacterProfileMapper characterProfileMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    public List<Map<String, Object>> getFactions(Long projectId) {
        String cacheKey = "factions:" + projectId;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached instanceof List) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> result = (List<Map<String, Object>>) cached;
            return result;
        }

        List<Faction> factions = list(new LambdaQueryWrapper<Faction>()
                .eq(Faction::getProjectId, projectId)
                .orderByAsc(Faction::getSortOrder));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Faction f : factions) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", f.getId());
            item.put("name", f.getName());
            item.put("leaderName", f.getLeaderName());
            item.put("highestRealm", f.getHighestRealm());
            item.put("territory", f.getTerritory());
            item.put("trueGoal", f.getTrueGoal());
            item.put("description", f.getDescription());
            item.put("color", f.getColor());

            // 成员
            List<CharacterProfile> members = characterProfileMapper.selectList(
                    new LambdaQueryWrapper<CharacterProfile>()
                            .eq(CharacterProfile::getFactionId, f.getId()));
            item.put("memberCount", members.size());
            item.put("members", members.stream().map(c -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("id", c.getId());
                m.put("name", c.getName());
                m.put("realm", c.getRealm());
                m.put("status", c.getStatus());
                return m;
            }).collect(Collectors.toList()));

            // 势力关系
            List<FactionRelation> outgoing = factionRelationMapper.selectList(
                    new LambdaQueryWrapper<FactionRelation>()
                            .eq(FactionRelation::getSourceFactionId, f.getId()));
            List<FactionRelation> incoming = factionRelationMapper.selectList(
                    new LambdaQueryWrapper<FactionRelation>()
                            .eq(FactionRelation::getTargetFactionId, f.getId()));

            List<Map<String, Object>> relations = new ArrayList<>();
            Set<Long> seen = new HashSet<>();
            for (FactionRelation r : outgoing) {
                if (seen.add(r.getId())) {
                    Map<String, Object> rel = new LinkedHashMap<>();
                    Faction target = getById(r.getTargetFactionId());
                    rel.put("targetId", r.getTargetFactionId());
                    rel.put("targetName", target != null ? target.getName() : "?");
                    rel.put("targetColor", target != null ? target.getColor() : "#666");
                    rel.put("relationType", r.getRelationType());
                    rel.put("description", r.getDescription());
                    rel.put("direction", "out");
                    relations.add(rel);
                }
            }
            for (FactionRelation r : incoming) {
                if (seen.add(r.getId())) {
                    Map<String, Object> rel = new LinkedHashMap<>();
                    Faction source = getById(r.getSourceFactionId());
                    rel.put("targetId", r.getSourceFactionId());
                    rel.put("targetName", source != null ? source.getName() : "?");
                    rel.put("targetColor", source != null ? source.getColor() : "#666");
                    rel.put("relationType", r.getRelationType());
                    rel.put("description", r.getDescription());
                    rel.put("direction", "in");
                    relations.add(rel);
                }
            }
            item.put("relations", relations);

            result.add(item);
        }

        redisTemplate.opsForValue().set(cacheKey, result, 15, TimeUnit.MINUTES);
        return result;
    }

    public List<Map<String, Object>> getAllRelations(Long projectId) {
        List<FactionRelation> all = factionRelationMapper.selectList(null);
        return all.stream().map(r -> {
            Map<String, Object> item = new LinkedHashMap<>();
            Faction source = getById(r.getSourceFactionId());
            Faction target = getById(r.getTargetFactionId());
            item.put("sourceId", r.getSourceFactionId());
            item.put("sourceName", source != null ? source.getName() : "?");
            item.put("sourceColor", source != null ? source.getColor() : "#666");
            item.put("targetId", r.getTargetFactionId());
            item.put("targetName", target != null ? target.getName() : "?");
            item.put("targetColor", target != null ? target.getColor() : "#666");
            item.put("relationType", r.getRelationType());
            item.put("description", r.getDescription());
            return item;
        }).collect(Collectors.toList());
    }
}