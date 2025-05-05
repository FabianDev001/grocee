package de.fab001.grocee.domain.model;

import java.util.Objects;
import java.util.UUID;

public class Product {
    private final UUID id;
    private String name;
    private String category;
    private String brand;
    private ExpirationDate expiration;

    public Product(String name, String category, String brand, ExpirationDate expiration) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.category = category;
        this.brand = brand;
        this.expiration = expiration;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ExpirationDate gExpirationDate() {
        return expiration;
    }

    public void setExpirationDate(ExpirationDate neu) {
        this.expiration = neu;
    }

    public String getCategory() {
        return category;
    }

    public String getBrand() {
        return brand;
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
