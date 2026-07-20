package com.codewithmosh.store.entities;

import com.codewithmosh.store.exceptions.ProductNotFoundException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

// NOT @Data, cause it implements toString and LAZY loading the related entities can cause infinite recursion
@Getter
@Setter
@Entity
@Table(name = "carts")
public class Cart {
    // We only need Entity related annotations
    // Constraints are in the DB level

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Only for ID/primary keys!
    @Column(name = "id")
    private UUID id;

    //
    @Column(name = "date_created", insertable = false, updatable = false)
    private LocalDate dateCreated;

    @OneToMany(mappedBy = "cart", orphanRemoval = true, cascade = CascadeType.MERGE)
    private Set<CartItem> cartItems = new LinkedHashSet<>();

    public BigDecimal computeTotalPrice() {
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {
            totalPrice = totalPrice.add(cartItem.computeTotalPrice());
        }

        return totalPrice;
    }

    public CartItem getItem(Long productId) {
        for (CartItem cartItem : cartItems) {
            if (cartItem.getProduct().getId().equals(productId)) {
                return cartItem;
            }
        }

        return null;
    }

    public CartItem addItem(Product product) {
        var cartItem = getItem(product.getId());

        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + 1);
        } else {
            cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setQuantity(1);

            cartItem.setCart(this);
            cartItems.add(cartItem);
        }

        return cartItem;

    }

    public void updateItemQuantity(Long productId, Integer quantity) {
        var cartItem = getItem(productId);
        if (cartItem == null) {
            throw new ProductNotFoundException();
        }
        cartItem.setQuantity(quantity);
    }

    public void deleteItem(Long productId) {
        cartItems.removeIf(cartItem -> cartItem.getProduct().getId().equals(productId));
    }

    public void clearCartItems() {
        cartItems.clear();
    }

    public boolean isEmpty() {
        return cartItems.isEmpty();
    }

    public BigDecimal getTotalPrice() {
        return computeTotalPrice();
    }
}