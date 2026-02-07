package com.mini_crm.main.controller;

import com.mini_crm.main.model.User;
import com.mini_crm.main.service.UserService;
import com.mini_crm.main.util.JwtTokenProvider;
import com.mini_crm.main.dto.SuccessResponse;
import com.mini_crm.main.dto.user.UserChangePassword;
import com.mini_crm.main.dto.user.UserCreate;
import com.mini_crm.main.dto.user.UserUpdate;
import com.mini_crm.main.dto.ErrorResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {
        private static final Logger logger = LoggerFactory.getLogger(UserController.class);
        @Value("${password.default}")
        private String defaultPassword;

        @Autowired
        private UserService userService;

        @Autowired
        private JwtTokenProvider jwtTokenProvider;

        // Create - POST /api/users
        @PostMapping
        public ResponseEntity<?> createUser(@RequestBody UserCreate userCreate) {
                Optional<User> userByEmail = userService.getUserByEmail(userCreate.getEmail());
                if (userByEmail.isPresent()) {
                        return new ResponseEntity<>(
                                        new ErrorResponse("Email is already exist",
                                                        HttpStatus.BAD_REQUEST.value()),
                                        HttpStatus.BAD_REQUEST);
                }
                Optional<User> userByPhoneNumber = userService.getUserByPhoneNumber(userCreate.getPhoneNumber());
                if (userByPhoneNumber.isPresent()) {
                        return new ResponseEntity<>(new ErrorResponse("Phone number is already exist",
                                        HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
                }

                User user = new User();
                user.setName(userCreate.getName());
                user.setEmail(userCreate.getEmail());
                user.setPhoneNumber(userCreate.getPhoneNumber());
                user.setStatus(userCreate.getStatus());
                user.setRole(userCreate.getRole());

                user.setPassword(userService.hashPassword(userCreate.getPassword()));
                User createdUser = userService.createUser(user);
                return new ResponseEntity<>(
                                new SuccessResponse<>("User created successfully", HttpStatus.CREATED.value(),
                                                createdUser),
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
                org.springframework.data.domain.Page<User> users = userService.getUsers(search, role, status, page,
                                size,
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
        public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserUpdate userUpdate) {
                Optional<User> userByEmail = userService.getUserByEmail(userUpdate.getEmail());
                if (userByEmail.isPresent() && !userByEmail.get().getId().equals(id)) {
                        return new ResponseEntity<>(
                                        new ErrorResponse("Email is already exist",
                                                        HttpStatus.BAD_REQUEST.value()),
                                        HttpStatus.BAD_REQUEST);
                }
                Optional<User> userByPhoneNumber = userService.getUserByPhoneNumber(userUpdate.getPhoneNumber());
                if (userByPhoneNumber.isPresent() && !userByPhoneNumber.get().getId().equals(id)) {
                        return new ResponseEntity<>(new ErrorResponse("Phone number is already exist",
                                        HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
                }
                User user = userService.getUserById(id)
                                .orElseThrow(() -> new com.mini_crm.main.exception.ResourceNotFoundException("User",
                                                "id", id));
                if (userUpdate.getName() != null)
                        user.setName(userUpdate.getName());
                if (userUpdate.getEmail() != null)
                        user.setEmail(userUpdate.getEmail());
                if (userUpdate.getPhoneNumber() != null)
                        user.setPhoneNumber(userUpdate.getPhoneNumber());
                if (userUpdate.getStatus() != null)
                        user.setStatus(userUpdate.getStatus());
                if (userUpdate.getRole() != null)
                        user.setRole(userUpdate.getRole());
                User updatedUser = userService.updateUser(id, user);
                if (updatedUser != null) {
                        return new ResponseEntity<>(
                                        new SuccessResponse<>(),
                                        HttpStatus.OK);
                }
                return new ResponseEntity<>(new ErrorResponse("User not found", HttpStatus.NOT_FOUND.value()),
                                HttpStatus.NOT_FOUND);
        }

        // Change Password - PUT /api/users/{id}/change-password
        @PutMapping("/{id}/change-password")
        public ResponseEntity<?> changePassword(@PathVariable Long id,
                        @RequestBody UserChangePassword userChangePassword,
                        @RequestHeader("Authorization") String token) {
                String email = jwtTokenProvider.getEmailFromToken(token);
                Optional<User> requester = userService.getUserByEmail(email);
                if (requester.isPresent() && !requester.get().getId().equals(id)) {
                        return new ResponseEntity<>(new ErrorResponse("You have no permission",
                                        HttpStatus.UNAUTHORIZED.value()), HttpStatus.UNAUTHORIZED);
                }
                if (!requester.isPresent()) {
                        return new ResponseEntity<>(new ErrorResponse("User not found", HttpStatus.NOT_FOUND.value()),
                                        HttpStatus.NOT_FOUND);
                }
                Optional<User> user = userService.getUserById(id);
                if (!user.isPresent()) {
                        return new ResponseEntity<>(new ErrorResponse("User not found", HttpStatus.NOT_FOUND.value()),
                                        HttpStatus.NOT_FOUND);
                }
                String currentPassword = userChangePassword.getCurrentPassword();
                String newPassword = userChangePassword.getNewPassword();
                if (!userService.verifyPassword(currentPassword, user.get().getPassword())) {
                        return new ResponseEntity<>(new ErrorResponse("Password is incorrect",
                                        HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
                }

                user.get().setPassword(userService.hashPassword(newPassword));
                User updatedUser = userService.updateUser(id, user.get());
                if (updatedUser != null) {
                        return new ResponseEntity<>(
                                        new SuccessResponse<>(),
                                        HttpStatus.OK);
                }
                return new ResponseEntity<>(new ErrorResponse("Password not changed", HttpStatus.NOT_FOUND.value()),
                                HttpStatus.NOT_FOUND);
        }

        @PutMapping("/{id}/reset-password")
        public ResponseEntity<?> resetPassword(@PathVariable Long id, @RequestHeader("Authorization") String token) {
                String emailFromToken = jwtTokenProvider.getEmailFromToken(token);
                Optional<User> requester = userService.getUserByEmail(emailFromToken);
                if (requester.isPresent() && !requester.get().getRole().equals("admin")) {
                        return new ResponseEntity<>(new ErrorResponse("You have no permission",
                                        HttpStatus.UNAUTHORIZED.value()), HttpStatus.UNAUTHORIZED);
                }
                if (!requester.isPresent()) {
                        return new ResponseEntity<>(new ErrorResponse("User not found", HttpStatus.NOT_FOUND.value()),
                                        HttpStatus.NOT_FOUND);
                }
                Optional<User> user = userService.getUserById(id);
                if (!user.isPresent()) {
                        return new ResponseEntity<>(new ErrorResponse("User not found", HttpStatus.NOT_FOUND.value()),
                                        HttpStatus.NOT_FOUND);
                }
                user.get().setPassword(userService.hashPassword(defaultPassword));
                User updatedUser = userService.updateUser(id, user.get());
                if (updatedUser != null) {
                        return new ResponseEntity<>(
                                        new SuccessResponse<>(),
                                        HttpStatus.OK);
                }
                return new ResponseEntity<>(new ErrorResponse("Password not changed", HttpStatus.NOT_FOUND.value()),
                                HttpStatus.NOT_FOUND);
        }

        // Delete - DELETE /api/users/{id}
        @DeleteMapping("/{id}")
        public ResponseEntity<?> deleteUser(@PathVariable Long id) {
                boolean deleted = userService.deleteUser(id);
                if (deleted) {
                        return new ResponseEntity<>(
                                        new SuccessResponse<>(),
                                        HttpStatus.OK);
                }
                return new ResponseEntity<>(new ErrorResponse("User not found", HttpStatus.NOT_FOUND.value()),
                                HttpStatus.NOT_FOUND);
        }
}
