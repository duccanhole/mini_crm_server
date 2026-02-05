package com.mini_crm.main.scheduler;

import com.mini_crm.main.service.NotificationService;

import tools.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.util.List;
import com.mini_crm.main.model.Lead;
import com.mini_crm.main.model.Notification;
import com.mini_crm.main.repository.LeadRepository;

@Component
public class NotificationScheduler {
    private static final Logger logger = LoggerFactory.getLogger(NotificationScheduler.class);

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private LeadRepository leadRepository;

    // Run every day at midnight to clean up old notifications (after 5 days)
    @Scheduled(cron = "0 0 0 * * *")
    // Run every minute for testing
    // @Scheduled(cron = "0 * * * * *")
    public void autoDeleteNotifications() {
        logger.info("Auto delete notifications");
        notificationService.deleteOldNotifications(5);
    }

    @Scheduled(cron = "0 0 * * * ?")
    public void runEveryHour() {
        logger.info("Checking for leads expiring in the next 1 hour");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime soon = now.plusHours(1);

        List<Lead> expiringLeads = leadRepository.findByExpectedCloseDateBetween(now,
                soon);

        for (Lead lead : expiringLeads) {
            if (lead.getAssignedTo() != null) {
                Notification notification = new Notification();
                notification.setUser(lead.getAssignedTo());
                notification.setType("LEAD_REMINDER");
                notification.setTitle("Lead Expiring Soon");
                notification.setMessage("Lead for customer " +
                        (lead.getCustomer() != null ? lead.getCustomer().getName() : "Unknown") +
                        " is expiring soon.");
                notification.setMetaData(new ObjectMapper().writeValueAsString(lead));

                notificationService.createNotification(notification);
                logger.info("Sent expiration notification for Lead ID: {} to User: {}", lead.getId(),
                        lead.getAssignedTo().getEmail());
            }
        }
    }
}
