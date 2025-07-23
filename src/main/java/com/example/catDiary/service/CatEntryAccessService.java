package com.example.catDiary.service;

import com.example.catDiary.dao.CatEntryRepository;
import com.example.catDiary.model.CatEntry;
import com.example.catDiary.security.SecurityUtilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CatEntryAccessService {
    @Autowired
    private CatEntryRepository catEntryRepository;

    public boolean canCreate(Long currentUserId) {
        if (currentUserId == null) {
            return false;
        }

        return true;

    }

    public boolean canEdit(CatEntry catEntry, Long currentUserId) {
        if (catEntry == null) {
            return false;
        }

        if (currentUserId == null) {
            return false;
        }

        if (!catEntry.getCreatedBy().equals(currentUserId)) {
            return false;
        }

        return true;
    }

    public boolean canEdit(Long entryId, Long currentUserId) {
        if (entryId == null) {
            return false;
        }

        try {
            Optional<CatEntry> entry = catEntryRepository.findById(entryId);
            return entry.map(catEntry -> canEdit(catEntry, currentUserId)).orElse(false);
        } catch (Exception e) {
            return false;
        }

    }

    public boolean canDelete(CatEntry catEntry, Long currentUserId) {
        if (catEntry == null) {
            return false;
        }

        if (currentUserId == null) {
            return false;
        }

        if (!catEntry.getCreatedBy().equals(currentUserId)) {
            return false;
        }

        return true;
    }

    public boolean canDelete(Long entryId, Long currentUserId) {
        if (entryId == null) {
            return false;
        }

        try {
            Optional<CatEntry> entry = catEntryRepository.findById(entryId);
            return entry.map(catEntry -> canDelete(catEntry, currentUserId)).orElse(false);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean canView(CatEntry catEntry, Long currentUserId) {
        if (catEntry == null) {
            return false;
        }

        if (currentUserId == null) {
            return false;
        }

        if (!catEntry.getCreatedBy().equals(currentUserId)) {
            return false;
        }

        return true;
    }

    public boolean canView(Long entryId, Long currentUserId) {
        if (entryId == null) {
            return false;
        }

        try {
            Optional<CatEntry> entry = catEntryRepository.findById(entryId);
            return entry.map(catEntry -> canView(catEntry, currentUserId)).orElse(false);
        } catch (Exception e) {
            return false;
        }
    }

    public String validateAccess(boolean hasAccess, String operation) {
        if (!hasAccess) {
            return "Access denied for operation:" + operation;
        }

        return null;
    }

    public String validateCreateAccess(Long currentUserId) {
        return validateAccess(canCreate(currentUserId), "CREATE");
    }

    public String validateEditAccess(CatEntry catEntry, Long currentUserId) {
        return validateAccess(canEdit(catEntry, currentUserId), "EDIT");
    }

    public String validateEditAccess(Long entryId, Long currentUserId) {
        return validateAccess(canEdit(entryId, currentUserId), "EDIT");
    }

    public String validateDeleteAccess(CatEntry catEntry, Long currentUserId) {
        return validateAccess(canDelete(catEntry, currentUserId), "DELETE");
    }


    public String validateDeleteAccess(Long entryId, Long currentUserId) {
        return validateAccess(canDelete(entryId, currentUserId), "DELETE");
    }

    public String validateViewAccess(CatEntry catEntry, Long currentUserId) {
        return validateAccess(canView(catEntry, currentUserId), "VIEW");
    }

    public String validateViewAccess(Long entryId, Long currentUserId) {
        return validateAccess(canView(entryId, currentUserId), "VIEW");
    }

}