package com.investhoodit.RevisionHub.repository;

import com.investhoodit.RevisionHub.model.Subject;
import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.model.UserSubjects;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserSubjectsRepository extends JpaRepository<UserSubjects, Long> {
    List<UserSubjects> findByUser(User user);
    boolean existsByUserAndSubject(User user, Subject subject);

    int countByUser(User user);

    List<UserSubjects> findBySubjectAndUser(Subject subject, User user);

    List<UserSubjects> findBySubject(Subject subject);

    UserSubjects findTopByUserOrderByCreatedAtDesc(User user);

    Optional<UserSubjects> findFirstBySubject(Subject subject);

}
