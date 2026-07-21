package com.codewithmosh.store.services;

import com.codewithmosh.store.dtos.OrderCheckoutResponse;
import com.codewithmosh.store.entities.Order;
import com.codewithmosh.store.exceptions.CartIsEmptyException;
import com.codewithmosh.store.repositories.CartRepository;
import com.codewithmosh.store.repositories.OrderRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.codewithmosh.store.exceptions.CartNotFoundException;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.util.UUID;

@RequiredArgsConstructor // only final fields are injected
@Service
public class CheckoutService {
    private final AuthService authService;
    private final CartService cartService;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;

    @Value("${websiteUrl}")
    private String websiteUrl;

    @Transactional
    public OrderCheckoutResponse checkoutOrder(UUID cartId) throws StripeException {
        var cart = cartRepository.getCartWithItems(cartId).orElseThrow(CartNotFoundException::new);

        if (cart.isEmpty()) {
            throw new CartIsEmptyException();
        }

        var order = Order.fromCart(cart, authService.getCurrentUser());

        orderRepository.save(order);

        try {
            // Create a checkout session with Stripe
            var builder = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(websiteUrl + "/checkout-success?orderId=" + order.getId())
                    .setCancelUrl(websiteUrl + "/checkout-cancel");

            order.getOrderItems().forEach(item ->
                    {
                        var lineItem = SessionCreateParams.LineItem.builder()
                                .setQuantity(Long.valueOf(item.getQuantity()))
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("eur")
                                                .setUnitAmountDecimal(item.getUnitPrice().multiply(BigDecimal.valueOf(100)))
                                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                        .setName(item.getProduct().getName())
                                                        .build()
                                                )
                                                .build()
                                )
                                .build();
                        builder.addLineItem(lineItem);
                    }
            );
            var session = Session.create(builder.build());

            cartService.clearItemsInCart(cartId);

            return new OrderCheckoutResponse(order.getId(), session.getUrl());
        } catch (StripeException e) {
            orderRepository.delete(order);
            throw e;
        }
    }


}
