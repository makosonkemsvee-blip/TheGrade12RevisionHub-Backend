package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.model.Notification;
import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.repository.NotificationRepository;
import com.investhoodit.RevisionHub.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Transactional
    public Notification createNotification(Long userId, String message, String type) {
        logger.info("Creating notification for userId: {}, message: {}, type: {}", userId, message, type);
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setMessage(message);
        notification.setType(type);
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        Notification savedNotification = repository.save(notification);
        logger.info("Saved notification with id: {}", savedNotification.getId());
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", savedNotification.getId());
        payload.put("userId", savedNotification.getUserId());
        payload.put("message", savedNotification.getMessage());
        payload.put("type", savedNotification.getType());
        payload.put("isRead", savedNotification.isRead());
        payload.put("createdAt", savedNotification.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        payload.put("senderName", savedNotification.getSenderName());
        payload.put("messageSnippet", savedNotification.getMessageSnippet());
        payload.put("groupName", savedNotification.getGroupName());
        messagingTemplate.convertAndSend("/topic/notifications/" + userId, payload);
        return savedNotification;
    }

    public List<Notification> getUnreadNotifications(Long userId) {
        logger.info("Fetching unread notifications for userId: {}", userId);
        return repository.findByUserIdAndIsReadFalse(userId);
    }

    public List<Notification> getAllNotifications(Long userId) {
        return repository.findByUserId(userId);
    }

    @Transactional
    public void markNotificationAsRead(Long notificationId) {
        logger.info("Marking notification with id: {} as read", notificationId);
        Notification notification = repository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found: " + notificationId));
        notification.setIsRead(true);
        repository.save(notification);
        logger.info("Notification {} marked as read", notificationId);
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", notification.getId());
        payload.put("isRead", notification.isRead());
        messagingTemplate.convertAndSend("/topic/notifications/" + notification.getUserId(), payload);
    }

    @Transactional
    public void markAllNotificationsAsRead(Long userId) {
        logger.info("Marking all notifications as read for userId: {}", userId);
        List<Notification> notifications = repository.findByUserId(userId);
        for (Notification notification : notifications) {
            notification.setIsRead(true);
            repository.save(notification);
        }
        logger.info("Marked {} notifications as read for userId: {}", notifications.size(), userId);
        Map<String, String> payload = new HashMap<>();
        payload.put("message", "All notifications marked as read");
        payload.put("type", "INFO");
        messagingTemplate.convertAndSend("/topic/notifications/" + userId, payload);
    }

    @Transactional
    public void deleteNotification(Long notificationId) {
        logger.info("Deleting notification with id: {}", notificationId);
        Notification notification = repository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found: " + notificationId));
        Long userId = notification.getUserId();
        repository.delete(notification);
        logger.info("Deleted notification with id: {}", notificationId);
        Map<String, String> payload = new HashMap<>();
        payload.put("message", "Notification deleted");
        payload.put("type", "INFO");
        payload.put("id", notificationId.toString());
        messagingTemplate.convertAndSend("/topic/notifications/" + userId, payload);
    }

    @Transactional
    public void deleteAllNotificationsForUser(Long userId) {
        logger.info("Deleting all notifications for userId: {}", userId);
        repository.deleteByUserId(userId);
        logger.info("Deleted all notifications for userId: {}", userId);
        Map<String, String> payload = new HashMap<>();
        payload.put("message", "All notifications cleared");
        payload.put("type", "INFO");
        messagingTemplate.convertAndSend("/topic/notifications/" + userId, payload);
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
                    createNotification(user.getId(), message, "BIRTHDAY");
                }
            } else {
                logger.warn("Invalid idNumber for user {}: {}", user.getId(), idNumber);
            }
        }
    }
}