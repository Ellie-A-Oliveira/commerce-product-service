package com.commerce.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
  @Mock ProductRepository products;
  @Mock CategoryRepository categories;
  @InjectMocks ProductService service;

  @Test
  void listsActiveProductsOnly() {
    Product active = product("sku_active", true);
    when(products.findByActiveTrue()).thenReturn(List.of(active));

    assertThat(service.listActive()).containsExactly(active);
  }

  @Test
  void hidesDeactivatedProductFromLookup() {
    when(products.findByIdAndActiveTrue("sku_inactive")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.getActive("sku_inactive"))
        .isInstanceOf(ProductNotFoundException.class);
  }

  @Test
  void deactivatesExistingProduct() {
    Product product = product("sku_keyboard", true);
    when(products.findById("sku_keyboard")).thenReturn(Optional.of(product));

    service.deactivate("sku_keyboard");

    assertThat(product.isActive()).isFalse();
    verify(products).findById("sku_keyboard");
  }

  @Test
  void updatesManagedProductFieldsWithoutReplacingPersistenceChildren() {
    Product product = product("sku_keyboard", true);
    when(products.findById("sku_keyboard")).thenReturn(Optional.of(product));
    when(categories.findAllById(List.of())).thenReturn(List.of());

    service.update("sku_keyboard", new ProductRequest("sku_keyboard", "Updated", "New description", 1500, 7, false, List.of()));

    assertThat(product.getName()).isEqualTo("Updated");
    assertThat(product.getCurrentPrice().getPriceInCents()).isEqualTo(1500);
    assertThat(product.getAvailability().getAvailableStock()).isEqualTo(7);
    assertThat(product.getAvailability().isAvailable()).isFalse();
  }

  @Test
  void rejectsUnknownCategories() {
    when(products.existsById("sku_keyboard")).thenReturn(false);
    when(categories.findAllById(List.of("cat_missing"))).thenReturn(List.of());

    assertThatThrownBy(() -> service.create(new ProductRequest("sku_keyboard", "Keyboard", "", 1000, 1, true, List.of("cat_missing"))))
        .isInstanceOf(UnknownCategoryException.class);
  }

  private static Product product(String id, boolean active) {
    return new Product(id, "Demo", "Description", active,
        new ProductPrice("price_" + id, 1000), new ProductAvailability("availability_" + id, 3, true));
  }
}
