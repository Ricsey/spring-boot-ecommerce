package com.codewithmosh.store.services;

import com.codewithmosh.store.dtos.OrderCheckoutResponse;
import com.codewithmosh.store.entities.Order;
import com.codewithmosh.store.entities.PaymentStatus;
import com.codewithmosh.store.exceptions.CartIsEmptyException;
import com.codewithmosh.store.exceptions.PaymentException;
import com.codewithmosh.store.repositories.CartRepository;
import com.codewithmosh.store.repositories.OrderRepository;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.codewithmosh.store.exceptions.CartNotFoundException;

import java.util.UUID;

@RequiredArgsConstructor // only final fields are injected
@Service
public class CheckoutService {
    private final AuthService authService;
    private final CartService cartService;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final PaymentGateway paymentGateway;

    @Transactional
    public OrderCheckoutResponse checkoutOrder(UUID cartId) {
        var cart = cartRepository.getCartWithItems(cartId).orElseThrow(CartNotFoundException::new);

        if (cart.isEmpty()) {
            throw new CartIsEmptyException();
        }

        var order = Order.fromCart(cart, authService.getCurrentUser());

        orderRepository.save(order);

        try {
            var session = paymentGateway.createCheckoutSession(order);

            cartService.clearItemsInCart(cartId);

            return new OrderCheckoutResponse(order.getId(), session.getCheckoutUrl());
        } catch (PaymentException e) {
            orderRepository.delete(order);
            throw e;
        }
    }

    public void handleWebhookEvent(WebhookRequest webhookRequest) {
        paymentGateway
            .parseWebhookRequest(webhookRequest)
            .ifPresent(paymentResult-> {
                var order = orderRepository.findById(paymentResult.getOrderId()).orElseThrow();
                order.setStatus(paymentResult.getPaymentStatus());
                orderRepository.save(order);
        });

    }

}
