package com.mini_crm.main.listener;

import com.mini_crm.main.dto.event.CustomerCreated;
import com.mini_crm.main.dto.event.CustomerUpdated;
import com.mini_crm.main.model.Customer;
import com.mini_crm.main.model.Lead;
import com.mini_crm.main.model.Activity;
import com.mini_crm.main.service.NotificationService;

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

    @org.springframework.transaction.event.TransactionalEventListener
    public void handleCustomerAssigned(CustomerCreated event) {
        Customer customer = event.getCustomer();
        logger.info("Customer assigned: {}", customer);
        notificationService.createNotification(customer.getSale(), "CUSTOMER_ASSIGNED", "Customer assigned",
                "You have been assigned a new customer");
    }

    @org.springframework.transaction.event.TransactionalEventListener
    public void handleCustomerUpdated(CustomerUpdated event) {
        Customer customer = event.getCustomer();
        logger.info("Customer updated: {}", customer);
        notificationService.createNotification(customer.getSale(), "CUSTOMER_UPDATED", "Customer updated",
                "You have been updated a customer");
    }

    @EventListener
    public void handleActivityCreated(Activity activity) {

    }

    @EventListener
    public void handleLeadCreated(Lead lead) {

    }

    @EventListener
    public void handleLeadUpdated(Lead lead) {

    }
}
