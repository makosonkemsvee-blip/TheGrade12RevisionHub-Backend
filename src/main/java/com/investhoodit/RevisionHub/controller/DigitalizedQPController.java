package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.dto.DigitalizedQPRequest;
import com.investhoodit.RevisionHub.model.ApiResponse;
import com.investhoodit.RevisionHub.model.DigitalizedQuestionPaper;
import com.investhoodit.RevisionHub.service.DigitalizedQuestionPaperService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/digitalizedQP")
public class DigitalizedQPController {
    private final DigitalizedQuestionPaperService dqpService;

    public DigitalizedQPController(DigitalizedQuestionPaperService dqpService) {
        this.dqpService = dqpService;
    }

    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<DigitalizedQuestionPaper>> submitDigitalizedQP(@RequestBody DigitalizedQPRequest digitalizedQPRequest) {
        return dqpService.submitDigitalizedQuestionPaper(digitalizedQPRequest);
    }

    @GetMapping("/getAvgScore")
    public ResponseEntity<ApiResponse<Double>> getAverageScorePerSubject(@RequestBody DigitalizedQPRequest digitalizedQPRequest) {
        return dqpService.getAverageScorePerSubject(digitalizedQPRequest);
    }

}
