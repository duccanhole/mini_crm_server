package com.mini_crm.main.service;

import com.mini_crm.main.model.Notification;
import com.mini_crm.main.model.User;
import com.mini_crm.main.repository.NotificationRepository;
import com.mini_crm.main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    // Create
    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
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
                predicates.add(criteriaBuilder.equal(root.get("userId").get("id"), userId));
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
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return notificationRepository.countByUserIdAndIsReadFalse(user.get());
        }
        return 0;
    }

    // Mark single notification as read
    public boolean markAsRead(Long id, Long userId) {
        Optional<User> user = userRepository.findById(userId);
        Optional<Notification> notification = notificationRepository.findById(id);

        if (user.isPresent() && notification.isPresent()) {
            Notification notif = notification.get();
            if (notif.getUser().getId().equals(userId)) {
                notif.setRead(true);
                notificationRepository.save(notif);
                return true;
            }
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

    public void createNotification(User user, String type, String title, String message) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setRead(false);
        notificationRepository.save(notification);
    }
}
