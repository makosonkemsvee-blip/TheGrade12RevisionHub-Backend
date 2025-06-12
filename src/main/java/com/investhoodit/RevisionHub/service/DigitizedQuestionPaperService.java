package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.model.*;
import com.investhoodit.RevisionHub.repository.*;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DigitizedQuestionPaperService {
    private static final Logger log = LoggerFactory.getLogger(DigitizedQuestionPaperService.class);

    @Value("${pdf.folder.path}")
    private String pdfFolderPath;

    private final DigitizedQuestionPaperRepository digitizedQuestionPaperRepository;
    private final SubjectRepository subjectRepository;
    private final UserSubjectsRepository userSubjectsRepository;
    private final UserRepository userRepository;
    private final QuestionPaperRepository questionPaperRepository;

    public DigitizedQuestionPaperService(
            DigitizedQuestionPaperRepository digitizedQuestionPaperRepository,
            SubjectRepository subjectRepository,
            UserSubjectsRepository userSubjectsRepository,
            UserRepository userRepository, QuestionPaperRepository questionPaperRepository) {
        this.digitizedQuestionPaperRepository = digitizedQuestionPaperRepository;
        this.subjectRepository = subjectRepository;
        this.userSubjectsRepository = userSubjectsRepository;
        this.userRepository = userRepository;
        this.questionPaperRepository = questionPaperRepository;
    }

    @PostConstruct
    @Transactional
    public void initializeInteractivePapers() {
        try {

            for (QuestionPaper questionPaper: questionPaperRepository.findAll()) {
                log.info("Starting initializeInteractivePapers");
                Subject subject = questionPaper.getSubject();
                String fileName = questionPaper.getFileName().substring(0, questionPaper.getFileName().lastIndexOf("."));

                Optional<DigitizedQuestionPaper> existingPaper = digitizedQuestionPaperRepository
                        .findAll()
                        .stream()
                        .filter(p -> p.getFileName().equals(fileName) && p.isInteractive())
                        .findFirst();

                if (existingPaper.isEmpty()) {
                    log.info("Creating paper");
                    DigitizedQuestionPaper interactivePaper = new DigitizedQuestionPaper();
                    interactivePaper.setFileName(fileName);
                    interactivePaper.setSubject(subject);
                    interactivePaper.setInteractive(true);
                    digitizedQuestionPaperRepository.save(interactivePaper);
                    log.info("Saved paper");
                } else {
                    log.info("Paper already exists");
                }

            }

//            log.info("Starting initializeInteractivePapers");
//            Subject englishSubject = subjectRepository.findBySubjectName("English")
//                    .orElseGet(() -> {
//                        log.info("Creating English subject");
//                        Subject newSubject = new Subject();
//                        newSubject.setSubjectName("English");
//                        return subjectRepository.save(newSubject);
//                    });
//
//            Optional<DigitizedQuestionPaper> existingPaper = digitizedQuestionPaperRepository
//                    .findAll()
//                    .stream()
//                    .filter(p -> p.getFileName().equals("English First Additional Language P1 - November 2020") && p.isInteractive())
//                    .findFirst();
//
//            if (existingPaper.isEmpty()) {
//                log.info("Creating EnglishFALP12020 paper");
//                DigitizedQuestionPaper interactivePaper = new DigitizedQuestionPaper();
//                interactivePaper.setFileName("English First Additional Language P1 - November 2020");
//                interactivePaper.setSubject(englishSubject);
//                interactivePaper.setInteractive(true);
//                digitizedQuestionPaperRepository.save(interactivePaper);
//                log.info("Saved EnglishFALP12020 paper");
//            } else {
//                log.info("EnglishFALP12020 paper already exists");
//            }
//
//            //log.info("Starting initializeInteractivePapers");
//            Subject mathsSubject = subjectRepository.findBySubjectName("Mathematics")
//                    .orElseGet(() -> {
//                      //  log.info("Creating English subject");
//                        Subject newSubject = new Subject();
//                        newSubject.setSubjectName("Mathematics");
//                        return subjectRepository.save(newSubject);
//                    });
//
//            Optional<DigitizedQuestionPaper> existingPaper2 = digitizedQuestionPaperRepository
//                    .findAll()
//                    .stream()
//                    .filter(p -> p.getFileName().equals("MATHEMATICS P1 - NOVEMBER 2022") && p.isInteractive())
//                    .findFirst();
//
//            if (existingPaper2.isEmpty()) {
//                //log.info("Creating EnglishFALP12020 paper");
//                DigitizedQuestionPaper interactivePaper = new DigitizedQuestionPaper();
//                interactivePaper.setFileName("MATHEMATICS P1 - NOVEMBER 2022");
//                interactivePaper.setSubject(mathsSubject);
//                interactivePaper.setInteractive(true);
//                digitizedQuestionPaperRepository.save(interactivePaper);
//              //  log.info("Saved EnglishFALP12020 paper");
//            } else {
//              //  log.info("EnglishFALP12020 paper already exists");
//            }
        } catch (Exception e) {
            log.error("Error initializing interactive papers: {}", e.getMessage(), e);
        }
    }

    /*/public void savePdfFilesFromFolder() throws IOException {
        File folder = new File(pdfFolderPath + "/digitized");
        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));
        List<DigitizedQuestionPaper> digitizedQuestionPapers = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                byte[] fileData = new byte[(int) file.length()];
                try (FileInputStream fis = new FileInputStream(file)) {
                    fis.read(fileData);
                }
                DigitizedQuestionPaper pdfFile = new DigitizedQuestionPaper();
                pdfFile.setFileName(file.getName());
                pdfFile.setFileData(fileData);
                digitizedQuestionPapers.add(pdfFile);
            }
        }
        for (DigitizedQuestionPaper digitizedQuestionPaper : digitizedQuestionPapers) {
            for (Subject subject : subjectRepository.findAll()) {
                String subjectName = subject.getSubjectName();
                if (digitizedQuestionPaper.getFileName().toLowerCase().contains(subjectName.toLowerCase())) {
                    digitizedQuestionPaper.setSubject(subject);
                }
            }
        }

        if (allDigitizedQuestionPapers().isEmpty()) {
            digitizedQuestionPaperRepository.saveAll(digitizedQuestionPapers);
        }
    }*/

    public List<DigitizedQuestionPaper> allDigitizedQuestionPapers() {
        return digitizedQuestionPaperRepository.findAll();
    }

    public DigitizedQuestionPaper getPaperById(Long id) {
        return digitizedQuestionPaperRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Digitized question paper not found"));
    }

    public void uploadDigitizedQuestionPaper(DigitizedQuestionPaper digitizedQuestionPaper) {
        digitizedQuestionPaperRepository.save(digitizedQuestionPaper);
    }

    public int count() {
        return (int) digitizedQuestionPaperRepository.count();
    }

    // DigitizedQuestionPaperService
    public List<DigitizedQuestionPaper> findBySubjectName(String subjectName) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Subject> subjects = subjectRepository.findBySubjectNameContainingIgnoreCase(subjectName);
        if (subjects.isEmpty()) {
            throw new RuntimeException("No subjects found matching: " + subjectName);
        }
        Subject subject = subjects.get(0); // Use first match
        userSubjectsRepository.findByUserAndSubject(user, subject)
                .orElseThrow(() -> new RuntimeException("User not enrolled in subject: " + subjectName));
        return digitizedQuestionPaperRepository.findBySubject(subject);
    }

    public List<DigitizedQuestionPaper> findAllPapersForUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<UserSubjects> userSubjects = userSubjectsRepository.findByUser(user);
        List<DigitizedQuestionPaper> allPapers = new ArrayList<>();
        for (UserSubjects userSubject : userSubjects) {
            Subject subject = userSubject.getSubject();
            allPapers.addAll(digitizedQuestionPaperRepository.findBySubject(subject));
        }
        return allPapers;
    }
}