package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.dto.LoginRequest;
import com.investhoodit.RevisionHub.dto.LoginResponse;
import com.investhoodit.RevisionHub.service.UserLoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class UserLoginController {

    private final UserLoginService userService;

    public UserLoginController(UserLoginService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = userService.authenticateAndGenerateToken(loginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new LoginResponse(null, e.getMessage(), null));
        }
    }
}
