package com.investhoodit.RevisionHub.repository;

import com.investhoodit.RevisionHub.model.DigitalizedQuestionPaper;
import com.investhoodit.RevisionHub.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DigitalizedQuestionPaperRepository extends JpaRepository<DigitalizedQuestionPaper, Long> {
    Optional<DigitalizedQuestionPaper> findBySubmitterAndPaperTitle(User user, String paperTitle);
}
