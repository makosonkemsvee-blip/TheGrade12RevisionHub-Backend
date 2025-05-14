package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.service.UserSignupService;
import com.investhoodit.RevisionHub.model.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/signup")
public class UserSignupController {

    private final UserSignupService userSignupService;

    public UserSignupController(UserSignupService userSignupService) {
        this.userSignupService = userSignupService;
    }

    @PostMapping
    public User signUp(@RequestBody User user) {
        return userSignupService.singUp(user);
    }
}
