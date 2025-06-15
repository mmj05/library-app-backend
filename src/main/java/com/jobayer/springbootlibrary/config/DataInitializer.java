package com.jobayer.springbootlibrary.config;

import com.jobayer.springbootlibrary.dao.UserRepository;
import com.jobayer.springbootlibrary.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Check if any admin users exist
        boolean adminExists = userRepository.findAll().stream()
                .anyMatch(user -> user.getRole() == User.Role.ADMIN);

        if (!adminExists) {
            // Create default admin user
            User admin = new User();
            admin.setEmail("admin@library.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setRole(User.Role.ADMIN);

            userRepository.save(admin);
            
            System.out.println("Default admin user created:");
            System.out.println("Email: admin@library.com");
            System.out.println("Password: admin123");
            System.out.println("Please change the password after first login!");
        }
        
        // Check if test user exists
        if (!userRepository.findByEmail("testuser@library.com").isPresent()) {
            // Create test user
            User testUser = new User();
            testUser.setEmail("testuser@library.com");
            testUser.setPassword(passwordEncoder.encode("password"));
            testUser.setFirstName("Test");
            testUser.setLastName("User");
            testUser.setRole(User.Role.USER);

            userRepository.save(testUser);
            
            System.out.println("Test user created:");
            System.out.println("Email: testuser@library.com");
            System.out.println("Password: password");
        }
    }
} 