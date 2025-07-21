package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.dto.PerformanceRequest;
import com.investhoodit.RevisionHub.model.DigitizedQuestionPaper;
import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.model.UserPaperPerformance;
import com.investhoodit.RevisionHub.repository.DigitizedQuestionPaperRepository;
import com.investhoodit.RevisionHub.repository.UserPaperPerformanceRepository;
import com.investhoodit.RevisionHub.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PerformanceService {
    private final UserPaperPerformanceRepository performanceRepository;
    private final UserRepository userRepository;
    private final DigitizedQuestionPaperRepository paperRepository;

    public PerformanceService(UserPaperPerformanceRepository performanceRepository,
                              UserRepository userRepository,
                              DigitizedQuestionPaperRepository paperRepository) {
        this.performanceRepository = performanceRepository;
        this.userRepository = userRepository;
        this.paperRepository = paperRepository;
    }

    public UserPaperPerformance recordAttempt(PerformanceRequest request) {
        User user = userRepository.findByEmail(
                SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        DigitizedQuestionPaper paper = paperRepository.findById(request.getPaperId())
                .orElseThrow(() -> new EntityNotFoundException("Paper not found"));
        UserPaperPerformance performance;
        DecimalFormat df = new DecimalFormat("#.##");
        Double average;

        if (performanceRepository.existsByUserIdAndPaperId(user.getId(), request.getPaperId())) {
            performance = performanceRepository.findByUserIdAndPaperId(user.getId(), request.getPaperId());
            performance.setScore(request.getScore());
            performance.setHighestScore(performance.getHighestScore() > request.getScore()?performance.getHighestScore():request.getScore());
            average = (performance.getAverageScore() * performance.getAttempts() + request.getScore()) / (performance.getAttempts() + 1);
            performance.setAverageScore(Double.parseDouble(df.format(average)));
            performance.setAttempts(performance.getAttempts() + 1);
        } else {
            performance = new UserPaperPerformance();
            performance.setUser(user);
            performance.setPaper(paper);
            performance.setScore(request.getScore());
            performance.setMaxScore(100);
            performance.setAttemptDate(LocalDateTime.now());

            performance.setAttempts(1);
            performance.setHighestScore(request.getScore());
            performance.setAverageScore(0);
        }
        return performanceRepository.save(performance);
    }

    public List<UserPaperPerformance> getUserPerformance() {
        return performanceRepository.findByUserId(findByToken().getId());
    }

    public User findByToken() {
        return userRepository.findByEmail(
                SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

}