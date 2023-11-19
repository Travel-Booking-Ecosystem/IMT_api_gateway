package com.imatalk.apigateway.config;

import com.imatalk.apigateway.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.apache.http.HttpHeaders.AUTHORIZATION;

@Configuration
@RequiredArgsConstructor
public class GatewayFilter implements WebFilter {
    private final JwtService jwtService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String jwt = extractTokenFromRequest(exchange);
        if (jwt != null) {

                if (jwtService.validateToken(jwt)) {
                    String id = jwtService.extractId(jwt);
                    String email = jwtService.extractEmail(jwt);

                    // set the UsernamePasswordAuthenticationToken as principal in the SecurityWebFiltersOrder
                    exchange.getAttributes().put("id", id);
                    exchange.getAttributes().put("email", email);

                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(id, null, List.of());
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);


                    // set the id as header key currentUserId in the request
                    exchange.getRequest().mutate().headers(httpHeaders -> httpHeaders.set("currentUserId", id));
                    return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(authenticationToken));
                }

        }

        return chain.filter(exchange);
    }

    private String extractTokenFromRequest(ServerWebExchange exchange) {
        String authorizationHeader = exchange.getRequest().getHeaders().getFirst(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
}