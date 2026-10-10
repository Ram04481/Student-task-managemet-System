package com.studenttask.config;

import com.studenttask.security.JwtService;
import com.studenttask.security.JwtAuthenticationFilter;
import com.studenttask.security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Bean
    public JwtService jwtService() {
        System.out.println("=== [JwtConfig] Creating JwtService bean ===");
        JwtService service = new JwtService(jwtSecret, jwtExpiration);
        System.out.println("=== [JwtConfig] Created JwtService instance: " + service + " ===");
        return service;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        System.out.println("=== [JwtConfig] Creating JwtAuthenticationFilter with JwtService: " + jwtService + " ===");
        return new JwtAuthenticationFilter(jwtService, userDetailsService);
    }
}