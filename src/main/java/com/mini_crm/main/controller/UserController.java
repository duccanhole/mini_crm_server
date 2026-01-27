package com.mini_crm.main.controller;

import com.mini_crm.main.model.User;
import com.mini_crm.main.service.UserService;
import com.mini_crm.main.dto.SuccessResponse;
import com.mini_crm.main.dto.ErrorResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @Autowired
    private UserService userService;

    // Create - POST /api/users
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        Optional<User> userByEmail = userService.getUserByEmail(user.getEmail());
        if (userByEmail.isPresent()) {
            return new ResponseEntity<>(
                    new ErrorResponse("Email is already exist",
                            HttpStatus.BAD_REQUEST.value()),
                    HttpStatus.BAD_REQUEST);
        }
        Optional<User> userByPhoneNumber = userService.getUserByPhoneNumber(user.getPhoneNumber());
        if (userByPhoneNumber.isPresent()) {
            return new ResponseEntity<>(new ErrorResponse("Phone number is already exist",
                    HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
        }

        user.setPassword(userService.hashPassword(user.getPassword()));
        // User createdUser = userService.createUser(user);
        return new ResponseEntity<>(
                new SuccessResponse<>("User created successfully", HttpStatus.CREATED.value(), null),
                HttpStatus.CREATED);
    }

    // Read All - GET /api/users
    @GetMapping
    public ResponseEntity<?> getUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        org.springframework.data.domain.Page<User> users = userService.getUsers(search, role, status, page, size,
                sortBy, sortDir);
        return new ResponseEntity<>(new SuccessResponse<>(users), HttpStatus.OK);
    }

    // Read by ID - GET /api/users/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            return new ResponseEntity<>(new SuccessResponse<>(user.get()), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ErrorResponse("User not found", HttpStatus.NOT_FOUND.value()),
                HttpStatus.NOT_FOUND);
    }

    // Update - PUT /api/users/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        userDetails.setPassword(userService.hashPassword(userDetails.getPassword()));
        User updatedUser = userService.updateUser(id, userDetails);
        if (updatedUser != null) {
            return new ResponseEntity<>(new SuccessResponse<>("User updated successfully", HttpStatus.OK.value(), null),
                    HttpStatus.OK);
        }
        return new ResponseEntity<>(new ErrorResponse("User not found", HttpStatus.NOT_FOUND.value()),
                HttpStatus.NOT_FOUND);
    }

    // Delete - DELETE /api/users/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.deleteUser(id);
        if (deleted) {
            return new ResponseEntity<>(new SuccessResponse<>("User deleted successfully", HttpStatus.OK.value(), null),
                    HttpStatus.OK);
        }
        return new ResponseEntity<>(new ErrorResponse("User not found", HttpStatus.NOT_FOUND.value()),
                HttpStatus.NOT_FOUND);
    }
}
