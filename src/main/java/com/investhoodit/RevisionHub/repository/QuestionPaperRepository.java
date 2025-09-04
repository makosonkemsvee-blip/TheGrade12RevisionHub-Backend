package com.investhoodit.RevisionHub.repository;

import com.investhoodit.RevisionHub.model.QuestionPaper;
import com.investhoodit.RevisionHub.model.Subject;
import com.investhoodit.RevisionHub.model.UserSubjects;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.util.List;

public interface QuestionPaperRepository extends JpaRepository<QuestionPaper, Long>, Serializable {

    List<QuestionPaper> findBySubject(Subject subject);
    boolean existsByFileName(String fileName);
}
