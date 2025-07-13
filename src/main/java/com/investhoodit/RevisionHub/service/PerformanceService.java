package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.model.DigitizedQuestionPaper;
import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.model.UserPaperPerformance;
import com.investhoodit.RevisionHub.repository.DigitizedQuestionPaperRepository;
import com.investhoodit.RevisionHub.repository.UserPaperPerformanceRepository;
import com.investhoodit.RevisionHub.repository.UserRepository;
import com.investhoodit.RevisionHub.util.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PerformanceService {
    private final UserPaperPerformanceRepository performanceRepository;
    private final UserRepository userRepository;
    private final DigitizedQuestionPaperRepository paperRepository;
    private final JwtUtil jwtUtil;

    public PerformanceService(UserPaperPerformanceRepository performanceRepository,
                              UserRepository userRepository,
                              DigitizedQuestionPaperRepository paperRepository, JwtUtil jwtUtil) {
        this.performanceRepository = performanceRepository;
        this.userRepository = userRepository;
        this.paperRepository = paperRepository;
        this.jwtUtil = jwtUtil;
    }

    public UserPaperPerformance recordAttempt(Long userId, Long paperId, int score, int maxScore) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        DigitizedQuestionPaper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new EntityNotFoundException("Paper not found"));

        UserPaperPerformance performance = new UserPaperPerformance();
        performance.setUser(user);
        performance.setPaper(paper);
        performance.setScore(score);
        performance.setMaxScore(maxScore);
        performance.setAttemptDate(LocalDateTime.now());

        // Calculate aggregates
        List<UserPaperPerformance> allAttempts = performanceRepository.findByUserIdAndPaperId(userId, paperId);
        performance.setAttempts(allAttempts.size() + 1);

        int highest = performanceRepository.findHighestScore(userId, paperId)
                .orElse(0);
        performance.setHighestScore(Math.max(highest, score));

        double average = performanceRepository.findAverageScore(userId, paperId)
                .orElse(0.0);
        performance.setAverageScore((average * allAttempts.size() + score) / (allAttempts.size() + 1));

        return performanceRepository.save(performance);
    }

    public List<UserPaperPerformance> getUserPerformance(Long userId) {
        return performanceRepository.findByUserId(userId);
    }

    public User findByToken() {
        return userRepository.findByEmail(
                SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
}