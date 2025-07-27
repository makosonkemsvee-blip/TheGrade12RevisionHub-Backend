package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.dto.SubjectMasteryDTO;
import com.investhoodit.RevisionHub.model.SubjectMastery;
import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.repository.SubjectMasteryRepository;
import com.investhoodit.RevisionHub.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectMasteryService {

    private final SubjectMasteryRepository subjectMasteryRepository;
    private final UserRepository userRepository;

    public SubjectMasteryService(SubjectMasteryRepository subjectMasteryRepository, UserRepository userRepository) {
        this.subjectMasteryRepository = subjectMasteryRepository;
        this.userRepository = userRepository;
    }

    public List<SubjectMasteryDTO> getSubjectMastery() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("Fetching subject mastery for user email: " + userEmail);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userEmail));
        List<SubjectMastery> records = subjectMasteryRepository.findByUser(user);
        System.out.println("Found " + records.size() + " mastery records");
        return records.stream()
                .map(record -> new SubjectMasteryDTO(
                        record.getSubject().getSubjectName(),
                        record.getProgress()
                ))
                .toList();
    }
}