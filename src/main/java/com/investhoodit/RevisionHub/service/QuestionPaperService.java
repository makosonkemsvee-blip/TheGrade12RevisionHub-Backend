package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.model.QuestionPaper;
import com.investhoodit.RevisionHub.model.Subject;
import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.model.UserSubjects;
import com.investhoodit.RevisionHub.repository.QuestionPaperRepository;
import com.investhoodit.RevisionHub.repository.SubjectRepository;
import com.investhoodit.RevisionHub.repository.UserRepository;
import com.investhoodit.RevisionHub.repository.UserSubjectsRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionPaperService {

    @Value("${pdf.folder.path}")
    private String pdfFolderPath;

    private final QuestionPaperRepository questionPaperRepository;
    private final SubjectRepository subjectRepository;
    private final UserSubjectsRepository userSubjectsRepository;
    private final UserRepository userRepository;

    public QuestionPaperService(QuestionPaperRepository questionPaperRepository, SubjectRepository subjectRepository, UserSubjectsRepository userSubjectsRepository, UserRepository userRepository) {
        this.questionPaperRepository = questionPaperRepository;
        this.subjectRepository = subjectRepository;
        this.userSubjectsRepository = userSubjectsRepository;
        this.userRepository = userRepository;
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
}
