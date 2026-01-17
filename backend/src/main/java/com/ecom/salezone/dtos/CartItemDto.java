package com.ecom.salezone.dtos;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CartItemDto {

    /**
     * Unique identifier for cart item
     */
    private int cartItemId;

    /**
     * Product details added to cart
     */
    private ProductDto product;

    /**
     * Quantity of product in cart
     */
    private int quantity;

    /**
     * Total price for this cart item
     * (product price × quantity)
     */
    private int totalPrice;
}

