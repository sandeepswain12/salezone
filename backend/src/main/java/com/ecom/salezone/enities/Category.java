package com.ecom.salezone.enities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "categories")
public class Category {

    /**
     * Primary key for Category
     * UUID or custom generated value recommended
     */
    @Id
    @Column(name = "c_id")
    private String categoryId;

    /**
     * Category title/name (e.g., Electronics, Fashion)
     */
    @Column(name = "c_title", length = 60, nullable = false)
    private String title;

    /**
     * Short description of category
     */
    @Column(name = "c_desc", length = 500)
    private String description;

    /**
     * Cover image for category (banner image)
     */
    @Column(name = "c_cover_image", length = 500)
    private String coverImage;

    /**
     * Products belonging to this category
     */
    @OneToMany(
            mappedBy = "category",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    private List<Product> products = new ArrayList<>();

    /**
     * Category creation timestamp
     */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Category last update timestamp
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Set timestamps before insert
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Update timestamp before update
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
