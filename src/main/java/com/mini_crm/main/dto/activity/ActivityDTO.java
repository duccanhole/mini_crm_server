package com.mini_crm.main.dto.activity;

public class ActivityDTO {
    private String type;
    private String description;
    private Long leadId;
    private Long createdById;

    public ActivityDTO() {
    }

    public ActivityDTO(String type, String description, Long leadId, Long createdById) {
        this.type = type;
        this.description = description;
        this.leadId = leadId;
        this.createdById = createdById;
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

    public Long getCreatedById() {
        return createdById;
    }

    public void setCreatedById(Long createdById) {
        this.createdById = createdById;
    }

    @Override
    public String toString() {
        return "ActivityDTO{" +
                "type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", leadId=" + leadId +
                ", createdById=" + createdById +
                '}';
    }
}
