package com.investhoodit.RevisionHub.repository;

import com.investhoodit.RevisionHub.model.DigitizedQuestionPaper;
import com.investhoodit.RevisionHub.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.util.List;

public interface DigitizedQuestionPaperRepository extends JpaRepository<DigitizedQuestionPaper, Long>, Serializable {

    List<DigitizedQuestionPaper> findBySubject(Subject subject);
}