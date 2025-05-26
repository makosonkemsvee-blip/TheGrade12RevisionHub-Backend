package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.model.ApiResponse;
import com.investhoodit.RevisionHub.model.QuestionPaper;
import com.investhoodit.RevisionHub.service.QuestionPaperService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class FindQuestionPaperController {

    private final QuestionPaperService questionPaperService;

    public FindQuestionPaperController(QuestionPaperService questionPaperService) {
        this.questionPaperService = questionPaperService;
    }

    @GetMapping("/question-papers")
    public ResponseEntity<ApiResponse<List<QuestionPaper>>> findQuestionPaperBySubject() {
//        if (subjectName == null || subjectName.trim().isEmpty()) {
//            return ResponseEntity.badRequest()
//                    .body(new ApiResponse("Question papers name cannot be empty", false, null));
//        }
        try {
            List<QuestionPaper> papers = questionPaperService.findBySubjectName();
            ApiResponse<List<QuestionPaper>> response = new ApiResponse<>(
                    true,
                    "Question papers retrieved successfully",
                    papers
            );
                return ResponseEntity.ok().body(response);

        } catch (Exception e) {
            //log.error("Error removing subject: {}", subjectName, e);
            ApiResponse<List<QuestionPaper>> response = new ApiResponse<>(
                    false,
                    "An error occurred while retrieving the question papers: " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }
}
