package com.ecom.salezone.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI configuration for the SaleZone E-Commerce platform.
 *
 * This configuration enables Swagger/OpenAPI documentation
 * for all REST APIs in the application.
 *
 * Features:
 * - API documentation metadata
 * - JWT authentication support in Swagger UI
 * - Global security configuration
 *
 * Users can authenticate using the "Authorize" button
 * in Swagger UI by providing a valid JWT token.
 *
 * Example:
 * Authorization: Bearer <JWT_TOKEN>
 *
 * @author : Sandeep Kumar Swain
 * @version : 1.0
 * @since : 2026
 */
@Configuration
@ConditionalOnProperty(
        name = "app.swagger.enabled",
        havingValue = "true",
        matchIfMissing = false   // OFF by default — must explicitly enable
)
@OpenAPIDefinition(
        info = @Info(
                title = "SaleZone E-Commerce Platform API",
                version = "v1.0",
                description = "RESTful APIs for SaleZone — a scalable and secure e-commerce platform " +
                        "supporting user authentication, product catalog management, cart operations, " +
                        "order processing, and payment integration.",
                summary = "Secure, scalable, and modular e-commerce backend built with Spring Boot.",
                contact = @Contact(
                        name = "Sandeep Kumar Swain",
                        email = "sandeepswain027@gmail.com"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                )
        ),
        security = {
                @SecurityRequirement(name = "bearerAuth")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class SalezoneOpenApiDocConfig {
}