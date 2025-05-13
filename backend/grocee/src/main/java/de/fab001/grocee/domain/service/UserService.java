package de.fab001.grocee.domain.service;

import de.fab001.grocee.domain.model.User;
import de.fab001.grocee.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public User createUser(User user) {
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Der Benutzername darf nicht leer sein");
        }
        
        // Prüfen, ob E-Mail eindeutig ist, falls vorhanden
        if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
            repository.findByEmail(user.getEmail()).ifPresent(existingUser -> {
                throw new IllegalArgumentException("Ein Benutzer mit dieser E-Mail-Adresse existiert bereits");
            });
        }
        
        repository.save(user);
        return user;
    }

    public Optional<User> findById(UUID id) {
        return repository.findById(id.toString());
    }

    public List<User> findAll() {
        return repository.findAll();
    }

    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    public User updateUser(UUID id, User updatedUser) {
        User existingUser = repository.findById(id.toString())
                .orElseThrow(() -> new IllegalArgumentException("Benutzer nicht gefunden"));
        
        if (updatedUser.getName() != null && !updatedUser.getName().trim().isEmpty()) {
            existingUser.setName(updatedUser.getName());
        }
        
        if (updatedUser.getEmail() != null && !updatedUser.getEmail().equals(existingUser.getEmail())) {
            // Prüfen, ob neue E-Mail eindeutig ist
            repository.findByEmail(updatedUser.getEmail()).ifPresent(otherUser -> {
                if (!otherUser.getId().equals(id)) {
                    throw new IllegalArgumentException("Ein Benutzer mit dieser E-Mail-Adresse existiert bereits");
                }
            });
            existingUser.setEmail(updatedUser.getEmail());
        }
        
        repository.save(existingUser);
        return existingUser;
    }

    public void deleteById(UUID id) {
        repository.deleteById(id.toString());
    }
} 