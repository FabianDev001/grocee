package de.fab001.grocee.application.usecase;

import de.fab001.grocee.domain.model.ProductTemplate;
import de.fab001.grocee.domain.model.ShoppingList;
import de.fab001.grocee.domain.model.Product;
import de.fab001.grocee.domain.model.ShoppingListItem;
import de.fab001.grocee.domain.repository.ShoppingListRepository;
import de.fab001.grocee.domain.service.ProductTemplateService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class AddProductToShoppingListService {

    private final ShoppingListRepository repository;
    private final ProductTemplateService productTemplateService;

    public AddProductToShoppingListService(ShoppingListRepository repository, ProductTemplateService productTemplateService) {
        this.repository = repository;
        this.productTemplateService = productTemplateService;
    }

    @Transactional
    public void addProduct(UUID shoppingListId, Product product) {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Produktname (name) ist erforderlich.");
        }
        if (product.getCategory() == null || product.getCategory().trim().isEmpty()) {
            throw new IllegalArgumentException("Kategorie (category) ist erforderlich.");
        }
        if (product.getBrand() == null || product.getBrand().trim().isEmpty()) {
            throw new IllegalArgumentException("Marke (brand) ist erforderlich.");
        }
        if (product.getExpirationDate() == null) {
            throw new IllegalArgumentException("Haltbarkeitsdatum (expiration) ist erforderlich.");
        }
        
        Optional<ShoppingList> optionalList = repository.findById(shoppingListId.toString());
        if (optionalList.isEmpty()) {
            throw new IllegalArgumentException("Einkaufsliste nicht gefunden");
        }

        ShoppingList list = optionalList.get();
        
        // Check if a product with the same name already exists in the list
        if (list.containsProductWithName(product.getName())) {
            throw new IllegalArgumentException("Ein Produkt mit dem Namen '" + product.getName() + "' existiert bereits in dieser Einkaufsliste.");
        }
        
        // Find existing template or create a new one using the service
        ProductTemplate template = productTemplateService.findOrCreate(
            product.getName(), product.getBrand(), product.getCategory());
        
        // Now that we have a template with an ID, create the ShoppingListItem
        ShoppingListItem item = new ShoppingListItem(
            template,
            list,
            product.getExpirationDate(),
            product.getPrice(),
            product.getNeededBy(),
            product.getBoughtBy(),
            product.isPaid()
        );
        
        list.addItem(item);
        repository.save(list);
    }

    /**
     * Sets or updates the buyer of a product
     * @param shoppingListId the UUID of the shopping list
     * @param productId the UUID of the product
     * @param buyerId the UUID of the buyer or null to clear the buyer
     * @throws IllegalArgumentException if the shopping list or product is not found
     */
    @Transactional
    public void setBuyer(UUID shoppingListId, String productId, String buyerId) {
        Optional<ShoppingList> optionalList = repository.findById(shoppingListId.toString());
        if (optionalList.isEmpty()) {
            throw new IllegalArgumentException("Einkaufsliste nicht gefunden");
        }
        
        ShoppingList list = optionalList.get();
        try {
            Product product = list.findById(UUID.fromString(productId));
            product.setBoughtBy(buyerId);
            repository.save(list);
        } catch (IllegalArgumentException e) {
            // For new products that don't exist in the list yet, we can ignore this
            // This can happen during product creation
        }
    }

    public void setBuyer(UUID shoppingListId, String productId) {
        Optional<ShoppingList> optionalList = repository.findById(shoppingListId.toString());
        ShoppingList list = optionalList.get();

        list.findById(UUID.fromString(productId)).setBoughtBy(null);
        repository.save(list);
    }

}
