package com.mini_crm.main.controller;

import com.mini_crm.main.model.Customer;
import com.mini_crm.main.model.User;
import com.mini_crm.main.service.CustomerService;
import com.mini_crm.main.service.UserService;
import com.mini_crm.main.dto.SuccessResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CustomerController {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerService customerService;

    @Autowired
    private UserService userService;

    // Create - POST /api/customers
    @PostMapping
    public ResponseEntity<?> createCustomer(@RequestBody com.mini_crm.main.dto.customer.CustomerDTO customerDTO) {
        Optional<Customer> customerByEmail = customerService.findByEmail(customerDTO.getEmail());
        if (customerByEmail.isPresent()) {
            throw new com.mini_crm.main.exception.BadRequestException("Email is already exist");
        }
        Optional<Customer> customerByPhone = customerService.findByPhone(customerDTO.getPhone());
        if (customerByPhone.isPresent()) {
            throw new com.mini_crm.main.exception.BadRequestException("Phone number is already exist");
        }
        Customer customer = new Customer();
        customer.setName(customerDTO.getName());
        customer.setPhone(customerDTO.getPhone());
        customer.setEmail(customerDTO.getEmail());
        customer.setCompany(customerDTO.getCompany());
        customer.setNotes(customerDTO.getNotes());

        if (customerDTO.getSaleId() != null) {
            Optional<User> sale = userService.getUserById(customerDTO.getSaleId());
            if (sale.isPresent()) {
                customer.setSale(sale.get());
            } else {
                throw new com.mini_crm.main.exception.ResourceNotFoundException("User", "id", customerDTO.getSaleId());
            }
        }

        customerService.createCustomer(customer);
        return new ResponseEntity<>(
                new SuccessResponse<>(),
                HttpStatus.CREATED);
    }

    // Read All - GET /api/customers
    @GetMapping
    public ResponseEntity<?> getCustomers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long saleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        org.springframework.data.domain.Page<Customer> customers = customerService.getCustomers(search, saleId, page,
                size, sortBy, sortDir);
        return new ResponseEntity<>(new SuccessResponse<>(customers), HttpStatus.OK);
    }

    // Read by ID - GET /api/customers/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable Long id) {
        Customer customer = customerService.getCustomerById(id)
                .orElseThrow(() -> new com.mini_crm.main.exception.ResourceNotFoundException("Customer", "id", id));
        return new ResponseEntity<>(new SuccessResponse<>(customer), HttpStatus.OK);
    }

    // Update - PUT /api/customers/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable Long id,
            @RequestBody com.mini_crm.main.dto.customer.CustomerDTO customerDTO) {
        Optional<Customer> customerByEmail = customerService.findByEmail(customerDTO.getEmail());
        if (customerByEmail.isPresent() && !customerByEmail.get().getId().equals(id)) {
            throw new com.mini_crm.main.exception.BadRequestException("Email is already exist");
        }
        Optional<Customer> customerByPhone = customerService.findByPhone(customerDTO.getPhone());
        if (customerByPhone.isPresent() && !customerByPhone.get().getId().equals(id)) {
            throw new com.mini_crm.main.exception.BadRequestException("Phone number is already exist");
        }
        Customer customer = customerService.getCustomerById(id)
                .orElseThrow(() -> new com.mini_crm.main.exception.ResourceNotFoundException("Customer", "id", id));
        if (customerDTO.getName() != null)
            customer.setName(customerDTO.getName());
        if (customerDTO.getPhone() != null)
            customer.setPhone(customerDTO.getPhone());
        if (customerDTO.getEmail() != null)
            customer.setEmail(customerDTO.getEmail());
        if (customerDTO.getCompany() != null)
            customer.setCompany(customerDTO.getCompany());
        if (customerDTO.getNotes() != null)
            customer.setNotes(customerDTO.getNotes());

        if (customerDTO.getSaleId() != null) {
            Optional<User> sale = userService.getUserById(customerDTO.getSaleId());
            if (sale.isPresent()) {
                customer.setSale(sale.get());
            } else {
                throw new com.mini_crm.main.exception.ResourceNotFoundException("User", "id", customerDTO.getSaleId());
            }
        }
        Customer updatedCustomer = customerService.updateCustomer(id, customer);
        if (updatedCustomer == null) {
            throw new com.mini_crm.main.exception.ResourceNotFoundException("Customer", "id", id);
        }
        return new ResponseEntity<>(new SuccessResponse<>(), HttpStatus.OK);
    }

    // Delete - DELETE /api/customers/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long id) {
        boolean deleted = customerService.deleteCustomer(id);
        if (!deleted) {
            throw new com.mini_crm.main.exception.ResourceNotFoundException("Customer", "id", id);
        }
        return new ResponseEntity<>(
                new SuccessResponse<>(),
                HttpStatus.OK);
    }

    @GetMapping("/count")
    public ResponseEntity<?> getCustomerCount(@RequestParam(required = false) String search,
            @RequestParam(required = false) Long saleId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdTo) {
        long count = customerService.getCustomerCount(search, saleId, createdFrom != null ? createdFrom.toLocalDate() : null, createdTo != null ? createdTo.toLocalDate() : null);
        return new ResponseEntity<>(new SuccessResponse<>(count), HttpStatus.OK);
    }

}
