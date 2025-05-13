package de.fab001.grocee.domain.repository;

import de.fab001.grocee.domain.model.ProductTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductTemplateRepository extends JpaRepository<ProductTemplate, Long> {
    Optional<ProductTemplate> findByNameAndBrandAndCategory(String name, String brand, String category);
} 