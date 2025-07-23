package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.model.ApiResponse;
import com.investhoodit.RevisionHub.model.QuestionPaper;
import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.service.UploadQuestionPaperService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin")
public class UploadQuestionPaperController {

    private final UploadQuestionPaperService uploadQuestionPaperService;

    public UploadQuestionPaperController(UploadQuestionPaperService uploadQuestionPaperService) {
        this.uploadQuestionPaperService = uploadQuestionPaperService;
    }

    @PostMapping("/upload-paper")
    public ResponseEntity<ApiResponse<QuestionPaper>> uploadQuestionPaper(@RequestParam String subjectName, @RequestPart MultipartFile file) {
        try{
            QuestionPaper questionPaper = uploadQuestionPaperService.uploadQuestionPaper(subjectName,file);
            if(questionPaper == null){
                ApiResponse<QuestionPaper> response = new ApiResponse<>(
                        "Failed to upload new question paper",
                        false,
                        null
                );
                return ResponseEntity.badRequest()
                        .body(response);
            }else {
                ApiResponse<QuestionPaper> response = new ApiResponse<>(
                        "New " + subjectName + " question paper upload successful",
                        true,
                        questionPaper
                );
                return ResponseEntity.ok().body(response);
            }
        } catch (IllegalArgumentException e) {
            ApiResponse<QuestionPaper> response = new ApiResponse<>(
                    "Invalid input: " + e.getMessage(),
                    false,
                    null
            );
            return ResponseEntity.badRequest()
                    .body(response);
        }catch (Exception e) {
            ApiResponse<QuestionPaper> response = new ApiResponse<>(
                    e.getMessage(),
                    false,
                    null
            );
            return  ResponseEntity.badRequest()
                    .body(response);
        }
    }
}
