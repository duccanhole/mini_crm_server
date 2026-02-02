package com.mini_crm.main.dto.event;

import org.springframework.context.ApplicationEvent;

import com.mini_crm.main.model.Lead;

public class LeadUpdated extends ApplicationEvent {
    private Lead lead;

    public LeadUpdated(Lead lead) {
        super(lead);
        this.lead = lead;
    }

    public Lead getLead() {
        return lead;
    }
}
