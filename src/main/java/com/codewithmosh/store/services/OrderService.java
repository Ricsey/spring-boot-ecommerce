package com.codewithmosh.store.services;

import com.codewithmosh.store.dtos.OrderCheckoutResponse;
import com.codewithmosh.store.entities.Order;
import com.codewithmosh.store.entities.OrderItem;
import com.codewithmosh.store.entities.OrderStatus;
import com.codewithmosh.store.exceptions.CartIsEmptyException;
import com.codewithmosh.store.repositories.CartRepository;
import com.codewithmosh.store.repositories.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.codewithmosh.store.exceptions.CartNotFoundException;

import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@Service
public class OrderService {
    private final AuthService authService;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final CartService cartService;

    public OrderCheckoutResponse checkoutOrder(UUID cartId) {
        var cart = cartRepository.getCartWithItems(cartId).orElseThrow(CartNotFoundException::new);

        if (cart.isEmpty()) {
            throw new CartIsEmptyException();
        }

        var order = new Order();
        order.setTotalPrice(cart.getTotalPrice());
        order.setStatus(OrderStatus.PENDING);
        order.setCustomer(authService.getCurrentUser());

        cart.getCartItems().forEach(item -> {
            var orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(item.getProduct());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setTotalPrice(item.getCart().getTotalPrice());
            orderItem.setUnitPrice(item.getProduct().getPrice());
            order.getOrderItems().add(orderItem);
        });

        orderRepository.save(order);

        cartService.clearItemsInCart(cartId);

        return new OrderCheckoutResponse(order.getId());
    }
}
