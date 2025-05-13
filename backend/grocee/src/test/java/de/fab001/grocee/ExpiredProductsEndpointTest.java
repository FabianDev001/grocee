package de.fab001.grocee;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fab001.grocee.domain.model.ExpirationDate;
import de.fab001.grocee.domain.model.Product;
import de.fab001.grocee.domain.repository.ShoppingListRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ExpiredProductsEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ShoppingListRepository shoppingListRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getExpiringProducts_allProductStatuses_returnsCorrectClassification() throws Exception {
        UUID listId = UUID.randomUUID();
        TestShoppingList list = new TestShoppingList(listId, "Test Shopping List");

        // Create products with various expiration dates
        Product expired = new Product("Eggs", "Dairy", "Farm Fresh", 
                new ExpirationDate(LocalDate.now().minusDays(2)));
        
        Product expiringToday = new Product("Bread", "Bakery", "Local Bakery",
                new ExpirationDate(LocalDate.now()));
        
        Product expiringSoon = new Product("Milk", "Dairy", "Brand A",
                new ExpirationDate(LocalDate.now().plusDays(3)));
        
        Product expiringEdgeCase = new Product("Yogurt", "Dairy", "Brand B",
                new ExpirationDate(LocalDate.now().plusDays(6)));
        
        Product notExpiringSoon = new Product("Cheese", "Dairy", "Brand C",
                new ExpirationDate(LocalDate.now().plusDays(7)));
        
        Product farFromExpiring = new Product("Canned Beans", "Canned Goods", "Brand D",
                new ExpirationDate(LocalDate.now().plusDays(365)));

        // Add products to the list
        list.addProduct(expired);
        list.addProduct(expiringToday);
        list.addProduct(expiringSoon);
        list.addProduct(expiringEdgeCase);
        list.addProduct(notExpiringSoon);
        list.addProduct(farFromExpiring);
        
        shoppingListRepository.save(list);

        // Perform the request and validate response
        mockMvc.perform(get("/shoppinglists/" + listId + "/expiring-products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name=='Eggs')].status").value("expired"))
                .andExpect(jsonPath("$[?(@.name=='Bread')].status").value("expiring"))
                .andExpect(jsonPath("$[?(@.name=='Milk')].status").value("expiring"))
                .andExpect(jsonPath("$[?(@.name=='Yogurt')].status").value("expiring"))
                .andExpect(jsonPath("$[?(@.name=='Cheese')].status").value("ok"))
                .andExpect(jsonPath("$[?(@.name=='Canned Beans')].status").value("ok"));
    }

    @Test
    void getExpiringProducts_productWithNullExpirationDate_returnsUnknownStatus() throws Exception {
        UUID listId = UUID.randomUUID();
        TestShoppingList list = new TestShoppingList(listId, "Test List");
        
        // Create a product without setting expiration date
        Product product = new Product();
        product.setName("Test Product");
        product.setCategory("Test Category");
        product.setBrand("Test Brand");
        // Deliberately not setting expiration date
        
        list.addProduct(product);
        shoppingListRepository.save(list);

        // Perform request and validate response
        mockMvc.perform(get("/shoppinglists/" + listId + "/expiring-products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name=='Test Product')].status").value("unknown"));
    }

    @Test
    void getExpiringProducts_verifyResponseStructure() throws Exception {
        UUID listId = UUID.randomUUID();
        TestShoppingList list = new TestShoppingList(listId, "Test List");
        
        Product product = new Product("Test Product", "Test Category", "Test Brand",
                new ExpirationDate(LocalDate.now().plusDays(5)));
        product.setPrice(9.99);
        product.setNeededBy("User123");
        product.setBoughtBy("User456");
        
        list.addProduct(product);
        shoppingListRepository.save(list);

        // Perform request and get the JSON response
        MvcResult result = mockMvc.perform(get("/shoppinglists/" + listId + "/expiring-products"))
                .andExpect(status().isOk())
                .andReturn();
        
        String content = result.getResponse().getContentAsString();
        List<Map<String, Object>> responseList = objectMapper.readValue(content, List.class);
        
        assertEquals(1, responseList.size());
        Map<String, Object> responseProduct = responseList.get(0);
        
        // Verify all expected fields are present
        assertNotNull(responseProduct.get("id"));
        assertEquals("Test Product", responseProduct.get("name"));
        assertEquals("Test Category", responseProduct.get("category"));
        assertEquals("Test Brand", responseProduct.get("brand"));
        assertNotNull(responseProduct.get("expiration"));
        assertEquals("expiring", responseProduct.get("status"));
    }

    @Test
    void getExpiringProducts_multipleListsWithSameProductNames_onlyReturnsProductsFromRequestedList() throws Exception {
        // Create first list
        UUID listId1 = UUID.randomUUID();
        TestShoppingList list1 = new TestShoppingList(listId1, "List 1");
        Product product1 = new Product("Milk", "Dairy", "Brand X", 
                new ExpirationDate(LocalDate.now().minusDays(1)));
        list1.addProduct(product1);
        
        // Create second list with same product name but different expiration
        UUID listId2 = UUID.randomUUID();
        TestShoppingList list2 = new TestShoppingList(listId2, "List 2");
        Product product2 = new Product("Milk", "Dairy", "Brand X", 
                new ExpirationDate(LocalDate.now().plusDays(10)));
        list2.addProduct(product2);
        
        shoppingListRepository.save(list1);
        shoppingListRepository.save(list2);

        // Check first list - should have expired product
        mockMvc.perform(get("/shoppinglists/" + listId1 + "/expiring-products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[?(@.name=='Milk')].status").value("expired"));
        
        // Check second list - should have ok product
        mockMvc.perform(get("/shoppinglists/" + listId2 + "/expiring-products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[?(@.name=='Milk')].status").value("ok"));
    }
} 