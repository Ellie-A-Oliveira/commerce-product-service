package com.commerce.product;
import org.springframework.boot.*; import org.springframework.boot.autoconfigure.SpringBootApplication; import org.springframework.web.bind.annotation.*; import java.util.List;
@SpringBootApplication public class ProductApplication { public static void main(String[] args) { SpringApplication.run(ProductApplication.class, args); } }
record Product(String id, String name, long priceInCents, int availableStock) {}
@RestController @RequestMapping("/products") class ProductController {
  @GetMapping List<Product> list() { return List.of(new Product("sku_1", "Demo Keyboard", 12900, 42), new Product("sku_2", "Demo Mouse", 4900, 80)); }
  @GetMapping("/{id}") Product get(@PathVariable String id) { return new Product(id, "Demo Product", 9999, 10); }
}
