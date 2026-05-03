package com.xuanjian.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("faction_relation")
public class FactionRelation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long sourceFactionId;
    private Long targetFactionId;
    private String relationType;
    private String description;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}