package com.mini_crm.main.service;

import com.mini_crm.main.model.Activity;
import com.mini_crm.main.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    // Create
    public Activity createActivity(Activity activity) {
        return activityRepository.save(activity);
    }

    // Read all with filter, sort, pagination
    public Page<Activity> getActivities(
            Long leadId,
            String type,
            int page,
            int size,
            String sortBy,
            String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Activity> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (leadId != null) {
                predicates.add(criteriaBuilder.equal(root.get("lead").get("id"), leadId));
            }
            if (type != null && !type.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("type"), type));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return activityRepository.findAll(spec, pageable);
    }

    // Read by ID
    public Optional<Activity> getActivityById(Long id) {
        return activityRepository.findById(id);
    }

    // Update
    public Activity updateActivity(Long id, Activity activityDetails) {
        Optional<Activity> activityOptional = activityRepository.findById(id);
        if (activityOptional.isPresent()) {
            Activity existingActivity = activityOptional.get();
            existingActivity.setType(activityDetails.getType());
            existingActivity.setDescription(activityDetails.getDescription());
            existingActivity.setLead(activityDetails.getLead());
            existingActivity.setCreatedBy(activityDetails.getCreatedBy());
            return activityRepository.save(existingActivity);
        }
        return null; // Or throw exception
    }

    // Delete
    public boolean deleteActivity(Long id) {
        if (activityRepository.existsById(id)) {
            activityRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
