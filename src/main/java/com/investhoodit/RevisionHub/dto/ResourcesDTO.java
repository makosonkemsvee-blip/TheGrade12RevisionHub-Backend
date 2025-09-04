package com.investhoodit.RevisionHub.dto;

import com.investhoodit.RevisionHub.model.Subject;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ResourcesDTO {
    private Subject subjectName;
    private MultipartFile file;
    private String resourceType;
    private String title;
    private String description;
    private List<String> tags;
    private String link;
}
