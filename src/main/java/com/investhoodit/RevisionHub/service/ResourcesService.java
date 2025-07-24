package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.model.Resources;
import com.investhoodit.RevisionHub.model.Subject;
import com.investhoodit.RevisionHub.repository.ResourceRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResourcesService {
    private final ResourceRepository resourceRepository;
    private final String uploadDir = "Uploads/";

    public ResourcesService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            System.out.println("Creating Uploads directory at: " + dir.getAbsolutePath());
            boolean created = dir.mkdirs();
            if (!created) {
                System.err.println("Failed to create Uploads directory");
            }
        }
    }

    public Resources uploadResource(String subjectName, String resourceType, String title,
                                    String description, String tags, String link, MultipartFile file)
            throws IOException {
        if (subjectName == null || subjectName.isEmpty()) {
            throw new IllegalArgumentException("Subject is required.");
        }
        if (resourceType == null || !Arrays.asList("file", "link").contains(resourceType)) {
            throw new IllegalArgumentException("Invalid resource type.");
        }
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Title is required.");
        }

        Resources resource = new Resources();
        resource.setTitle(title);
        resource.setDescription(description != null ? description : "Resource for " + subjectName);
        resource.setSubject(new Subject(subjectName));
        resource.setResourceType(resourceType);
        resource.setUploadedAt(LocalDateTime.now());

        if (tags != null && !tags.isEmpty()) {
            List<String> tagList = Arrays.stream(tags.split(","))
                    .map(String::trim)
                    .filter(tag -> !tag.isEmpty())
                    .collect(Collectors.toList());
            resource.setTags(tagList);
        }

        if (resourceType.equals("file")) {
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("File is required for file resource type.");
            }
            String[] allowedTypes = {
                    "application/pdf",
                    "image/png",
                    "image/jpeg",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    "video/mp4",
                    "video/webm"
            };
            boolean isValidType = Arrays.asList(allowedTypes).contains(file.getContentType());
            if (!isValidType) {
                throw new IllegalArgumentException("Only PDF, PNG, JPEG, DOCX, MP4, and WebM files are allowed.");
            }
            // Sanitize filename to remove spaces and special characters
            String originalFileName = file.getOriginalFilename();
            String sanitizedFileName = originalFileName.replaceAll("[^a-zA-Z0-9.-]", "_");
            String fileName = System.currentTimeMillis() + "-" + sanitizedFileName;
            Path filePath = Paths.get(uploadDir, fileName).toAbsolutePath().normalize();
            System.out.println("Saving file to: " + filePath);
            try {
                Files.write(filePath, file.getBytes());
                System.out.println("File saved successfully: " + fileName);
            } catch (IOException e) {
                System.err.println("Failed to save file: " + e.getMessage());
                throw new IOException("Failed to save file: " + fileName, e);
            }
            resource.setUrl("/Uploads/" + fileName);
            resource.setFileName(originalFileName); // Store original filename for display
            resource.setFileType(file.getContentType());
        } else if (resourceType.equals("link")) {
            if (link == null || link.isEmpty()) {
                throw new IllegalArgumentException("Link is required for link resource type.");
            }
            try {
                new URL(link).toURI();
                resource.setUrl(link);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid URL format.");
            }
        }

        Resources savedResource = resourceRepository.save(resource);
        System.out.println("Saved resource with URL: " + savedResource.getUrl());
        return savedResource;
    }

    public List<Resources> getResources() {
        return resourceRepository.findAll();
    }
}