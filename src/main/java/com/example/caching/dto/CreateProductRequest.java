package com.example.caching.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateProductRequest(
      @NotBlank String name,
      @NotNull @Positive Double price,
      @NotNull @Min(0) Integer quantity
  ) {}
