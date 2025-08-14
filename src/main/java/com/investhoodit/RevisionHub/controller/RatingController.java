package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/api/user")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @PostMapping("/rate-app")
    public ResponseEntity<?> rateApp(@AuthenticationPrincipal UserDetails userDetails,
                                     @RequestBody Map<String, String> body) {
        String username = userDetails.getUsername();
        String rating = body.get("rating");

        boolean saved = ratingService.saveRating(username, rating);
        if (saved) {
            return ResponseEntity.ok(Map.of("success", true, "message", "Rating saved successfully"));
        }
        return ResponseEntity.status(400).body(Map.of("success", false, "message", "Failed to save rating"));
    }

    @GetMapping("/has-rated")
    public ResponseEntity<Boolean> hasRated() {
//        String username = userDetails.getUsername();
        boolean hasRated = ratingService.hasUserRated();
        return ResponseEntity.ok().body(hasRated);
    }
}
