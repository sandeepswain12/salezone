package com.ecom.salezone.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CategoryDto {

    /**
     * Unique identifier of category
     */
    private String categoryId;

    /**
     * Category title/name
     */
    @NotBlank(message = "Title is required")
    @Size(min = 4, max = 50, message = "Title must be between 4 and 50 characters")
    private String title;

    /**
     * Category description
     */
    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    /**
     * Category cover image name/path
     */
    private String coverImage;
}
