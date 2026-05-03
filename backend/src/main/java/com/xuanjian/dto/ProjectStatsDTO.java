package com.xuanjian.dto;

import lombok.Data;

import java.util.Map;

@Data
public class ProjectStatsDTO {
    private Long projectId;
    private String projectName;
    private int totalItems;
    private int totalCategories;
    private int totalTags;
    private int goldenNaturesCount;
    private Map<String, Integer> categoryCounts; // 分类名 -> 条目数
}