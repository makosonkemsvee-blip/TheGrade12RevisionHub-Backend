package com.investhoodit.RevisionHub.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class SubjectDTO {
    private String subjectName;
    private String email;
}

//public record SubjectDTO(String subjectName) {}
