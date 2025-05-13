package de.fab001.grocee.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "app_user") // "user" ist ein reserviertes Wort in vielen Datenbanken
public class User {
    @Id
    @Column(length = 36)
    private String id;
    
    private String name;
    private String email;

    // Für JPA benötigt
    public User() {
        this.id = UUID.randomUUID().toString();
    }

    public User(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }
    
    public User(String name, String email) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
    }
    
    public User(UUID id, String name, String email) {
        this.id = id.toString();
        this.name = name;
        this.email = email;
    }

    public UUID getId() { 
        return UUID.fromString(id); 
    }
    
    public void setId(UUID id) {
        this.id = id.toString();
    }
    
    public String getName() { 
        return name; 
    }
    
    public void setName(String name) { 
        this.name = name; 
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
} 