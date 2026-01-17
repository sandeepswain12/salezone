package com.ecom.salezone.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class SalezoneConfig {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
