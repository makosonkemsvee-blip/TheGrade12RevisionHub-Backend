package com.investhoodit.RevisionHub.repository;

import com.investhoodit.RevisionHub.model.UserPaperPerformance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserPaperPerformanceRepository extends JpaRepository<UserPaperPerformance, Long> {
    List<UserPaperPerformance> findByUserId(Long userId);
    List<UserPaperPerformance> findByPaperId(Long paperId);
    boolean existsByUserIdAndPaperId(Long userId, Long paperId);
    UserPaperPerformance findByUserIdAndPaperId(Long userId, Long paperId);

    @Query("SELECT MAX(p.score) FROM UserPaperPerformance p WHERE p.user.id = :userId AND p.paper.id = :paperId")
    Optional<Integer> findHighestScore(@Param("userId") Long userId, @Param("paperId") Long paperId);

    @Query("SELECT AVG(p.score) FROM UserPaperPerformance p WHERE p.user.id = :userId AND p.paper.id = :paperId")
    Optional<Double> findAverageScore(@Param("userId") Long userId, @Param("paperId") Long paperId);
}
