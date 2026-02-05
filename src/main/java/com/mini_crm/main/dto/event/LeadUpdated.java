package com.mini_crm.main.dto.event;

import org.springframework.context.ApplicationEvent;

import com.mini_crm.main.model.Lead;
import com.mini_crm.main.model.User;

public class LeadUpdated extends ApplicationEvent {
    private Lead lead;
    private User updatedBy;

    public LeadUpdated(Lead lead, User updatedBy) {
        super(lead);
        this.lead = lead;
        this.updatedBy = updatedBy;
    }

    public Lead getLead() {
        return lead;
    }

    public User getUpdatedBy() {
        return updatedBy;
    }
}
