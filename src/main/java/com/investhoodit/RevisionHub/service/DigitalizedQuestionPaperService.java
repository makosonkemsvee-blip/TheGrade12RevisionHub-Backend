package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.model.DigitalizedQuestionPaper;
import com.investhoodit.RevisionHub.repository.DigitalizedQuestionPaperRepository;
import org.springframework.stereotype.Service;

@Service
public class DigitalizedQuestionPaperService {
    private final DigitalizedQuestionPaperRepository digitalizedQuestionPaperRepository;

    public DigitalizedQuestionPaperService(DigitalizedQuestionPaperRepository digitalizedQuestionPaperRepository) {
        this.digitalizedQuestionPaperRepository = digitalizedQuestionPaperRepository;
    }

    public DigitalizedQuestionPaper save(DigitalizedQuestionPaper digitalizedQuestionPaper) {
        return digitalizedQuestionPaperRepository.save(digitalizedQuestionPaper);
    }

}
