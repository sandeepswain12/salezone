package com.ecom.salezone.enities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "cart_items")
public class CartItem {
    @Id
    @Column(name = "c_i_id")
    private int cartItemId;
    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;
    @Column(name = "c_i_quantity")
    private  int quantity;
    @Column(name = "c_i_totalprice")
    private  int totalPrice;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private  Cart cart;
}
