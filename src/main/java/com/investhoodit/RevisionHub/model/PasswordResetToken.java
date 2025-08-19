package com.investhoodit.RevisionHub.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Entity
public class PasswordResetToken {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    private LocalDateTime expiryDate;
	private String email;

	@ManyToOne
	@JoinColumn(name = "user_id")
    private User user;

    public PasswordResetToken(String token, LocalDateTime expiryDate, User user) {
		super();
		this.token = token;
		this.expiryDate = expiryDate;
		this.user = user;
	}

	public PasswordResetToken(String token, LocalDateTime expiryDate, User user, String email) {
		super();
		this.token = token;
		this.expiryDate = expiryDate;
		this.user = user;
		this.email = email;
	}

	public PasswordResetToken() {

	}

}
