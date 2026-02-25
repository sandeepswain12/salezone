package com.ecom.salezone.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;


@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "SaleZone E-Commerce Platform API",
                version = "v1.0",
                description = "RESTful APIs for SaleZone - A scalable and secure e-commerce platform " +
                        "supporting user authentication, product catalog management, cart operations, " +
                        "and order processing.",
                summary = "Secure, scalable and modular E-Commerce backend built with Spring Boot.",
                contact = @Contact(
                        name = "Sandeep Kumar Swain",
                        email = "ecom.salezone@gmail.com"
                )
        ),
        security = {
                @SecurityRequirement(name = "bearerAuth")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer", //Authorization: Bearer htokenaswga,
        bearerFormat = "JWT"

)
public class SalezoneOpenApiDocConfig {
}

