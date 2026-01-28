package com.ecom.salezone.dtos;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class AddItemToCartRequest {
    private  String productId;
    private  int quantity;

}
