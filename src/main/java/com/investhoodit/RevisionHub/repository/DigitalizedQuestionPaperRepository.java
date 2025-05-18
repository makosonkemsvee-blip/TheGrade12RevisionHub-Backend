package com.investhoodit.RevisionHub.repository;

import com.investhoodit.RevisionHub.model.DigitalizedQuestionPaper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DigitalizedQuestionPaperRepository extends JpaRepository<DigitalizedQuestionPaper, Long> {
}
