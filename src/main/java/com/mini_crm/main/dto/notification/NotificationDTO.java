package com.mini_crm.main.dto.notification;

public class NotificationDTO {
    private Long userId;
    private String type;
    private String title;
    private String message;
    private String metaData;
    private Boolean isRead;

    public NotificationDTO() {
    }

    public NotificationDTO(Long userId, String type, String title, String message, String metaData, Boolean isRead) {
        this.userId = userId;
        this.type = type;
        this.title = title;
        this.message = message;
        this.metaData = metaData;
        this.isRead = isRead;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMetaData() {
        return metaData;
    }

    public void setMetaData(String metaData) {
        this.metaData = metaData;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }
}
