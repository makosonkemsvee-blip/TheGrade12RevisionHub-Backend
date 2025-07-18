/* src/main/java/com/investhoodit/RevisionHub/service/ResourcesService.java */
package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.model.Resources;
import com.investhoodit.RevisionHub.model.Subject;
import com.investhoodit.RevisionHub.repository.ResourceRepository;
import com.investhoodit.RevisionHub.repository.SubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ResourcesService {
    private final ResourceRepository resourceRepository;
    private final SubjectRepository subjectRepository;
    private final String uploadDir = "uploads/";

    public ResourcesService(ResourceRepository resourceRepository, SubjectRepository subjectRepository) {
        this.resourceRepository = resourceRepository;
        this.subjectRepository = subjectRepository;
        // Create uploads directory if it doesn't exist
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public Resources uploadResource(String subjectName, MultipartFile file) throws IOException {
        if (subjectName == null || subjectName.isEmpty() || file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Subject and file are required.");
        }

        Subject subject = subjectRepository.findById(subjectName)
                .orElseGet(() -> {
                    Subject newSubject = new Subject(subjectName);
                    return subjectRepository.save(newSubject);
                });

        String[] allowedTypes = {"application/pdf", "image/png", "image/jpeg", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"};
        boolean isValidType = false;
        for (String type : allowedTypes) {
            if (type.equals(file.getContentType())) {
                isValidType = true;
                break;
            }
        }
        if (!isValidType) {
            throw new IllegalArgumentException("Only PDF, PNG, JPEG, and DOCX files are allowed.");
        }

        String fileName = System.currentTimeMillis() + "-" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + fileName);
        Files.write(filePath, file.getBytes());

        Resources resource = new Resources();
        resource.setTitle(file.getOriginalFilename());
        resource.setUrl("/uploads/" + fileName);
        resource.setDescription("Resource for " + subjectName);
        resource.setSubject(subject);
        resource.setFileName(file.getOriginalFilename());
        resource.setFileType(file.getContentType());
        resource.setUploadedAt(LocalDateTime.now());

        return resourceRepository.save(resource);
    }

    public List<Resources> getResources() {
        return resourceRepository.findAll();
    }
}