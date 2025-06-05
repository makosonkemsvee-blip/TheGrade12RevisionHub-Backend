package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.model.ApiResponse;
import com.investhoodit.RevisionHub.model.DigitizedQuestionPaper;
import com.investhoodit.RevisionHub.model.Subject;
import com.investhoodit.RevisionHub.repository.SubjectRepository;
import com.investhoodit.RevisionHub.service.DigitizedQuestionPaperService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class UploadDigitizedQuestionPaperController {

    private final DigitizedQuestionPaperService digitizedQuestionPaperService;
    private final SubjectRepository subjectRepository;

    public UploadDigitizedQuestionPaperController(
            DigitizedQuestionPaperService digitizedQuestionPaperService,
            SubjectRepository subjectRepository) {
        this.digitizedQuestionPaperService = digitizedQuestionPaperService;
        this.subjectRepository = subjectRepository;
    }

    @PostMapping("/upload-digitized-paper")
    public ResponseEntity<ApiResponse> uploadDigitizedQuestionPaper(
            @RequestParam String subjectName,
            @RequestPart MultipartFile file) {
        try {
            Subject subject = subjectRepository.findBySubjectName(subjectName)
                    .orElseThrow(() -> new IllegalArgumentException("Subject not found"));
            DigitizedQuestionPaper digitizedQuestionPaper = new DigitizedQuestionPaper();
            digitizedQuestionPaper.setFileName(file.getOriginalFilename());
            digitizedQuestionPaper.setFileData(file.getBytes());
            digitizedQuestionPaper.setSubject(subject);
            digitizedQuestionPaperService.uploadDigitizedQuestionPaper(digitizedQuestionPaper);
            return ResponseEntity.ok(new ApiResponse("New " + subjectName + " digitized question paper upload successful", true, digitizedQuestionPaper));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Invalid input: " + e.getMessage(), false, null));
        } catch (IOException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Error processing file: " + e.getMessage(), false, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("An error occurred: " + e.getMessage(), false, null));
        }
    }
}