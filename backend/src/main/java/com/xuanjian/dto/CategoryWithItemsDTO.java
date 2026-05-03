package com.xuanjian.dto;

import lombok.Data;

import java.util.List;

@Data
public class CategoryWithItemsDTO {
    private Long id;
    private String name;
    private String element;
    private int itemCount;
    private List<SubCategoryWithItemsDTO> subCategories;
    private List<ItemBriefDTO> items; // 没有子分类时直接挂载

    @Data
    public static class SubCategoryWithItemsDTO {
        private Long id;
        private String name;
        private List<ItemBriefDTO> items;
    }
}