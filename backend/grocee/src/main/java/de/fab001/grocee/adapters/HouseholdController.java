package de.fab001.grocee.adapters;

import de.fab001.grocee.domain.model.Household;
import de.fab001.grocee.adapters.dto.HouseholdDTO;
import de.fab001.grocee.domain.model.Product;
import de.fab001.grocee.domain.model.User;
import de.fab001.grocee.adapters.dto.UserReference;
import de.fab001.grocee.domain.repository.HouseholdRepository;
import de.fab001.grocee.domain.service.HouseholdService;
import de.fab001.grocee.domain.service.ShoppingListService;
import de.fab001.grocee.domain.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.HashMap;
import java.util.Optional;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.logging.Logger;

@RestController
@RequestMapping("/households")
public class HouseholdController {
    
    private static final Logger LOGGER = Logger.getLogger(HouseholdController.class.getName());
    
    private final HouseholdService householdService;
    private final ShoppingListService shoppingListService;
    private final HouseholdRepository householdRepository;
    private final UserService userService;
    
    public HouseholdController(HouseholdService householdService, 
                              ShoppingListService shoppingListService,
                              HouseholdRepository householdRepository,
                              UserService userService) {
        this.householdService = householdService;
        this.shoppingListService = shoppingListService;
        this.householdRepository = householdRepository;
        this.userService = userService;
    }
    
    @PostMapping
    public ResponseEntity<?> createHousehold(@RequestBody HouseholdDTO householdDTO) {
        try {
            LOGGER.info("Creating household with name: " + householdDTO.getName());
            LOGGER.info("Members count: " + (householdDTO.getMembers() != null ? householdDTO.getMembers().size() : 0));
            
            if (householdDTO.getName() == null || householdDTO.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Household name is required");
            }
            
            // Create new household
            Household household = new Household();
            household.setName(householdDTO.getName());
            
            // Load users by their IDs
            List<User> members = new ArrayList<>();
            if (householdDTO.getMembers() != null) {
                for (UserReference userRef : householdDTO.getMembers()) {
                    try {
                        if (userRef.getId() == null) {
                            LOGGER.warning("User reference contains null ID");
                            continue;
                        }
                        
                        LOGGER.info("Loading user with ID: " + userRef.getId());
                        UUID userId = UUID.fromString(userRef.getId());
                        Optional<User> userOpt = userService.findById(userId);
                        
                        if (userOpt.isPresent()) {
                            User user = userOpt.get();
                            LOGGER.info("Found user: " + user.getName() + " (" + user.getId() + ")");
                            members.add(user);
                        } else {
                            LOGGER.warning("User with ID " + userId + " not found");
                            return ResponseEntity.badRequest().body("User with ID " + userId + " not found");
                        }
                    } catch (IllegalArgumentException e) {
                        LOGGER.warning("Invalid user ID format: " + userRef.getId() + " - " + e.getMessage());
                        return ResponseEntity.badRequest().body("Invalid user ID format: " + userRef.getId());
                    } catch (Exception e) {
                        LOGGER.severe("Error loading user: " + e.getMessage());
                        return ResponseEntity.badRequest().body("Error loading user: " + e.getMessage());
                    }
                }
            }
            
            LOGGER.info("Setting " + members.size() + " members on household");
            household.setMembers(members);
            
            LOGGER.info("Saving household to repository");
            householdRepository.save(household);
            
            LOGGER.info("Household created successfully with ID: " + household.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(household);
        } catch (Exception e) {
            LOGGER.severe("Error creating household: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error creating household: " + e.getMessage());
        }
    }
    
    @GetMapping
    public ResponseEntity<?> getAllHouseholds() {
        return ResponseEntity.ok(householdRepository.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getHousehold(@PathVariable("id") UUID id) {
        Optional<Household> household = householdRepository.findById(id.toString());
        return household
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}/cost-allocation")
    public ResponseEntity<?> getCostAllocation(@PathVariable("id") UUID householdId, 
                                             @RequestParam(required = false) UUID shoppingListId) {
        try {
            // Get household
            Optional<Household> householdOpt = householdRepository.findById(householdId.toString());
            if (householdOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            Household household = householdOpt.get();
            
            List<Product> products;
            if (shoppingListId != null) {
                // Get products from specific shopping list
                var shoppingListOpt = shoppingListService.findById(shoppingListId);
                if (shoppingListOpt.isEmpty()) {
                    return ResponseEntity.badRequest().body("Shopping list not found");
                }
                products = shoppingListOpt.get().getProduct();
            } else {
                // TODO: Get all products for this household across lists
                return ResponseEntity.badRequest().body("Must specify a shopping list ID");
            }
            
            // Calculate cost allocation
            Map<UUID, Double> costAllocation = householdService.calculateCostAllocation(household, products);
            
            // Convert to a map with string keys for JSON serialization
            Map<String, Double> result = new HashMap<>();
            costAllocation.forEach((key, value) -> result.put(key.toString(), value));
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/{id}/debts")
    public ResponseEntity<?> getDebts(@PathVariable("id") UUID householdId,
                                    @RequestParam(required = false) UUID shoppingListId) {
        try {
            // Get household
            Optional<Household> householdOpt = householdRepository.findById(householdId.toString());
            if (householdOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            Household household = householdOpt.get();
            
            List<Product> products;
            if (shoppingListId != null) {
                // Get products from specific shopping list
                var shoppingListOpt = shoppingListService.findById(shoppingListId);
                if (shoppingListOpt.isEmpty()) {
                    return ResponseEntity.badRequest().body("Shopping list not found");
                }
                products = shoppingListOpt.get().getProduct();
            } else {
                // TODO: Get all products for this household across lists
                return ResponseEntity.badRequest().body("Must specify a shopping list ID");
            }
            
            // Calculate debts
            Map<UUID, Map<UUID, Double>> debts = householdService.calculateDebts(household, products);
            
            // Convert to a map with string keys for JSON serialization
            Map<String, Map<String, Double>> result = new HashMap<>();
            debts.forEach((debtorId, creditorMap) -> {
                Map<String, Double> creditorResult = new HashMap<>();
                creditorMap.forEach((creditorId, amount) -> 
                    creditorResult.put(creditorId.toString(), amount));
                result.put(debtorId.toString(), creditorResult);
            });
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateHousehold(@PathVariable("id") UUID id, @RequestBody HouseholdDTO householdDTO) {
        try {
            Optional<Household> existingOpt = householdRepository.findById(id.toString());
            if (existingOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Household existing = existingOpt.get();
            
            // Update name if provided
            if (householdDTO.getName() != null) {
                existing.setName(householdDTO.getName());
            }
            
            // Update members if provided
            if (householdDTO.getMembers() != null) {
                List<User> members = new ArrayList<>();
                for (UserReference userRef : householdDTO.getMembers()) {
                    try {
                        UUID userId = UUID.fromString(userRef.getId());
                        Optional<User> userOpt = userService.findById(userId);
                        if (userOpt.isPresent()) {
                            members.add(userOpt.get());
                        } else {
                            return ResponseEntity.badRequest().body("User with ID " + userId + " not found");
                        }
                    } catch (IllegalArgumentException e) {
                        return ResponseEntity.badRequest().body("Invalid user ID format: " + userRef.getId());
                    }
                }
                existing.setMembers(members);
            }
            
            householdRepository.save(existing);
            return ResponseEntity.ok(existing);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHousehold(@PathVariable("id") UUID id) {
        try {
            if (!householdRepository.existsById(id.toString())) {
                return ResponseEntity.notFound().build();
            }
            
            householdRepository.deleteById(id.toString());
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
} 