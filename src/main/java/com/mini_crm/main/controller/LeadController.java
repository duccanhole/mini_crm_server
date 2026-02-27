package com.mini_crm.main.controller;

import com.mini_crm.main.model.Lead;
import com.mini_crm.main.model.Customer;
import com.mini_crm.main.model.User;
import com.mini_crm.main.service.LeadService;
import com.mini_crm.main.service.CustomerService;
import com.mini_crm.main.service.UserService;
import com.mini_crm.main.util.JwtTokenProvider;
import com.mini_crm.main.dto.SuccessResponse;
import com.mini_crm.main.dto.lead.LeadDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/leads")
@CrossOrigin(origins = "*", maxAge = 3600)
public class LeadController {
    private static final Logger logger = LoggerFactory.getLogger(LeadController.class);

    @Autowired
    private LeadService leadService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // Create - POST /api/leads
    @PostMapping
    public ResponseEntity<?> createLead(@RequestBody LeadDTO leadDTO, @RequestHeader("Authorization") String token) {
        Lead lead = new Lead();
        lead.setValue(leadDTO.getValue());
        lead.setStatus(leadDTO.getStatus());
        lead.setExpectedCloseDate(leadDTO.getExpectedCloseDate());

        if (leadDTO.getCustomerId() != null) {
            Optional<Customer> customer = customerService.getCustomerById(leadDTO.getCustomerId());
            if (customer.isPresent()) {
                lead.setCustomer(customer.get());
            } else {
                throw new com.mini_crm.main.exception.ResourceNotFoundException("Customer", "id",
                        leadDTO.getCustomerId());
            }
        }

        if (leadDTO.getAssignedToId() != null) {
            Optional<User> assignedTo = userService.getUserById(leadDTO.getAssignedToId());
            if (assignedTo.isPresent()) {
                lead.setAssignedTo(assignedTo.get());
            } else {
                throw new com.mini_crm.main.exception.ResourceNotFoundException("User", "id",
                        leadDTO.getAssignedToId());
            }
        }

        String email = jwtTokenProvider.getEmailFromToken(token);
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new com.mini_crm.main.exception.ResourceNotFoundException("User", "email", email));
        lead.setCreatedBy(user);

        Lead createdLead = leadService.createLead(lead);
        return new ResponseEntity<>(
                new SuccessResponse<>("Lead created successfully", HttpStatus.CREATED.value(), createdLead),
                HttpStatus.CREATED);
    }

    // Read All - GET /api/leads
    @GetMapping
    public ResponseEntity<?> getLeads(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Long assignedToId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        org.springframework.data.domain.Page<Lead> leads = leadService.getLeads(status, customerId, assignedToId,
                createdFrom, createdTo,
                page, size, sortBy, sortDir);
        return new ResponseEntity<>(new SuccessResponse<>(leads), HttpStatus.OK);
    }

    // Read by ID - GET /api/leads/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getLeadById(@PathVariable Long id) {
        Lead lead = leadService.getLeadById(id)
                .orElseThrow(() -> new com.mini_crm.main.exception.ResourceNotFoundException("Lead", "id", id));
        return new ResponseEntity<>(new SuccessResponse<>(lead), HttpStatus.OK);
    }

    // Update - PUT /api/leads/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> updateLead(@PathVariable Long id, @RequestBody LeadDTO leadDTO,
            @RequestHeader("Authorization") String token) {
        Lead lead = leadService.getLeadById(id)
                .orElseThrow(() -> new com.mini_crm.main.exception.ResourceNotFoundException("Lead", "id", id));
        if (leadDTO.getValue() != null)
            lead.setValue(leadDTO.getValue());
        if (leadDTO.getStatus() != null)
            lead.setStatus(leadDTO.getStatus());
        if (leadDTO.getExpectedCloseDate() != null)
            lead.setExpectedCloseDate(leadDTO.getExpectedCloseDate());

        if (leadDTO.getCustomerId() != null) {
            Optional<Customer> customer = customerService.getCustomerById(leadDTO.getCustomerId());
            if (customer.isPresent()) {
                lead.setCustomer(customer.get());
            } else {
                throw new com.mini_crm.main.exception.ResourceNotFoundException("Customer", "id",
                        leadDTO.getCustomerId());
            }
        }

        if (leadDTO.getAssignedToId() != null) {
            Optional<User> assignedTo = userService.getUserById(leadDTO.getAssignedToId());
            if (assignedTo.isPresent()) {
                lead.setAssignedTo(assignedTo.get());
            } else {
                throw new com.mini_crm.main.exception.ResourceNotFoundException("User", "id",
                        leadDTO.getAssignedToId());
            }
        }

        String email = jwtTokenProvider.getEmailFromToken(token);
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new com.mini_crm.main.exception.ResourceNotFoundException("User", "email", email));
        Lead updatedLead = leadService.updateLead(id, lead, user);
        if (updatedLead == null) {
            throw new com.mini_crm.main.exception.ResourceNotFoundException("Lead", "id", id);
        }
        return new ResponseEntity<>(new SuccessResponse<>(), HttpStatus.OK);
    }

    // Delete - DELETE /api/leads/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLead(@PathVariable Long id) {
        boolean deleted = leadService.deleteLead(id);
        if (!deleted) {
            throw new com.mini_crm.main.exception.ResourceNotFoundException("Lead", "id", id);
        }
        return new ResponseEntity<>(
                new SuccessResponse<>(),
                HttpStatus.OK);
    }

    @GetMapping("/count")
    public ResponseEntity<?> getLeadCount(@RequestParam(required = false) String status,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Long assignedToId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdTo) {
        long count = leadService.getLeadCount(status, customerId, assignedToId, createdFrom, createdTo);
        return new ResponseEntity<>(new SuccessResponse<>(count), HttpStatus.OK);
    }

    @GetMapping("/value")
    public ResponseEntity<?> getLeadValue(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Long assignedToId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdTo) {
        double totalValue = leadService.getLeadValue(status, customerId, assignedToId, createdFrom, createdTo);
        return new ResponseEntity<>(new SuccessResponse<>(totalValue), HttpStatus.OK);
    }

}
