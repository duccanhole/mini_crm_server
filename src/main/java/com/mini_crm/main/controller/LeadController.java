package com.mini_crm.main.controller;

import com.mini_crm.main.model.Lead;
import com.mini_crm.main.service.LeadService;
import com.mini_crm.main.dto.SuccessResponse;
import com.mini_crm.main.dto.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/leads")
@CrossOrigin(origins = "*", maxAge = 3600)
public class LeadController {

    @Autowired
    private LeadService leadService;

    // Create - POST /api/leads
    @PostMapping
    public ResponseEntity<?> createLead(@RequestBody Lead lead) {
        try {
            Lead createdLead = leadService.createLead(lead);
            return new ResponseEntity<>(
                    new SuccessResponse<>("Lead created successfully", HttpStatus.CREATED.value(), createdLead),
                    HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse("Failed to create lead: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
        }
    }

    // Read All - GET /api/leads
    @GetMapping
    public ResponseEntity<?> getLeads(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Long assignedToId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        org.springframework.data.domain.Page<Lead> leads = leadService.getLeads(status, customerId, assignedToId,
                page, size, sortBy, sortDir);
        return new ResponseEntity<>(new SuccessResponse<>(leads), HttpStatus.OK);
    }

    // Read by ID - GET /api/leads/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getLeadById(@PathVariable Long id) {
        Optional<Lead> lead = leadService.getLeadById(id);
        if (lead.isPresent()) {
            return new ResponseEntity<>(new SuccessResponse<>(lead.get()), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ErrorResponse("Lead not found", HttpStatus.NOT_FOUND.value()),
                HttpStatus.NOT_FOUND);
    }

    // Update - PUT /api/leads/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> updateLead(@PathVariable Long id, @RequestBody Lead leadDetails) {
        Lead updatedLead = leadService.updateLead(id, leadDetails);
        if (updatedLead != null) {
            return new ResponseEntity<>(new SuccessResponse<>(updatedLead), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ErrorResponse("Lead not found", HttpStatus.NOT_FOUND.value()),
                HttpStatus.NOT_FOUND);
    }

    // Delete - DELETE /api/leads/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLead(@PathVariable Long id) {
        boolean deleted = leadService.deleteLead(id);
        if (deleted) {
            return new ResponseEntity<>(new SuccessResponse<>("Lead deleted successfully", HttpStatus.OK.value(), null),
                    HttpStatus.OK);
        }
        return new ResponseEntity<>(new ErrorResponse("Lead not found", HttpStatus.NOT_FOUND.value()),
                HttpStatus.NOT_FOUND);
    }
}
