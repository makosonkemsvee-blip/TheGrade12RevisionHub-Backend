package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ProfileManagementService {

    private final UserRepository userRepository;

    public ProfileManagementService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Lana we will be updating the user first name and last name based on the logged-in user
    public void updateUserFullName(User user, HttpSession session) {
        // Retrieve the logged-in user from the session
        User loggedInUser = (User)session.getAttribute("loggedInUser");
        // Get the new details from the client side
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        // Update the details with new details from the client side
        loggedInUser.setFirstName(firstName);
        loggedInUser.setLastName(lastName);
        // Update the database
        userRepository.save(loggedInUser);
    }

    // Lana we are updating the user phone number based on the logged in use
    public void updateUserPhoneNumber(User user, HttpSession session) {
        // Retrieve the logged-in user from the session
        User loggedInUser = (User)session.getAttribute("loggedInUser");
        // Get the new details from the client side
        String phoneNumber = user.getPhoneNumber();
        // Update the details with new details from the client side
        loggedInUser.setPhoneNumber(phoneNumber);
        // Update the database
        userRepository.save(loggedInUser);

    }

    // So lana we are updating the user profile picture
    public void updateUserProfilePicture(MultipartFile profilePicture,HttpSession session) throws IOException {
        // Retrieve the logged-in user from the session
        User loggedInUser = (User)session.getAttribute("loggedInUser");
        // Get the new details from the client side
        byte[] profilePictureBytes = profilePicture.getBytes();
        loggedInUser.setProfilePicture(profilePictureBytes);
        // Update the database
        userRepository.save(loggedInUser);
    }

    public ResponseEntity<byte[]> getProfilePicture(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || user.getProfilePicture() == null) {
            return ResponseEntity.notFound().build();
        }
        byte[] image = user.getProfilePicture();
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(image);
    }
}
