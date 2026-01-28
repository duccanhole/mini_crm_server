package com.mini_crm.main.controller;

import com.mini_crm.main.model.Customer;
import com.mini_crm.main.service.CustomerService;
import com.mini_crm.main.dto.SuccessResponse;
import com.mini_crm.main.dto.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CustomerController {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerService customerService;

    // Create - POST /api/customers
    @PostMapping
    public ResponseEntity<?> createCustomer(@RequestBody com.mini_crm.main.dto.CustomerDTO customerDTO) {
        Optional<Customer> customerByEmail = customerService.findByEmail(customerDTO.getEmail());
        if (customerByEmail.isPresent()) {
            return new ResponseEntity<>(new ErrorResponse("Email is already exist", HttpStatus.BAD_REQUEST.value()),
                    HttpStatus.BAD_REQUEST);
        }
        Optional<Customer> customerByPhone = customerService.findByPhone(customerDTO.getPhone());
        if (customerByPhone.isPresent()) {
            return new ResponseEntity<>(
                    new ErrorResponse("Phone number is already exist", HttpStatus.BAD_REQUEST.value()),
                    HttpStatus.BAD_REQUEST);
        }
        Customer customer = new Customer();
        customer.setName(customerDTO.getName());
        customer.setPhone(customerDTO.getPhone());
        customer.setEmail(customerDTO.getEmail());
        customer.setCompany(customerDTO.getCompany());
        customer.setNotes(customerDTO.getNotes());

        if (customerDTO.getSaleId() != null) {
            com.mini_crm.main.model.User sale = new com.mini_crm.main.model.User();
            sale.setId(customerDTO.getSaleId());
            customer.setSaleId(sale);
        }

        Customer createdCustomer = customerService.createCustomer(customer);
        return new ResponseEntity<>(
                new SuccessResponse<>("Customer created successfully", HttpStatus.CREATED.value(), createdCustomer),
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
        Optional<Customer> customer = customerService.getCustomerById(id);
        if (customer.isPresent()) {
            return new ResponseEntity<>(new SuccessResponse<>(customer.get()), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ErrorResponse("Customer not found", HttpStatus.NOT_FOUND.value()),
                HttpStatus.NOT_FOUND);
    }

    // Update - PUT /api/customers/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable Long id, @RequestBody Customer customerDetails) {
        Optional<Customer> customerByEmail = customerService.findByEmail(customerDetails.getEmail());
        if (customerByEmail.isPresent()) {
            return new ResponseEntity<>(new ErrorResponse("Email is already exist", HttpStatus.BAD_REQUEST.value()),
                    HttpStatus.BAD_REQUEST);
        }
        Optional<Customer> customerByPhone = customerService.findByPhone(customerDetails.getPhone());
        if (customerByPhone.isPresent()) {
            return new ResponseEntity<>(
                    new ErrorResponse("Phone number is already exist", HttpStatus.BAD_REQUEST.value()),
                    HttpStatus.BAD_REQUEST);
        }
        Customer updatedCustomer = customerService.updateCustomer(id, customerDetails);
        if (updatedCustomer != null) {
            return new ResponseEntity<>(new SuccessResponse<>(updatedCustomer), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ErrorResponse("Customer not found", HttpStatus.NOT_FOUND.value()),
                HttpStatus.NOT_FOUND);
    }

    // Delete - DELETE /api/customers/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long id) {
        boolean deleted = customerService.deleteCustomer(id);
        if (deleted) {
            return new ResponseEntity<>(
                    new SuccessResponse<>("Customer deleted successfully", HttpStatus.OK.value(), null),
                    HttpStatus.OK);
        }
        return new ResponseEntity<>(new ErrorResponse("Customer not found", HttpStatus.NOT_FOUND.value()),
                HttpStatus.NOT_FOUND);
    }
}
