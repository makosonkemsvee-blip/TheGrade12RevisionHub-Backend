package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.dto.EmailRequestDTO;
import com.investhoodit.RevisionHub.dto.QuizDTO;
import com.investhoodit.RevisionHub.dto.ScoreResponseDTO;
import com.investhoodit.RevisionHub.dto.UserAnswerDTO;
import com.investhoodit.RevisionHub.model.QuestionPaper;
import com.investhoodit.RevisionHub.model.Quiz;
import com.investhoodit.RevisionHub.model.Subject;
import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.repository.QuizRepository;
import com.investhoodit.RevisionHub.repository.SubjectRepository;
import com.investhoodit.RevisionHub.service.EmailService;
import com.investhoodit.RevisionHub.service.QuestionPaperService;
import com.investhoodit.RevisionHub.service.QuizService;
import com.investhoodit.RevisionHub.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;
    private final QuestionPaperService questionPaperService;
    private final SubjectRepository subjectRepository;
    private final EmailService emailService;
    private final QuizRepository quizRepository;
    private final QuizService quizService;

    public AdminController(UserService userService,
                           QuestionPaperService questionPaperService,
                           SubjectRepository subjectRepository, EmailService emailService, QuizRepository quizRepository, QuizService quizService) {
        this.userService = userService;
        this.questionPaperService = questionPaperService;
        this.subjectRepository = subjectRepository;
        this.emailService = emailService;
        this.quizRepository = quizRepository;
        this.quizService = quizService;
    }

    @GetMapping("/students")
    public List<User> getStudents() {
        return userService.getAllStudents();
    }

    @DeleteMapping("/students/{email}")
    public ResponseEntity<String> deleteStudent(@PathVariable String email) {
        boolean deleted = userService.removeUser(email);
        return deleted ?
                ResponseEntity.ok("Student deleted") :
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");
    }

    @GetMapping("/subjects")
    public List<Subject> getSubjects() {
        return subjectRepository.findAll();
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadPaper(@RequestParam String subjectName, @RequestPart MultipartFile file) throws IOException {
        Subject subject = subjectRepository.findById(subjectName).orElse(null);
        if (subject == null) return ResponseEntity.badRequest().body("Invalid subject");

        QuestionPaper questionPaper = new QuestionPaper();
        questionPaper.setSubject(subject);
        questionPaper.setFileName(file.getOriginalFilename());
        questionPaper.setFileData(file.getBytes());

        questionPaperService.uploadQuestionPaper(questionPaper);
        return ResponseEntity.ok("Uploaded successfully");
    }

    @GetMapping("/search")
    public ResponseEntity<User> searchUser(@RequestParam String email) {
        User user = userService.searchUser(email);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        long studentCount = userService.countStudents();
        Map<String, Object> stats = new HashMap<>();
        stats.put("studentCount", studentCount);
        return stats;
    }

    @DeleteMapping("/quizzes/{id}")
    public ResponseEntity<String> deleteQuiz(@PathVariable Long id) {
        try {
            quizService.deleteQuiz(id);
            return ResponseEntity.ok("Quiz deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid quiz ID: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error deleting quiz: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete quiz: " + e.getMessage());
        }
    }

    @PostMapping("/quizzes")
    public ResponseEntity<String> createQuiz(@Valid @RequestBody QuizDTO.CreateQuizDTO quizDTO, BindingResult result) {
        if (result.hasErrors()) {
            String errors = result.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest().body("Validation failed: " + errors);
        }
        try {
            Quiz quiz = quizService.createQuiz(quizDTO);
            return ResponseEntity.ok("Quiz created successfully for : " + quiz.getSubject());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid data: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error creating quiz: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create quiz: " + e.getMessage());
        }
    }

    @GetMapping("/allQuizzes")
    public ResponseEntity<List<QuizDTO>> getAllQuizzes() {
        List<Quiz> quizzes = quizRepository.findAll();
        List<QuizDTO> quizDTOs = quizzes.stream().map(quiz -> {
            QuizDTO dto = new QuizDTO();
            dto.setId(quiz.getId());
            dto.setTitle(quiz.getTitle());
            dto.setDescription(quiz.getDescription());
            dto.setSubject(quiz.getSubject().getSubjectName());
            List<QuizDTO.QuestionDTO> questionDTOs = quiz.getQuestions().stream().map(question -> {
                QuizDTO.QuestionDTO questionDTO = new QuizDTO.QuestionDTO();
                questionDTO.setQuestionText(question.getQuestionText());
                questionDTO.setOptions(question.getOptions());
                questionDTO.setCorrectAnswer(question.getCorrectAnswer());
                return questionDTO;
            }).collect(Collectors.toList());
            dto.setQuestions(questionDTOs);
            return dto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(quizDTOs);
    }

    @PostMapping("/quizzes/{quizId}/score")
    public ResponseEntity<ScoreResponseDTO> calculateQuizScore(
            @PathVariable Long quizId,
            @RequestBody UserAnswerDTO userAnswerDTO) {
        try {
            if (userAnswerDTO == null || userAnswerDTO.getAnswers() == null || userAnswerDTO.getAnswers().isEmpty()) {
                return ResponseEntity.badRequest().body(new ScoreResponseDTO("User answers cannot be null or empty"));
            }

            // Convert UserAnswerDTO to Map<Long, String>
            Map<Long, String> userAnswers = userAnswerDTO.getAnswers().stream()
                    .collect(Collectors.toMap(
                            UserAnswerDTO.Answer::getQuestionId,
                            UserAnswerDTO.Answer::getAnswer
                    ));

            double score = questionPaperService.calculateScore(quizId, userAnswers);
            return ResponseEntity.ok(new ScoreResponseDTO(score));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ScoreResponseDTO(e.getMessage()));
        } catch (HttpMessageNotReadableException e) {
            return ResponseEntity.badRequest().body(new ScoreResponseDTO("Invalid JSON format: Expected a list of {questionId: Long, answer: String}"));
        }
    }

    @PostMapping("/email/send")
    public ResponseEntity<String> sendEmailToStudent(@Valid @RequestBody EmailRequestDTO emailRequest) {
        try {
            emailService.sendEmail(emailRequest.getTo(), emailRequest.getSubject(), emailRequest.getBody());
            return ResponseEntity.ok("Email sent successfully to " + emailRequest.getTo());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send email: " + e.getMessage());
        }
    }

    @GetMapping("/quizzes/count")
    public Map<String, Object> getQuizCount() {
        long quizCount = questionPaperService.countQuizzes();
        Map<String, Object> stats = new HashMap<>();
        stats.put("quizCount", quizCount);
        return stats;
    }

}