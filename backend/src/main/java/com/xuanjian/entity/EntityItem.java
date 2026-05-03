package com.xuanjian.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("entity_item")
public class EntityItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long projectId;
    private Long categoryId;
    private Long subCategoryId;
    private String name;
    private String badge;
    private String goldenNature;
    private String notes;
    private Integer sortOrder;

    @TableField("is_empty_certification")
    private Boolean isEmptyCertification;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}