package com.ecom.salezone.dtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class OrderItemDto {

    /**
     * Unique identifier for order item
     */
    private int orderItemId;

    /**
     * Quantity of product ordered
     */
    private int quantity;

    /**
     * Total price for this order item
     * (price × quantity)
     */
    private int totalPrice;

    /**
     * Product details snapshot
     */
    private ProductDto product;
}
