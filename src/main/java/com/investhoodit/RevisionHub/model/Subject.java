package com.investhoodit.RevisionHub.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "subject")
public class Subject {

	@Id
	@Column(name = "subject_name", nullable = false, unique = true)
	private String subjectName;

	public Subject() {
	}

	public Subject(String subjectName) {
		this.subjectName = subjectName;
	}

}
