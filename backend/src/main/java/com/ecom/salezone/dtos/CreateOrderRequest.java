package com.ecom.salezone.dtos;

import com.ecom.salezone.enums.OrderStatus;
import com.ecom.salezone.enums.PaymentStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CreateOrderRequest {

    /**
     * Unique identifier of the cart
     * from which the order will be created.
     */
    @NotBlank(message = "Cart id is required !!")
    private String cartId;

    /**
     * Unique identifier of the user
     * who is placing the order.
     */
    @NotBlank(message = "User id is required !!")
    private String userId;

    /**
     * Current status of the order.
     * Default value is PENDING when order is created.
     */
    private OrderStatus orderStatus = OrderStatus.PENDING;

    /**
     * Payment status of the order.
     * Default value is NOTPAID before payment completion.
     */
    private PaymentStatus paymentStatus = PaymentStatus.NOT_PAID;

    /**
     * Billing address where the order
     * will be delivered or associated.
     */
    @NotBlank(message = "Address is required !!")
    private String billingAddress;

    /**
     * Contact phone number for billing
     * and delivery-related communication.
     */
    @NotBlank(message = "Phone number is required !!")
    private String billingPhone;

    /**
     * Name of the person to whom
     * the bill/order is addressed.
     */
    @NotBlank(message = "Billing name is required !!")
    private String billingName;

}

