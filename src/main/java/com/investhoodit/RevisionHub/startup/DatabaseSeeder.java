package com.investhoodit.RevisionHub.startup;

import com.investhoodit.RevisionHub.service.AddSubjectService;
import com.investhoodit.RevisionHub.service.QuestionPaperService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    //private final QuestionPaperService questionPaperService;
    private final AddSubjectService subjectService;
    private final QuestionPaperService questionPaperService;

    public DatabaseSeeder(AddSubjectService subjectService, QuestionPaperService questionPaperService) {
        this.subjectService = subjectService;
        this.questionPaperService = questionPaperService;
    }

    @Override
    public void run(String... args) throws Exception {
        questionPaperService.savePdfFilesFromFolder();
        subjectService.autoAddSubject();
    }
}
