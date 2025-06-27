package com.investhoodit.RevisionHub.repository;

import com.investhoodit.RevisionHub.model.PerformanceMetric;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;

public interface PerformanceMetricRepository extends JpaRepository<PerformanceMetric, Long> {
    @Query("SELECT p FROM PerformanceMetric p WHERE p.user.id = :userId " +
            "AND (:subjectName IS NULL OR p.subject.subjectName = :subjectName) " +
            "AND (:activityType IS NULL OR p.activityType = :activityType) " +
            "AND (:startDate IS NULL OR p.date >= :startDate) " +
            "AND (:endDate IS NULL OR p.date <= :endDate)")
    Page<PerformanceMetric> findByFilters(
            @Param("userId") Long userId,
            @Param("subjectName") String subjectName,
            @Param("activityType") String activityType,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );
}