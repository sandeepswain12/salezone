package com.ecom.salezone.enities;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "order_items")
public class OrderItem {

    /**
     * Primary key for OrderItem
     */
    @Id
    @Column(name = "oi_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderItemId;

    /**
     * Quantity of product ordered
     */
    @Column(name = "oi_quantity", nullable = false)
    private int quantity;

//    /**
//     * Price of product at the time of order
//     * (snapshot, not current product price)
//     */
//    @Column(name = "oi_price", nullable = false)
//    private int price;

    /**
     * Total price for this order item
     * price * quantity
     */
    @Column(name = "oi_total_price", nullable = false)
    private int totalPrice;

    /**
     * Product associated with this order item
     * Many order items can refer to same product
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * Order to which this item belongs
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

//    /**
//     * Automatically calculate total price before saving
//     */
//    @PrePersist
//    @PreUpdate
//    protected void calculateTotalPrice() {
//        this.totalPrice = this.price * this.quantity;
//    }
}
