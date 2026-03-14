package com.ecom.salezone.dtos;

import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ProductDto implements Serializable {

    /**
     * Unique product identifier
     */
    private String productId;

    /**
     * Product title/name
     */
    @NotBlank(message = "Product title is required")
    @Size(min = 3, max = 100, message = "Product title must be between 3 and 100 characters")
    private String title;

    /**
     * Detailed product description
     */
    @NotBlank(message = "Product description is required")
    @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
    private String description;

    /**
     * Original price before discount
     */
    @Positive(message = "Price must be greater than 0")
    private int price;

    /**
     * Discounted price after applying offers
     */
    @PositiveOrZero(message = "Discounted price cannot be negative")
    private int discountedPrice;

    /**
     * Available quantity in inventory
     */
    @Min(value = 0, message = "Quantity cannot be negative")
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
    @NotBlank(message = "Product image name is required")
    private String productImageName;

    /**
     * Product image url stored in server/cloud
     */
    private String productImageUrl;

    /**
     * Category details of product
     */
    @NotNull(message = "Category is required")
    private CategoryDto category;
}
