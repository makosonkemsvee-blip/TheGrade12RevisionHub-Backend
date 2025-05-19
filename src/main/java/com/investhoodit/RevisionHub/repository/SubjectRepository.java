package com.investhoodit.RevisionHub.repository;

import com.investhoodit.RevisionHub.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, String> {
    Optional<Subject> findBySubjectName(String subjectName);
}
