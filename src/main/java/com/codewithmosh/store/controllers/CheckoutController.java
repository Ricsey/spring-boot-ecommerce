package com.codewithmosh.store.controllers;

import com.codewithmosh.store.dtos.OrderCheckoutRequest;
import com.codewithmosh.store.dtos.OrderCheckoutResponse;
import com.codewithmosh.store.repositories.CartRepository;
import com.codewithmosh.store.services.OrderService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/checkout")
public class CheckoutController {
    private final CartRepository cartRepository;
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<?> checkoutOrder(
            @Valid @RequestBody OrderCheckoutRequest request
    ) {
        var responseDto = orderService.checkoutOrder(request.getCartId());

        return ResponseEntity.ok(responseDto);
    }
}
