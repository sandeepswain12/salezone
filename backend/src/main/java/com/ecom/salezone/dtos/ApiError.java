package com.ecom.salezone.dtos;

import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ApiError{
        private int status;
        private String error;
        private String message;
        private String path;
        private OffsetDateTime timestamp;
}
