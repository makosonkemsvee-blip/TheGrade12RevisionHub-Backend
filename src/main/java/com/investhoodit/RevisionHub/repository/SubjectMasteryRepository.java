package com.investhoodit.RevisionHub.repository;

import com.investhoodit.RevisionHub.model.Subject;
import com.investhoodit.RevisionHub.model.SubjectMastery;
import com.investhoodit.RevisionHub.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectMasteryRepository extends JpaRepository<SubjectMastery, Long> {
    //List<SubjectMastery> findByUserId(String userId);
    //void deleteByUserId(String userId);

    List<SubjectMastery> findByUser(User user);
    Optional<SubjectMastery> findByUserAndSubject(User user, Subject subject);
    void deleteByUser(User user);
}