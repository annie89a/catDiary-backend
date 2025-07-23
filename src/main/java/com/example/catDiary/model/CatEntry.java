package com.example.catDiary.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "cat_diary_entries")
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CatEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "catName")
    private String catName;

    @Column(name = "mood")
    private String mood;


    @Column(name = "location")
    private String location;

    @Column(name = "notes")
    private String notes;

    @Column(name = "imageUrl")
    private String imageUrl;

    @UpdateTimestamp
    @Column(name = "modifiedDate")
    private LocalDateTime modifiedDate;

    @CreationTimestamp
    @Column(name = "createdDate", updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "createdBy")
    private Long createdBy;

    @Column(name = "modifiedBy")
    private Long modifiedBy;
}
