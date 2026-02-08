package com.ecom.salezone.dtos;

import lombok.*;

/**
 * Request DTO used for adding or updating an item in the user's cart.
 * This object is received from the client (frontend).
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class AddItemToCartRequest {

    /**
     * Unique identifier of the product to be added to the cart.
     * This value must match an existing product ID in the system.
     */
    private String productId;

    /**
     * Quantity of the product requested by the user.
     * Must be greater than zero.
     */
    private int quantity;

}
