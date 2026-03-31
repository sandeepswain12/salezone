package com.ecom.salezone.enities;

import com.ecom.salezone.enums.OrderStatus;
import com.ecom.salezone.enums.PaymentMethod;
import com.ecom.salezone.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "orders")
public class Order {

    /**
     * Primary key for Order
     */
    @Id
    @Column(name = "o_id")
    private String orderId;

    /**
     * Current status of order
     * (PENDING, DISPATCHED, DELIVERED, etc.)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "o_status")
    private OrderStatus orderStatus;

    /**
     * Payment status of order
     * (PAID, NOT_PAID, FAILED)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "p_status")
    private PaymentStatus paymentStatus;

    /**
     * Billing address for the order
     */
//    @Column(name = "b_address", length = 1000)
//    private String billingAddress;
//
//    /**
//     * Billing phone number
//     */
//    @Column(name = "b_phone", length = 15)
//    private String billingPhone;
//
//    private String billingName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    private int orderAmount;
    /**
     * Date when order was placed
     */
    @Column(name = "o_date", updatable = false)
    private LocalDateTime orderedDate;

    /**
     * Date when order was delivered
     */
    @Column(name = "d_date")
    private LocalDateTime deliveredDate;

    /**
     * Razorpay order ID (used for payment gateway)
     */
    @Column(name = "r_o_id")
    private String razorpayOrderId;

    /**
     * Payment ID received from gateway
     */
    @Column(name = "p_id")
    private String paymentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "p_method")
    private PaymentMethod paymentMethod;

    /**
     * User who placed the order
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "u_id", nullable = false)
    private User user;

    /**
     * Items included in this order
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderItem> orderItems = new ArrayList<>();

    /**
     * Set default values before order creation
     */
    @PrePersist
    protected void onCreate() {
        this.orderedDate = LocalDateTime.now();
        this.orderStatus = OrderStatus.PENDING;
        this.paymentStatus = PaymentStatus.NOT_PAID;
    }
}
