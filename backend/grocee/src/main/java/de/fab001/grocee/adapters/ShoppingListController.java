package de.fab001.grocee.adapters;

import de.fab001.grocee.application.usecase.AddProductToShoppingListService;
import de.fab001.grocee.domain.model.Product;
import de.fab001.grocee.domain.service.CostAllocationService;
import de.fab001.grocee.domain.service.ShoppingListService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/shoppinglists")
public class ShoppingListController {

    private final AddProductToShoppingListService addProductService;
    private final ShoppingListService shoppingListService;
    private final CostAllocationService costAllocationService = new CostAllocationService();

    public ShoppingListController(AddProductToShoppingListService addProductService, ShoppingListService shoppingListService) {
        this.addProductService = addProductService;
        this.shoppingListService = shoppingListService;
    }

    @PostMapping("/{id}/products")
    public ResponseEntity<?> addProductToList(@PathVariable("id") UUID shoppingListId, @RequestBody Product product) {
        try {
            // Validate required fields
            if (product.getName() == null || product.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Product name is required");
            }
            if (product.getCategory() == null || product.getCategory().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Product category is required");
            }
            if (product.getBrand() == null || product.getBrand().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Product brand is required");
            }
            if (product.getExpirationDate() == null) {
                return ResponseEntity.badRequest().body("Product expiration date is required");
            }
            if (product.getBoughtBy() == null) {
                addProductService.setBuyer(shoppingListId, product.getId(), null);
            }
            if (product.getNeededBy() == null) {
                return ResponseEntity.badRequest().body("Product consumer is required");
            }
            if (product.getPrice() < 0) {
                return ResponseEntity.badRequest().body("Product price cannot be negative");
            }

            addProductService.addProduct(shoppingListId, product);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{listId}/products/{productId}")
    public ResponseEntity<?> removeProductFromList(@PathVariable("listId") UUID shoppingListId, @PathVariable("productId") UUID productId) {
        try {
            var listOpt = shoppingListService.findById(shoppingListId);
            if (listOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            var list = listOpt.get();
            boolean removed = list.removeProduct(productId);
            
            if (!removed) {
                return ResponseEntity.notFound().build();
            }
            
            // Save the updated shopping list
            shoppingListService.updateShoppingList(shoppingListId, list);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/expiring-products")
    public ResponseEntity<?> getExpiringProducts(@PathVariable("id") UUID shoppingListId) {
        var listOpt = shoppingListService.findById(shoppingListId);
        if (listOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var products = listOpt.get().getProduct();
        var result = products.stream().map(p -> {
            String status;
            if (p.getExpirationDate() == null) {
                status = "unknown";
            } else if (p.getExpirationDate().isExpired()) {
                status = "expired";
            } else if (p.getExpirationDate().isCloseToExpire()) {
                status = "expiring";
            } else {
                status = "ok";
            }
            var map = new java.util.LinkedHashMap<String, Object>();
            map.put("id", p.getId());
            map.put("name", p.getName());
            map.put("category", p.getCategory());
            map.put("brand", p.getBrand());
            map.put("expiration", p.getExpirationDate());
            map.put("status", status);
            return map;
        }).toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}/cost-allocation")
    public ResponseEntity<?> getCostAllocation(@PathVariable("id") UUID shoppingListId) {
        var listOpt = shoppingListService.findById(shoppingListId);
        if (listOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var products = listOpt.get().getProduct();
        var result = costAllocationService.calculateOpenDebts(products);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<?> createShoppingList(@RequestBody de.fab001.grocee.domain.model.ShoppingList shoppingList) {
        try {
            var saved = shoppingListService.createShoppingList(shoppingList);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllShoppingLists() {
        return ResponseEntity.ok(shoppingListService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getShoppingList(@PathVariable("id") UUID id) {
        return shoppingListService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateShoppingList(@PathVariable("id") UUID id, @RequestBody de.fab001.grocee.domain.model.ShoppingList updated) {
        try {
            var saved = shoppingListService.updateShoppingList(id, updated);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteShoppingList(@PathVariable("id") UUID id) {
        shoppingListService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/products")
    public ResponseEntity<?> getProductsOfList(@PathVariable("id") UUID shoppingListId) {
        var listOpt = shoppingListService.findById(shoppingListId);
        if (listOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(listOpt.get().getProduct());
    }
} 