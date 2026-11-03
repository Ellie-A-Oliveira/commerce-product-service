package com.commerce.product;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductController {
  private final ProductRepository repository;
  public ProductController(ProductRepository repository) { this.repository = repository; }

  @GetMapping
  List<ProductResponse> list() { return repository.findAll().stream().map(ProductResponse::from).toList(); }

  @GetMapping("/{id}")
  ResponseEntity<ProductResponse> get(@PathVariable String id) {
    return repository.findById(id).map(product -> ResponseEntity.ok(ProductResponse.from(product)))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }
}

record ProductResponse(String id, String name, String description, long priceInCents, int availableStock, boolean active, boolean available, List<String> categoryIds) {
  static ProductResponse from(Product product) {
    return new ProductResponse(product.getId(), product.getName(), product.getDescription(), product.getCurrentPrice().getPriceInCents(), product.getAvailability().getAvailableStock(), product.isActive(), product.getAvailability().isAvailable(), product.getCategories().stream().map(Category::getId).toList());
  }
}
