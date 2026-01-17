package com.ecom.salezone.dtos;

import com.ecom.salezone.enums.OrderStatus;
import com.ecom.salezone.enums.PaymentStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class OrderDto {

    /**
     * Unique identifier of the order
     */
    private String orderId;

    /**
     * Current status of the order
     * (PENDING, CONFIRMED, DISPATCHED, DELIVERED, CANCELLED)
     */
    private OrderStatus orderStatus;

    /**
     * Payment status of the order
     * (NOT_PAID, PAID, FAILED, REFUNDED)
     */
    private PaymentStatus paymentStatus;

    /**
     * Total payable amount for the order
     */
    private int orderAmount;

    /**
     * Billing details
     */
    private String billingAddress;
    private String billingPhone;
    private String billingName;

    /**
     * Order lifecycle timestamps
     */
    private LocalDateTime orderedDate;
    private LocalDateTime deliveredDate;

    /**
     * Items included in the order
     */
    @Builder.Default
    private List<OrderItemDto> orderItems = new ArrayList<>();

    /**
     * Payment gateway references
     */
    private String razorPayOrderId;
    private String paymentId;

    /**
     * User who placed the order
     */
    private UserDto user;
}

