package com.codewithmosh.store.controllers;

import com.codewithmosh.store.dtos.AddCartItemRequest;
import com.codewithmosh.store.dtos.CartDto;
import com.codewithmosh.store.entities.Cart;
import com.codewithmosh.store.entities.CartItem;
import com.codewithmosh.store.mappers.CartMapper;
import com.codewithmosh.store.repositories.CartRepository;
import com.codewithmosh.store.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@RestController()
@RequestMapping("/carts")
public class CartController {
    private final ProductRepository productRepository;
    private CartRepository cartRepository;
    private CartMapper cartMapper;

    @GetMapping
    public List<CartDto> getCarts() {
        return cartRepository.findAll()
                .stream()
                .map(cartMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CartDto> getCart(@PathVariable UUID id) {
        var cart = cartRepository.findById(id).orElse(null);

        if (cart == null) {
            return ResponseEntity.notFound().build();
        }

        var cartDto = cartMapper.toDto(cart);
        return ResponseEntity.ok(cartDto);
    }

    @PostMapping
    public ResponseEntity<CartDto> createCart() {
        var cart = new Cart();
        cartRepository.save(cart);
        return ResponseEntity.ok(cartMapper.toDto(cart));
    }

    @PostMapping("/{cart_id}/items")
    public ResponseEntity<CartDto> addItemToCart(
            @PathVariable UUID cart_id,
            @RequestBody AddCartItemRequest request
    ) {
        var productId = request.getProductId();
        var cart = cartRepository.findById(cart_id).orElse(null);
        if (cart == null) {
            return ResponseEntity.notFound().build();
        }

        var product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return ResponseEntity.badRequest().build();
        }

        var cartItem = cart.getCartItems()
                .stream()
                .filter(cartItemDto -> cartItemDto.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + 1);
        } else {
            cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setQuantity(1);

            cartItem.setCart(cart);
            cart.getCartItems().add(cartItem);
        }

        cartRepository.save(cart);

        var cartItemDto = cartMapper.toDto(cart);

        return ResponseEntity.status(HttpStatus.CREATED).body(cartItemDto);
    }
}
