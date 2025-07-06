package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.dto.PerformanceDTO;
import com.investhoodit.RevisionHub.model.PerformanceMetric;
import com.investhoodit.RevisionHub.repository.PerformanceMetricRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class PerformanceService {
    private final PerformanceMetricRepository performanceMetricRepository;

    public PerformanceService(PerformanceMetricRepository performanceMetricRepository) {
        this.performanceMetricRepository = performanceMetricRepository;
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
                p.getScore(),
                p.getMaxScore(),
                p.getTimeSpent(),
                p.getDifficulty(),
                p.getStatus(),
                p.getComments()
        ));
    }
}