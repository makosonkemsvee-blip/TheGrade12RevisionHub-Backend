package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.model.Rating;
import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.repository.RatingRepository;
import com.investhoodit.RevisionHub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private UserRepository userRepository;

    public boolean saveRating(String username, String ratingValue) {
        User user = userRepository.findByEmail(username).orElse(null);
        if (user != null) {
            Rating rating = new Rating();
            rating.setUser(user);
            rating.setRatingValue(ratingValue);
            ratingRepository.save(rating);
            return true;
        }
        return false;
    }

    public boolean hasUserRated() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ratingRepository.existsByUserEmail(email);
    }
}
