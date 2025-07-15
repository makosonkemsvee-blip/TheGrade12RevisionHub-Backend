package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.dto.QuizDTO;
import com.investhoodit.RevisionHub.dto.QuizResultDTO;
import com.investhoodit.RevisionHub.dto.QuizSubmissionDTO;
import com.investhoodit.RevisionHub.model.*;
import com.investhoodit.RevisionHub.repository.QuizRepository;
import com.investhoodit.RevisionHub.repository.UserRepository;
import com.investhoodit.RevisionHub.repository.UserSubjectsRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<QuizDTO> findQuizzesForUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("Fetching quizzes for user: " + email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        System.out.println("Found user: ID=" + user.getId());

        List<UserSubjects> userSubjects = userSubjectsRepository.findByUser(user);
        System.out.println("User subjects count: " + userSubjects.size());
        userSubjects.forEach(us -> System.out.println("Subject: " + us.getSubject().getSubjectName()));

        List<Quiz> allQuizzes = new ArrayList<>();
        for (UserSubjects userSubject : userSubjects) {
            Subject subject = userSubject.getSubject();
            List<Quiz> subjectQuizzes = quizRepository.findBySubject(subject);
            System.out.println("Found " + subjectQuizzes.size() + " quizzes for subject: " + subject.getSubjectName());
            subjectQuizzes.forEach(quiz -> System.out.println("Quiz: ID=" + quiz.getId() + ", Title=" + quiz.getTitle()));
            allQuizzes.addAll(subjectQuizzes);
        }
        System.out.println("Total quizzes found: " + allQuizzes.size());

        List<QuizDTO> quizDTOs = allQuizzes.stream().map(quiz -> {
            QuizDTO dto = new QuizDTO();
            dto.setId(quiz.getId());
            dto.setTitle(quiz.getTitle());
            dto.setSubjectId(quiz.getSubject().getSubjectName());
            List<QuizDTO.QuestionDTO> questionDTOs = quiz.getQuestions().stream().map(question -> {
                QuizDTO.QuestionDTO questionDTO = new QuizDTO.QuestionDTO();
                questionDTO.setQuestionId(question.getId());
                questionDTO.setQuestionText(question.getQuestionText());
                questionDTO.setOptions(question.getOptions());
                questionDTO.setCorrectAnswer(question.getCorrectAnswer());
                return questionDTO;
            }).collect(Collectors.toList());
            dto.setQuestions(questionDTOs);
            return dto;
        }).collect(Collectors.toList());

        System.out.println("Total QuizDTOs created: " + quizDTOs.size());
        return quizDTOs;
    }

    public QuizDTO findQuizQuestions(Long quizId) {
        System.out.println("Fetching questions for quiz ID: " + quizId);
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found with ID: " + quizId));

        // Verify user has access to the quiz's subject
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        List<UserSubjects> userSubjects = userSubjectsRepository.findByUser(user);
        boolean hasAccess = userSubjects.stream()
                .anyMatch(us -> us.getSubject().getSubjectName().equals(quiz.getSubject().getSubjectName()));
        if (!hasAccess) {
            throw new RuntimeException("User does not have access to this quiz's subject");
        }

        QuizDTO dto = new QuizDTO();
        dto.setId(quiz.getId());
        dto.setTitle(quiz.getTitle());
        dto.setSubjectId(quiz.getSubject().getSubjectName());
        List<QuizDTO.QuestionDTO> questionDTOs = quiz.getQuestions().stream().map(question -> {
            QuizDTO.QuestionDTO questionDTO = new QuizDTO.QuestionDTO();
            questionDTO.setQuestionId(question.getId());
            questionDTO.setQuestionText(question.getQuestionText());
            questionDTO.setOptions(question.getOptions());
            questionDTO.setCorrectAnswer(null); // Hide correct answer
            return questionDTO;
        }).collect(Collectors.toList());
        dto.setQuestions(questionDTOs);
        System.out.println("Found " + questionDTOs.size() + " questions for quiz ID: " + quizId);
        return dto;
    }

    public QuizResultDTO submitQuiz(Long quizId, QuizSubmissionDTO submission) {
        System.out.println("Processing submission for quiz ID: " + quizId);
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found with ID: " + quizId));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        List<UserSubjects> userSubjects = userSubjectsRepository.findByUser(user);
        boolean hasAccess = userSubjects.stream()
                .anyMatch(us -> us.getSubject().getSubjectName().equals(quiz.getSubject().getSubjectName()));
        if (!hasAccess) {
            throw new RuntimeException("User does not have access to this quiz's subject");
        }

        int score = 0;
        int totalQuestions = quiz.getQuestions().size();
        for (QuizSubmissionDTO.AnswerDTO answer : submission.getAnswers()) {
            for (com.investhoodit.RevisionHub.model.Question question : quiz.getQuestions()) {
                if (question.getId().equals(answer.getQuestionId())) {
                    String correctAnswer = question.getCorrectAnswer() != null ? question.getCorrectAnswer().trim() : null;
                    String selectedAnswer = answer.getSelectedAnswer() != null ? answer.getSelectedAnswer().trim() : null;
                    System.out.println("Comparing: questionId=" + answer.getQuestionId() +
                            ", selectedAnswer='" + selectedAnswer + "'" +
                            ", correctAnswer='" + correctAnswer + "'");
                    if (correctAnswer != null && correctAnswer.equals(selectedAnswer)) {
                        score++;
                        System.out.println("Match found, incrementing score to: " + score);
                    } else {
                        System.out.println("No match for questionId=" + answer.getQuestionId());
                    }
                    break;
                }
            }
        }

        QuizResultDTO result = new QuizResultDTO();
        result.setQuizId(quizId);
        result.setQuizTitle(quiz.getTitle());
        result.setScore(score);
        result.setTotalQuestions(totalQuestions);
        System.out.println("Submission processed: Score=" + score + "/" + totalQuestions);
        return result;
    }
}