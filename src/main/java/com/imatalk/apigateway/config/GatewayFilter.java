package com.imatalk.apigateway.config;

import com.imatalk.apigateway.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
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
        //TODO: check if the request is for websocket
        // if the request is for websocket, then we don't need to validate the token
        // because the token is already validated in the http request
        // if the request is for http, then we need to validate the token
        // but if the request is to permit-all endpoint, then we don't need to validate the token
        if (jwt != null) {

                if (jwtService.validateToken(jwt)) {
                    String id = jwtService.extractId(jwt);
                    String email = jwtService.extractEmail(jwt);

                    // set the id as header key currentUserId in the request
                    exchange.getRequest().mutate().headers(httpHeaders -> httpHeaders.set("currentUserId", id));
                    return chain.filter(exchange);
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