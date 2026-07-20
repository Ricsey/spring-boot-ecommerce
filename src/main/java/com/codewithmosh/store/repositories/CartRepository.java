package com.codewithmosh.store.repositories;

import com.codewithmosh.store.entities.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {

    @Query("SELECT c FROM Cart c JOIN FETCH c.cartItems WHERE c.id = :id")
    Optional<Cart> getCartWithItems(@Param("id") UUID id);
}
