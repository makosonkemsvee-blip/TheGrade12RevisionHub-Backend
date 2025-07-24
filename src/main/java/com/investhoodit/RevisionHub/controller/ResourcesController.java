package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.dto.ResourcesDTO;
import com.investhoodit.RevisionHub.model.ApiResponse;
import com.investhoodit.RevisionHub.model.Resources;
import com.investhoodit.RevisionHub.service.ResourcesService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ResourcesController {
    private final ResourcesService resourcesService;

    public ResourcesController(ResourcesService resourcesService) {
        this.resourcesService = resourcesService;
    }

    @PostMapping("/admin/upload-resource")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Resources>> uploadResource(@ModelAttribute ResourcesDTO resourcesDTO) {
        try {
            String tags = resourcesDTO.getTags() != null ? String.join(",", resourcesDTO.getTags()) : "";
            Resources resource = resourcesService.uploadResource(
                    resourcesDTO.getSubjectName() != null ? resourcesDTO.getSubjectName().getSubjectName() : null,
                    resourcesDTO.getResourceType(),
                    resourcesDTO.getTitle(),
                    resourcesDTO.getDescription(),
                    tags,
                    resourcesDTO.getLink(),
                    resourcesDTO.getFile()
            );
            if (resource == null) {
                ApiResponse<Resources> response = new ApiResponse<>(
                        "Failed to upload new resource",
                        false,
                        null
                );
                return ResponseEntity.badRequest().body(response);
            }
            ApiResponse<Resources> response = new ApiResponse<>(
                    "New " + resourcesDTO.getSubjectName().getSubjectName() + " resource upload successful",
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

    @GetMapping("/user/resources")
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

    @GetMapping("/user/Uploads/view/{filename:.+}")
    @PreAuthorize("isAuthenticated() or @securityConfig.isTokenValid(#request.getParameter('token'))")
    public ResponseEntity<Resource> viewFile(@PathVariable String filename, @RequestParam(value = "token", required = false) String token, HttpServletRequest request) {
        File file = new File("Uploads/" + filename).getAbsoluteFile();
        System.out.println("Received view request for filename: " + filename);
        System.out.println("Attempting to view file: " + file.getAbsolutePath());
        if (!file.exists() || !file.isFile()) {
            System.out.println("File not found or not a file: " + file.getAbsolutePath());
            return ResponseEntity.status(404).body(null);
        }
        Resource resource = new FileSystemResource(file);
        String contentType = determineContentType(filename);
        System.out.println("Serving file with content type: " + contentType);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                .header("X-Content-Type-Options", "nosniff")
                .body(resource);
    }

    @GetMapping("/user/Uploads/download/{filename:.+}")
    @PreAuthorize("isAuthenticated() or @securityConfig.isTokenValid(#request.getParameter('token'))")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename, @RequestParam(value = "token", required = false) String token, HttpServletRequest request) {
        File file = new File("Uploads/" + filename).getAbsoluteFile();
        System.out.println("Received download request for filename: " + filename);
        System.out.println("Attempting to download file: " + file.getAbsolutePath());
        if (!file.exists() || !file.isFile()) {
            System.out.println("File not found or not a file: " + file.getAbsolutePath());
            return ResponseEntity.status(404).body(null);
        }
        Resource resource = new FileSystemResource(file);
        String contentType = determineContentType(filename);
        System.out.println("Downloading file with content type: " + contentType);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                .header("X-Content-Type-Options", "nosniff")
                .body(resource);
    }

    private String determineContentType(String filename) {
        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        switch (extension) {
            case "pdf":
                return "application/pdf";
            case "png":
                return "image/png";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "mp4":
                return "video/mp4";
            case "webm":
                return "video/webm";
            default:
                try {
                    String contentType = Files.probeContentType(new File("Uploads/" + filename).toPath());
                    return contentType != null ? contentType : "application/octet-stream";
                } catch (IOException e) {
                    System.err.println("Failed to determine content type for " + filename + ": " + e.getMessage());
                    return "application/octet-stream";
                }
        }
    }
}