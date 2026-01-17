package com.ecom.salezone.enities;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "cart_items")
public class CartItem {

    /**
     * Primary key for CartItem
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "c_i_id")
    private int cartItemId;

    /**
     * Product added to cart
     * Many cart items can reference the same product
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * Quantity of the product in cart
     */
    @Column(name = "c_i_quantity", nullable = false)
    private int quantity;

    /**
     * Total price for this cart item
     * (product price × quantity)
     */
    @Column(name = "c_i_totalprice", nullable = false)
    private int totalPrice;

    /**
     * Cart to which this item belongs
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    /**
     * Automatically calculate total price
     * whenever cart item is created or updated
     */
    @PrePersist
    @PreUpdate
    protected void calculateTotalPrice() {
        this.totalPrice = this.product.getDiscountedPrice() * this.quantity;
    }
}
