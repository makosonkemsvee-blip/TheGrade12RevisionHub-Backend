package com.investhoodit.RevisionHub.repository;

import com.investhoodit.RevisionHub.model.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {
//    @Query("SELECT COUNT(c) FROM Certificate c WHERE c.user.id = :userId")
//    long countCertificatesByUserId(@Param("userId") Long userId);

    List<Certificate> findAllByIdNumber(String idNumber);

    // Alternative using Spring Data JPA derived query
    long countCertificatesByIdNumber(String idNumber);
}