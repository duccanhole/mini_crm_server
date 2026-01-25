package com.mini_crm.main.service;

import com.mini_crm.main.model.Customer;
import com.mini_crm.main.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    // Create
    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    // Read all
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    // Read by ID
    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    // Update
    public Customer updateCustomer(Long id, Customer customerDetails) {
        Optional<Customer> customerOptional = customerRepository.findById(id);
        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();
            customer.setName(customerDetails.getName());
            customer.setPhone(customerDetails.getPhone());
            customer.setEmail(customerDetails.getEmail());
            customer.setCompany(customerDetails.getCompany());
            customer.setNotes(customerDetails.getNotes());
            customer.setSaleId(customerDetails.getSaleId());
            // CreatedAt is typically not updated
            return customerRepository.save(customer);
        }
        return null;
    }

    // Delete
    public boolean deleteCustomer(Long id) {
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
