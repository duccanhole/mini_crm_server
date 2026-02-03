package com.mini_crm.main.dto.lead;

import java.time.LocalDate;

public class LeadDTO {
    private Long customerId;
    private Double value;
    private String status;
    private Long assignedToId;
    private LocalDate expectedCloseDate;
    // private Long createdById;

    public LeadDTO() {
    }

    public LeadDTO(Long customerId, Double value, String status, Long assignedToId, LocalDate expectedCloseDate) {
        this.customerId = customerId;
        this.value = value;
        this.status = status;
        this.assignedToId = assignedToId;
        this.expectedCloseDate = expectedCloseDate;
        // this.createdById = createdById;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getAssignedToId() {
        return assignedToId;
    }

    public void setAssignedToId(Long assignedToId) {
        this.assignedToId = assignedToId;
    }

    public LocalDate getExpectedCloseDate() {
        return expectedCloseDate;
    }

    public void setExpectedCloseDate(LocalDate expectedCloseDate) {
        this.expectedCloseDate = expectedCloseDate;
    }

    @Override
    public String toString() {
        return "LeadDTO{" +
                "customerId=" + customerId +
                ", value=" + value +
                ", status='" + status + '\'' +
                ", assignedToId=" + assignedToId +
                ", expectedCloseDate=" + expectedCloseDate +
                '}';
    }
}
