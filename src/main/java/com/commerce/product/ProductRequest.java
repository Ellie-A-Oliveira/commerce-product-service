package com.commerce.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

record ProductRequest(
    @NotBlank String id,
    @NotBlank String name,
    @Size(max = 2000) String description,
    @Min(0) long priceInCents,
    @Min(0) int availableStock,
    @NotNull Boolean available,
    List<String> categoryIds) {
  ProductRequest {
    categoryIds = categoryIds == null ? List.of() : List.copyOf(categoryIds);
  }
}
