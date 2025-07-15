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
            mastery.setQuizMarks(null);
            mastery.setExamMarks(null);
            subjectMasteryRepository.save(mastery);
            System.out.println("Saved mastery for subject: " + us.getSubject().getSubjectName() + " for user: " + userEmail);
        }
        System.out.println("Migration completed for " + userSubjects.size() + " subjects of user " + userEmail);
    }
}