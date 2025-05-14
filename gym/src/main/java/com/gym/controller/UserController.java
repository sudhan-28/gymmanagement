package com.gym.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gym.model.User;
import com.gym.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")  // Be more specific in production
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            logger.info("Registration request received for email: {}", user.getEmail());
            User registeredUser = userService.register(user);
            logger.info("User registered successfully with ID: {}", registeredUser.getId());
            return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Registration failed: {}", e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginData) {
        try {
            logger.info("Login attempt for email: {}", loginData.getEmail());
            Optional<User> user = userService.login(loginData.getEmail(), loginData.getPassword());
            
            if (user.isPresent()) {
                logger.info("Login successful for user: {}", loginData.getEmail());
                return new ResponseEntity<>(user.get(), HttpStatus.OK);
            } else {
                logger.warn("Login failed for email: {}", loginData.getEmail());
                return new ResponseEntity<>("Invalid email or password", HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            logger.error("Login error: {}", e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody User user) {
        try {
            logger.info("Update request received for user ID: {}", id);
            User updatedUser = userService.updateUser(id, user);
            
            if (updatedUser != null) {
                logger.info("User updated successfully: {}", id);
                return new ResponseEntity<>(updatedUser, HttpStatus.OK);
            } else {
                logger.warn("User not found for update: {}", id);
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Update error for user {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        try {
            logger.info("Delete request received for user ID: {}", id);
            boolean result = userService.deleteUser(id);
            
            if (result) {
                logger.info("User deleted successfully: {}", id);
                return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
            } else {
                logger.warn("User not found for deletion: {}", id);
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Delete error for user {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/check-email/{email}")
    public ResponseEntity<Boolean> checkEmailExists(@PathVariable String email) {
        try {
            logger.info("Checking if email exists: {}", email);
            boolean exists = userService.emailExists(email);
            return new ResponseEntity<>(exists, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error checking email: {}", e.getMessage(), e);
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}