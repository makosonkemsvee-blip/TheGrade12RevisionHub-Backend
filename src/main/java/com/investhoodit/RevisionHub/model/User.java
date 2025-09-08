package com.investhoodit.RevisionHub.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private String createdAt;
    private boolean twoFactorEnabled;
    private LocalDate birthday;

    @NotNull(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(unique = true)
    private String email;

    @NotNull(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @JsonIgnore // Prevent password from being serialized
    private String password;

    @Lob
    @JsonIgnore // Prevent binary data from being serialized
    private byte[] profilePicture;

    private String role;

    @Column(name = "first_login", nullable = false)
    private boolean firstLogin = true;

    @Column(name = "otp_code")
    @JsonIgnore // Prevent sensitive data from being serialized
    private String otpCode;

    @Column(name = "otp_expiry")
    @JsonIgnore
    private LocalDateTime otpExpiry;

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified = false;

    @OneToOne
    @JoinColumn(name = "settings_email")
    @JsonIgnore // Ignore settings to avoid circular references
    private Settings settings;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // Ignore related collections to prevent deep serialization
    private List<UserSubjects> userSubjects;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<PerformanceMetric> performanceMetrics;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<SubjectMastery> subjectMasteries;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Schedule> schedules;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<GroupMembership> groupMemberships;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<UserPaperPerformance> userPaperPerformances;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Attendance attendances;

    public boolean getTwoFactorEnabled() { return twoFactorEnabled; }
    public boolean getIsVerified() { return isVerified; }
    public void setIsVerified(boolean verified) { isVerified = verified; }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }
}