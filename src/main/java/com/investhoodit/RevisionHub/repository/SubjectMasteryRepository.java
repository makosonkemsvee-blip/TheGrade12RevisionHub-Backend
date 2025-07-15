package com.investhoodit.RevisionHub.repository;

import com.investhoodit.RevisionHub.model.SubjectMastery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectMasteryRepository extends JpaRepository<SubjectMastery, Long> {
    List<SubjectMastery> findByUserId(String userId);
    void deleteByUserId(String userId);
}