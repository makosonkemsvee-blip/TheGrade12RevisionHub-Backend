package com.investhoodit.RevisionHub.repository;

import com.investhoodit.RevisionHub.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    boolean existsByUserEmail(String email);
}
