package com.xuanjian.dto;

import lombok.Data;

import java.util.List;

@Data
public class ItemBriefDTO {
    private Long id;
    private String name;
    private String badge;
    private String goldenNature;
    private String notes;
    private List<TagDTO> tags;
    private List<SubstituteNoteDTO> substituteNotes;
    private Boolean isEmptyCertification;

    @Data
    public static class TagDTO {
        private Long id;
        private String name;
        private boolean substitute;
    }

    @Data
    public static class SubstituteNoteDTO {
        private String source;
        private String target;
    }
}