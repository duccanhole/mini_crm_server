package com.mini_crm.main.controller;

import com.mini_crm.main.model.Activity;
import com.mini_crm.main.model.Lead;
import com.mini_crm.main.model.User;
import com.mini_crm.main.service.ActivityService;
import com.mini_crm.main.service.LeadService;
import com.mini_crm.main.service.UserService;
import com.mini_crm.main.util.JwtTokenProvider;
import com.mini_crm.main.dto.SuccessResponse;
import com.mini_crm.main.dto.activity.ActivityDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/activities")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private LeadService leadService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // Create - POST /api/activities
    @PostMapping
    public ResponseEntity<?> createActivity(@RequestBody ActivityDTO activityDTO,
            @RequestHeader("Authorization") String token) {
        Activity activity = new Activity();
        activity.setType(activityDTO.getType());
        activity.setDescription(activityDTO.getDescription());

        if (activityDTO.getLeadId() != null) {
            Optional<Lead> lead = leadService.getLeadById(activityDTO.getLeadId());
            if (lead.isPresent()) {
                activity.setLead(lead.get());
            } else {
                throw new com.mini_crm.main.exception.ResourceNotFoundException("Lead", "id", activityDTO.getLeadId());
            }
        }

        // if (activityDTO.getCreatedById() != null) {
        // Optional<User> createdBy =
        // userService.getUserById(activityDTO.getCreatedById());
        // if (createdBy.isPresent()) {
        // activity.setCreatedBy(createdBy.get());
        // } else {
        // throw new com.mini_crm.main.exception.ResourceNotFoundException("User", "id",
        // activityDTO.getCreatedById());
        // }
        // }

        String email = jwtTokenProvider.getEmailFromToken(token);
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new com.mini_crm.main.exception.ResourceNotFoundException("User", "email", email));
        activity.setCreatedBy(user);

        Activity createdActivity = activityService.createActivity(activity);
        return new ResponseEntity<>(
                new SuccessResponse<>("Activity created successfully", HttpStatus.CREATED.value(), createdActivity),
                HttpStatus.CREATED);
    }

    // Read All - GET /api/activities
    @GetMapping
    public ResponseEntity<?> getActivities(
            @RequestParam(required = false) Long leadId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long createdById,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Page<Activity> activities = activityService.getActivities(leadId, type, createdById, page, size, sortBy, sortDir);
        return new ResponseEntity<>(new SuccessResponse<>(activities), HttpStatus.OK);
    }

    // Read by ID - GET /api/activities/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getActivityById(@PathVariable Long id) {
        Activity activity = activityService.getActivityById(id)
                .orElseThrow(() -> new com.mini_crm.main.exception.ResourceNotFoundException("Activity", "id", id));
        return new ResponseEntity<>(new SuccessResponse<>(activity), HttpStatus.OK);
    }

    // Update - PUT /api/activities/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> updateActivity(@PathVariable Long id, @RequestBody ActivityDTO activityDTO) {
        Activity activity = activityService.getActivityById(id)
                .orElseThrow(() -> new com.mini_crm.main.exception.ResourceNotFoundException("Activity", "id", id));
        if (activityDTO.getType() != null)
            activity.setType(activityDTO.getType());
        if (activityDTO.getDescription() != null)
            activity.setDescription(activityDTO.getDescription());

        if (activityDTO.getLeadId() != null) {
            Optional<Lead> lead = leadService.getLeadById(activityDTO.getLeadId());
            if (lead.isPresent()) {
                activity.setLead(lead.get());
            } else {
                throw new com.mini_crm.main.exception.ResourceNotFoundException("Lead", "id", activityDTO.getLeadId());
            }
        }

        Activity updatedActivity = activityService.updateActivity(id, activity);
        if (updatedActivity == null) {
            throw new com.mini_crm.main.exception.ResourceNotFoundException("Activity", "id", id);
        }
        return new ResponseEntity<>(new SuccessResponse<>(), HttpStatus.OK);
    }

    // Delete - DELETE /api/activities/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteActivity(@PathVariable Long id) {
        boolean deleted = activityService.deleteActivity(id);
        if (!deleted) {
            throw new com.mini_crm.main.exception.ResourceNotFoundException("Activity", "id", id);
        }
        return new ResponseEntity<>(
                new SuccessResponse<>(),
                HttpStatus.OK);
    }
}
