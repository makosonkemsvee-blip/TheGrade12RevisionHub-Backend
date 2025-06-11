package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.model.Quiz;
import com.investhoodit.RevisionHub.model.Subject;
import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.model.UserSubjects;
import com.investhoodit.RevisionHub.repository.QuizRepository;
import com.investhoodit.RevisionHub.repository.UserRepository;
import com.investhoodit.RevisionHub.repository.UserSubjectsRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final UserSubjectsRepository userSubjectsRepository;
    private final UserRepository userRepository;

    public QuizService(QuizRepository quizRepository, UserSubjectsRepository userSubjectsRepository, UserRepository userRepository) {
        this.quizRepository = quizRepository;
        this.userSubjectsRepository = userSubjectsRepository;
        this.userRepository = userRepository;
    }

    public List<Quiz> findQuizzesForUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<UserSubjects> userSubjects = userSubjectsRepository.findByUser(user);
        List<Quiz> allQuizzes = new ArrayList<>();
        for (UserSubjects userSubject : userSubjects) {
            allQuizzes.addAll(quizRepository.findBySubject(userSubject.getSubject()));
        }
        return allQuizzes;
    }
}