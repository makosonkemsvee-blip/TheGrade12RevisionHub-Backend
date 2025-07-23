package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.dto.PerformanceRequest;
import com.investhoodit.RevisionHub.model.DigitizedQuestionPaper;
import com.investhoodit.RevisionHub.model.PerformanceMetric;
import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.model.UserPaperPerformance;
import com.investhoodit.RevisionHub.repository.DigitizedQuestionPaperRepository;
import com.investhoodit.RevisionHub.repository.PerformanceMetricRepository;
import com.investhoodit.RevisionHub.repository.UserPaperPerformanceRepository;
import com.investhoodit.RevisionHub.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserPaperPerformanceService {
    private final PerformanceMetricRepository performanceRepository;
    private final UserRepository userRepository;
    private final DigitizedQuestionPaperRepository paperRepository;

    public UserPaperPerformanceService(PerformanceMetricRepository performanceRepository, UserRepository userRepository, DigitizedQuestionPaperRepository paperRepository) {
        this.performanceRepository = performanceRepository;
        this.userRepository = userRepository;
        this.paperRepository = paperRepository;
    }

    public PerformanceMetric recordAttempt(PerformanceRequest request) {
        User user = userRepository.findByEmail(
                        SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        DigitizedQuestionPaper paper = paperRepository.findById(request.getPaperId())
                .orElseThrow(() -> new EntityNotFoundException("Paper not found"));
        PerformanceMetric performance;
        DecimalFormat df = new DecimalFormat("#.##");
        Double average;

        if (performanceRepository.existsByUserIdAndActivityId(user.getId(), request.getPaperId())) {
            performance = performanceRepository.findByUserIdAndActivityId(user.getId(), request.getPaperId());
            performance.setScore(performance.getScore() > request.getScore()?performance.getScore():request.getScore());
            performance.setDate(LocalDate.now());
        } else {
            performance = new PerformanceMetric();
            performance.setUser(user);
            performance.setSubject(paper.getSubject());
            performance.setScore(request.getScore());
            performance.setActivityName(paper.getFileName());
            performance.setActivityType("Digitized QP");
            performance.setActivityId(paper.getId());
            performance.setDate(LocalDate.now());

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
