package com.investhoodit.RevisionHub.dto;

import lombok.Data;

@Data
public class UserDTO {

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String idNumber;
    private String email;
    private String password;
    private String role;
    private Long id;

    public UserDTO(Long id, String email, String firstName, String lastName, String role) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
/*public class UserDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String role;

    **/