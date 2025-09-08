package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.model.ApiResponse;
import com.investhoodit.RevisionHub.model.Certificate;
import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.repository.UserRepository;
import com.investhoodit.RevisionHub.service.CertificateService;

import com.investhoodit.RevisionHub.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user/certificates")
 // adjust for your React app
public class CertificateController {
    private final CertificateService service;
    private final UserService userService;

    public CertificateController(CertificateService service, UserService userService) {
        this.service = service;
        this.userService = userService;

    }

    @PostMapping("/save")
    public Certificate saveCertificate(@RequestBody Certificate certificate) {
        return service.saveCertificate(certificate);
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getCertificateCount() {
        int count = service.countCert();
        System.out.println("Mzwandile "+count);
        return ResponseEntity.ok(count);
    }

//    // ✅ Check how many times an idNumber exists
//    @GetMapping("/count/{idNumber}")
//    public long getCertificateCount(@PathVariable String idNumber) {
//        return service.getCertificateCountByIdNumber(idNumber);
//    }



}
