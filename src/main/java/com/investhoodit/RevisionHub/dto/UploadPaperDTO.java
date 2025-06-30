package com.investhoodit.RevisionHub.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UploadPaperDTO {
    private String subjectName;
    private MultipartFile file;
}
