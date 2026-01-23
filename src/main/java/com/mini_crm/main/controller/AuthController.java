package com.mini_crm.main.controller;

import com.mini_crm.main.dto.LoginRequest;
import com.mini_crm.main.dto.LoginResponse;
import com.mini_crm.main.dto.RegisterRequest;
import com.mini_crm.main.dto.RegisterResponse;
import com.mini_crm.main.dto.ErrorResponse;
import com.mini_crm.main.dto.SuccessResponse;
import com.mini_crm.main.model.User;
import com.mini_crm.main.service.UserService;
import com.mini_crm.main.util.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // Login - POST /api/auth/login
    // Can login with email or phone_number
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> user = Optional.empty();
        
        // Try to find user by email
        if (loginRequest.getEmail() != null && !loginRequest.getEmail().isEmpty()) {
            user = userService.getUserByEmail(loginRequest.getEmail());
        }
        
        // If not found by email, try to find by phone_number
        if (user.isEmpty() && loginRequest.getPhone_number() != null && !loginRequest.getPhone_number().isEmpty()) {
            user = userService.getUserByPhoneNumber(loginRequest.getPhone_number());
        }

        if (user.isPresent()) {
            User foundUser = user.get();
            // Verify password using BCrypt
            if (userService.verifyPassword(loginRequest.getPassword(), foundUser.getPassword())) {
                String token = jwtTokenProvider.generateToken(foundUser.getEmail());
                LoginResponse loginResponse = new LoginResponse(
                        token,
                        foundUser.getEmail(),
                        foundUser.getRole(),
                        "Login successful"
                );
                SuccessResponse<LoginResponse> response = new SuccessResponse<>(
                        "Login successful",
                        HttpStatus.OK.value(),
                        loginResponse
                );
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(
                new ErrorResponse("Invalid credentials", HttpStatus.UNAUTHORIZED.value()),
                HttpStatus.UNAUTHORIZED
        );
    }

    // Validate Token - GET /api/auth/validate
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            if (jwtTokenProvider.validateToken(jwtToken)) {
                String email = jwtTokenProvider.getEmailFromToken(jwtToken);
                SuccessResponse<String> response = new SuccessResponse<>(
                        "Login successful",
                        HttpStatus.OK.value(),
                        email
                );
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(
                new ErrorResponse("Invalid token", HttpStatus.UNAUTHORIZED.value()),
                HttpStatus.UNAUTHORIZED
        );
    }

    // Refresh Token - POST /api/auth/refresh
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            if (jwtTokenProvider.validateToken(jwtToken)) {
                String email = jwtTokenProvider.getEmailFromToken(jwtToken);
                String newToken = jwtTokenProvider.generateToken(email);
                SuccessResponse<String> response = new SuccessResponse<>(
                        "Token refreshed successfully",
                        HttpStatus.OK.value(),
                        newToken
                );
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(
                new ErrorResponse("Invalid token", HttpStatus.UNAUTHORIZED.value()),
                HttpStatus.UNAUTHORIZED
        );
    }

    // Register - POST /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        // Validate input
        if (registerRequest.getName() == null || registerRequest.getName().isEmpty()) {
            return new ResponseEntity<>(
                    new ErrorResponse("Name is required", HttpStatus.BAD_REQUEST.value()),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (registerRequest.getEmail() == null || registerRequest.getEmail().isEmpty()) {
            return new ResponseEntity<>(
                    new ErrorResponse("Email is required", HttpStatus.BAD_REQUEST.value()),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (registerRequest.getPassword() == null || registerRequest.getPassword().isEmpty()) {
            return new ResponseEntity<>(
                    new ErrorResponse("Password is required", HttpStatus.BAD_REQUEST.value()),
                    HttpStatus.BAD_REQUEST
            );
        }

        // Check if passwords match
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            return new ResponseEntity<>(
                    new ErrorResponse("Passwords do not match", HttpStatus.BAD_REQUEST.value()),
                    HttpStatus.BAD_REQUEST
            );
        }

        // Check if email already exists
        if (userService.getUserByEmail(registerRequest.getEmail()).isPresent()) {
            return new ResponseEntity<>(
                    new ErrorResponse("Email already exists", HttpStatus.CONFLICT.value()),
                    HttpStatus.CONFLICT
            );
        }

        // Check if phone_number already exists (if provided)
        if (registerRequest.getPhone_number() != null && !registerRequest.getPhone_number().isEmpty()) {
            if (userService.getUserByPhoneNumber(registerRequest.getPhone_number()).isPresent()) {
                return new ResponseEntity<>(
                        new ErrorResponse("Phone number already exists", HttpStatus.CONFLICT.value()),
                        HttpStatus.CONFLICT
                );
            }
        }

        // Create new user
        User newUser = new User();
        newUser.setName(registerRequest.getName());
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPhoneNumber(registerRequest.getPhone_number());
        // Hash password before saving
        newUser.setPassword(userService.hashPassword(registerRequest.getPassword()));
        newUser.setStatus("active");
        newUser.setRole(registerRequest.getRole());

        User createdUser = userService.createUser(newUser);

        RegisterResponse registerResponse = new RegisterResponse(
                "User registered successfully",
                createdUser.getEmail(),
                true
        );
        SuccessResponse<RegisterResponse> response = new SuccessResponse<>(
                "User registered successfully",
                HttpStatus.CREATED.value(),
                registerResponse
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
