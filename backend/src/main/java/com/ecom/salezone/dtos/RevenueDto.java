package com.ecom.salezone.dtos;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class RevenueDto {
    private String date;
    private Double revenue;

    // getters
}
