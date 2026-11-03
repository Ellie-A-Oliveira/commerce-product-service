package com.commerce.product;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "categories")
public class Category {
  @Id
  private String id;
  private String name;

  protected Category() {}
  public Category(String id, String name) { this.id = id; this.name = name; }
  public String getId() { return id; }
  public String getName() { return name; }
}
