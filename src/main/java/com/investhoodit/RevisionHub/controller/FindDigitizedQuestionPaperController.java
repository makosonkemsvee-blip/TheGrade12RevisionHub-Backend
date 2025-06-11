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
@RequestMapping("/user")
public class FindDigitizedQuestionPaperController {

    private final DigitizedQuestionPaperService digitizedQuestionPaperService;

    public FindDigitizedQuestionPaperController(DigitizedQuestionPaperService digitizedQuestionPaperService) {
        this.digitizedQuestionPaperService = digitizedQuestionPaperService;
    }

    @GetMapping
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

    /*/@GetMapping("/digitized-question-papers/{id}/view")
    public ResponseEntity<byte[]> viewDigitizedQuestionPaper(@PathVariable Long id) {
        try {
            DigitizedQuestionPaper paper = digitizedQuestionPaperService.getPaperById(id);
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

    @GetMapping("/digitized-question-papers/{id}/download")
    public ResponseEntity<byte[]> downloadDigitizedQuestionPaper(@PathVariable Long id) {
        try {
            DigitizedQuestionPaper paper = digitizedQuestionPaperService.getPaperById(id);
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
    }*/
}