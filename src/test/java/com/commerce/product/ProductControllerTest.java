package com.commerce.product;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.junit.jupiter.api.BeforeEach;

class ProductControllerTest {
  private MockMvc mvc;
  private FakeProductService service;

  @BeforeEach
  void setUp() {
    service = new FakeProductService();
    mvc = MockMvcBuilders.standaloneSetup(new ProductController(service))
        .setControllerAdvice(new ProductApiExceptionHandler()).build();
  }

  @Test
  void createsProductWithCreatedResponse() throws Exception {
    service.created = product("sku_keyboard", true);

    mvc.perform(post("/products").contentType(MediaType.APPLICATION_JSON)
        .content("{\"id\":\"sku_keyboard\",\"name\":\"Keyboard\",\"description\":\"Mechanical\",\"priceInCents\":12900,\"availableStock\":42,\"available\":true,\"categoryIds\":[]}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value("sku_keyboard"));
  }

  @Test
  void rejectsInvalidProduct() throws Exception {
    mvc.perform(post("/products").contentType(MediaType.APPLICATION_JSON)
        .content("{\"id\":\"\",\"name\":\"\",\"priceInCents\":-1,\"availableStock\":-1}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Validation failed"));
  }

  @Test
  void listsOnlyProductsReturnedByActiveService() throws Exception {
    service.active = List.of(product("sku_active", true));

    mvc.perform(get("/products")).andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value("sku_active"));
  }

  @Test
  void deactivatesProduct() throws Exception {
    mvc.perform(delete("/products/sku_keyboard")).andExpect(status().isNoContent());
    org.assertj.core.api.Assertions.assertThat(service.deactivatedId).isEqualTo("sku_keyboard");
  }

  @Test
  void returnsNotFoundForMissingProduct() throws Exception {
    service.missingId = "sku_missing";

    mvc.perform(delete("/products/sku_missing")).andExpect(status().isNotFound());
  }

  @Test
  void updatesProduct() throws Exception {
    service.updated = product("sku_keyboard", true);

    mvc.perform(put("/products/sku_keyboard").contentType(MediaType.APPLICATION_JSON)
        .content("{\"id\":\"sku_keyboard\",\"name\":\"Updated\",\"description\":\"New\",\"priceInCents\":13000,\"availableStock\":10,\"available\":true,\"categoryIds\":[]}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("sku_keyboard"));
  }

  private static Product product(String id, boolean active) {
    return new Product(id, "Demo", "Description", active,
        new ProductPrice("price_" + id, 1000), new ProductAvailability("availability_" + id, 3, true));
  }

  private static final class FakeProductService extends ProductService {
    private List<Product> active = List.of();
    private Product created;
    private Product updated;
    private String deactivatedId;
    private String missingId;

    FakeProductService() { super(null, null); }
    @Override public List<Product> listActive() { return active; }
    @Override public Product create(ProductRequest request) { return created; }
    @Override public Product update(String id, ProductRequest request) { return updated; }
    @Override public void deactivate(String id) {
      if (id.equals(missingId)) throw new ProductNotFoundException(id);
      deactivatedId = id;
    }
  }
}
