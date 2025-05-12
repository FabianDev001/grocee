package de.fab001.grocee.domain.model;

import java.util.List;
import java.util.UUID;

public class Household {
    private final UUID id;
    private String name;
    private List<User> members;

    public Household(String name, List<User> members) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.members = members;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public List<User> getMembers() { return members; }
    public void setName(String name) { this.name = name; }
    public void setMembers(List<User> members) { this.members = members; }
} 