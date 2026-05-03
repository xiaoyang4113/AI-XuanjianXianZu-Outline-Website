package com.xuanjian.dto;

import lombok.Data;

import java.util.List;

@Data
public class ItemDetailDTO {
    private Long id;
    private String name;
    private String badge;
    private String goldenNature;
    private String notes;
    private String categoryName;
    private String subCategoryName;
    private List<ItemBriefDTO.TagDTO> tags;
    private List<ItemBriefDTO.SubstituteNoteDTO> substituteNotes;
    private Boolean isEmptyCertification;


}