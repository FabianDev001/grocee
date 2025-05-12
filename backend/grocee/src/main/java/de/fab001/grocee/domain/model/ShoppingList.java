package de.fab001.grocee.domain.model;

import java.util.*;
import jakarta.persistence.*;

@Entity
public class ShoppingList {
    @Id
    @Column(length = 36)
    private String id;

    @OneToMany(mappedBy = "shoppingList", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Product> products = new ArrayList<>();

    private String name;

    public ShoppingList() {
        this.id = UUID.randomUUID().toString();
        this.name = null;
    }

    public ShoppingList(UUID id, String name) {
        this.id = id.toString();
        this.name = name;
    }

    public void addProduct(Product product) {
        Optional<Product> existing = products.stream()
            .filter(p -> p.getName().equalsIgnoreCase(product.getName()))
            .findFirst();
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Produkt existiert bereits in der Liste");
        }
        product.setShoppingList(this);
        products.add(product);
    }

    public List<Product> getProduct() {
        return products;
    }

    public Product findById(UUID productId) {
        return products.stream()
            .filter(p -> p.getId().equals(productId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Produkt nicht gefunden"));
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
}
