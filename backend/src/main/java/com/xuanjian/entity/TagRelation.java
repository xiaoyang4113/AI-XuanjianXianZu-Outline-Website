package com.xuanjian.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("tag_relation")
public class TagRelation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long sourceTagId;
    private Long targetTagId;
    private String targetName;
    private String relationType;
    private String notes;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}