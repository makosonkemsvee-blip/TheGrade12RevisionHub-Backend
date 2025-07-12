package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.model.ApiResponse;
import com.investhoodit.RevisionHub.model.DigitizedQuestionPaper;
import com.investhoodit.RevisionHub.service.DigitizedQuestionPaperService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class FindDigitizedQuestionPaperController {

    private final DigitizedQuestionPaperService digitizedQuestionPaperService;

    public FindDigitizedQuestionPaperController(DigitizedQuestionPaperService digitizedQuestionPaperService) {
        this.digitizedQuestionPaperService = digitizedQuestionPaperService;
    }

    @GetMapping("/digitized")
    public ResponseEntity<Map<String, Object>> findDigitizedQuestionPapers(
            @RequestParam(required = false) String subjectName) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<DigitizedQuestionPaper> papers = subjectName != null
                    ? digitizedQuestionPaperService.findBySubjectName(subjectName)
                    : digitizedQuestionPaperService.findAllPapersForUser();
            response.put("success", true);
            response.put("data", papers);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/digitized/{id}")
    public ResponseEntity<Map<String, Object>> getDigitizedPaperById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            DigitizedQuestionPaper paper = digitizedQuestionPaperService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Paper not found"));

            response.put("success", true);
            response.put("data", paper);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

}