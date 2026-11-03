package com.commerce.product;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "products")
public class Product {
  @Id private String id;
  private String name;
  private String description;
  private boolean active;
  @OneToOne(optional = false) private ProductPrice currentPrice;
  @OneToOne(optional = false) private ProductAvailability availability;
  @ManyToMany
  @JoinTable(name = "product_categories", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
  private Set<Category> categories = new LinkedHashSet<>();

  protected Product() {}
  public Product(String id, String name, String description, boolean active, ProductPrice price, ProductAvailability availability) {
    this.id = id; this.name = name; this.description = description; this.active = active;
    this.currentPrice = price; this.availability = availability;
  }
  public String getId() { return id; }
  public String getName() { return name; }
  public String getDescription() { return description; }
  public boolean isActive() { return active; }
  public ProductPrice getCurrentPrice() { return currentPrice; }
  public ProductAvailability getAvailability() { return availability; }
  public Set<Category> getCategories() { return categories; }
  public void update(String name, String description, ProductPrice price, ProductAvailability availability, Set<Category> categories) {
    this.name = name;
    this.description = description;
    this.currentPrice.updatePrice(price.getPriceInCents());
    this.availability.update(availability.getAvailableStock(), availability.isAvailable());
    this.categories = new LinkedHashSet<>(categories);
  }
  public void deactivate() { this.active = false; }
}
