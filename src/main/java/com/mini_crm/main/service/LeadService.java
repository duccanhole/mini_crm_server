package com.mini_crm.main.service;

import com.mini_crm.main.dto.event.LeadCreated;
import com.mini_crm.main.dto.event.LeadUpdated;
import com.mini_crm.main.model.Lead;
import com.mini_crm.main.repository.LeadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LeadService {

    @Autowired
    private LeadRepository leadRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    // Create
    public Lead createLead(Lead lead) {
        Lead newLead = leadRepository.save(lead);
        if (newLead.getAssignedTo().getId() != null) {
            eventPublisher.publishEvent(new LeadCreated(newLead));
        }
        return lead;
    }

    // Read all with filter, sort, pagination
    public org.springframework.data.domain.Page<Lead> getLeads(
            String status,
            Long customerId,
            Long assignedToId,
            int page,
            int size,
            String sortBy,
            String sortDir) {
        org.springframework.data.domain.Sort sort = sortDir.equalsIgnoreCase("desc")
                ? org.springframework.data.domain.Sort.by(sortBy).descending()
                : org.springframework.data.domain.Sort.by(sortBy).ascending();

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size,
                sort);

        org.springframework.data.jpa.domain.Specification<Lead> spec = (root, query, criteriaBuilder) -> {
            java.util.List<jakarta.persistence.criteria.Predicate> predicates = new java.util.ArrayList<>();

            if (status != null && !status.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            if (customerId != null) {
                predicates.add(criteriaBuilder.equal(root.get("customer").get("id"), customerId));
            }
            if (assignedToId != null) {
                predicates.add(criteriaBuilder.equal(root.get("assignedTo").get("id"), assignedToId));
            }

            return criteriaBuilder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        return leadRepository.findAll(spec, pageable);
    }

    // Read by ID
    public Optional<Lead> getLeadById(Long id) {
        return leadRepository.findById(id);
    }

    // Update
    public Lead updateLead(Long id, Lead leadDetails) {
        Optional<Lead> leadOptional = leadRepository.findById(id);
        if (leadOptional.isPresent()) {
            Lead existingLead = leadOptional.get();
            existingLead.setCustomer(leadDetails.getCustomer());
            existingLead.setValue(leadDetails.getValue());
            existingLead.setStatus(leadDetails.getStatus());
            existingLead.setAssignedTo(leadDetails.getAssignedTo());
            existingLead.setExpectedCloseDate(leadDetails.getExpectedCloseDate());
            existingLead.setCreatedBy(leadDetails.getCreatedBy());
            // createdAt is not updated
            // updatedAt is handled by @PreUpdate in Model
            Lead leadSave = leadRepository.save(existingLead);
            if (leadSave.getCreatedBy().getId() != null) {
                eventPublisher.publishEvent(new LeadUpdated(leadSave));
            }
            return leadSave;
        }
        return null; // Or throw exception
    }

    // Delete
    public boolean deleteLead(Long id) {
        if (leadRepository.existsById(id)) {
            leadRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
