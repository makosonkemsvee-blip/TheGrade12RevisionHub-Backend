package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.dto.QuizDTO;
import com.investhoodit.RevisionHub.model.*;
import com.investhoodit.RevisionHub.repository.QuizRepository;
import com.investhoodit.RevisionHub.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuestionPaperService {

    @Value("${pdf.folder.path}")
    private String pdfFolderPath;

    private final QuestionPaperRepository questionPaperRepository;
    private final SubjectRepository subjectRepository;
    private final UserSubjectsRepository userSubjectsRepository;
    private final UserRepository userRepository;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;

    public QuestionPaperService(QuestionPaperRepository questionPaperRepository, SubjectRepository subjectRepository, UserSubjectsRepository userSubjectsRepository, UserRepository userRepository, QuizRepository quizRepository, QuestionRepository questionRepository) {
        this.questionPaperRepository = questionPaperRepository;
        this.subjectRepository = subjectRepository;
        this.userSubjectsRepository = userSubjectsRepository;
        this.userRepository = userRepository;
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
    }

    public void savePdfFilesFromFolder() throws IOException {
        File folder = new File(pdfFolderPath);
        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));
        List<QuestionPaper> questionPapers = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                byte[] fileData = new byte[(int) file.length()];
                try (FileInputStream fis = new FileInputStream(file)) {
                    fis.read(fileData);
                }
                QuestionPaper pdfFile = new QuestionPaper();
                pdfFile.setFileName(file.getName());
                pdfFile.setFileData(fileData);
                questionPapers.add(pdfFile);
            }
        }
        for (QuestionPaper questionPaper : questionPapers) {
            for (Subject subject: subjectRepository.findAll()) {
                String subjectName = subject.getSubjectName();
                if (questionPaper.getFileName().toLowerCase().contains(subjectName.toLowerCase())) {
                    questionPaper.setSubject(subject);
                }
            }
        }

        if (allQuestionPapers().isEmpty()) {
            questionPaperRepository.saveAll(questionPapers);
        }
    }

    public List<QuestionPaper> allQuestionPapers() {
        return questionPaperRepository.findAll();
    }

    public QuestionPaper getPaperById(Long id) {
        return questionPaperRepository.findById(id).orElseThrow(() -> new RuntimeException("Paper not found"));
    }

    public void uploadQuestionPaper(QuestionPaper questionPaper){
        questionPaperRepository.save(questionPaper);
    }

    public int count(){
        return (int) questionPaperRepository.count();
    }

    public List<QuestionPaper> findBySubjectName(String subjectName) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
//        List<UserSubjects> subjects = userSubjectsRepository.findByUser(user);
//        List<QuestionPaper> questionPapers = new ArrayList<>();
//        List<QuestionPaper> questionPaperList = questionPaperRepository.findAll();

//        for (UserSubjects subject: subjects){
//            questionPapers.addAll(questionPaperRepository.findBySubject(subject.getSubject()));
//        }

        Subject subject = subjectRepository.findBySubjectName(subjectName)
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        return questionPaperRepository.findBySubject(subject);
    }

    public QuestionPaper findById(Long id) {
        return questionPaperRepository.findById(id).orElseThrow(() -> new RuntimeException("Question paper not found"));
    }

    public Quiz createQuiz(QuizDTO quizDTO) {
        Subject subject = subjectRepository.findById(quizDTO.getSubjectId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid subject ID"));

        Quiz quiz = new Quiz();
        quiz.setTitle(quizDTO.getTitle());
        quiz.setSubject(subject);

        // Convert QuestionDTOs to Question entities
        List<Question> questions = quizDTO.getQuestions().stream()
                .map(dto -> new Question(dto.getQuestionText(), dto.getOptions(), dto.getCorrectAnswer(), quiz))
                .collect(Collectors.toList());
        quiz.setQuestions(questions);

        return quizRepository.save(quiz);
    }

    public double calculateScore(Long quizId, Map<Long, String> userAnswers) {
        Optional<Quiz> quizOptional = quizRepository.findById(quizId);
        if (!quizOptional.isPresent()) {
            throw new IllegalArgumentException("Quiz not found with ID: " + quizId);
        }

        List<Question> questions = questionRepository.findByQuizId(quizId);
        if (questions.isEmpty()) {
            return 0.0; // No questions in the quiz
        }

        int correctAnswers = 0;
        for (Question question : questions) {
            String userAnswer = userAnswers.get(question.getId());
            if (userAnswer != null && userAnswer.equals(question.getCorrectAnswer())) {
                correctAnswers++;
            }
        }

        return ((double) correctAnswers / questions.size()) * 100.0;
    }

    public long countQuizzes() {
        long count = quizRepository.count();
        System.out.println("Quiz count: " + count);
        return count;
    }


}
