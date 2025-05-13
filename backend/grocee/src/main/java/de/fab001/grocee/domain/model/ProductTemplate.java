package de.fab001.grocee.domain.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name", "brand", "category"}))
public class ProductTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String brand;
    
    @Column(nullable = false)
    private String category;

    public ProductTemplate() {}
    public ProductTemplate(String name, String brand, String category) {
        this.name = name;
        this.brand = brand;
        this.category = category;
    }
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getBrand() { return brand; }
    public String getCategory() { return category; }
    public void setName(String name) { this.name = name; }
    public void setBrand(String brand) { this.brand = brand; }
    public void setCategory(String category) { this.category = category; }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductTemplate)) return false;
        ProductTemplate that = (ProductTemplate) o;
        return Objects.equals(name, that.name) && Objects.equals(brand, that.brand) && Objects.equals(category, that.category);
    }
    @Override
    public int hashCode() {
        return Objects.hash(name, brand, category);
    }
}
