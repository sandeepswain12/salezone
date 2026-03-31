package com.ecom.salezone.dtos;

import com.ecom.salezone.enums.OrderStatus;
import com.ecom.salezone.enums.PaymentMethod;
import com.ecom.salezone.enums.PaymentStatus;
import lombok.*;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class OrderUpdateRequest {

    /**
     * Updated status of the order.
     * Example: PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
     */
    private OrderStatus orderStatus;

    /**
     * Updated payment status of the order.
     * Example: NOTPAID, PAID, FAILED, REFUNDED
     */
    private PaymentStatus paymentStatus;

    /**
     * Updated billing name of the customer.
     * Used when customer edits billing details.
     */
//    private String billingName;
//
//    /**
//     * Updated billing phone number.
//     * Used for delivery and order-related communication.
//     */
//    private String billingPhone;
//
//    /**
//     * Updated billing address for order delivery.
//     */
//    private String billingAddress;
    private String addressId;

    /**
     * Date when the order was delivered to the customer.
     * Set only when order status becomes DELIVERED.
     */
    private LocalDateTime deliveredDate;

    private PaymentMethod paymentMethod;

}

