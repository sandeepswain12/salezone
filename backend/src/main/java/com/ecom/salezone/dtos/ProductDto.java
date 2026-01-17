package com.ecom.salezone.dtos;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ProductDto {

    /**
     * Unique product identifier
     */
    private String productId;

    /**
     * Product title/name
     */
    private String title;

    /**
     * Detailed product description
     */
    private String description;

    /**
     * Original price before discount
     */
    private int price;

    /**
     * Discounted price after applying offers
     */
    private int discountedPrice;

    /**
     * Available quantity in inventory
     */
    private int quantity;

    /**
     * Date when product was added
     */
    private LocalDateTime addedDate;

    /**
     * Date when product was updated
     */
    private LocalDateTime updatedDate;

    /**
     * Indicates whether product is visible to users
     */
    private boolean live;

    /**
     * Indicates whether product is in stock
     */
    private boolean stock;

    /**
     * Product image name or path
     */
    private String productImageName;

    /**
     * Category details of product
     */
    private CategoryDto category;
}
