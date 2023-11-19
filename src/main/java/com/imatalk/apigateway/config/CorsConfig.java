package com.imatalk.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            var cors = new org.springframework.web.cors.CorsConfiguration();
            cors.setAllowCredentials(false);
            cors.addAllowedOriginPattern("*");
            cors.addAllowedHeader("*");
            cors.addAllowedMethod("*");
            return cors;
        };
    }
}
