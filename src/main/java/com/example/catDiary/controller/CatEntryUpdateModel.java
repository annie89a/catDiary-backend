package com.example.catDiary.controller;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class CatEntryUpdateModel {
    private Long id;
    private String catName;
    private String mood;
    private String location;
    private String notes;
    private String imageUrl;
    private Long modifiedBy;
}
