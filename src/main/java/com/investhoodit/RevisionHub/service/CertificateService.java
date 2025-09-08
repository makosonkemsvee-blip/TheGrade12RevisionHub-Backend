package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.model.Certificate;
import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.repository.CertificateRepository;
import com.investhoodit.RevisionHub.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CertificateService {
    private final CertificateRepository repository;
    private final UserRepository userRepository;

    public CertificateService(CertificateRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public Certificate saveCertificate(Certificate certificate) {
        return repository.save(certificate);
    }

//    public long getCertificateCountByIdNumber(String idNumber) {
//        return userRepository.countByIdNumber(idNumber);
//        // or certificateRepository.countCertificatesByIdNumber(idNumber);
//    }

    public int countCert(){

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User not found"));

        return repository.findAllByIdNumber(user.getIdNumber()).size();


    }

}

