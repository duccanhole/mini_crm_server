package com.mini_crm.main.dto.event;

import com.mini_crm.main.model.Customer;

public class CustomerCreated {
    private Customer customer;

    public CustomerCreated(Customer customer) {
        this.customer = customer;
    }

    public Customer getCustomer() {
        return customer;
    }
}
