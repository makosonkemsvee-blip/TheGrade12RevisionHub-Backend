package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.model.Notification;
import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.repository.NotificationRepository;
import com.investhoodit.RevisionHub.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private final NotificationRepository repository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(NotificationRepository repository, UserRepository userRepository, SimpMessagingTemplate messagingTemplate) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public Notification createNotification(String userId, String message, String type) {
        logger.info("Creating notification for userId: {}, message: {}, type: {}", userId, message, type);
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setMessage(message);
        notification.setType(type);
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        Notification savedNotification = repository.save(notification);
        logger.info("Saved notification with id: {}", savedNotification.getId());
        messagingTemplate.convertAndSend("/topic/notifications/" + userId, savedNotification);
        return savedNotification;
    }

    public List<Notification> getUnreadNotifications(String userId) {
        logger.info("Fetching unread notifications for userId: {}", userId);
        return repository.findByUserIdAndIsReadFalse(userId);
    }

    public void sendBirthdayNotifications() {
        LocalDate today = LocalDate.now();
        String currentMonthDay = String.format("%02d%02d", today.getMonthValue(), today.getDayOfMonth());
        logger.info("Checking birthdays for date: {}", currentMonthDay);

        List<User> users = userRepository.findAll();
        logger.info("Found {} users in database", users.size());
        for (User user : users) {
            String idNumber = user.getIdNumber();
            if (idNumber != null && idNumber.length() >= 6 && idNumber.matches("\\d+")) {
                String birthMonthDay = idNumber.substring(2, 6);
                logger.info("User {} has idNumber: {}, extracted MMDD: {}", user.getId(), idNumber, birthMonthDay);
                if (birthMonthDay.equals(currentMonthDay)) {
                    String message = user.getFirstName() != null
                            ? "Happy Birthday, " + user.getFirstName() + " " + user.getLastName() + "! Wishing you a fantastic year ahead!"
                            : "Happy Birthday! Wishing you a fantastic year ahead!";
                    createNotification(user.getId().toString(), message, "BIRTHDAY");
                }
            } else {
                logger.warn("Invalid idNumber for user {}: {}", user.getId(), idNumber);
            }
        }
    }
}