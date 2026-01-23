package com.mini_crm.main.controller;

import com.mini_crm.main.dto.LoginRequest;
import com.mini_crm.main.dto.LoginResponse;
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
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> user = userService.getUserByEmail(loginRequest.getEmail());

        if (user.isPresent()) {
            User foundUser = user.get();
            // Simple password check (In production, use BCrypt)
            if (foundUser.getPassword().equals(loginRequest.getPassword())) {
                String token = jwtTokenProvider.generateToken(foundUser.getEmail());
                LoginResponse response = new LoginResponse(
                        token,
                        foundUser.getEmail(),
                        foundUser.getRole(),
                        "Login successful"
                );
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("Invalid email or password", HttpStatus.UNAUTHORIZED);
    }

    // Validate Token - GET /api/auth/validate
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            if (jwtTokenProvider.validateToken(jwtToken)) {
                String email = jwtTokenProvider.getEmailFromToken(jwtToken);
                return new ResponseEntity<>("Token is valid. Email: " + email, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
    }

    // Refresh Token - POST /api/auth/refresh
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            if (jwtTokenProvider.validateToken(jwtToken)) {
                String email = jwtTokenProvider.getEmailFromToken(jwtToken);
                String newToken = jwtTokenProvider.generateToken(email);
                return new ResponseEntity<>("Token refreshed: " + newToken, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
    }
}
