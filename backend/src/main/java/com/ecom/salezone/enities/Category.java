package com.ecom.salezone.enities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @Column(name = "c_id")
    private String categoryId;

    @Column(name = "c_title", length = 60, nullable = false)
    private String title;

    @Column(name = "c_desc", length = 500)
    private String description;

    @Column(name = "c_coverImage", length = 500)
    private String coverImage;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Product> products = new ArrayList<>();
}
