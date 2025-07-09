package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.model.ApiResponse;
import com.investhoodit.RevisionHub.model.QuestionPaper;
import com.investhoodit.RevisionHub.model.Quiz;
import com.investhoodit.RevisionHub.service.QuizService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("/quizzes")
    public ResponseEntity<ApiResponse<List<Quiz>>> findQuizzes() {
        try {
            List<Quiz> quizzes = quizService.findQuizzesForUser();
            ApiResponse<List<Quiz>> response = new ApiResponse<>(
                    "Quizzes retrieved successfully",
                    true,
                    quizzes
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            ApiResponse<List<Quiz>> response = new ApiResponse<>(
                    "No quizzes found: " + e.getMessage(),
                    false,
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        } catch (Exception e) {
            ApiResponse<List<Quiz>> response = new ApiResponse<>(
                    "An error occurred while retrieving quizzes: " + e.getMessage(),
                    false,
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }
}