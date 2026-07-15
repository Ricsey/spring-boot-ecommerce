package com.codewithmosh.store.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    @OneToMany(mappedBy = "cart")
    private Set<CartItem> cartItems = new LinkedHashSet<>();


}