package com.investhoodit.RevisionHub.repository;

import com.investhoodit.RevisionHub.model.Quiz;
import com.investhoodit.RevisionHub.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long>, Serializable {
    List<Quiz> findBySubject(Subject subject);
}