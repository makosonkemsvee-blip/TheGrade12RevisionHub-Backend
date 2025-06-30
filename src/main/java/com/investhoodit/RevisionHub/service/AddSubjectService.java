package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.dto.AddSubjectDTO;
import com.investhoodit.RevisionHub.model.Subject;
import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.model.UserSubjects;
import com.investhoodit.RevisionHub.repository.SubjectRepository;
import com.investhoodit.RevisionHub.repository.UserRepository;
import com.investhoodit.RevisionHub.repository.UserSubjectsRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AddSubjectService {

	private final SubjectRepository subjectRepository;
	private final UserSubjectsRepository userSubjectsRepository;
	private final UserRepository userRepository;

    public AddSubjectService(SubjectRepository subjectRepository, UserSubjectsRepository userSubjectsRepository, UserRepository userRepository) {
        this.subjectRepository = subjectRepository;
        this.userSubjectsRepository = userSubjectsRepository;
        this.userRepository = userRepository;
    }

    public void autoAddSubject() {
		List<String> subjectNames = Arrays.asList(
				"Accounting", "Afrikaans", "Agricultural Science",
				"Business Studies", "Computer Applications Technology",
				"Economics", "English", "Geography", "History", "IsiNdebele",
				"IsiXhosa", "IsiZulu", "Life Orientation", "Life Science",
				"Mathematics", "Mathematical Literacy", "Physical Sciences",
				"Sepedi", "Sesotho", "Setswana", "Siswati", "Technical Maths",
				"Tourism", "Tshivenda", "Xitsonga"
		);

		// Retrieve existing subjects from the database
		List<Subject> existingSubjects = subjectRepository.findAll();
		Set<String> existingSubjectNames = existingSubjects.stream()
				.map(Subject::getSubjectName)
				.collect(Collectors.toSet());

		// Filter new subjects that are not in the database
		List<Subject> newSubjects = subjectNames.stream()
				.filter(subjectName -> !existingSubjectNames.contains(subjectName))
				.map(Subject::new)
				.toList();

		// Save only the new subjects
		if (!newSubjects.isEmpty()) {
			subjectRepository.saveAll(newSubjects);
		}
	}

	public boolean addSubject(AddSubjectDTO dto) {

		User user = userRepository.findByEmail(dto.getEmail())
				.orElseThrow(() -> new RuntimeException("User not found"));

		if (user == null) {
			return false;
		}

		// Check if subject exists in database
		Subject subject = subjectRepository.findById(dto.getSubjectName())
				.orElseThrow(() -> new RuntimeException("Subject not found."));

		if (subject == null) {
			return false;
		}

			// Check if user already added this subject
			boolean exists = userSubjectsRepository.existsByUserAndSubject(user, subject);
			if (exists) {
				return false;
			}

			// Add subject to user
			UserSubjects userSubjects = new UserSubjects();
			userSubjects.setUser(user);
			userSubjects.setSubject(subject);
			userSubjects.setCreatedAt(new Date());
		    userSubjectsRepository.save(userSubjects);
			return true;
		}

	public List<UserSubjects> getAllStudentSubjects(User user) {
		return userSubjectsRepository.findByUser(user);
	}
}
