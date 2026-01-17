package com.ecom.salezone.dtos;

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
public class CartDto {

    /**
     * Unique identifier of the cart
     */
    private String cartId;

    /**
     * Date and time when cart was created
     */
    private LocalDateTime createdAt;

    /**
     * User who owns this cart
     */
    private UserDto user;

    /**
     * Items present in the cart
     */
    @Builder.Default
    private List<CartItemDto> items = new ArrayList<>();
}

