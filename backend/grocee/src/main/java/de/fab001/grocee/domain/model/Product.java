package de.fab001.grocee.domain.model;

import java.util.Objects;
import java.util.UUID;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class Product {
    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shopping_list_id")
    private ShoppingList shoppingList;

    private String name;
    private String category;
    private String brand;
    @Embedded
    @JsonProperty("expirationDate")
    private ExpirationDate expiration;
    private double price;
    private String neededBy;
    private String boughtBy;
    private boolean isPaid;

    public Product() {
        this.id = UUID.randomUUID().toString();
    }

    public Product(String name, String category, String brand, ExpirationDate expiration) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.category = category;
        this.brand = brand;
        this.expiration = expiration;
        this.price = 0.0;
        this.neededBy = null;
        this.boughtBy = null;
        this.isPaid = false;
    }

    public Product(String name, String category, String brand, ExpirationDate expiration, double price, String neededBy, String boughtBy, boolean isPaid) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.category = category;
        this.brand = brand;
        this.expiration = expiration;
        this.price = price;
        this.neededBy = neededBy;
        this.boughtBy = boughtBy;
        this.isPaid = isPaid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ShoppingList getShoppingList() {
        return shoppingList;
    }

    public void setShoppingList(ShoppingList shoppingList) {
        this.shoppingList = shoppingList;
    }

    public String getName() {
        return name;
    }

    public ExpirationDate getExpirationDate() {
        return expiration;
    }

    public void setExpiration(ExpirationDate expiration) {
        this.expiration = expiration;
    }

    public String getCategory() {
        return category;
    }

    public String getBrand() {
        return brand;
    }

    public void getBrand(String brand) {
        this.brand = brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getNeededBy() { return neededBy; }
    public void setNeededBy(String neededBy) { this.neededBy = neededBy; }
    public String getBoughtBy() { return boughtBy; }
    public void setBoughtBy(String boughtBy) { this.boughtBy = boughtBy; }
    public boolean isPaid() { return isPaid; }
    public void setPaid(boolean paid) { isPaid = paid; }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
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

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", brand='" + brand + '\'' +
                ", expiration=" + expiration +
                ", price=" + price +
                ", neededBy='" + neededBy + '\'' +
                ", boughtBy='" + boughtBy + '\'' +
                ", isPaid=" + isPaid +
                '}';
    }
}
