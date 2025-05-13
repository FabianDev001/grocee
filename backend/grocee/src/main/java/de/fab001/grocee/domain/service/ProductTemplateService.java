package de.fab001.grocee.domain.service;

import de.fab001.grocee.domain.model.ProductTemplate;
import de.fab001.grocee.domain.repository.ProductTemplateRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ProductTemplateService {
    
    private final ProductTemplateRepository productTemplateRepository;
    
    public ProductTemplateService(ProductTemplateRepository productTemplateRepository) {
        this.productTemplateRepository = productTemplateRepository;
    }
    
    /**
     * Find an existing template or create a new one.
     * This method ensures that a ProductTemplate with the given parameters exists in the database.
     * 
     * @param name The product name
     * @param brand The product brand
     * @param category The product category
     * @return A ProductTemplate entity that is guaranteed to be persisted in the database
     */
    @Transactional
    public ProductTemplate findOrCreate(String name, String brand, String category) {
        // First try to find an existing template with the same properties
        Optional<ProductTemplate> existingTemplate = 
            productTemplateRepository.findByNameAndBrandAndCategory(name, brand, category);
        
        if (existingTemplate.isPresent()) {
            return existingTemplate.get();
        }
        
        try {
            // Create a new template if none exists
            ProductTemplate newTemplate = new ProductTemplate(name, brand, category);
            return productTemplateRepository.save(newTemplate);
        } catch (DataIntegrityViolationException e) {
            // If there's a concurrent save, try to find it again
            return productTemplateRepository.findByNameAndBrandAndCategory(name, brand, category)
                .orElseThrow(() -> new RuntimeException("Failed to create or find product template", e));
        }
    }
} 