package de.fab001.grocee;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fab001.grocee.domain.model.ExpirationDate;
import de.fab001.grocee.domain.model.Product;
import de.fab001.grocee.domain.model.ProductTemplate;
import de.fab001.grocee.domain.model.ShoppingList;
import de.fab001.grocee.domain.model.ShoppingListItem;
import de.fab001.grocee.domain.repository.ShoppingListRepository;
import de.fab001.grocee.domain.service.ProductTemplateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ShoppingListControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ShoppingListRepository shoppingListRepository;
    
    @Autowired
    private ProductTemplateService productTemplateService;
    
    /**
     * Helper method to create a shopping list and save it to the repository
     */
    private ShoppingList createShoppingList(UUID id, String name) {
        ShoppingList list = new ShoppingList(id, name);
        return shoppingListRepository.save(list);
    }
    
    /**
     * Helper method to add a product to a shopping list using templates
     */
    private Product addProductToList(ShoppingList list, String name, String category, String brand, 
                                   LocalDate expirationDate, double price, String neededBy, String boughtBy, boolean isPaid) {
        // Get or create a template
        ProductTemplate template = productTemplateService.findOrCreate(name, brand, category);
        
        // Create a shopping list item
        ShoppingListItem item = new ShoppingListItem(
            template,
            list,
            expirationDate != null ? new ExpirationDate(expirationDate) : null,
            price,
            neededBy,
            boughtBy,
            isPaid
        );
        
        // Add the item to the list
        list.addItem(item);
        
        // Save the list
        shoppingListRepository.save(list);
        
        // Return a Product representation (for test assertions)
        Product product = new Product(name, category, brand,
                expirationDate != null ? new ExpirationDate(expirationDate) : null,
                price, neededBy, boughtBy, isPaid);
        return product;
    }

    @Test
    void addProductToList_success() throws Exception {
        // Create and save the shopping list
        UUID listId = UUID.randomUUID();
        ShoppingList list = createShoppingList(listId, "Testliste");
        
        // Create a product to add via API
        Product product = new Product("Banana", "Fruit", "Chiquita", new ExpirationDate(LocalDate.now().plusDays(5)));
        product.setNeededBy(UUID.randomUUID().toString());
        product.setBoughtBy(UUID.randomUUID().toString());

        mockMvc.perform(post("/shoppinglists/" + listId + "/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated());
    }

    @Test
    void getExpiringProducts_statusClassification() throws Exception {
        UUID listId = UUID.randomUUID();
        ShoppingList list = createShoppingList(listId, "Testliste");

        // Add products with various expiration dates
        addProductToList(list, "Joghurt", "Milchprodukte", "Gut&Günstig",
                LocalDate.now().minusDays(1), 0.0, UUID.randomUUID().toString(), null, false);
        
        addProductToList(list, "Milch", "Milchprodukte", "Weihenstephan",
                LocalDate.now().plusDays(3), 0.0, UUID.randomUUID().toString(), null, false);
                
        addProductToList(list, "Käse", "Milchprodukte", "Alpenhain",
                LocalDate.now().plusDays(10), 0.0, UUID.randomUUID().toString(), null, false);

        mockMvc.perform(get("/shoppinglists/" + listId + "/expiring-products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name=='Joghurt')].status").value("expired"))
                .andExpect(jsonPath("$[?(@.name=='Milch')].status").value("expiring"))
                .andExpect(jsonPath("$[?(@.name=='Käse')].status").value("ok"));
    }

    @Test
    void getExpiringProducts_emptyList_returnsEmptyArray() throws Exception {
        UUID listId = UUID.randomUUID();
        ShoppingList list = createShoppingList(listId, "Testliste");

        mockMvc.perform(get("/shoppinglists/" + listId + "/expiring-products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getExpiringProducts_listNotFound_returns404() throws Exception {
        UUID listId = UUID.randomUUID();
        mockMvc.perform(get("/shoppinglists/" + listId + "/expiring-products"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCostAllocation_returnsCorrectDebts() throws Exception {
        UUID listId = UUID.randomUUID();
        ShoppingList list = createShoppingList(listId, "Testliste");

        // Use consistent UUIDs for the test
        String userA = "e3c8fba4-4b79-43b2-8b51-825da7cf65c0";
        String userB = "dbb90609-2833-4daf-be47-23cb91bc68b6";

        // Add products with cost information
        addProductToList(list, "Brot", "Backwaren", "Bäcker", 
            LocalDate.now().plusDays(2), 10.0, userA, userB, false);
            
        addProductToList(list, "Milch", "Milchprodukte", "Molkerei", 
            LocalDate.now().plusDays(5), 5.0, userB, userA, false);
            
        addProductToList(list, "Butter", "Milchprodukte", "Molkerei", 
            LocalDate.now().plusDays(5), 3.0, userA, userB, true);

        mockMvc.perform(get("/shoppinglists/" + listId + "/cost-allocation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['" + userA + "']['" + userB + "']").value(10.0))
                .andExpect(jsonPath("$['" + userB + "']['" + userA + "']").value(5.0));
    }

    @Test
    void addProductToList_missingExpiration_returns400() throws Exception {
        UUID listId = UUID.randomUUID();
        ShoppingList list = createShoppingList(listId, "Testliste");

        Product product = new Product();
        product.setName("Banana");
        product.setCategory("Fruit");
        product.setBrand("Chiquita");
        // No expiration set

        mockMvc.perform(post("/shoppinglists/" + listId + "/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.containsString("Product expiration date is required")));
    }

    @Test
    void addProductToList_missingName_returns400() throws Exception {
        UUID listId = UUID.randomUUID();
        ShoppingList list = createShoppingList(listId, "Testliste");

        Product product = new Product();
        product.setCategory("Fruit");
        product.setBrand("Chiquita");
        product.setExpiration(new ExpirationDate(LocalDate.now().plusDays(5)));

        mockMvc.perform(post("/shoppinglists/" + listId + "/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.containsString("Product name is required")));
    }

    @Test
    void addProductToList_missingCategory_returns400() throws Exception {
        UUID listId = UUID.randomUUID();
        ShoppingList list = createShoppingList(listId, "Testliste");

        Product product = new Product();
        product.setName("Banana");
        product.setBrand("Chiquita");
        product.setExpiration(new ExpirationDate(LocalDate.now().plusDays(5)));

        mockMvc.perform(post("/shoppinglists/" + listId + "/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.containsString("Product category is required")));
    }

    @Test
    void addProductToList_missingBrand_returns400() throws Exception {
        UUID listId = UUID.randomUUID();
        ShoppingList list = createShoppingList(listId, "Testliste");

        Product product = new Product();
        product.setName("Banana");
        product.setCategory("Fruit");
        product.setExpiration(new ExpirationDate(LocalDate.now().plusDays(5)));

        mockMvc.perform(post("/shoppinglists/" + listId + "/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.containsString("Product brand is required")));
    }
} 