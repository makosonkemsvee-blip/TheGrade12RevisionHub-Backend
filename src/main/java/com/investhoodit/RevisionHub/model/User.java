package com.investhoodit.RevisionHub.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import com.investhoodit.RevisionHub.model.Settings;

@Data
@Entity
@Table(name = "user_tbl")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String idNumber;
    private String phoneNumber;

    @NotNull(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(unique = true)
    private String email;

    @NotNull(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
    @Lob
    private byte[] profilePicture;
    private String role;
    @Column(name = "first_login", nullable = false)
    private boolean firstLogin = true;

    @ManyToOne
    @JoinColumn(name = "settings_username")
    private Settings settings;
}
