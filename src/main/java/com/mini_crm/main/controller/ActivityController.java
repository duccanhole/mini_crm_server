package com.mini_crm.main.controller;

import com.mini_crm.main.model.Activity;
import com.mini_crm.main.service.ActivityService;
import com.mini_crm.main.dto.SuccessResponse;
import com.mini_crm.main.dto.ErrorResponse;
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

    // Create - POST /api/activities
    @PostMapping
    public ResponseEntity<?> createActivity(@RequestBody Activity activity) {
        try {
            Activity createdActivity = activityService.createActivity(activity);
            return new ResponseEntity<>(
                    new SuccessResponse<>("Activity created successfully", HttpStatus.CREATED.value(), createdActivity),
                    HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse("Failed to create activity: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
        }
    }

    // Read All - GET /api/activities
    @GetMapping
    public ResponseEntity<?> getActivities(
            @RequestParam(required = false) Long leadId,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Page<Activity> activities = activityService.getActivities(leadId, type, page, size, sortBy, sortDir);
        return new ResponseEntity<>(new SuccessResponse<>(activities), HttpStatus.OK);
    }

    // Read by ID - GET /api/activities/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getActivityById(@PathVariable Long id) {
        Optional<Activity> activity = activityService.getActivityById(id);
        if (activity.isPresent()) {
            return new ResponseEntity<>(new SuccessResponse<>(activity.get()), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ErrorResponse("Activity not found", HttpStatus.NOT_FOUND.value()),
                HttpStatus.NOT_FOUND);
    }

    // Update - PUT /api/activities/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> updateActivity(@PathVariable Long id, @RequestBody Activity activityDetails) {
        Activity updatedActivity = activityService.updateActivity(id, activityDetails);
        if (updatedActivity != null) {
            return new ResponseEntity<>(new SuccessResponse<>(updatedActivity), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ErrorResponse("Activity not found", HttpStatus.NOT_FOUND.value()),
                HttpStatus.NOT_FOUND);
    }

    // Delete - DELETE /api/activities/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteActivity(@PathVariable Long id) {
        boolean deleted = activityService.deleteActivity(id);
        if (deleted) {
            return new ResponseEntity<>(
                    new SuccessResponse<>("Activity deleted successfully", HttpStatus.OK.value(), null),
                    HttpStatus.OK);
        }
        return new ResponseEntity<>(new ErrorResponse("Activity not found", HttpStatus.NOT_FOUND.value()),
                HttpStatus.NOT_FOUND);
    }
}
