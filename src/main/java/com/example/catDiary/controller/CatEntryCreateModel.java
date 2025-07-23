package com.example.catDiary.controller;

import com.example.catDiary.model.CatEntry;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class CatEntryCreateModel {
    //takes in object of CatEntry in the constructor
    private String catName;
    private String mood;
    private String location;
    private String notes;
    private String imageUrl;
    private Long createdBy;
}
