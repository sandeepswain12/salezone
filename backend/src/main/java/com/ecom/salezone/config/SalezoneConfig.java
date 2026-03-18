package com.ecom.salezone.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Global configuration class for the SaleZone application.
 *
 * This class defines common Spring beans used across
 * the application.
 *
 * Current Beans:
 * - ModelMapper → used for mapping DTOs and entities
 *
 * Example usage:
 * ProductDto dto = modelMapper.map(product, ProductDto.class);
 *
 * @author : Sandeep Kumar Swain
 * @version : 1.0
 * @since : 2026
 */
@Configuration
public class SalezoneConfig {

    /**
     * Provides a ModelMapper bean for object mapping
     * between DTOs and entities.
     *
     * @return configured ModelMapper instance
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}