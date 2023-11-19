package com.imatalk.apigateway.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import reactor.core.publisher.Mono;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final GatewayFilter gatewayFilter;
    private final CorsConfigurationSource corsConfigSource;

    @Bean
    public ReactiveAuthenticationManager authenticationManager() {
        return authentication -> Mono.empty();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .cors(corsSpec -> corsSpec.configurationSource(corsConfigSource))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec
                        .pathMatchers( "/eureka/**",
                                "/api/auth/health",
                                "/api/auth/login",
                                "/api/auth/register",
                                "/api/user/health",
                                "/api/chat/health",
                                "/api/notification/health",
                                "/api/search/health",
                                "/ws/**").permitAll()
                        .anyExchange().authenticated()
                )
                .addFilterAt(gatewayFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .authenticationManager(authenticationManager())
                .build();
    }
}