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
    @Id
    @Column(name = "c_i_id")
    private int cartItemId;
    @OneToOne
    @JoinColumn(name = "product_id")
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
