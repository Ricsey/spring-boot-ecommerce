package com.codewithmosh.store.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigInteger;
import java.util.UUID;

@Data
public class OrderCheckoutResponse {
    private Long orderId;
    private String checkoutUrl;

    public OrderCheckoutResponse(Long orderId, String checkoutUrl) {
        this.orderId = orderId;
        this.checkoutUrl = checkoutUrl;
    }
}
