package com.commerce.product;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {
  @Override
  @EntityGraph(attributePaths = {"categories", "currentPrice", "availability"})
  List<Product> findAll();

  @EntityGraph(attributePaths = {"categories", "currentPrice", "availability"})
  List<Product> findByActiveTrue();

  @EntityGraph(attributePaths = {"categories", "currentPrice", "availability"})
  Optional<Product> findByIdAndActiveTrue(String id);

  @Override
  @EntityGraph(attributePaths = {"categories", "currentPrice", "availability"})
  Optional<Product> findById(String id);
}
