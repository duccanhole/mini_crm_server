package com.mini_crm.main.dto.event;

import com.mini_crm.main.model.Customer;
import org.springframework.context.ApplicationEvent;

public class CustomerCreated extends ApplicationEvent {
    private Customer customer;

    public CustomerCreated(Customer customer) {
        super(customer);
        this.customer = customer;
    }

    public Customer getCustomer() {
        return customer;
    }
}
