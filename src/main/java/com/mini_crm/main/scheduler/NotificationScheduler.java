package com.mini_crm.main.scheduler;

import com.mini_crm.main.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class NotificationScheduler {
    private static final Logger logger = LoggerFactory.getLogger(NotificationScheduler.class);

    @Autowired
    private NotificationService notificationService;

    // Run every day at midnight to clean up old notifications (after 5 days)
    @Scheduled(cron = "0 0 0 * * *")
    // Run every minute for testing
    // @Scheduled(cron = "0 * * * * *")
    public void autoDeleteNotifications() {
        logger.info("Auto delete notifications");
        notificationService.deleteOldNotifications(5);
    }
}
