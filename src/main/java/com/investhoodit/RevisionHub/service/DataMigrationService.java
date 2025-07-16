package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.model.SubjectMastery;
import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.model.UserSubjects;
import com.investhoodit.RevisionHub.repository.SubjectMasteryRepository;
import com.investhoodit.RevisionHub.repository.UserRepository;
import com.investhoodit.RevisionHub.repository.UserSubjectsRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DataMigrationService implements CommandLineRunner {

    private final UserSubjectsRepository userSubjectsRepository;
    private final SubjectMasteryRepository subjectMasteryRepository;
    private final UserRepository userRepository;

    public DataMigrationService(UserSubjectsRepository userSubjectsRepository,
                                SubjectMasteryRepository subjectMasteryRepository,
                                UserRepository userRepository) {
        this.userSubjectsRepository = userSubjectsRepository;
        this.subjectMasteryRepository = subjectMasteryRepository;
        this.userRepository = userRepository;
        System.out.println("DataMigrationService initialized");
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("DataMigrationService run method starting");
        try {
            migrateUserSubjectsToMastery();
        } catch (Exception e) {
            System.err.println("Migration failed during startup: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("DataMigrationService run method completed");
    }

    @Transactional
    public void migrateUserSubjectsToMastery() {
        System.out.println("Starting migration for all users");
        List<User> users = userRepository.findAll();
        System.out.println("Found " + users.size() + " users to process");
        for (User user : users) {
            migrateSubjectsForUser(user);
        }
        System.out.println("Migration completed for all users");
    }

    @Transactional
    public void migrateSubjectsForUser(User user) {
        String userEmail = user.getEmail();
        System.out.println("Processing migration for user email: " + userEmail);
        List<UserSubjects> userSubjects = userSubjectsRepository.findByUser(user);
        System.out.println("Found " + userSubjects.size() + " user subjects for " + userEmail);
        subjectMasteryRepository.deleteByUserId(userEmail);
        System.out.println("Cleared existing mastery data for " + userEmail);
        for (UserSubjects us : userSubjects) {
            SubjectMastery mastery = new SubjectMastery();
            mastery.setUserId(userEmail);
            mastery.setSubjectName(us.getSubject().getSubjectName());
            mastery.setQuizMarks(null); // Initialize as null, update if data exists later
            mastery.setExamMarks(null); // Initialize as null, update if data exists later
            // Calculate progress based on available marks (placeholder logic)
            double progress = calculateProgress(null, null); // Update with actual marks if available
            mastery.setProgress(progress);
            subjectMasteryRepository.save(mastery);
            System.out.println("Saved mastery for subject: " + us.getSubject().getSubjectName() + " for user: " + userEmail);
        }
        System.out.println("Migration completed for " + userSubjects.size() + " subjects of user " + userEmail);
    }

    private double calculateProgress(Integer quizMarks, Integer examMarks) {
        if (quizMarks == null && examMarks == null) {
            return 0.0; // No data, return 0%
        }
        if (quizMarks == null) {
            return examMarks != null ? (examMarks.doubleValue() / 100.0) * 100.0 : 0.0;
        }
        if (examMarks == null) {
            return (quizMarks.doubleValue() / 100.0) * 100.0;
        }
        // Average of quiz and exam marks, scaled to 0-100
        double average = (quizMarks.doubleValue() + examMarks.doubleValue()) / 2.0;
        return Math.min(Math.max(average, 0.0), 100.0); // Clamp to 0-100
    }
}