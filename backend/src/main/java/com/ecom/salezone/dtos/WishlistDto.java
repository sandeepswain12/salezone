package com.ecom.salezone.dtos;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class WishlistDto {

    private String userId;
    private List<ProductDto> products;
}