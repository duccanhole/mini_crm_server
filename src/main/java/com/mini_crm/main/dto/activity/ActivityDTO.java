package com.mini_crm.main.dto.activity;

public class ActivityDTO {
    private String type;
    private String description;
    private Long leadId;

    public ActivityDTO() {
    }

    public ActivityDTO(String type, String description, Long leadId) {
        this.type = type;
        this.description = description;
        this.leadId = leadId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getLeadId() {
        return leadId;
    }

    public void setLeadId(Long leadId) {
        this.leadId = leadId;
    }

    @Override
    public String toString() {
        return "ActivityDTO{" +
                "type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", leadId=" + leadId +
                '}';
    }
}
