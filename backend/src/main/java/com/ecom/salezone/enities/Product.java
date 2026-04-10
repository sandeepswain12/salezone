package com.ecom.salezone.enities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "products")
public class Product {

    /**
     * Primary key for Product
     * Prefer UUID or custom generated value
     */
    @Id
    @Column(name = "p_id")
    private String productId;

    /**
     * Product name/title shown to users
     */
    @Column(name = "p_title", nullable = false)
    private String title;

    /**
     * Detailed product description
     */
    @Column(name = "p_description", length = 10000)
    private String description;

    /**
     * Original price before discount
     */
    @Column(name = "p_price", nullable = false)
    private int price;

    /**
     * Discounted price after applying offers
     */
    @Column(name = "p_discounted_price")
    private int discountedPrice;

    /**
     * Total available quantity in inventory
     */
    @Column(name = "p_quantity")
    private int quantity;

    /**
     * Indicates whether product is visible to users
     */
    @Column(name = "p_live")
    private boolean live;

    /**
     * Indicates whether product is in stock
     * (derived from quantity)
     */
    @Column(name = "p_stock")
    private boolean stock;

    /**
     * Product image file name stored in server/cloud
     */
    @Column(name = "p_imagename")
    private String productImageName;

    /**
     * Product image url stored in server/cloud
     */
    @Column(name = "p_imageurl")
    private String productImageUrl;

//    /**
//     * Brand of the product (e.g., Apple, Samsung)
//     */
//    @Column(name = "p_brand")
//    private String brand;
//
//    /**
//     * Average product rating (0–5)
//     */
//    @Column(name = "p_rating")
//    private double rating;
//
//    /**
//     * Total number of reviews
//     */
//    @Column(name = "p_total_reviews")
//    private int totalReviews;

    /**
     * Date when product was added
     */
    @Column(name = "p_added_at", updatable = false)
    private LocalDateTime addedDate;

    /**
     * Date when product was last updated
     */
    @Column(name = "p_updated_at")
    private LocalDateTime updatedAt;

    /**
     * Category to which product belongs
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cg_id", nullable = false)
    private Category category;

    /**
     * Set default values before inserting product
     */
    @PrePersist
    protected void onCreate() {
        this.addedDate = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.stock = this.quantity > 0;
    }

    /**
     * Update timestamp & stock status before update
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        this.stock = this.quantity > 0;
    }
}
