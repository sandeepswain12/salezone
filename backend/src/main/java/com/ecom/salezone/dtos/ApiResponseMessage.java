package com.ecom.salezone.dtos;

import lombok.*;
import org.springframework.http.HttpStatus;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ApiResponseMessage {
    private String message;
    private boolean success;
    private HttpStatus status;
}
