package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.model.Notification;
import com.investhoodit.RevisionHub.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotBlank; // Updated import
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    // DTO for request
    public record NotificationRequest(
            @NotBlank String userId,
            @NotBlank String message,
            @NotBlank String type
    ) {}

    @PostMapping
    public ResponseEntity<Notification> createNotification(@Validated @RequestBody NotificationRequest request) {
        logger.info("Received request to create notification for userId: {}", request.userId());
        try {
            Notification notification = service.createNotification(
                    request.userId(), request.message(), request.type());
            return new ResponseEntity<>(notification, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            logger.error("Failed to create notification: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Notification>> getUnreadNotifications(@PathVariable String userId) {
        logger.info("Fetching unread notifications for userId: {}", userId);
        try {
            List<Notification> notifications = service.getUnreadNotifications(userId);
            return new ResponseEntity<>(notifications, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            logger.error("Failed to fetch unread notifications: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable Long notificationId) {
        logger.info("Marking notification as read: {}", notificationId);
        try {
            service.markNotificationAsRead(notificationId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            logger.error("Failed to mark notification as read: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/read/all/{userId}")
    public ResponseEntity<Void> markAllNotificationsAsRead(@PathVariable String userId) {
        logger.info("Marking all notifications as read for userId: {}", userId);
        try {
            service.markAllNotificationsAsRead(userId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            logger.error("Failed to mark all notifications as read: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Map<String, String>> deleteNotification(@PathVariable Long notificationId) {
        logger.info("Deleting notification with id: {}", notificationId);
        try {
            service.deleteNotification(notificationId);
            Map<String, String> response = Map.of("message", "Notification deleted", "type", "INFO");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            logger.error("Failed to delete notification: {}", e.getMessage());
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/all/{userId}")
    public ResponseEntity<Map<String, String>> deleteAllNotificationsForUser(@PathVariable String userId) {
        logger.info("Deleting all notifications for userId: {}", userId);
        try {
            service.deleteAllNotificationsForUser(userId);
            Map<String, String> response = Map.of("message", "All notifications cleared", "type", "INFO");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            logger.error("Failed to delete all notifications: {}", e.getMessage());
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }
}