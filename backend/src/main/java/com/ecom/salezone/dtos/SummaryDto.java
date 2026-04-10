package com.ecom.salezone.dtos;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class SummaryDto {
    private Double totalRevenue;
    private Long totalOrders;
    private Long totalUsers;
    private Long totalProducts;

    // getters
}