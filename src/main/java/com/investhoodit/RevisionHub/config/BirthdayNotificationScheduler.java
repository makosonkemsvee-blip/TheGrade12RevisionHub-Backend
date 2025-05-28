package com.investhoodit.RevisionHub.config;

import com.investhoodit.RevisionHub.service.NotificationService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class BirthdayNotificationScheduler {
    private final NotificationService notificationService;

    public BirthdayNotificationScheduler(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void sendDailyBirthdayNotifications() {
        notificationService.sendBirthdayNotifications();
    }
}