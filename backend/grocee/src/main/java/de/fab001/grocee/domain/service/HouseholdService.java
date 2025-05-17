package de.fab001.grocee.domain.service;

import de.fab001.grocee.domain.model.Household;
import de.fab001.grocee.domain.model.Product;
import de.fab001.grocee.domain.model.User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class HouseholdService {
    
    /**
     * Calculates cost allocation among household members based on products they need.
     * @param household The household for which to calculate cost allocation
     * @param products List of products purchased for the household
     * @return Map of user IDs to their cost allocation amount
     */
    public Map<UUID, Double> calculateCostAllocation(Household household, List<Product> products) {
        // Get map of user IDs in this household for quicker lookup
        Map<String, User> householdUsers = household.getMembers().stream()
                .collect(Collectors.toMap(user -> user.getId().toString(), user -> user));
        
        // Create map to store each user's allocated costs
        Map<UUID, Double> costAllocation = new HashMap<>();
        
        // Initialize allocation with 0 for each household member
        for (User user : household.getMembers()) {
            costAllocation.put(user.getId(), 0.0);
        }
        
        // Allocate costs based on who needed each product
        int unallocatedProducts = 0;
        double unallocatedCost = 0.0;
        
        for (Product product : products) {
            String neededBy = product.getNeededBy();
            double price = product.getPrice();
            
            // If product is needed by a household member, add its cost to their allocation
            if (neededBy != null && householdUsers.containsKey(neededBy)) {
                User user = householdUsers.get(neededBy);
                costAllocation.merge(user.getId(), price, Double::sum);
            } else {
                // Keep track of unallocated products
                unallocatedProducts++;
                unallocatedCost += price;
            }
        }
        
        // If there are unallocated products, split their cost evenly among all members
        if (unallocatedProducts > 0 && !household.getMembers().isEmpty()) {
            double evenShare = unallocatedCost / household.getMembers().size();
            for (User user : household.getMembers()) {
                costAllocation.merge(user.getId(), evenShare, Double::sum);
            }
        }
        
        return costAllocation;
    }
    
    /**
     * Calculates how much each user owes to the one who made the purchase.
     * @param household The household
     * @param products List of products
     * @return Map of debtor ID to creditor ID to amount
     */
    public Map<UUID, Map<UUID, Double>> calculateDebts(Household household, List<Product> products) {
        Map<UUID, Map<UUID, Double>> debts = new HashMap<>();
        
        for (Product product : products) {
            if (product.getNeededBy() != null && product.getBoughtBy() != null && 
                !product.getNeededBy().equals(product.getBoughtBy())) {
                
                // Ensure both users are in this household
                User neededByUser = household.findMemberById(product.getNeededBy());
                User boughtByUser = household.findMemberById(product.getBoughtBy());
                
                if (neededByUser != null && boughtByUser != null) {
                    UUID debtorId = neededByUser.getId();
                    UUID creditorId = boughtByUser.getId();
                    double amount = product.getPrice();
                    
                    // Initialize debt map for this debtor if not exists
                    debts.computeIfAbsent(debtorId, k -> new HashMap<>());
                    
                    // Add or update debt amount
                    Map<UUID, Double> debtorDebts = debts.get(debtorId);
                    debtorDebts.merge(creditorId, amount, Double::sum);
                }
            }
        }
        
        return debts;
    }
} 