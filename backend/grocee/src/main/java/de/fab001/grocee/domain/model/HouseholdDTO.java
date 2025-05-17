package de.fab001.grocee.domain.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for creating/updating Households
 */
public class HouseholdDTO {
    private String name;
    private List<UserReference> members = new ArrayList<>();
    
    public HouseholdDTO() {
    }
    
    public HouseholdDTO(String name, List<UserReference> members) {
        this.name = name;
        this.members = members != null ? members : new ArrayList<>();
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public List<UserReference> getMembers() {
        return members;
    }
    
    public void setMembers(List<UserReference> members) {
        this.members = members != null ? members : new ArrayList<>();
    }
} 