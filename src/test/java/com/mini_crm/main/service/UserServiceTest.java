package com.mini_crm.main.service;

import com.mini_crm.main.model.User;
import com.mini_crm.main.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void hashPassword_callsEncoderAndReturnsHashedValue() {
        // Given
        when(passwordEncoder.encode("raw-pass")).thenReturn("hashed-pass");

        // When
        String hashed = userService.hashPassword("raw-pass");

        // Then
        assertEquals("hashed-pass", hashed);
        verify(passwordEncoder).encode("raw-pass");
    }

    @Test
    void verifyPassword_callsEncoderMatchesAndReturnsResult() {
        // Given
        when(passwordEncoder.matches("raw-pass", "hashed-pass")).thenReturn(true);

        // When
        boolean matched = userService.verifyPassword("raw-pass", "hashed-pass");

        // Then
        assertTrue(matched);
        verify(passwordEncoder).matches("raw-pass", "hashed-pass");
    }

    @Test
    void updateUser_whenFound_updatesFieldsAndSaves() {
        // Given
        User existing = new User();
        existing.setId(1L);
        existing.setName("Old");

        User details = new User();
        details.setName("New");
        details.setEmail("new@mail.com");
        details.setPhoneNumber("090");
        details.setPassword("secret");
        details.setStatus("active");
        details.setRole("sale");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);

        // When
        User result = userService.updateUser(1L, details);

        // Then
        assertEquals(existing, result);
        assertEquals("New", existing.getName());
        assertEquals("new@mail.com", existing.getEmail());
        assertEquals("090", existing.getPhoneNumber());
        assertEquals("secret", existing.getPassword());
        assertEquals("active", existing.getStatus());
        assertEquals("sale", existing.getRole());
    }

    @Test
    void updateUser_whenNotFound_returnsNull() {
        // Given
        when(userRepository.findById(404L)).thenReturn(Optional.empty());

        // When
        User result = userService.updateUser(404L, new User());

        // Then
        assertNull(result);
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser_whenNotExists_returnsFalseAndDoesNotDelete() {
        // Given
        when(userRepository.existsById(5L)).thenReturn(false);

        // When
        boolean result = userService.deleteUser(5L);

        // Then
        assertFalse(result);
        verify(userRepository, never()).deleteById(any());
    }
}
