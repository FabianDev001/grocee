package de.fab001.grocee;

import de.fab001.grocee.domain.model.ExpirationDate;
import de.fab001.grocee.domain.model.Household;
import de.fab001.grocee.domain.model.Product;
import de.fab001.grocee.domain.model.User;
import de.fab001.grocee.domain.service.HouseholdService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class HouseholdCostAllocationTest {
    
    private HouseholdService householdService;
    private Household household;
    private User user1, user2, user3;
    private List<Product> products;
    
    @BeforeEach
    public void setup() {
        householdService = new HouseholdService();
        
        // Create test users
        user1 = new User("Alice", "alice@example.com");
        user2 = new User("Bob", "bob@example.com");
        user3 = new User("Charlie", "charlie@example.com");
        
        // Create a household with these users
        household = new Household("Test WG", Arrays.asList(user1, user2, user3));
        
        // Create some products
        Product p1 = new Product();
        p1.setId(UUID.randomUUID().toString());
        p1.setName("Milk");
        p1.setCategory("Dairy");
        p1.setBrand("Brand A");
        p1.setPrice(2.99);
        p1.setNeededBy(user1.getId().toString());
        p1.setBoughtBy(user2.getId().toString());
        p1.setExpiration(new ExpirationDate(LocalDate.now().plusDays(7)));
        
        Product p2 = new Product();
        p2.setId(UUID.randomUUID().toString());
        p2.setName("Bread");
        p2.setCategory("Bakery");
        p2.setBrand("Brand B");
        p2.setPrice(3.49);
        p2.setNeededBy(user1.getId().toString());
        p2.setBoughtBy(user2.getId().toString());
        p2.setExpiration(new ExpirationDate(LocalDate.now().plusDays(3)));
        
        Product p3 = new Product();
        p3.setId(UUID.randomUUID().toString());
        p3.setName("Cheese");
        p3.setCategory("Dairy");
        p3.setBrand("Brand C");
        p3.setPrice(4.99);
        p3.setNeededBy(user2.getId().toString());
        p3.setBoughtBy(user2.getId().toString());
        p3.setExpiration(new ExpirationDate(LocalDate.now().plusDays(14)));
        
        Product p4 = new Product();
        p4.setId(UUID.randomUUID().toString());
        p4.setName("Apples");
        p4.setCategory("Produce");
        p4.setBrand("Brand D");
        p4.setPrice(5.99);
        p4.setNeededBy(user3.getId().toString());
        p4.setBoughtBy(user1.getId().toString());
        p4.setExpiration(new ExpirationDate(LocalDate.now().plusDays(10)));
        
        products = Arrays.asList(p1, p2, p3, p4);
    }
    
    @Test
    public void testCostAllocation() {
        // Calculate cost allocation
        Map<UUID, Double> allocation = householdService.calculateCostAllocation(household, products);
        
        // Total cost should be 17.46 (2.99 + 3.49 + 4.99 + 5.99)
        double totalCost = products.stream().mapToDouble(Product::getPrice).sum();
        assertEquals(17.46, totalCost, 0.01);
        
        // User1 should be allocated the cost of milk and bread (2.99 + 3.49 = 6.48)
        // User2 should be allocated the cost of cheese (4.99)
        // User3 should be allocated the cost of apples (5.99)
        
        // Check if all users received an allocation
        assertEquals(3, allocation.size());
        assertTrue(allocation.containsKey(user1.getId()));
        assertTrue(allocation.containsKey(user2.getId()));
        assertTrue(allocation.containsKey(user3.getId()));
        
        // Check specific allocations based on neededBy assignments
        assertEquals(6.48, allocation.get(user1.getId()), 0.01);
        assertEquals(4.99, allocation.get(user2.getId()), 0.01);
        assertEquals(5.99, allocation.get(user3.getId()), 0.01);
        
        // Total of allocations should equal total cost
        double totalAllocated = allocation.values().stream().mapToDouble(Double::doubleValue).sum();
        assertEquals(totalCost, totalAllocated, 0.01);
    }
    
    @Test
    public void testDebtCalculation() {
        // Calculate debts
        Map<UUID, Map<UUID, Double>> debts = householdService.calculateDebts(household, products);
        
        // User1 owes User2 for milk and bread (2.99 + 3.49 = 6.48)
        // User3 owes User1 for apples (5.99)
        
        // Check user1's debts (should owe user2)
        assertTrue(debts.containsKey(user1.getId()));
        assertTrue(debts.get(user1.getId()).containsKey(user2.getId()));
        assertEquals(6.48, debts.get(user1.getId()).get(user2.getId()), 0.01);
        
        // Check user3's debts (should owe user1)
        assertTrue(debts.containsKey(user3.getId()));
        assertTrue(debts.get(user3.getId()).containsKey(user1.getId()));
        assertEquals(5.99, debts.get(user3.getId()).get(user1.getId()), 0.01);
        
        // User2 bought their own cheese, so no debt entry needed
        if (debts.containsKey(user2.getId())) {
            assertFalse(debts.get(user2.getId()).containsKey(user2.getId()));
        }
    }
    
    @Test
    public void testEvenSplitWhenNoProductsAssociated() {
        // Create products not associated with any member
        Product p1 = new Product();
        p1.setName("Shared Item");
        p1.setPrice(15.0);
        p1.setNeededBy(null); // No specific user
        
        List<Product> sharedProducts = List.of(p1);
        
        // Calculate allocation
        Map<UUID, Double> allocation = householdService.calculateCostAllocation(household, sharedProducts);
        
        // Should be split evenly (15 / 3 = 5 per user)
        assertEquals(3, allocation.size());
        assertEquals(5.0, allocation.get(user1.getId()), 0.01);
        assertEquals(5.0, allocation.get(user2.getId()), 0.01);
        assertEquals(5.0, allocation.get(user3.getId()), 0.01);
    }
} 