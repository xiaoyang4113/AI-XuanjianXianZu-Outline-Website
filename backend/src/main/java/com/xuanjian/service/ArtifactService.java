package com.xuanjian.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuanjian.entity.Artifact;
import com.xuanjian.mapper.ArtifactMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ArtifactService extends ServiceImpl<ArtifactMapper, Artifact> {

    private final RedisTemplate<String, Object> redisTemplate;

    public java.util.List<Artifact> getByProject(Long projectId) {
        String key = "arts:" + projectId;
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached instanceof java.util.List) {
            @SuppressWarnings("unchecked")
            java.util.List<Artifact> result = (java.util.List<Artifact>) cached;
            return result;
        }
        java.util.List<Artifact> list = list(new LambdaQueryWrapper<Artifact>()
                .eq(Artifact::getProjectId, projectId));
        redisTemplate.opsForValue().set(key, list, 30, TimeUnit.MINUTES);
        return list;
    }
}