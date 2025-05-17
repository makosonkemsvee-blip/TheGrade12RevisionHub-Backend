package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserSignupService {

    private final UserRepository userRepository;
    private final PasswordEncoderService passwordEncoderService;

    public UserSignupService(UserRepository userRepository, PasswordEncoderService passwordEncoderService) {
        this.userRepository = userRepository;
        this.passwordEncoderService = passwordEncoderService;
    }

    public User signUp(User user) {
        if (!doesUserExist(user.getEmail())) {
            user.setPassword(passwordEncoderService.encodePassword(user.getPassword()));
            return userRepository.save(user);
        }else {
            return null;
        }
    }

    public boolean doesUserExist(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}
