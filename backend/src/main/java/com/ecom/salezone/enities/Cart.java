package com.ecom.salezone.enities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "carts")
public class Cart {

    /**
     * Primary key for Cart
     * UUID or custom generated value recommended
     */
    @Id
    @Column(name = "c_id")
    private String cartId;

    /**
     * Date & time when cart was created
     */
    @Column(name = "c_created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Each cart belongs to exactly one user
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Items present in the cart
     * orphanRemoval = true ensures removed items
     * are deleted from DB automatically
     */
    @OneToMany(
            mappedBy = "cart",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<CartItem> items = new ArrayList<>();

    /**
     * Automatically set cart creation time
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
