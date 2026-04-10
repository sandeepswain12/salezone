package com.ecom.salezone.enities;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "cart_items")
public class CartItem {
    @Id
    @Column(name = "ci_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cartItemId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "p_id", nullable = false)
    private Product product;

    /**
     * Quantity of the product in cart
     */
    @Column(name = "ci_quantity", nullable = false)
    private int quantity;

    /**
     * Total price for this cart item
     * (product price × quantity)
     */
    @Column(name = "ci_total_price", nullable = false)
    private int totalPrice;

    /**
     * Cart to which this item belongs
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "c_id", nullable = false)
    private Cart cart;

//    /**
//     * Automatically calculate total price
//     * whenever cart item is created or updated
//     */
//    @PrePersist
//    @PreUpdate
//    protected void calculateTotalPrice() {
//        this.totalPrice = this.product.getDiscountedPrice() * this.quantity;
//    }
}
