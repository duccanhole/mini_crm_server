package com.mini_crm.main.service;

import com.mini_crm.main.dto.event.NotificationCreated;
import com.mini_crm.main.model.Notification;
import com.mini_crm.main.model.User;
import com.mini_crm.main.repository.NotificationRepository;
import com.mini_crm.main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    // Create
    public Notification createNotification(Notification notification) {
        Notification newNotification = notificationRepository.save(notification);
        eventPublisher.publishEvent(new NotificationCreated(newNotification));
        return newNotification;
    }

    // Get notifications by userId with pagination
    public Page<Notification> getNotifications(
            Long userId,
            Boolean isRead,
            int page,
            int size,
            String sortBy,
            String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Notification> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (userId != null) {
                predicates.add(criteriaBuilder.equal(root.get("user").get("id"), userId));
            }
            if (isRead != null) {
                predicates.add(criteriaBuilder.equal(root.get("isRead"), isRead));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return notificationRepository.findAll(spec, pageable);
    }

    // Count unread notifications
    public long countUnread(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    // Mark single notification as read
    public boolean markAsRead(Long id) {
        Optional<Notification> notification = notificationRepository.findById(id);

        if (notification.isPresent()) {
            Notification notif = notification.get();
            notif.setRead(true);
            notificationRepository.save(notif);
            return true;
        }
        return false;
    }

    // Mark all notifications as read for a user
    public boolean markAllAsRead(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            notificationRepository.markAllAsRead(user.get());
            return true;
        }
        return false;
    }

    // Delete notification by ID
    public boolean deleteNotification(Long id) {
        Optional<Notification> notification = notificationRepository.findById(id);

        if (notification.isPresent()) {
            Notification notif = notification.get();
            notificationRepository.delete(notif);
            return true;
        }
        return false;
    }

    public void deleteOldNotifications(Integer days) {
        LocalDateTime fiveDaysAgo = LocalDateTime.now().minusDays(days);
        notificationRepository.deleteByCreatedAtBefore(fiveDaysAgo);
    }
}
