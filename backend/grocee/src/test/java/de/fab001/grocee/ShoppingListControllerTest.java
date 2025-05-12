package de.fab001.grocee;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fab001.grocee.domain.model.ExpirationDate;
import de.fab001.grocee.domain.model.Product;
import de.fab001.grocee.domain.model.ShoppingList;
import de.fab001.grocee.domain.repository.ShoppingListRepository;
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

  
    @Test
    void addProductToList_success() throws Exception {
        UUID listId = UUID.randomUUID();
        ShoppingList list = new ShoppingList(listId, "Testliste");
        shoppingListRepository.save(list);

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
        ShoppingList list = new ShoppingList(listId, "Testliste");

        // Abgelaufen: gestern
        Product expired = new Product("Joghurt", "Milchprodukte", "Gut&Günstig",
                new ExpirationDate(LocalDate.now().minusDays(1)));
        // Nahend: in 3 Tagen
        Product expiring = new Product("Milch", "Milchprodukte", "Weihenstephan",
                new ExpirationDate(LocalDate.now().plusDays(3)));
        // Ok: in 10 Tagen
        Product ok = new Product("Käse", "Milchprodukte", "Alpenhain",
                new ExpirationDate(LocalDate.now().plusDays(10)));

        list.addProduct(expired);
        list.addProduct(expiring);
        list.addProduct(ok);
        shoppingListRepository.save(list);

        mockMvc.perform(get("/shoppinglists/" + listId + "/expiring-products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name=='Joghurt')].status").value("expired"))
                .andExpect(jsonPath("$[?(@.name=='Milch')].status").value("expiring"))
                .andExpect(jsonPath("$[?(@.name=='Käse')].status").value("ok"));
    }

    @Test
    void getExpiringProducts_emptyList_returnsEmptyArray() throws Exception {
        UUID listId = UUID.randomUUID();
        ShoppingList list = new ShoppingList(listId, "Testliste");
        shoppingListRepository.save(list);

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
        ShoppingList list = new ShoppingList(listId, "Testliste");

        UUID userA = UUID.randomUUID();
        UUID userB = UUID.randomUUID();

        Product p1 = new Product("Brot", "Backwaren", "Bäcker", new ExpirationDate(LocalDate.now().plusDays(2)), 10.0, userA.toString(), userB.toString(), false);
        Product p2 = new Product("Milch", "Milchprodukte", "Molkerei", new ExpirationDate(LocalDate.now().plusDays(5)), 5.0, userB.toString(), userA.toString(), false);
        Product p3 = new Product("Butter", "Milchprodukte", "Molkerei", new ExpirationDate(LocalDate.now().plusDays(5)), 3.0, userA.toString(), userB.toString(), true);

        list.addProduct(p1);
        list.addProduct(p2);
        list.addProduct(p3);
        shoppingListRepository.save(list);

        mockMvc.perform(get("/shoppinglists/" + listId + "/cost-allocation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['" + userA + "']['" + userB + "']").value(10.0))
                .andExpect(jsonPath("$['" + userB + "']['" + userA + "']").value(5.0))
                .andExpect(jsonPath("$['" + userA + "']['" + userB + "']").value(10.0));
    }

    @Test
    void addProductToList_missingExpiration_returns400() throws Exception {
        UUID listId = UUID.randomUUID();
        ShoppingList list = new ShoppingList(listId, "Testliste");
        shoppingListRepository.save(list);

        Product product = new Product();
        product.setName("Banana");
        product.setCategory("Fruit");
        product.setBrand("Chiquita");
        // Keine expiration gesetzt

        mockMvc.perform(post("/shoppinglists/" + listId + "/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.containsString("Product expiration date is required")));
    }

    @Test
    void addProductToList_missingName_returns400() throws Exception {
        UUID listId = UUID.randomUUID();
        ShoppingList list = new ShoppingList(listId, "Testliste");
        shoppingListRepository.save(list);

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
        ShoppingList list = new ShoppingList(listId, "Testliste");
        shoppingListRepository.save(list);

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
        ShoppingList list = new ShoppingList(listId, "Testliste");
        shoppingListRepository.save(list);

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