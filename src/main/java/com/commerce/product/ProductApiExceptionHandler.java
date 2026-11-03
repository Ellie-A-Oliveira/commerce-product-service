package com.commerce.product;

import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class ProductApiExceptionHandler {
  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<Map<String, Object>> validation(MethodArgumentNotValidException exception) {
    Map<String, String> errors = exception.getBindingResult().getFieldErrors().stream()
        .collect(Collectors.toMap(error -> error.getField(), error -> error.getDefaultMessage(), (first, ignored) -> first));
    return ResponseEntity.badRequest().body(Map.of("error", "Validation failed", "fields", errors));
  }

  @ExceptionHandler(UnknownCategoryException.class)
  ResponseEntity<Map<String, String>> unknownCategory(UnknownCategoryException exception) {
    return ResponseEntity.badRequest().body(Map.of("error", exception.getMessage()));
  }

  @ExceptionHandler(ProductNotFoundException.class)
  ResponseEntity<Void> notFound() { return ResponseEntity.notFound().build(); }

  @ExceptionHandler(DuplicateProductException.class)
  ResponseEntity<Map<String, String>> duplicate(DuplicateProductException exception) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", exception.getMessage()));
  }
}
