package com.xuanjian.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("realm_stage")
public class RealmStage {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long realmId;
    private String name;
    private String description;
    private Boolean isCritical;
    private Integer sortOrder;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}