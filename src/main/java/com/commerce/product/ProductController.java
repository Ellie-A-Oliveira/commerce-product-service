package com.commerce.product;

import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductController {
  private final ProductService service;
  public ProductController(ProductService service) { this.service = service; }

  @GetMapping
  public List<ProductResponse> list() { return service.listActive().stream().map(ProductResponse::from).toList(); }

  @PostMapping
  public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(ProductResponse.from(service.create(request)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ProductResponse> get(@PathVariable String id) {
    return ResponseEntity.ok(ProductResponse.from(service.getActive(id)));
  }

  @PutMapping("/{id}")
  public ProductResponse update(@PathVariable String id, @Valid @RequestBody ProductRequest request) {
    return ProductResponse.from(service.update(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deactivate(@PathVariable String id) {
    service.deactivate(id);
    return ResponseEntity.noContent().build();
  }
}

record ProductResponse(String id, String name, String description, long priceInCents, int availableStock, boolean active, boolean available, List<String> categoryIds) {
  static ProductResponse from(Product product) {
    return new ProductResponse(product.getId(), product.getName(), product.getDescription(), product.getCurrentPrice().getPriceInCents(), product.getAvailability().getAvailableStock(), product.isActive(), product.getAvailability().isAvailable(), product.getCategories().stream().map(Category::getId).toList());
  }
}
