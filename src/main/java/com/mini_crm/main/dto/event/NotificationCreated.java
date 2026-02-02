package com.mini_crm.main.dto.event;

import org.springframework.context.ApplicationEvent;

import com.mini_crm.main.model.Notification;

public class NotificationCreated extends ApplicationEvent {
    private Notification notification;

    public NotificationCreated(Notification notification) {
        super(notification);
        this.notification = notification;
    }

    public Notification getNotification() {
        return notification;
    }
}
