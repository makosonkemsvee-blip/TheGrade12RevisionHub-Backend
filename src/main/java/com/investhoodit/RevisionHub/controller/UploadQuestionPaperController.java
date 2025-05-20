package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.model.ApiResponse;
import com.investhoodit.RevisionHub.model.QuestionPaper;
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
    public ResponseEntity<ApiResponse> uploadQuestionPaper(@RequestParam String subjectName, @RequestPart MultipartFile file) {
        try{
            QuestionPaper questionPaper = uploadQuestionPaperService.uploadQuestionPaper(subjectName,file);
            if(questionPaper == null){
                return ResponseEntity.badRequest()
                        .body(new ApiResponse("Failed to upload new question paper", false, null));
            }else {
                return ResponseEntity.ok(new ApiResponse("New " + subjectName + " question paper upload successful", true, questionPaper));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Invalid input: " + e.getMessage(), false, null));
        }catch (Exception e) {
            return  ResponseEntity.badRequest()
                    .body(new ApiResponse(e.getMessage(),false,null));
        }
    }
}
