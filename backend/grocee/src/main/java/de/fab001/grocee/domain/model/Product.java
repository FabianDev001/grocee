package de.fab001.grocee.domain.model;

import java.util.Objects;
import java.util.UUID;

public class Product {
    private final UUID id;
    private String name;
    private String category;
    private String brand;
    private ExpirationDate expiration;

    public Product(String name, String kategorie, String marke, ExpirationDate expiration) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.category = kategorie;
        this.brand = marke;
        this.expiration = expiration;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ExpirationDate getHaltbarkeit() {
        return expiration;
    }

    public void aktualisiereHaltbarkeit(ExpirationDate neu) {
        this.expiration = neu;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        return id.equals(product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
