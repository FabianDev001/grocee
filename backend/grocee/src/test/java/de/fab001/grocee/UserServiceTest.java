package de.fab001.grocee;

import de.fab001.grocee.domain.model.User;
import de.fab001.grocee.domain.repository.UserRepository;
import de.fab001.grocee.domain.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserServiceTest {

    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    
    private User testUser;
    
    @BeforeEach
    public void setup() {
        // Create a test user
        testUser = new User("Test User", "test@example.com");
        userRepository.save(testUser);
    }
    
    @Test
    public void testFindUserById() {
        // Get the UUID of the test user
        UUID userId = testUser.getId();
        System.out.println("Test user ID: " + userId);
        
        // Try to find the user by ID
        Optional<User> foundUser = userService.findById(userId);
        
        // Verify the user was found
        assertTrue(foundUser.isPresent(), "User should be found by ID");
        assertEquals(testUser.getName(), foundUser.get().getName(), "User name should match");
        assertEquals(testUser.getEmail(), foundUser.get().getEmail(), "User email should match");
    }
    
    @Test
    public void testCreateNewUserAndFindById() {
        // Create another user
        User newUser = new User("New User", "new@example.com");
        User savedUser = userService.createUser(newUser);
        
        // Get the UUID
        UUID userId = savedUser.getId();
        System.out.println("New user ID: " + userId);
        
        // Try to find the user by ID
        Optional<User> foundUser = userService.findById(userId);
        
        // Verify the user was found
        assertTrue(foundUser.isPresent(), "New user should be found by ID");
        assertEquals(savedUser.getName(), foundUser.get().getName(), "User name should match");
    }
} 