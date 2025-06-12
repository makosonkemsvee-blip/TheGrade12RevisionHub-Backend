package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.model.ApiResponse;
import com.investhoodit.RevisionHub.model.QuestionPaper;
import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.service.UploadQuestionPaperService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
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
                        false,
                        "Failed to upload new question paper",
                        null
                );
                return ResponseEntity.badRequest()
                        .body(response);
            }else {
                ApiResponse<QuestionPaper> response = new ApiResponse<>(
                        true,
                        "New " + subjectName + " question paper upload successful",
                        questionPaper
                );
                return ResponseEntity.ok().body(response);
            }
        } catch (IllegalArgumentException e) {
            ApiResponse<QuestionPaper> response = new ApiResponse<>(
                    false,
                    "Invalid input: " + e.getMessage(),
                    null
            );
            return ResponseEntity.badRequest()
                    .body(response);
        }catch (Exception e) {
            ApiResponse<QuestionPaper> response = new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null
            );
            return  ResponseEntity.badRequest()
                    .body(response);
        }
    }
}
