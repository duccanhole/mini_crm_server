package com.mini_crm.main.service;

import com.mini_crm.main.model.User;
import com.mini_crm.main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Create
    public User createUser(User user) {
        return userRepository.save(user);
    }

    // Read all
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Read by ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Read by Email
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Update
    public User updateUser(Long id, User userDetails) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            User existingUser = user.get();
            existingUser.setName(userDetails.getName());
            existingUser.setEmail(userDetails.getEmail());
            existingUser.setPhoneNumber(userDetails.getPhoneNumber());
            existingUser.setPassword(userDetails.getPassword());
            existingUser.setStatus(userDetails.getStatus());
            existingUser.setRole(userDetails.getRole());
            return userRepository.save(existingUser);
        }
        return null;
    }

    // Delete
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
