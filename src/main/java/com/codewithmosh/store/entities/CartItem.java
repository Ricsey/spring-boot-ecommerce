package com.codewithmosh.store.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

// NOT @Data, cause it implements toString and LAZY loading the related entities can cause infinite recursion
@Getter
@Setter
@Entity
@Table(name = "cart_items")
public class CartItem {
    // We only need Entity related annotations
    // Constraints are in the DB level

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Only for ID/primary keys!
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "quantity")
    private Integer quantity;
}