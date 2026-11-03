package com.commerce.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
@Transactional
class ProductPersistenceTest {
  @Container static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

  @DynamicPropertySource
  static void databaseProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  @Autowired ProductRepository products;
  @Autowired EntityManager entityManager;

  @Test
  void persistsProductWithCategoriesAndLoadsRelationshipsForResponses() {
    Category category = new Category("cat_keyboard", "Keyboards");
    ProductPrice price = new ProductPrice("price_keyboard", 12900);
    ProductAvailability availability = new ProductAvailability("availability_keyboard", 42, true);
    Product product = new Product("sku_keyboard", "Demo Keyboard", "A mechanical keyboard", true, price, availability);
    product.getCategories().add(category);

    entityManager.persist(category);
    entityManager.persist(price);
    entityManager.persist(availability);
    entityManager.persist(product);
    entityManager.flush();
    entityManager.clear();

    Product loaded = products.findById(product.getId()).orElseThrow();
    ProductResponse response = ProductResponse.from(loaded);

    assertThat(response.priceInCents()).isEqualTo(12900);
    assertThat(response.availableStock()).isEqualTo(42);
    assertThat(response.categoryIds()).containsExactly("cat_keyboard");
    assertThat(loaded.getCategories()).extracting(Category::getId).containsExactly("cat_keyboard");
  }

  @Test
  void enforcesCurrentPriceOneToOneConstraint() {
    ProductPrice price = new ProductPrice("price_shared", 4900);
    ProductAvailability firstAvailability = new ProductAvailability("availability_first", 10, true);
    ProductAvailability secondAvailability = new ProductAvailability("availability_second", 20, true);
    entityManager.persist(price);
    entityManager.persist(firstAvailability);
    entityManager.persist(secondAvailability);
    entityManager.persist(new Product("sku_mouse", "Demo Mouse", "Wireless mouse", true, price, firstAvailability));
    entityManager.flush();

    Product duplicate = new Product("sku_duplicate", "Duplicate", "Should fail", true, price, secondAvailability);
    entityManager.persist(duplicate);

    assertThatThrownBy(() -> entityManager.flush())
        .isInstanceOf(PersistenceException.class);
  }

  @Test
  void enforcesAvailabilityOneToOneConstraint() {
    ProductPrice firstPrice = new ProductPrice("price_first", 4900);
    ProductPrice secondPrice = new ProductPrice("price_second", 5900);
    ProductAvailability availability = new ProductAvailability("availability_shared", 10, true);
    entityManager.persist(firstPrice);
    entityManager.persist(secondPrice);
    entityManager.persist(availability);
    entityManager.persist(new Product("sku_mouse", "Demo Mouse", "Wireless mouse", true, firstPrice, availability));
    entityManager.flush();

    Product duplicate = new Product("sku_duplicate", "Duplicate", "Should fail", true, secondPrice, availability);
    entityManager.persist(duplicate);

    assertThatThrownBy(() -> entityManager.flush())
        .isInstanceOf(PersistenceException.class);
  }
}
