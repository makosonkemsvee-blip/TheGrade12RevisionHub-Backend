package com.investhoodit.RevisionHub.dto;

import lombok.Data;

@Data
public class UserResponse {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String profilePicture; // Base64 string or URL
}
