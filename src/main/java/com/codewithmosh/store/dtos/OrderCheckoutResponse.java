package com.codewithmosh.store.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigInteger;
import java.util.UUID;

@Data
@AllArgsConstructor
public class OrderCheckoutResponse {
    private Long orderId;
}
