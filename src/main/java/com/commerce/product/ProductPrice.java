package com.commerce.product;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "product_prices")
public class ProductPrice {
  @Id private String id;
  private long priceInCents;
  protected ProductPrice() {}
  public ProductPrice(String id, long priceInCents) { this.id = id; this.priceInCents = priceInCents; }
  public String getId() { return id; }
  public long getPriceInCents() { return priceInCents; }
}
