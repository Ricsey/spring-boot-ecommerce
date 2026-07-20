package com.codewithmosh.store.dtos;

import lombok.Data;

import java.math.BigInteger;
import java.util.UUID;

@Data
public class OrderCheckoutResponse {
    private Long orderId;

    public OrderCheckoutResponse(Long id) {
        this.orderId = id;
    }
}
