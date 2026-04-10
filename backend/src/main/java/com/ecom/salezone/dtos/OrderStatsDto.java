package com.ecom.salezone.dtos;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class OrderStatsDto {
    private String date;
    private Long orders;

    // getters
}
