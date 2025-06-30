package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.model.ApiResponse;
import com.investhoodit.RevisionHub.model.QuestionPaper;
import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.service.QuestionPaperService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class FindQuestionPaperController {

    private final QuestionPaperService questionPaperService;

    public FindQuestionPaperController(QuestionPaperService questionPaperService) {
        this.questionPaperService = questionPaperService;
    }

    @GetMapping("/question-papers")
    public ResponseEntity<ApiResponse<List<QuestionPaper>>> findQuestionPaperBySubject(@RequestParam String subjectName) {
        try {
            List<QuestionPaper> papers = questionPaperService.findBySubjectName(subjectName);
            ApiResponse<List<QuestionPaper>> response = new ApiResponse<>(
                    "Question papers retrieved successfully",
                    true,
                    papers
            );
                return ResponseEntity.ok(response);

        } catch (Exception e) {
            //log.error("Error removing subject: {}", subjectName, e);
            ApiResponse<List<QuestionPaper>> response = new ApiResponse<>(
                    "An error occurred while retrieving the question papers: " + e.getMessage(),
                    false,
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    @GetMapping("/question-papers/{id}/view")
    public ResponseEntity<byte[]> viewQuestionPaper(@PathVariable Long id) {
        try {
            QuestionPaper paper = questionPaperService.findById(id);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", paper.getFileName());
            return new ResponseEntity<>(paper.getFileData(), headers, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping("/question-papers/{id}/download")
    public ResponseEntity<byte[]> downloadQuestionPaper(@PathVariable Long id) {
        try {
            QuestionPaper paper = questionPaperService.findById(id);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", paper.getFileName());
            return new ResponseEntity<>(paper.getFileData(), headers, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}
