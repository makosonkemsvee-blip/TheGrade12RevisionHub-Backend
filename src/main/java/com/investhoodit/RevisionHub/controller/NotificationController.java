package com.investhoodit.RevisionHub.controller;
import com.investhoodit.RevisionHub.model.Notification;
import com.investhoodit.RevisionHub.service.NotificationService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService service;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationController(NotificationService service, SimpMessagingTemplate messagingTemplate) {
        this.service = service;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping
    public Notification create(@RequestBody NotificationRequest request) {
        Notification notification = service.createNotification(request.userId(), request.message(), request.type());
        messagingTemplate.convertAndSend("/topic/notifications/" + request.userId(), notification);
        return notification;
    }

    @GetMapping("/{userId}")
    public List<Notification> getUnread(@PathVariable String userId) {
        return service.getUnreadNotifications(userId);
    }

    //temporary
    @PostMapping("/test-birthdays")
    public String testBirthdayNotifications() {
        service.sendBirthdayNotifications();
        return "Birthday notifications triggered";
    }
}

// DTO for request
record NotificationRequest(String userId, String message, String type) {}