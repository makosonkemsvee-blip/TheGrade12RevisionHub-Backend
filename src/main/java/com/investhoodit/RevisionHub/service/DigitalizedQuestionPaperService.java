package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.dto.DigitalizedQPRequest;
import com.investhoodit.RevisionHub.model.ApiResponse;
import com.investhoodit.RevisionHub.model.DigitalizedQuestionPaper;
import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.repository.DigitalizedQuestionPaperRepository;
import com.investhoodit.RevisionHub.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DigitalizedQuestionPaperService {
    private final DigitalizedQuestionPaperRepository dqpRepository;
    private final UserRepository userRepository;

    public DigitalizedQuestionPaperService(DigitalizedQuestionPaperRepository digitalizedQuestionPaperRepository, UserRepository userRepository) {
        this.dqpRepository = digitalizedQuestionPaperRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<ApiResponse<DigitalizedQuestionPaper>> submitDigitalizedQuestionPaper(DigitalizedQPRequest digitalizedQPRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<DigitalizedQuestionPaper> existingSubmissionOpt = dqpRepository.findBySubmitterAndPaperTitle(user,digitalizedQPRequest.getPaperTitle());

        DigitalizedQuestionPaper dqp;

        if (existingSubmissionOpt.isPresent()) {
            dqp = existingSubmissionOpt.get();
            if(dqp.getScore() < digitalizedQPRequest.getScore()){
                dqp.setScore(digitalizedQPRequest.getScore());
            }
        }else{
            dqp = new DigitalizedQuestionPaper();
            dqp.setPaperTitle(digitalizedQPRequest.getPaperTitle());
            dqp.setSubject(digitalizedQPRequest.getSubject());
            dqp.setYear(digitalizedQPRequest.getYear());
            dqp.setScore(digitalizedQPRequest.getScore());
            dqp.setSubmitter(user);
        }

        dqpRepository.save(dqp);

        ApiResponse<DigitalizedQuestionPaper> response = new ApiResponse<>(
                true,
                "Submitted successfully",
                dqp
        );

        return ResponseEntity.status(201)
                .body(response);
    }

    public ResponseEntity<ApiResponse<Double>> getAverageScorePerSubject(DigitalizedQPRequest digitalizedQPRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<DigitalizedQuestionPaper> allDqp = dqpRepository.findAll();
        String subject = "";
        for (DigitalizedQuestionPaper dqp : allDqp) {
            if(dqp.getSubject().equals(digitalizedQPRequest.getSubject())){
                subject = dqp.getSubject();
            }
        }

        if(subject.isEmpty()){
            throw new RuntimeException("Subject not found");
        }

        List<DigitalizedQuestionPaper> dqpList = dqpRepository.findAllBySubjectAndSubmitter(digitalizedQPRequest.getSubject(),user);
        double averageScore;
        int totalScore = 0;

        if (!dqpList.isEmpty()) {
            for(DigitalizedQuestionPaper dqp : dqpList ){
                totalScore += dqp.getScore();
            }
            averageScore = (double) totalScore / dqpList.size();
        }else {
            throw new RuntimeException("You have not submitted a question paper yet");
        }

        ApiResponse<Double> response = new ApiResponse<>(
                true,
                "Average successfully retrieved",
                averageScore
        );
        return ResponseEntity.status(200).body(response);
    }
}
