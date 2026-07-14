package com.codewithmosh.store.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateNewProductResponse {
    private String name;
    private String description;
    private BigDecimal price;
    private Long categoryId;
}
