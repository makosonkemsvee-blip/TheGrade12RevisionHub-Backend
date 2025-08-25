package com.investhoodit.RevisionHub.repository;

import com.investhoodit.RevisionHub.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
	PasswordResetToken findByToken(String token);

	@Query("SELECT t FROM PasswordResetToken t WHERE t.token = :token AND t.email = :email")
	PasswordResetToken findByTokenAndEmail(String token, String email);
}
