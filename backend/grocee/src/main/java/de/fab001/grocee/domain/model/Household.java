package de.fab001.grocee.domain.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

@Entity
public class Household {
    @Id
    @Column(length = 36)
    private String id;
    
    private String name;
    
    @ManyToMany
    @JoinTable(
        name = "household_members",
        joinColumns = @JoinColumn(name = "household_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> members = new ArrayList<>();

    public Household() {
        this.id = UUID.randomUUID().toString();
    }
    
    public Household(String name, List<User> members) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.members = members != null ? members : new ArrayList<>();
    }
    
    /**
     * Find a member of this household by their ID
     * @param userId The user ID to search for
     * @return The User if found, null otherwise
     */
    public User findMemberById(String userId) {
        if (userId == null || members == null) {
            return null;
        }
        
        return members.stream()
            .filter(user -> userId.equals(user.getId().toString()))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Calculate each member's share of the total cost based on their product count
     * @param totalCost Total cost to allocate
     * @param userProductCounts Map of user IDs to their product counts
     * @return Map of user IDs to their cost allocation
     */
    public Map<UUID, Double> calculateMemberShares(double totalCost, Map<String, Integer> userProductCounts) {
        Map<UUID, Double> costAllocation = new HashMap<>();
        
        // If no product count data, split evenly
        if (userProductCounts == null || userProductCounts.isEmpty()) {
            double evenShare = totalCost / members.size();
            for (User member : members) {
                costAllocation.put(member.getId(), evenShare);
            }
            return costAllocation;
        }
        
        // Calculate total products first
        int totalProducts = userProductCounts.values().stream().mapToInt(Integer::intValue).sum();
        
        // Calculate each member's share
        for (User member : members) {
            String memberId = member.getId().toString();
            Integer productCount = userProductCounts.getOrDefault(memberId, 0);
            
            double share = totalProducts > 0 
                ? (double) productCount / totalProducts * totalCost 
                : totalCost / members.size();
                
            costAllocation.put(member.getId(), share);
        }
        
        return costAllocation;
    }

    public String getId() { return id; }
    public UUID getUUID() { return UUID.fromString(id); }
    public String getName() { return name; }
    public List<User> getMembers() { return members; }
    public void setName(String name) { this.name = name; }
    public void setMembers(List<User> members) { this.members = members != null ? members : new ArrayList<>(); }
    
    public void addMember(User user) {
        if (user != null && !members.contains(user)) {
            members.add(user);
        }
    }
    
    public boolean removeMember(User user) {
        return members.remove(user);
    }
} 