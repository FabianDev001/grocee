package de.fab001.grocee.adapters;

import de.fab001.grocee.application.usecase.AddProductToShoppingListService;
import de.fab001.grocee.domain.model.Product;
import de.fab001.grocee.domain.service.ExpirationChecker;
import de.fab001.grocee.domain.repository.ShoppingListRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/shoppinglists")
public class ShoppingListController {

    private final AddProductToShoppingListService addProductService;
    private final ExpirationChecker expirationChecker = new ExpirationChecker();
    private final ShoppingListRepository shoppingListRepository;

    public ShoppingListController(AddProductToShoppingListService addProductService, ShoppingListRepository shoppingListRepository) {
        this.addProductService = addProductService;
        this.shoppingListRepository = shoppingListRepository;
    }

    @PostMapping("/{id}/products")
    public ResponseEntity<?> addProductToList(@PathVariable("id") UUID shoppingListId, @RequestBody Product product) {
        try {
            addProductService.addProduct(shoppingListId, product);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/expiring-products")
    public ResponseEntity<?> getExpiringProducts(@PathVariable("id") UUID shoppingListId) {
        var listOpt = shoppingListRepository.findById(shoppingListId);
        if (listOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var products = listOpt.get().getProduct();
        var result = products.stream().map(p -> {
            String status;
            if (p.gExpirationDate().isExpired()) {
                status = "expired";
            } else if (p.gExpirationDate().isCloseToExpire()) {
                status = "expiring";
            } else {
                status = "ok";
            }
            var map = new java.util.LinkedHashMap<String, Object>();
            map.put("id", p.getId());
            map.put("name", p.getName());
            map.put("category", p.getCategory());
            map.put("brand", p.getBrand());
            map.put("expiration", p.gExpirationDate());
            map.put("status", status);
            return map;
        }).toList();
        return ResponseEntity.ok(result);
    }
} 