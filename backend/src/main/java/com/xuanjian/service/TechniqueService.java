package com.xuanjian.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuanjian.entity.Technique;
import com.xuanjian.mapper.TechniqueMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TechniqueService extends ServiceImpl<TechniqueMapper, Technique> {

    private final RedisTemplate<String, Object> redisTemplate;

    public java.util.List<Technique> getByProject(Long projectId) {
        String key = "techs:" + projectId;
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached instanceof java.util.List) {
            @SuppressWarnings("unchecked")
            java.util.List<Technique> result = (java.util.List<Technique>) cached;
            return result;
        }
        java.util.List<Technique> list = list(new LambdaQueryWrapper<Technique>()
                .eq(Technique::getProjectId, projectId)
                .orderByAsc(Technique::getId));
        redisTemplate.opsForValue().set(key, list, 30, TimeUnit.MINUTES);
        return list;
    }
}