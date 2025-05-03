package de.fab001.grocee.domain.model;

import java.util.*;

public class ShoppingList {
    private final UUID id;
    private final List<Product> produkte = new ArrayList<>();

    public ShoppingList() {
        this.id = UUID.randomUUID();
    }

    public void produktHinzufügen(Product produkt) {
        Optional<Product> existierendes = produkte.stream()
            .filter(p -> p.getName().equalsIgnoreCase(produkt.getName()))
            .findFirst();

        if (existierendes.isPresent()) {
            throw new IllegalArgumentException("Produkt existiert bereits in der Liste");
        }

        produkte.add(produkt);
    }

    public List<Product> getProdukte() {
        return Collections.unmodifiableList(produkte);
    }

    public UUID getId() {
        return id;
    }
}
