package com.example.catDiary.dao;

import com.example.catDiary.model.CatEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CatEntryRepository extends JpaRepository<CatEntry, Long> {
    List<CatEntry> findAllByCreatedBy(Long createdBy); //find all by user

    int deleteByIdAndCreatedBy(Long id, Long createdBy);
}