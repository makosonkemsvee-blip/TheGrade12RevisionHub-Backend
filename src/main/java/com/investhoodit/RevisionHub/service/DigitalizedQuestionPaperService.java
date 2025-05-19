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

import java.util.Optional;

@Service
public class DigitalizedQuestionPaperService {
    private final DigitalizedQuestionPaperRepository digitalizedQuestionPaperRepository;
    private final UserRepository userRepository;

    public DigitalizedQuestionPaperService(DigitalizedQuestionPaperRepository digitalizedQuestionPaperRepository, UserRepository userRepository) {
        this.digitalizedQuestionPaperRepository = digitalizedQuestionPaperRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<ApiResponse> save(DigitalizedQPRequest digitalizedQPRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<DigitalizedQuestionPaper> existingSubmissionOpt = digitalizedQuestionPaperRepository.findBySubmitterAndPaperTitle(user,digitalizedQPRequest.getPaperTitle());

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

        digitalizedQuestionPaperRepository.save(dqp);

        return ResponseEntity.status(201)
                .body(new ApiResponse("Submitted successfully",true,dqp));
    }
}
