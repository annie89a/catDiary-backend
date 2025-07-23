package com.example.catDiary.service;

import com.example.catDiary.controller.CatEntryCreateModel;
import com.example.catDiary.controller.CatEntryUpdateModel;
import com.example.catDiary.dao.CatEntryRepository;
import com.example.catDiary.model.CatEntry;
import com.example.catDiary.security.SecurityUtilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CatEntryService {

    @Autowired
    private CatEntryRepository catEntryRepository;

    public List<CatEntry> getAllEntriesForCurrentUser(Long currentUserId) {
        return catEntryRepository.findAllByCreatedBy(currentUserId);
    }

    public Optional<CatEntry> getEntryById(Long id) {
        return catEntryRepository.findById(id);
    }

    public CatEntry createEntry(CatEntryCreateModel catEntryCreateModel, Long currentUserId) {
        CatEntry catEntry = new CatEntry();
        catEntry.setCatName(catEntryCreateModel.getCatName());
        catEntry.setMood(catEntryCreateModel.getMood());
        catEntry.setLocation(catEntryCreateModel.getLocation());
        catEntry.setNotes(catEntryCreateModel.getNotes());
        catEntry.setImageUrl(catEntryCreateModel.getImageUrl());
        catEntry.setCreatedBy(currentUserId);

        return catEntryRepository.save(catEntry);
    }

    public Optional<CatEntry> updateEntry(CatEntryUpdateModel catEntryUpdateModel, Long currentUserId) {
        Optional<CatEntry> existingEntry = catEntryRepository.findById(catEntryUpdateModel.getId());

        if (existingEntry.isPresent()) {
            CatEntry catEntry = existingEntry.get();
            catEntry.setCatName(catEntryUpdateModel.getCatName());
            catEntry.setMood(catEntryUpdateModel.getMood());
            catEntry.setLocation(catEntryUpdateModel.getLocation());
            catEntry.setNotes(catEntryUpdateModel.getNotes());
            catEntry.setImageUrl(catEntryUpdateModel.getImageUrl());
            catEntry.setModifiedBy(currentUserId);

            return Optional.of(catEntryRepository.save(catEntry));
        }

        return Optional.empty();
    }

    @Transactional
    public void deleteEntry(Long id, Long currentUserId) {
        int deletedCount = catEntryRepository.deleteByIdAndCreatedBy(id, currentUserId);

        if (deletedCount == 0) {
            throw new RuntimeException("Entry not found or no permission to delete entry");
        }
    }
}