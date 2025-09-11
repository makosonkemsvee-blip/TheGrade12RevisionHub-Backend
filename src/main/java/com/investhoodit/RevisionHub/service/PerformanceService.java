package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.dto.PerformanceDTO;
import com.investhoodit.RevisionHub.model.PerformanceMetric;
import com.investhoodit.RevisionHub.repository.PerformanceMetricRepository;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class PerformanceService {
    private final PerformanceMetricRepository performanceMetricRepository;
    private final NotificationService notificationService;

    public PerformanceService(PerformanceMetricRepository performanceMetricRepository, NotificationService notificationService) {
        this.performanceMetricRepository = performanceMetricRepository;
        this.notificationService = notificationService;
    }

    @Cacheable(value = "performanceMetrics", key = "#userId + '-' + #page + '-' + #size + '-' + #subjectName + '-' + #activityType + '-' + #startDate + '-' + #endDate")
    public Page<PerformanceDTO> getPerformanceByFilters(
            Long userId,
            String subjectName,
            String activityType,
            LocalDate startDate,
            LocalDate endDate,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PerformanceMetric> performancePage = performanceMetricRepository.findByFilters(
                userId,
                subjectName,
                activityType,
                startDate,
                endDate,
                pageable
        );

        return performancePage.map(p -> new PerformanceDTO(
                p.getId(),
                p.getUser().getId(),
                p.getSubject().getSubjectName(),
                p.getActivityType(),
                p.getActivityName(),
                p.getDate(),
                p.getScore()
        ));
    }
    @Transactional
    public PerformanceMetric savePerformanceMetric(PerformanceMetric performanceMetric) {
        PerformanceMetric saved = performanceMetricRepository.save(performanceMetric);

        Long userId = saved.getUser().getId();
        String subjectName = saved.getSubject().getSubjectName();
        String activityType = saved.getActivityType();
        String activityName = saved.getActivityName();
        int score = (int) saved.getScore();

        // Notification message with activityType + activityName included
        String message = "📌 " + activityType + " (" + activityName + ") in " + subjectName +
                " has been recorded with score: " + score;

        // Optional: Adjust based on score thresholds
        if (score >= 90) {
            message = "🎉 Great job! You scored a percentage of " + score + "% in " + activityType +
                    " (" + activityName + ", " + subjectName + ")";
        } else if (score < 50) {
            message = "⚠️ You scored a percentage of " + score + "% in " + activityType +
                    " (" + activityName + ", " + subjectName + "). Keep practicing!";
        }

        //prevents duplications
        notificationService.createIfNotExists(userId, message, "PERFORMANCE");

        return saved;
    }

}