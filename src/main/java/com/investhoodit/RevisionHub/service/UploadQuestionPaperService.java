package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.model.ApiResponse;
import com.investhoodit.RevisionHub.model.QuestionPaper;
import com.investhoodit.RevisionHub.model.Subject;
import com.investhoodit.RevisionHub.repository.QuestionPaperRepository;
import com.investhoodit.RevisionHub.repository.SubjectRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class UploadQuestionPaperService {

    private final QuestionPaperRepository questionPaperRepository;
    private final SubjectRepository subjectRepository;

    public UploadQuestionPaperService(QuestionPaperRepository questionPaperRepository, SubjectRepository subjectRepository) {
        this.questionPaperRepository = questionPaperRepository;
        this.subjectRepository = subjectRepository;
    }

    public QuestionPaper uploadQuestionPaper(String subjectName, MultipartFile file) throws IOException {
        Subject subject = subjectRepository.findBySubjectName(subjectName)
                .orElseThrow(() -> new RuntimeException("Subject not found"));
        QuestionPaper questionPaper = new QuestionPaper();
        questionPaper.setSubject(subject);
        questionPaper.setFileName(file.getOriginalFilename());
        questionPaper.setFileData(file.getBytes());
        return questionPaperRepository.save(questionPaper);
    }
}
