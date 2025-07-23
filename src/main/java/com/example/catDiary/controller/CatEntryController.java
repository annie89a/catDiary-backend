package com.example.catDiary.controller;

import com.example.catDiary.model.CatEntry;
import com.example.catDiary.security.SecurityUtilityService;
import com.example.catDiary.service.CatEntryAccessService;
import com.example.catDiary.service.CatEntryService;
import com.example.catDiary.validation.ValidationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@RestController
public class CatEntryController {

    @Autowired
    private CatEntryValidator catEntryValidator;

    @Autowired
    private CatEntryService catEntryService;

    @Autowired
    private CatEntryAccessService catEntryAccessService;

    @Autowired
    private SecurityUtilityService securityUtilityService;

    private Long getCurrentUserId() {
        return securityUtilityService.getCurrentUserId();
    }

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @GetMapping("/apis/catEntry/list")
    public ApiResponse<List<CatEntry>> getAllEntries() {
        try {
            Long currentUserId = getCurrentUserId();
            List<CatEntry> entries = catEntryService.getAllEntriesForCurrentUser(currentUserId);
            return ApiResponse.success(entries, "Cat entries retrieved successfully");
        } catch (Exception e) {
            return ApiResponse.error("Failed to retrieve cat entries: " + e.getMessage());
        }
    }

    @GetMapping("/apis/catEntry/{id}")
    public ApiResponse<CatEntry> getEntryById(@PathVariable Long id) {
        Long currentUserId = getCurrentUserId();

        //check access first
        String accessError = catEntryAccessService.validateViewAccess(id, currentUserId);
        if (accessError != null) {
            return ApiResponse.error(accessError);
        }
        try {
            Optional<CatEntry> entry = catEntryService.getEntryById(id);

            if (entry.isPresent()) {
                return ApiResponse.success(entry.get(), "Cat entry retrieved successfully");
            } else {
                return ApiResponse.error("Cat entry not found with id: " + id);
            }
        } catch (Exception e) {
            return ApiResponse.error("Failed to retrieve cat entry: " + e.getMessage());
        }
    }

    @PostMapping("/apis/catEntry/create")
    public ApiResponse<CatEntry> createCatEntry(@RequestBody CatEntryCreateModel catEntryCreateModel) {
        Long currentUserId = getCurrentUserId();
        //check access first
        String accessError = catEntryAccessService.validateCreateAccess(currentUserId);
        if (accessError != null) {
            return ApiResponse.error(accessError);
        }
        //validation
        ValidationResult validation = catEntryValidator.validateCreate(catEntryCreateModel);

        if (!validation.isValid()) {
            return ApiResponse.error("Validation failed", validation.getErrors());
        }

        try {
            CatEntry createdEntry = catEntryService.createEntry(catEntryCreateModel, currentUserId);
            return ApiResponse.success(createdEntry, "Cat entry created successfully");
        } catch (Exception e) {
            return ApiResponse.error("Failed to create cat entry: " + e.getMessage());
        }
    }

    @PutMapping("/apis/catEntry/update")
    public ApiResponse<CatEntry> updateCatEntry(@RequestBody CatEntryUpdateModel catEntryUpdateModel) {
        Long currentUserId = getCurrentUserId();
        //check access first
        String accessError = catEntryAccessService.validateEditAccess(catEntryUpdateModel.getId(), currentUserId);
        if (accessError != null) {
            return ApiResponse.error(accessError);
        }
        //validation
        ValidationResult validation = catEntryValidator.validateUpdate(catEntryUpdateModel);

        if (!validation.isValid()) {
            return ApiResponse.error("Validation failed", validation.getErrors());
        }

        try {
            Optional<CatEntry> updatedEntry = catEntryService.updateEntry(catEntryUpdateModel, currentUserId);

            if (updatedEntry.isPresent()) {
                return ApiResponse.success(updatedEntry.get(), "Cat entry updated successfully");
            } else {
                return ApiResponse.error("Cat entry not found with id: " + catEntryUpdateModel.getId());
            }
        } catch (Exception e) {
            return ApiResponse.error("Failed to update cat entry: " + e.getMessage());
        }
    }

    @DeleteMapping("/apis/catEntry/delete/{id}")
    public ApiResponse<String> deleteEntry(@PathVariable Long id) {
        Long currentUserId = getCurrentUserId();
        //check access first
        String accessError = catEntryAccessService.validateDeleteAccess(id, currentUserId);
        if (accessError != null) {
            return ApiResponse.error(accessError);
        }
        try {
            catEntryService.deleteEntry(id, currentUserId);
            return ApiResponse.success("Cat entry deleted successfully");
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Failed to delete cat entry: " + e.getMessage());
        }
    }

    @PostMapping("/apis/catEntry/upload-image")
    public ApiResponse<ImageUploadResponse> uploadImage(@RequestParam("image") MultipartFile file) {
        try {
            //Long currentUserId = getCurrentUserId();

            // validate file
            if (file.isEmpty()) {
                return ApiResponse.error("Please select a file to upload");
            }

            // check file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ApiResponse.error("Only image files are allowed");
            }

            //check file size - 5MB
            long maxSize = 5 * 1024 * 1024; // 5MB
            if (file.getSize() > maxSize) {
                return ApiResponse.error("File size must be less than 5MB");
            }

            // create upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            //generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

            //save file
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            //generate URL
            String imageUrl = "/uploads/" + uniqueFilename;

            ImageUploadResponse response = new ImageUploadResponse(imageUrl, uniqueFilename);
            return ApiResponse.success(response, "Image uploaded successfully");

        } catch (IOException e) {
            return ApiResponse.error("Failed to upload image: " + e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("Unexpected error occurred: " + e.getMessage());
        }
    }
    public static class ImageUploadResponse {
        private String imageUrl;
        private String fileName;

        public ImageUploadResponse(String imageUrl, String fileName) {
            this.imageUrl = imageUrl;
            this.fileName = fileName;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }
    }
}
