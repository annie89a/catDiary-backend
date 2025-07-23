package com.example.catDiary.validation;

import java.util.ArrayList;
import java.util.List;

public class ValidationUtils {

    public static ValidationResult combine(ValidationResult... results) {
        List<String> allErrors = new ArrayList<>();

        for (ValidationResult result : results) {
            if (!result.isValid()) {
                allErrors.addAll(result.getErrors());
            }
        }

        return allErrors.isEmpty() ? ValidationResult.success() : ValidationResult.failure(allErrors);
    }

    public static ValidationResult combine(List<ValidationResult> results) {
        return combine(results.toArray(new ValidationResult[0]));
    }
}