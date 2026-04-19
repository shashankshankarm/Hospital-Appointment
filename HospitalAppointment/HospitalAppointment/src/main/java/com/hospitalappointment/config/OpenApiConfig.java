package com.hospitalappointment.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI hospitalApi() {
        return new OpenAPI().info(new Info()
                .title("Hospital Appointment API")
                .description("Phase 1 APIs for authentication, patient, doctor and appointment management")
                .version("v1"));
    }
}
