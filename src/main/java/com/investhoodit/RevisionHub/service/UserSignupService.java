package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.dto.UserDTO;
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

    public User signUp(UserDTO userDTO) throws Exception {
       try {
           if (userRepository.findByEmail(userDTO.getEmail()).isEmpty()) {
               User user = new User();
               user.setFirstName(userDTO.getFirstName());
               user.setLastName(userDTO.getLastName());
               user.setIdNumber(userDTO.getIdNumber());
               user.setEmail(userDTO.getEmail());
               user.setPassword(userDTO.getPassword());
               user.setPhoneNumber(userDTO.getPhoneNumber());
               user.setRole(userDTO.getRole());
               user.setFirstLogin(true);
               user.setPassword(passwordEncoderService.encodePassword(user.getPassword()));
               return userRepository.save(user);
           }
       }catch (Exception e){
           throw new Exception("User already exists");
       }
        return null;
    }
}
