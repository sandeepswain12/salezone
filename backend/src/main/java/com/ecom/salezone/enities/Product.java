package com.ecom.salezone.enities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "products")
public class Product {
    @Id
    @Column(name = "p_id")
    private String productId;
    @Column(name = "p_title")
    private String title;
    @Column(length = 10000,name = "p_desc")
    private String description;
    @Column(name = "p_price")
    private int price;
    @Column(name = "p_d_price")
    private int discountedPrice;
    @Column(name = "p_quantity")
    private int quantity;
    @Column(name = "p_addeddate")
    private Date addedDate;
    @Column(name = "p_live")
    private boolean live;
    @Column(name = "p_stock")
    private boolean stock;
    @Column(name = "p_imagename")
    private String productImageName;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private  Category category;
}
