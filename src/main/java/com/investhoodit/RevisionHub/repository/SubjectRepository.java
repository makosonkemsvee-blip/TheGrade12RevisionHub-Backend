package com.investhoodit.RevisionHub.repository;

import com.investhoodit.RevisionHub.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, String> {
    Optional<Subject> findBySubjectName(String subjectName);

    @Query("SELECT s FROM Subject s WHERE s.subjectName LIKE %:keyword%")
    Subject getSubject(@Param("keyword") String keyword);

}
