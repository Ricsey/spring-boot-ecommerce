package com.codewithmosh.store.controllers;

import com.codewithmosh.store.dtos.ErrorDto;
import com.codewithmosh.store.dtos.OrderCheckoutRequest;
import com.codewithmosh.store.dtos.OrderCheckoutResponse;
import com.codewithmosh.store.exceptions.CartIsEmptyException;
import com.codewithmosh.store.exceptions.CartNotFoundException;
import com.codewithmosh.store.exceptions.PaymentException;
import com.codewithmosh.store.repositories.OrderRepository;
import com.codewithmosh.store.services.CheckoutService;
import com.codewithmosh.store.services.WebhookRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/checkout")
public class CheckoutController {
    private final CheckoutService checkoutService;
    private final OrderRepository orderRepository;

    @PostMapping
    public OrderCheckoutResponse checkoutOrder(
            @Valid @RequestBody OrderCheckoutRequest request
    ) {
        return checkoutService.checkoutOrder(request.getCartId());
    }

    @PostMapping("/webhook")
    public void handleWebhook(
            @RequestHeader Map<String, String> headers,
            @RequestBody String payload
    ) {
        checkoutService.handleWebhookEvent(new WebhookRequest(headers, payload));
    }

    @ExceptionHandler({ CartNotFoundException.class, CartIsEmptyException.class })
    public ResponseEntity<ErrorDto> handleException(Exception ex) {
        return ResponseEntity.badRequest().body(new ErrorDto(ex.getMessage()));
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<?> handlePaymentException(PaymentException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating a checkout session: " + ex.getMessage());
    }
}
