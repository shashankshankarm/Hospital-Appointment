package com.hospitalappointment.config;

import com.hospitalappointment.security.JwtProperties;
import com.hospitalappointment.service.BootstrapProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({JwtProperties.class, BootstrapProperties.class})
public class AppConfig {
}
