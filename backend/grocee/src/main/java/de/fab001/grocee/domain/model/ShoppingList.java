package de.fab001.grocee.domain.model;

import java.util.*;

public class ShoppingList {
    private final UUID id;
    private final List<Product> products = new ArrayList<>();

    public ShoppingList() {
        this.id = UUID.randomUUID();
    }

    public void addProduct(Product product) {
        Optional<Product> existing = products.stream()
            .filter(p -> p.getName().equalsIgnoreCase(product.getName()))
            .findFirst();

        if (existing.isPresent()) {
            throw new IllegalArgumentException("Produkt existiert bereits in der Liste");
        }

        products.add(product);
    }

    public List<Product> getProduct() {
        return Collections.unmodifiableList(products);
    }

    public UUID getId() {
        return id;
    }
}
