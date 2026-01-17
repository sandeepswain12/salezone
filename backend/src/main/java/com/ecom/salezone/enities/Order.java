package com.ecom.salezone.enities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @Column(name = "o_id")
    private String orderId;
    @Column(name = "o_status")
    private String orderStatus;
    @Column(name = "p_status")
    private String paymentStatus;
    @Column(name = "b_address")
    private String billingAddress;
    @Column(name = "b_phone")
    private String billingPhone;
    @Column(name = "o_date")
    private Date orderDate;
    @Column(name = "d_date")
    private Date deliveredDate;
    @Column(name = "r_o_id")
    private String razorpayOrderId;
    @Column(name = "p_id")
    private String paymentId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "u_id")
    private User user;
    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER , cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();
}
