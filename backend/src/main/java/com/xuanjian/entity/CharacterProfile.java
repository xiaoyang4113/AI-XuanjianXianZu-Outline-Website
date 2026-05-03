package com.xuanjian.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("character_profile")
public class CharacterProfile {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long projectId;
    private String name;
    private String title;          // 新增
    private String realm;
    private String daoTradition;
    private Long factionId;
    private String status;
    private String lineage;        // 新增
    private String branch;         // 新增
    private String causeOfDeath;   // 新增
    private String description;
    private String notableEvents;  // 新增
    private String spouse;         // 新增
    private Boolean isKeyFigure;   // 新增
    private Integer sortOrder;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}