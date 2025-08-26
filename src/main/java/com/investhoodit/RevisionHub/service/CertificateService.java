package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.model.Certificate;
import com.investhoodit.RevisionHub.repository.CertificateRepository;
import org.springframework.stereotype.Service;

@Service
public class CertificateService {
    private final CertificateRepository repository;

    public CertificateService(CertificateRepository repository) {
        this.repository = repository;
    }

    public Certificate saveCertificate(Certificate certificate) {
        return repository.save(certificate);
    }
}

