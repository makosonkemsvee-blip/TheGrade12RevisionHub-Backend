package com.investhoodit.RevisionHub;

import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserSignupService {

    private final UserRepository userRepository;

    public UserSignupService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User singUp(User user) {
        return userRepository.save(user);
    }
}
