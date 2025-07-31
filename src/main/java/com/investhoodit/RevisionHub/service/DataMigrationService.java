package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.model.PerformanceMetric;
import com.investhoodit.RevisionHub.model.SubjectMastery;
import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.model.Subject;
import com.investhoodit.RevisionHub.model.UserSubjects;
import com.investhoodit.RevisionHub.repository.PerformanceMetricRepository;
import com.investhoodit.RevisionHub.repository.SubjectMasteryRepository;
import com.investhoodit.RevisionHub.repository.UserSubjectsRepository;
import com.investhoodit.RevisionHub.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DataMigrationService implements CommandLineRunner {

    private final PerformanceMetricRepository performanceMetricRepository;
    private final SubjectMasteryRepository subjectMasteryRepository;
    private final UserSubjectsRepository userSubjectsRepository;
    private final UserRepository userRepository;

    public DataMigrationService(PerformanceMetricRepository performanceMetricRepository,
                                SubjectMasteryRepository subjectMasteryRepository,
                                UserSubjectsRepository userSubjectsRepository,
                                UserRepository userRepository) {
        this.performanceMetricRepository = performanceMetricRepository;
        this.subjectMasteryRepository = subjectMasteryRepository;
        this.userSubjectsRepository = userSubjectsRepository;
        this.userRepository = userRepository;
        System.out.println("DataMigrationService initialized");
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("DataMigrationService run method starting");
        try {
            migrateToSubjectMastery();
        } catch (Exception e) {
            System.err.println("Migration failed during startup: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("DataMigrationService run method completed");
    }

    @Transactional
    public void migrateToSubjectMastery() {
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

        // Fetch user's subjects from UserSubjects
        List<UserSubjects> userSubjects = userSubjectsRepository.findByUser(user);
        System.out.println("Found " + userSubjects.size() + " subjects for " + userEmail + ": " +
                userSubjects.stream().map(us -> us.getSubject().getSubjectName()).collect(Collectors.joining(", ")));

        // Fetch performance metrics for the user
        List<PerformanceMetric> metrics = performanceMetricRepository.findByUser(user);
        System.out.println("Found " + metrics.size() + " performance metrics for " + userEmail + ": " +
                metrics.stream()
                        .map(m -> "Subject: " + (m.getSubject() != null ? m.getSubject().getSubjectName() : "null") +
                                ", Score: " + m.getScore() + ", ActivityType: " + m.getActivityType() +
                                ", ActivityName: " + m.getActivityName())
                        .collect(Collectors.joining("; ")));

        // Group metrics by subject
        Map<Subject, List<PerformanceMetric>> metricsBySubject = metrics.stream()
                .filter(m -> m.getSubject() != null)
                .collect(Collectors.groupingBy(PerformanceMetric::getSubject));

        // Log grouped metrics
        metricsBySubject.forEach((subject, metricList) -> {
            System.out.println("Metrics for subject " + subject.getSubjectName() + ": " +
                    metricList.stream()
                            .map(m -> "Score: " + m.getScore() + ", ActivityType: " + m.getActivityType() +
                                    ", ActivityName: " + m.getActivityName())
                            .collect(Collectors.joining("; ")));
        });

        // Clear existing mastery data
        subjectMasteryRepository.deleteByUser(user);
        System.out.println("Cleared existing mastery data for " + userEmail);

        for (UserSubjects userSubject : userSubjects) {
            Subject subject = userSubject.getSubject();
            if (subject == null) {
                System.out.println("Skipping null subject for user " + userEmail);
                continue;
            }
            // Calculate progress from Quiz and Digitized QP
            double progress = metricsBySubject.getOrDefault(subject, List.of()).stream()
                    .filter(m -> "Quiz".equalsIgnoreCase(m.getActivityType()) ||
                            "Digitized QP".equalsIgnoreCase(m.getActivityType()))
                    .mapToDouble(PerformanceMetric::getScore)
                    .average()
                    .orElse(0.0);

            SubjectMastery mastery = new SubjectMastery();
            mastery.setUser(user);
            mastery.setSubject(subject);
            mastery.setProgress(progress);

            subjectMasteryRepository.save(mastery);
            System.out.println("Saved mastery for subject: " + subject.getSubjectName() +
                    " with progress: " + progress + " for user: " + userEmail);
        }
        System.out.println("Migration completed for " + userSubjects.size() + " subjects of user " + userEmail);
    }
}