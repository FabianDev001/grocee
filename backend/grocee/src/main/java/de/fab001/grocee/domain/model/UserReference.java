package de.fab001.grocee.domain.model;

import java.util.UUID;

/**
 * Data Transfer Object for User references in JSON payloads
 */
public class UserReference {
    private String id;

    public UserReference() {
    }

    public UserReference(String id) {
        this.id = id;
    }

    public UserReference(UUID id) {
        this.id = id.toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UUID getUUID() {
        return id != null ? UUID.fromString(id) : null;
    }
} 