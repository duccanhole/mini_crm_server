package com.mini_crm.main.controller;

import com.mini_crm.main.model.Notification;
import com.mini_crm.main.service.NotificationService;
import com.mini_crm.main.dto.SuccessResponse;
import com.mini_crm.main.dto.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*", maxAge = 3600)
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // Create - POST /api/notifications (Optional, mostly for testing or internal
    // use)
    @PostMapping
    public ResponseEntity<?> createNotification(@RequestBody Notification notification) {
        try {
            Notification createdNotification = notificationService.createNotification(notification);
            return new ResponseEntity<>(
                    new SuccessResponse<>("Notification created successfully", HttpStatus.CREATED.value(),
                            createdNotification),
                    HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse("Failed to create notification: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
        }
    }

    // Get Notifications by User ID - GET /api/notifications
    @GetMapping
    public ResponseEntity<?> getNotifications(
            @RequestParam Long userId,
            @RequestParam(required = false) Boolean isRead,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Page<Notification> notifications = notificationService.getNotifications(userId, isRead, page, size, sortBy,
                sortDir);
        return new ResponseEntity<>(new SuccessResponse<>(notifications), HttpStatus.OK);
    }

    // Count Unread - GET /api/notifications/count-unread
    @GetMapping("/count-unread")
    public ResponseEntity<?> countUnread(@RequestParam Long userId) {
        long count = notificationService.countUnread(userId);
        return new ResponseEntity<>(new SuccessResponse<>(Map.of("count", count)), HttpStatus.OK);
    }

    // Mark as Read (Single) - PUT /api/notifications/{id}/read
    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id, @RequestParam Long userId) {
        boolean success = notificationService.markAsRead(id, userId);
        if (success) {
            return new ResponseEntity<>(
                    new SuccessResponse<>("Notification marked as read", HttpStatus.OK.value(), null), HttpStatus.OK);
        }
        return new ResponseEntity<>(
                new ErrorResponse("Notification not found or access denied", HttpStatus.NOT_FOUND.value()),
                HttpStatus.NOT_FOUND);
    }

    // Mark All as Read - PUT /api/notifications/read-all
    @PutMapping("/read-all")
    public ResponseEntity<?> markAllAsRead(@RequestParam Long userId) {
        boolean success = notificationService.markAllAsRead(userId);
        if (success) {
            return new ResponseEntity<>(
                    new SuccessResponse<>("All notifications marked as read", HttpStatus.OK.value(), null),
                    HttpStatus.OK);
        }
        return new ResponseEntity<>(new ErrorResponse("User not found", HttpStatus.NOT_FOUND.value()),
                HttpStatus.NOT_FOUND);
    }
}
