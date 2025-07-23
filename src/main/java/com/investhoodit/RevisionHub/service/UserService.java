package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.dto.PasswordChangeDTO;
import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoderService passwordEncoderService;

    public UserService(UserRepository userRepository, PasswordEncoderService passwordEncoderService) {
        this.userRepository = userRepository;
        this.passwordEncoderService = passwordEncoderService;
    }

    public void changePassword(UserDetails userDetails, PasswordChangeDTO passwordChangeDTO) {
        log.info("Attempting password change for user: {}", userDetails.getUsername());
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", userDetails.getUsername());
                    return new EntityNotFoundException("User not found with email: " + userDetails.getUsername());
                });

        // Verify current password
        if (!passwordEncoderService.verifyPassword(passwordChangeDTO.getCurrentPassword(), user.getPassword())) {
            log.warn("Current password is incorrect for user: {}", userDetails.getUsername());
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Update password
        String encodedNewPassword = passwordEncoderService.encodePassword(passwordChangeDTO.getNewPassword());
        user.setPassword(encodedNewPassword);
        userRepository.save(user);
        log.info("Password changed successfully for user: {}", userDetails.getUsername());
    }

    public void saveProfile(String email, Map<String, Object> profileData) {
        log.info("Saving profile for user: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", email);
                    return new EntityNotFoundException("User not found with email: " + email);
                });
        // Update user fields based on profileData
        if (profileData.containsKey("username")) {
            user.setFirstName((String) profileData.get("username"));
        }
        if (profileData.containsKey("email")) {
            user.setEmail((String) profileData.get("email"));
        }
        // Add other fields as needed
        userRepository.save(user);
        log.info("Profile saved successfully for user: {}", email);
    }

    public User searchUser(String email){

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));

    }

    public List<User> getAllStudents() {

        return userRepository.findAll();

    }

    public boolean removeUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));

        if(user != null) {
            userRepository.delete(user);
            return true;
        }else{
            return false;
        }

    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email); // Assumes UserRepository has this method
     }
    public long countStudents(){
        return userRepository.countByRole("STUDENT");
    }


}