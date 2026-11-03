package com.commerce.product;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductService {
  private final ProductRepository products;
  private final CategoryRepository categories;

  public ProductService(ProductRepository products, CategoryRepository categories) {
    this.products = products;
    this.categories = categories;
  }

  @Transactional(readOnly = true)
  public List<Product> listActive() { return products.findByActiveTrue(); }

  @Transactional(readOnly = true)
  public Product getActive(String id) {
    return products.findByIdAndActiveTrue(id).orElseThrow(() -> new ProductNotFoundException(id));
  }

  public Product create(ProductRequest request) {
    if (products.existsById(request.id())) throw new DuplicateProductException(request.id());
    Product product = new Product(request.id(), request.name(), request.description(), true,
        new ProductPrice("price_" + UUID.randomUUID(), request.priceInCents()),
        new ProductAvailability("availability_" + UUID.randomUUID(), request.availableStock(), request.available()));
    product.getCategories().addAll(resolveCategories(request.categoryIds()));
    try {
      return products.save(product);
    } catch (DataIntegrityViolationException exception) {
      throw new DuplicateProductException(request.id());
    }
  }

  public Product update(String id, ProductRequest request) {
    Product product = products.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    product.update(request.name(), request.description(),
        new ProductPrice(product.getCurrentPrice().getId(), request.priceInCents()),
        new ProductAvailability(product.getAvailability().getId(), request.availableStock(), request.available()),
        resolveCategories(request.categoryIds()));
    return product;
  }

  public void deactivate(String id) {
    Product product = products.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    product.deactivate();
  }

  private LinkedHashSet<Category> resolveCategories(List<String> categoryIds) {
    List<Category> resolved = categories.findAllById(categoryIds);
    if (resolved.size() != new LinkedHashSet<>(categoryIds).size()) throw new UnknownCategoryException();
    return new LinkedHashSet<>(resolved);
  }
}

class ProductNotFoundException extends RuntimeException {
  ProductNotFoundException(String id) { super("Product not found: " + id); }
}

class DuplicateProductException extends RuntimeException {
  DuplicateProductException(String id) { super("Product already exists: " + id); }
}

class UnknownCategoryException extends RuntimeException {
  UnknownCategoryException() { super("One or more category IDs do not exist"); }
}
