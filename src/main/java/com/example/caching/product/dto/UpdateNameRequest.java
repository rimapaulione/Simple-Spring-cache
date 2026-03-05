package com.example.caching.product.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateNameRequest(@NotBlank String name) {}
