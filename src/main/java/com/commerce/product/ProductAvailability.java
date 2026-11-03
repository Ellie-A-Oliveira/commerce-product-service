package com.commerce.product;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "product_availability")
public class ProductAvailability {
  @Id private String id;
  private int availableStock;
  private boolean available;
  protected ProductAvailability() {}
  public ProductAvailability(String id, int availableStock, boolean available) { this.id = id; this.availableStock = availableStock; this.available = available; }
  public String getId() { return id; }
  public int getAvailableStock() { return availableStock; }
  public boolean isAvailable() { return available; }
  public void update(int availableStock, boolean available) {
    this.availableStock = availableStock;
    this.available = available;
  }
}
