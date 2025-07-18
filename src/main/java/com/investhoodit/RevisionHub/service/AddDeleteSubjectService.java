package com.investhoodit.RevisionHub.service;

import java.util.*;
import java.util.stream.Collectors;

import com.investhoodit.RevisionHub.dto.SubjectDTO;
import com.investhoodit.RevisionHub.model.Subject;
import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.model.UserSubjects;
import com.investhoodit.RevisionHub.repository.SubjectRepository;
import com.investhoodit.RevisionHub.repository.UserRepository;
import com.investhoodit.RevisionHub.repository.UserSubjectsRepository;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddDeleteSubjectService {

	private final SubjectRepository subjectRepository;
	private final UserSubjectsRepository userSubjectsRepository;
	private final UserRepository userRepository;

	public AddDeleteSubjectService(SubjectRepository subjectRepository, UserSubjectsRepository userSubjectsRepository, UserRepository userRepository) {
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

	public boolean addSubject(SubjectDTO subjectDTO) {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User not found"));

		if (user == null) {
			return false;
		}

		// Check if subject exists in database
		Subject subject = subjectRepository.findBySubjectName(subjectDTO.getSubjectName())
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

	public List<String> getAllStudentSubjects() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByEmail(username)
				.orElseThrow(() -> new RuntimeException("User not found"));
		return userSubjectsRepository.findByUser(user)
				.stream()
				.map(userSubject -> userSubject.getSubject().getSubjectName())
				.collect(Collectors.toList());
	}

	@Transactional
	public boolean removeSubject(String subjectName) {
		// Get the authenticated user
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByEmail(username)
				.orElseThrow(() -> new RuntimeException("User not found"));

		// Find the subject by name
		Subject subject = subjectRepository.findBySubjectName(subjectName)
				.orElseThrow(() -> new RuntimeException("Subject not found"));

		// Delete the UserSubjects record
		userSubjectsRepository.deleteByUserAndSubject(user, subject);
		return true;
	}

	public List<String> allSubjects(){
		List<String> subjectNames = new ArrayList<>();
		for (Subject subject : subjectRepository.findAll()){
			subjectNames.add(subject.getSubjectName());
		}
		return subjectNames;
	}

//	public List<String> allSubjects() {
//		return subjectRepository.findAll().stream()
//				.map(Subject::getSubjectName)
//				.collect(Collectors.toList());
//	}

}