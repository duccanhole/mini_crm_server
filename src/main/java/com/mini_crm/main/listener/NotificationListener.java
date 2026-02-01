package com.mini_crm.main.listener;

import com.mini_crm.main.dto.event.CustomerCreated;
import com.mini_crm.main.dto.event.CustomerUpdated;
import com.mini_crm.main.model.Customer;
import com.mini_crm.main.model.Lead;
import com.mini_crm.main.model.Notification;
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
        notification.setMetaData(customer.toString());
        notificationService.createNotification(notification);
    }

    @EventListener
    public void handleCustomerUpdated(CustomerUpdated event) {
        Customer customer = event.getCustomer();
        logger.info("Customer updated: {}", customer);
        Notification notification = new Notification();
        notification.setUser(customer.getSale());
        notification.setType("CUSTOMER_UPDATED");
        notification.setTitle("Customer updated");
        notification.setMessage("You have been updated a customer");
        notification.setRead(false);

        notification.setMetaData(new ObjectMapper().writeValueAsString(customer));
        notificationService.createNotification(notification);
    }

    @org.springframework.transaction.event.TransactionalEventListener
    public void handleActivityCreated(Activity activity) {

    }

    @EventListener
    public void handleLeadCreated(Lead lead) {

    }

    @EventListener
    public void handleLeadUpdated(Lead lead) {

    }
}
