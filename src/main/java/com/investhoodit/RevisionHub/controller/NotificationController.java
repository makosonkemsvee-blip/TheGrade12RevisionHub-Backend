package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.model.Notification;
import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.repository.UserRepository;
import com.investhoodit.RevisionHub.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/notifications")
public class NotificationController {
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
    private final NotificationService service;
    private final UserRepository userRepository;

    public NotificationController(NotificationService service, UserRepository userRepository) {
        this.service = service;
        this.userRepository = userRepository;
    }

    public record NotificationRequest(
            @NotBlank String userId, // Should match email or userId from User model
            @NotBlank String message,
            @NotBlank String type
    ) {}

    @PostMapping
    public ResponseEntity<Notification> createNotification(@Validated @RequestBody NotificationRequest request) {
        logger.info("Received request to create notification for userId: {}", request.userId());
        try {
            Long userId = Long.parseLong(request.userId());
            Notification notification = service.createNotification(userId, request.message(), request.type());
            return new ResponseEntity<>(notification, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            logger.error("Failed to create notification: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }



    @GetMapping("/{userId}")
    public ResponseEntity<List<Notification>> getNotifications(@PathVariable String userId) {
        logger.info("Fetching all notifications for userId: {}", userId);
        try {
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User currentUser = userRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + currentUserEmail));
            Long userIdLong = Long.parseLong(userId);
            if (!currentUser.getId().equals(userIdLong)) {
                logger.error("Unauthorized access to notifications for userId: {}", userId);
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            List<Notification> notifications = service.getAllNotifications(userIdLong);
            return new ResponseEntity<>(notifications, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            logger.error("Failed to fetch notifications: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    //read one
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable Long notificationId) {
        logger.info("Marking notification as read: {}", notificationId);
        try {
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User currentUser = userRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + currentUserEmail));
            service.markNotificationAsRead(notificationId, currentUser.getId());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            logger.error("Failed to mark notification as read: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //read all
    @PutMapping("/read/all/{userId}")
    public ResponseEntity<Void> markAllNotificationsAsRead(@PathVariable String userId) {
        logger.info("Marking all notifications as read for userId: {}", userId);
        try {
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User currentUser = userRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + currentUserEmail));
            Long userIdLong = Long.parseLong(userId);
            if (!currentUser.getId().equals(userIdLong)) {
                logger.error("Unauthorized access to mark all notifications for userId: {}", userId);
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            service.markAllNotificationsAsRead(userIdLong);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            logger.error("Failed to mark all notifications as read: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //Delete one
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Map<String, String>> deleteNotification(@PathVariable Long notificationId) {
        logger.info("Deleting notification with id: {}", notificationId);
        try {
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User currentUser = userRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + currentUserEmail));
            service.deleteNotification(notificationId, currentUser.getId());
            Map<String, String> response = Map.of("message", "Notification deleted", "type", "INFO");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            logger.error("Failed to delete notification: {}", e.getMessage());
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    //delete all notifications
    @DeleteMapping("/all/{userId}")
    public ResponseEntity<Map<String, String>> deleteAllNotificationsForUser(@PathVariable String userId) {
        logger.info("Deleting all notifications for userId: {}", userId);
        try {
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User currentUser = userRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + currentUserEmail));
            Long userIdLong = Long.parseLong(userId);
            if (!currentUser.getId().equals(userIdLong)) {
                logger.error("Unauthorized access to delete all notifications for userId: {}", userId);
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            service.deleteAllNotificationsForUser(userIdLong);
            Map<String, String> response = Map.of("message", "All notifications cleared", "type", "INFO");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            logger.error("Failed to delete all notifications: {}", e.getMessage());
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }
}