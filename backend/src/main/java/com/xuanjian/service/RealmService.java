package com.xuanjian.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuanjian.entity.CultivationPath;
import com.xuanjian.entity.Realm;
import com.xuanjian.entity.RealmStage;
import com.xuanjian.mapper.CultivationPathMapper;
import com.xuanjian.mapper.RealmMapper;
import com.xuanjian.mapper.RealmStageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RealmService extends ServiceImpl<RealmMapper, Realm> {

    private final RealmStageMapper realmStageMapper;
    private final CultivationPathMapper cultivationPathMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final JdbcTemplate jdbcTemplate;

    /**
     * 获取所有境界（含子阶段），按排序返回
     */
    public List<Map<String, Object>> getRealms(Long projectId) {
        String cacheKey = "realms:" + projectId;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached instanceof List) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> result = (List<Map<String, Object>>) cached;
            return result;
        }

        List<Realm> realms = list(new LambdaQueryWrapper<Realm>()
                .eq(Realm::getProjectId, projectId)
                .orderByAsc(Realm::getSortOrder));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Realm realm : realms) {
            List<RealmStage> stages = realmStageMapper.selectList(
                    new LambdaQueryWrapper<RealmStage>()
                            .eq(RealmStage::getRealmId, realm.getId())
                            .orderByAsc(RealmStage::getSortOrder));

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", realm.getId());
            item.put("name", realm.getName());
            item.put("title", realm.getTitle());
            item.put("lifespan", realm.getLifespan());
            item.put("description", realm.getDescription());
            item.put("promotionCondition", realm.getPromotionCondition());
            item.put("isCriticalPoint", realm.getIsCriticalPoint());
            item.put("sortOrder", realm.getSortOrder());
            item.put("stages", stages.stream().map(s -> {
                Map<String, Object> stageMap = new LinkedHashMap<>();
                stageMap.put("id", s.getId());
                stageMap.put("name", s.getName());
                stageMap.put("description", s.getDescription());
                stageMap.put("isCritical", s.getIsCritical());
                return stageMap;
            }).collect(Collectors.toList()));
            result.add(item);
        }

        redisTemplate.opsForValue().set(cacheKey, result, 30, TimeUnit.MINUTES);
        return result;
    }

    /**
     * 获取所有修炼道统
     */
    public List<CultivationPath> getPaths(Long projectId) {
        String cacheKey = "paths:" + projectId;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached instanceof List) {
            @SuppressWarnings("unchecked")
            List<CultivationPath> result = (List<CultivationPath>) cached;
            return result;
        }

        List<CultivationPath> paths = cultivationPathMapper.selectList(
                new LambdaQueryWrapper<CultivationPath>()
                        .eq(CultivationPath::getProjectId, projectId)
                        .orderByAsc(CultivationPath::getSortOrder));

        redisTemplate.opsForValue().set(cacheKey, paths, 30, TimeUnit.MINUTES);
        return paths;
    }

    /**
     * 获取某个道统在各境界的表现（关联 realm）
     */
    public List<Map<String, Object>> getPathStages(Long pathId) {
        String cacheKey = "pathStages:" + pathId;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached instanceof List) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> result = (List<Map<String, Object>>) cached;
            return result;
        }

        // 从 cultivation_path_stage 表查
        List<Map<String, Object>> raw = jdbcTemplate.queryForList(
                "SELECT cps.*, r.name as realm_name, r.sort_order as realm_sort " +
                        "FROM cultivation_path_stage cps " +
                        "JOIN realm r ON cps.realm_id = r.id " +
                        "WHERE cps.path_id = ? ORDER BY r.sort_order", pathId);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> row : raw) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("realmId", row.get("realm_id"));
            item.put("realmName", row.get("realm_name"));
            item.put("realmSort", row.get("realm_sort"));
            item.put("description", row.get("description"));
            result.add(item);
        }

        redisTemplate.opsForValue().set(cacheKey, result, 30, TimeUnit.MINUTES);
        return result;
    }
}