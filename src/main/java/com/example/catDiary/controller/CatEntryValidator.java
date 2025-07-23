package com.example.catDiary.controller;

import com.example.catDiary.validation.ValidationResult;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class CatEntryValidator {

    public ValidationResult validateCreate(CatEntryCreateModel model) {
        ValidationResult result = new ValidationResult();

        //validate common fields
        validateCommonFields(model.getCatName(), model.getMood(), result);

        //validate createdBy field
        if (model.getCreatedBy() != null) {
            result.addError("createdBy is required");
        }

        //validate optional fields with length limits
        validateOptionalFields(model.getLocation(), model.getNotes(), model.getImageUrl(), result);

        return result;
    }

    public ValidationResult validateUpdate(CatEntryUpdateModel model) {
        ValidationResult result = new ValidationResult();

        //validate ID
        if (model.getId() == null) {
            result.addError("ID is required for update");
        }

        //validate common fields
        validateCommonFields(model.getCatName(), model.getMood(), result);

        //validate modifiedBy field
        if (model.getModifiedBy() != null) {
            result.addError("modifiedBy is required");
        }

        //validate optional fields with length limits
        validateOptionalFields(model.getLocation(), model.getNotes(), model.getImageUrl(), result);

        return result;
    }

    private void validateCommonFields(String catName, String mood, ValidationResult result) {
        //validate cat name
        if (!StringUtils.hasText(catName)) {
            result.addError("Cat name is required");
        } else if (catName.length() > 100) {
            result.addError("Cat name must be at most 100 characters");
        }

        //validate mood
        if (!StringUtils.hasText(mood)) {
            result.addError("Mood is required");
        } else if (mood.length() > 50) {
            result.addError("Mood must be at most 50 characters");
        }
    }

    private void validateOptionalFields(String location, String notes, String imageUrl, ValidationResult result) {
        //validate location (optional)
        if (location != null && location.length() > 255) {
            result.addError("Location must be at most 255 characters");
        }

        //validate notes (optional)
        if (notes != null && notes.length() > 1000) {
            result.addError("Notes must be at most 1000 characters");
        }

        //validate image URL (optional)
        if (imageUrl != null && imageUrl.length() > 500) {
            result.addError("Image URL must be at most 500 characters");
        }
    }
}
