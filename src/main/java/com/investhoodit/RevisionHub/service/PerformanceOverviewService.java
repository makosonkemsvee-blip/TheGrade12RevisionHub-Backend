package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.model.CoursePerformance;
import com.investhoodit.RevisionHub.model.SubjectMastery;
import com.investhoodit.RevisionHub.repository.SubjectMasteryRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PerformanceOverviewService {

    private final SubjectMasteryRepository subjectMasteryRepository;

    public PerformanceOverviewService(SubjectMasteryRepository subjectMasteryRepository) {
        this.subjectMasteryRepository = subjectMasteryRepository;
    }

    public List<CoursePerformance> getPerformanceOverview() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        List<SubjectMastery> masteryRecords = subjectMasteryRepository.findByUserId(userEmail);

        return masteryRecords.stream()
                .map(record -> new CoursePerformance(record.getSubjectName(), record.getProgress()))
                .collect(Collectors.toList());
    }
}