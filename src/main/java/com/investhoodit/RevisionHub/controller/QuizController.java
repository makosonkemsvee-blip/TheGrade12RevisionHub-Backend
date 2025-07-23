package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.dto.QuizDTO;
import com.investhoodit.RevisionHub.dto.QuizResultDTO;
import com.investhoodit.RevisionHub.dto.QuizSubmissionDTO;
import com.investhoodit.RevisionHub.model.ApiResponse;
import com.investhoodit.RevisionHub.service.QuizService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("/quizzes")
    public ResponseEntity<ApiResponse<List<QuizDTO>>> findQuizzes() {
        try {
            List<QuizDTO> quizzes = quizService.findQuizzesForUser();
            ApiResponse<List<QuizDTO>> response = new ApiResponse<>(
                    "Quizzes retrieved successfully",
                    true,
                    quizzes
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            ApiResponse<List<QuizDTO>> response = new ApiResponse<>(
                    "No quizzes found: " + e.getMessage(),
                    false,
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        } catch (Exception e) {
            ApiResponse<List<QuizDTO>> response = new ApiResponse<>(
                    "An error occurred while retrieving quizzes: " + e.getMessage(),
                    false,
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    @GetMapping("/quizzes/{id}/questions")
    public ResponseEntity<ApiResponse<QuizDTO>> findQuizQuestions(@PathVariable Long id) {
        try {
            QuizDTO quiz = quizService.findQuizQuestions(id);
            ApiResponse<QuizDTO> response = new ApiResponse<>(
                    "Quiz questions retrieved successfully",
                    true,
                    quiz
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            ApiResponse<QuizDTO> response = new ApiResponse<>(
                    "Quiz not found: " + e.getMessage(),
                    false,
                    null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(response);
        } catch (Exception e) {
            ApiResponse<QuizDTO> response = new ApiResponse<>(
                    "An error occurred while retrieving quiz questions: " + e.getMessage(),
                    false,
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    @PostMapping("/quizzes/{id}/submit")
    public ResponseEntity<ApiResponse<QuizResultDTO>> submitQuiz(
            @PathVariable Long id,
            @RequestBody QuizSubmissionDTO submission) {
        try {
            QuizResultDTO result = quizService.submitQuiz(id, submission);
            ApiResponse<QuizResultDTO> response = new ApiResponse<>(
                    "Quiz submitted successfully",
                    true,
                    result
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            ApiResponse<QuizResultDTO> response = new ApiResponse<>(
                    "Failed to submit quiz: " + e.getMessage(),
                    false,
                    null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(response);
        } catch (Exception e) {
            ApiResponse<QuizResultDTO> response = new ApiResponse<>(
                    "An error occurred while submitting quiz: " + e.getMessage(),
                    false,
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }
}