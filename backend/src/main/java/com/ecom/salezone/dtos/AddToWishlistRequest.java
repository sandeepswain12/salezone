package com.ecom.salezone.dtos;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class AddToWishlistRequest {

    private String productId;
}