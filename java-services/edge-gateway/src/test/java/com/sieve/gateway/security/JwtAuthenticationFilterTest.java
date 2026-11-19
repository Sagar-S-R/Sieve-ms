package com.sieve.gateway.security;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtAuthenticationFilterTest {

    private final JwtService jwtService =
            new JwtService("sieve-demo-jwt-secret-change-me-32chars");
    private final JwtAuthenticationFilter filter =
            new JwtAuthenticationFilter(jwtService);

    @Test
    void allowsValidBearerToken() {
        String token = jwtService.createToken("demo");
        AtomicBoolean chained = new AtomicBoolean();
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/tasks")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .build());

        filter.filter(exchange, ignored -> {
            chained.set(true);
            return reactor.core.publisher.Mono.empty();
        }).block();

        assertTrue(chained.get());
        assertFalse(exchange.getResponse().getStatusCode() != null
                && exchange.getResponse().getStatusCode().isError());
    }

    @Test
    void rejectsMissingBearerToken() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/tasks").build());
        AtomicBoolean chained = new AtomicBoolean();

        filter.filter(exchange, ignored -> {
            chained.set(true);
            return reactor.core.publisher.Mono.empty();
        }).block();

        assertFalse(chained.get());
        assertEquals(HttpStatus.UNAUTHORIZED,
                exchange.getResponse().getStatusCode());
    }

    @Test
    void rejectsInvalidBearerToken() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/tasks")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer not-a-valid-jwt")
                        .build());
        AtomicBoolean chained = new AtomicBoolean();

        filter.filter(exchange, ignored -> {
            chained.set(true);
            return reactor.core.publisher.Mono.empty();
        }).block();

        assertFalse(chained.get());
        assertEquals(HttpStatus.UNAUTHORIZED,
                exchange.getResponse().getStatusCode());
    }
}
