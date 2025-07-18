/* src/main/java/com/investhoodit/RevisionHub/controller/ResourcesController.java */
package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.dto.ResourcesDTO;
import com.investhoodit.RevisionHub.model.ApiResponse;
import com.investhoodit.RevisionHub.model.Resources;
import com.investhoodit.RevisionHub.service.ResourcesService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class ResourcesController {
    private final ResourcesService resourcesService;

    public ResourcesController(ResourcesService resourcesService) {
        this.resourcesService = resourcesService;
    }

    @PostMapping("/api/upload-resource")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Resources>> uploadResource(@ModelAttribute ResourcesDTO resourcesDTO) {
        try {
            Resources resource = resourcesService.uploadResource(resourcesDTO.getSubjectName(), resourcesDTO.getFile());
            if (resource == null) {
                ApiResponse<Resources> response = new ApiResponse<>(
                        "Failed to upload new resource",
                        false,
                        null
                );
                return ResponseEntity.badRequest().body(response);
            }
            ApiResponse<Resources> response = new ApiResponse<>(
                    "New " + resourcesDTO.getSubjectName() + " resource upload successful",
                    true,
                    resource
            );
            return ResponseEntity.ok().body(response);
        } catch (IllegalArgumentException e) {
            ApiResponse<Resources> response = new ApiResponse<>(
                    "Invalid input: " + e.getMessage(),
                    false,
                    null
            );
            return ResponseEntity.badRequest().body(response);
        } catch (IOException e) {
            ApiResponse<Resources> response = new ApiResponse<>(
                    "Error uploading file: " + e.getMessage(),
                    false,
                    null
            );
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/api/resources")
    public ResponseEntity<ApiResponse<List<Resources>>> getResources() {
        try {
            List<Resources> resources = resourcesService.getResources();
            ApiResponse<List<Resources>> response = new ApiResponse<>(
                    "Resources retrieved successfully",
                    true,
                    resources
            );
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            ApiResponse<List<Resources>> response = new ApiResponse<>(
                    "Error retrieving resources: " + e.getMessage(),
                    false,
                    null
            );
            return ResponseEntity.badRequest().body(response);
        }
    }
}