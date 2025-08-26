package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.model.Certificate;
import com.investhoodit.RevisionHub.service.CertificateService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/certificates")
@CrossOrigin(origins = "http://localhost:3000") // adjust for your React app
public class CertificateController {
    private final CertificateService service;

    public CertificateController(CertificateService service) {
        this.service = service;
    }

    @PostMapping("/save")
    public Certificate saveCertificate(@RequestBody Certificate certificate) {
        return service.saveCertificate(certificate);
    }
}
