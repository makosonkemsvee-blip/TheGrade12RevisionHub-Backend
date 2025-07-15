package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.dto.SubjectMasteryDTO;
import com.investhoodit.RevisionHub.model.SubjectMastery;
import com.investhoodit.RevisionHub.repository.SubjectMasteryRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectMasteryService {

    private final SubjectMasteryRepository subjectMasteryRepository;

    public SubjectMasteryService(SubjectMasteryRepository subjectMasteryRepository) {
        this.subjectMasteryRepository = subjectMasteryRepository;
    }

    public List<SubjectMasteryDTO> getSubjectMastery() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("Fetching subject mastery for user: " + userId);
        List<SubjectMastery> records = subjectMasteryRepository.findByUserId(userId);
        System.out.println("Found " + records.size() + " mastery records");
        return records.stream()
                .map(record -> new SubjectMasteryDTO(
                        record.getSubjectName(),
                        record.getQuizMarks(),
                        record.getExamMarks()
                ))
                .toList();
    }
}