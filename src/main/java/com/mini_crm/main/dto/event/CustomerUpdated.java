package com.mini_crm.main.dto.event;

import com.mini_crm.main.model.Customer;

public class CustomerUpdated {
    private Customer customer;

    public CustomerUpdated(Customer customer) {
        this.customer = customer;
    }

    public Customer getCustomer() {
        return customer;
    }
}
