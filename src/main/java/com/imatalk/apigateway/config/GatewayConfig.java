package com.imatalk.apigateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class GatewayConfig {

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/api/user/**")
                        .uri("lb://user-service"))
                .route(r -> r.path("/api/auth/**")
                        .uri("lb://auth-service"))
                .route(r -> r.path("/api/chat/**")
                        .uri("lb://chat-service"))
                .route(r -> r.path("/api/search/**")
                        .uri("lb://search-service"))
                .route(r -> r.path("/api/notification/**")
                        .uri("lb://notification-service"))
                .route(r -> r.path("/ws/**")
                        .uri("lb://ws-handler-service"))
                .build();
    }
}
