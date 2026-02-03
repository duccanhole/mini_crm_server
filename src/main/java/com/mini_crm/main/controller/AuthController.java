package com.mini_crm.main.controller;

import com.mini_crm.main.dto.auth.LoginRequest;
import com.mini_crm.main.dto.ErrorResponse;
import com.mini_crm.main.dto.SuccessResponse;
import com.mini_crm.main.dto.auth.LoginResponse;
import com.mini_crm.main.dto.auth.RegisterRequest;
import com.mini_crm.main.dto.auth.RegisterResponse;
import com.mini_crm.main.model.User;
import com.mini_crm.main.service.UserService;
import com.mini_crm.main.util.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.phone}")
    private String adminPhone;

    @Value("${manager.email}")
    private String managerEmail;

    @Value("${manager.phone}")
    private String managerPhone;

    @Value("${sale.email}")
    private String saleEmail;

    @Value("${sale.phone}")
    private String salePhone;

    @Value("${password.default}")
    private String defaultPassword;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // Login - POST /api/auth/login
    // Can login with email or phoneNumber
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> user = Optional.empty();

        // Try to find user by email
        if (loginRequest.getEmail() != null && !loginRequest.getEmail().isEmpty()) {
            user = userService.getUserByEmail(loginRequest.getEmail());
        }

        if (user.isPresent()) {
            User foundUser = user.get();
            // Verify password using BCrypt
            if (userService.verifyPassword(loginRequest.getPassword(), foundUser.getPassword())) {
                String token = jwtTokenProvider.generateToken(foundUser.getEmail());
                LoginResponse loginResponse = new LoginResponse(
                        token,
                        foundUser.getEmail(),
                        foundUser.getRole());
                SuccessResponse<LoginResponse> response = new SuccessResponse<>(
                        "Login successful",
                        HttpStatus.OK.value(),
                        loginResponse);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(
                new ErrorResponse("Invalid credentials", HttpStatus.UNAUTHORIZED.value()),
                HttpStatus.UNAUTHORIZED);
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
                        email);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(
                new ErrorResponse("Invalid token", HttpStatus.UNAUTHORIZED.value()),
                HttpStatus.UNAUTHORIZED);
    }

    // Refresh Token - POST /api/auth/refresh
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            if (jwtTokenProvider.validateToken(jwtToken)) {
                String email = jwtTokenProvider.getEmailFromToken(jwtToken);
                Optional<User> user = userService.getUserByEmail(email);
                if (!user.isPresent()) {
                    return new ResponseEntity<>(
                            new ErrorResponse("User not found", HttpStatus.UNAUTHORIZED.value()),
                            HttpStatus.UNAUTHORIZED);
                }
                String newToken = jwtTokenProvider.generateToken(email);
                SuccessResponse<String> response = new SuccessResponse<>(
                        "Token refreshed successfully",
                        HttpStatus.OK.value(),
                        newToken);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(
                new ErrorResponse("Invalid token", HttpStatus.UNAUTHORIZED.value()),
                HttpStatus.UNAUTHORIZED);
    }

    // Register - POST /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        // Validate input
        if (registerRequest.getName() == null || registerRequest.getName().isEmpty()) {
            return new ResponseEntity<>(
                    new ErrorResponse("Name is required", HttpStatus.BAD_REQUEST.value()),
                    HttpStatus.BAD_REQUEST);
        }

        if (registerRequest.getEmail() == null || registerRequest.getEmail().isEmpty()) {
            return new ResponseEntity<>(
                    new ErrorResponse("Email is required", HttpStatus.BAD_REQUEST.value()),
                    HttpStatus.BAD_REQUEST);
        }

        if (registerRequest.getPassword() == null || registerRequest.getPassword().isEmpty()) {
            return new ResponseEntity<>(
                    new ErrorResponse("Password is required", HttpStatus.BAD_REQUEST.value()),
                    HttpStatus.BAD_REQUEST);
        }

        // Check if passwords match
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            return new ResponseEntity<>(
                    new ErrorResponse("Passwords do not match", HttpStatus.BAD_REQUEST.value()),
                    HttpStatus.BAD_REQUEST);
        }

        // Check if email already exists
        if (userService.getUserByEmail(registerRequest.getEmail()).isPresent()) {
            return new ResponseEntity<>(
                    new ErrorResponse("Email already exists", HttpStatus.CONFLICT.value()),
                    HttpStatus.CONFLICT);
        }

        // Check if phoneNumber already exists (if provided)
        if (registerRequest.getphoneNumber() != null && !registerRequest.getphoneNumber().isEmpty()) {
            if (userService.getUserByPhoneNumber(registerRequest.getphoneNumber()).isPresent()) {
                return new ResponseEntity<>(
                        new ErrorResponse("Phone number already exists", HttpStatus.CONFLICT.value()),
                        HttpStatus.CONFLICT);
            }
        }

        // Create new user
        User newUser = new User();
        newUser.setName(registerRequest.getName());
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPhoneNumber(registerRequest.getphoneNumber());
        // Hash password before saving
        newUser.setPassword(userService.hashPassword(registerRequest.getPassword()));
        newUser.setStatus("active");
        newUser.setRole(registerRequest.getRole());

        User createdUser = userService.createUser(newUser);

        RegisterResponse registerResponse = new RegisterResponse(
                "User registered successfully",
                createdUser.getEmail(),
                true);
        SuccessResponse<RegisterResponse> response = new SuccessResponse<>(
                "User registered successfully",
                HttpStatus.CREATED.value(),
                registerResponse);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/set-default-account")
    public ResponseEntity<?> setDefaultAccount() {
        Optional<User> adminUser = userService.getUserByEmail(adminEmail);
        Optional<User> managerUser = userService.getUserByEmail(managerEmail);
        Optional<User> saleUser = userService.getUserByEmail(saleEmail);

        if (!adminUser.isPresent()) {
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail(adminEmail);
            admin.setPhoneNumber(adminPhone);
            admin.setPassword(userService.hashPassword(defaultPassword));
            admin.setStatus("active");
            admin.setRole("admin");
            userService.createUser(admin);
        }

        if (!managerUser.isPresent()) {
            User manager = new User();
            manager.setName("Manager");
            manager.setEmail(managerEmail);
            manager.setPhoneNumber(managerPhone);
            manager.setPassword(userService.hashPassword(defaultPassword));
            manager.setStatus("active");
            manager.setRole("manager");
            userService.createUser(manager);
        }

        if (!saleUser.isPresent()) {
            User sale = new User();
            sale.setName("Sale");
            sale.setEmail(saleEmail);
            sale.setPhoneNumber(salePhone);
            sale.setPassword(userService.hashPassword(defaultPassword));
            sale.setStatus("active");
            sale.setRole("sale");
            userService.createUser(sale);
        }

        return new ResponseEntity<>(new SuccessResponse<>(), HttpStatus.OK);
    }
}
