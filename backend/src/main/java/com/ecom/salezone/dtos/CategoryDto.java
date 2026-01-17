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
    @NotBlank(message = "Title is required !!")
    @Size(min = 4, message = "Title must be of minimum 4 characters")
    private String title;

    /**
     * Category description
     */
    @NotBlank(message = "Description is required !!")
    private String description;

    /**
     * Category cover image name/path
     */
    private String coverImage;
}

