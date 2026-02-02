package com.mini_crm.main.listener;

import com.mini_crm.main.dto.event.ActivityCreated;
import com.mini_crm.main.dto.event.CustomerCreated;
import com.mini_crm.main.dto.event.CustomerUpdated;
import com.mini_crm.main.dto.event.LeadCreated;
import com.mini_crm.main.dto.event.LeadUpdated;
import com.mini_crm.main.model.Customer;
import com.mini_crm.main.model.Lead;
import com.mini_crm.main.model.Notification;
import com.mini_crm.main.model.User;
import com.mini_crm.main.model.Activity;
import com.mini_crm.main.service.NotificationService;

import tools.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {
    private static final Logger logger = LoggerFactory.getLogger(NotificationListener.class);

    @Autowired
    private NotificationService notificationService;

    @EventListener
    public void handleCustomerAssigned(CustomerCreated event) {
        Customer customer = event.getCustomer();
        logger.info("Customer assigned: {}", customer);
        Notification notification = new Notification();
        notification.setUser(customer.getSale());
        notification.setType("CUSTOMER_ASSIGNED");
        notification.setTitle("Customer assigned");
        notification.setMessage("You have been assigned a new customer");
        notification.setRead(false);
        notification.setMetaData(new ObjectMapper().writeValueAsString(customer));
        notificationService.createNotification(notification);
    }

    @EventListener
    public void handleCustomerUpdated(CustomerUpdated event) {
        Customer customer = event.getCustomer();
        logger.info("Customer updated: {}", customer);
        Notification notification = new Notification();
        notification.setUser(customer.getSale());
        notification.setType("CUSTOMER_ASSIGNED");
        notification.setTitle("Customer assigned");
        notification.setMessage("You have been assigned a new customer");
        notification.setRead(false);

        notification.setMetaData(new ObjectMapper().writeValueAsString(customer));
        notificationService.createNotification(notification);
    }

    @EventListener
    public void handleActivityCreated(ActivityCreated event) {
        Activity activity = event.getActivity();
        logger.info("Listent activity created: {}", activity);
        Lead lead = activity.getLead();
        User createdBy = lead.getCreatedBy();
        if (createdBy.getRole().equals("manager") || createdBy.getRole().equals("admin")) {
            Notification notification = new Notification();
            notification.setUser(createdBy);
            notification.setType("ACTIVITY_CREATED");
            notification.setTitle("Activity created");
            notification.setMessage("Lead has been updated activity");
            notification.setRead(false);

            notification.setMetaData(new ObjectMapper().writeValueAsString(activity));
            notificationService.createNotification(notification);
        }
    }

    @EventListener
    public void handleLeadCreated(LeadCreated event) {
        Lead lead = event.getLead();
        User createdBy = lead.getCreatedBy();
        User sale = lead.getAssignedTo();
        if (createdBy.getId() != sale.getId()) {
            Notification notification = new Notification();
            notification.setUser(sale);
            notification.setType("LEAD_CREATED");
            notification.setTitle("Lead created");
            notification.setMessage("You have been assigned a new lead");
            notification.setRead(false);

            notification.setMetaData(new ObjectMapper().writeValueAsString(lead));
            notificationService.createNotification(notification);
        }
    }

    @EventListener
    public void handleLeadUpdated(LeadUpdated event) {
        Lead lead = event.getLead();
        User createdBy = lead.getCreatedBy();
        if (createdBy.getRole().equals("manager") || createdBy.getRole().equals("admin")) {
            Notification notification = new Notification();
            notification.setUser(createdBy);
            notification.setType("LEAD_UPDATED");
            notification.setTitle("Lead updated");
            notification.setMessage("Lead has been updated");
            notification.setRead(false);

            notification.setMetaData(new ObjectMapper().writeValueAsString(lead));
            notificationService.createNotification(notification);
        }
    }
}
