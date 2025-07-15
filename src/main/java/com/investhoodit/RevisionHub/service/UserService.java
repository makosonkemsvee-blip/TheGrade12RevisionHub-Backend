package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email); // Assumes UserRepository has this method
    }
}