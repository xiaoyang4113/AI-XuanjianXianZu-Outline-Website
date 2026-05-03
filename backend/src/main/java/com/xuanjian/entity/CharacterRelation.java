package com.xuanjian.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("character_relation")
public class CharacterRelation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long sourceCharId;
    private Long targetCharId;
    private String relationType;
    private String description;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}