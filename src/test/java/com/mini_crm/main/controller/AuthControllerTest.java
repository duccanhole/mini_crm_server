package com.mini_crm.main.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mini_crm.main.dto.LoginRequest;
import com.mini_crm.main.dto.RegisterRequest;
import com.mini_crm.main.model.User;
import com.mini_crm.main.service.UserService;
import com.mini_crm.main.util.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@ActiveProfiles("test")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    private User testUser;
    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole("USER");
        testUser.setStatus("active");
        testUser.setPhoneNumber("1234567890");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        registerRequest = new RegisterRequest();
        registerRequest.setName("New User");
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setPassword("password");
        registerRequest.setConfirmPassword("password");
        registerRequest.setphoneNumber("0987654321");
        registerRequest.setRole("USER");
    }

    @Test
    @WithMockUser(username = "user")
    void testLoginSuccess() throws Exception {
        given(userService.getUserByEmail(loginRequest.getEmail())).willReturn(Optional.of(testUser));
        given(userService.verifyPassword(loginRequest.getPassword(), testUser.getPassword())).willReturn(true);
        given(jwtTokenProvider.generateToken(testUser.getEmail())).willReturn("test-token");

        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.token").value("test-token"))
                .andExpect(jsonPath("$.data.email").value(testUser.getEmail()))
                .andExpect(jsonPath("$.data.role").value(testUser.getRole()));
    }

    @Test
    @WithMockUser(username = "user")
    void testLoginInvalidCredentials() throws Exception {
        given(userService.getUserByEmail(loginRequest.getEmail())).willReturn(Optional.of(testUser));
        given(userService.verifyPassword(loginRequest.getPassword(), testUser.getPassword())).willReturn(false);

        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid credentials"))
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @WithMockUser(username = "user")
    void testRegisterSuccess() throws Exception {
        given(userService.getUserByEmail(registerRequest.getEmail())).willReturn(Optional.empty());
        given(userService.getUserByPhoneNumber(registerRequest.getphoneNumber())).willReturn(Optional.empty());
        given(userService.hashPassword(registerRequest.getPassword())).willReturn("encodedPassword");

        User createdUser = new User();
        createdUser.setId(2L);
        createdUser.setEmail(registerRequest.getEmail());
        createdUser.setName(registerRequest.getName());
        createdUser.setRole(registerRequest.getRole());

        given(userService.createUser(any(User.class))).willReturn(createdUser);

        mockMvc.perform(post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.email").value(registerRequest.getEmail()));
    }

    @Test
    @WithMockUser(username = "user")
    void testRegisterEmailExists() throws Exception {
        given(userService.getUserByEmail(registerRequest.getEmail())).willReturn(Optional.of(testUser));

        mockMvc.perform(post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email already exists"))
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @WithMockUser(username = "user")
    void testValidateTokenSuccess() throws Exception {
        String token = "valid-token";
        given(jwtTokenProvider.validateToken(token)).willReturn(true);
        given(jwtTokenProvider.getEmailFromToken(token)).willReturn(testUser.getEmail());

        mockMvc.perform(get("/api/auth/validate")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").value(testUser.getEmail()));
    }

    @Test
    @WithMockUser(username = "user")
    void testValidateTokenInvalid() throws Exception {
        String token = "invalid-token";
        given(jwtTokenProvider.validateToken(token)).willReturn(false);

        mockMvc.perform(get("/api/auth/validate")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid token"))
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @WithMockUser(username = "user")
    void testRefreshTokenSuccess() throws Exception {
        String token = "valid-token";
        String newToken = "new-token";
        given(jwtTokenProvider.validateToken(token)).willReturn(true);
        given(jwtTokenProvider.getEmailFromToken(token)).willReturn(testUser.getEmail());
        given(jwtTokenProvider.generateToken(testUser.getEmail())).willReturn(newToken);

        mockMvc.perform(post("/api/auth/refresh")
                .with(csrf())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Token refreshed successfully"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").value(newToken));
    }
}
