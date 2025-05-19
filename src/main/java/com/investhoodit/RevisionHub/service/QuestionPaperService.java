package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.model.QuestionPaper;
import com.investhoodit.RevisionHub.repository.QuestionPaperRepository;
import com.investhoodit.RevisionHub.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Value;
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

    public QuestionPaperService(QuestionPaperRepository questionPaperRepository, SubjectRepository subjectRepository) {
        this.questionPaperRepository = questionPaperRepository;
        this.subjectRepository = subjectRepository;
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

            if (allQuestionPapers().isEmpty()) {
                questionPaperRepository.saveAll(questionPapers);
            }

    }

    public List<QuestionPaper> allQuestionPapers() {
        return questionPaperRepository.findAll();
    }

    public void deletePaper(Long id) {
        questionPaperRepository.deleteById(id);
    }

    public QuestionPaper getPaperById(Long id) {
        return questionPaperRepository.findById(id).orElseThrow(() -> new RuntimeException("Paper not found"));
    }

    public Optional<QuestionPaper> findById(Long id) {
        return questionPaperRepository.findById(id);
    }

    public void uploadQuestionPaper(QuestionPaper questionPaper){
        questionPaperRepository.save(questionPaper);
    }

    public int count(){
        return (int) questionPaperRepository.count();
    }

    public List<QuestionPaper> findBySubjectName(String subjectName) {
        return questionPaperRepository.findBySubject(subjectRepository.findBySubjectName(subjectName)
                .orElseThrow(() -> new RuntimeException("Subject not found")));
    }
}
