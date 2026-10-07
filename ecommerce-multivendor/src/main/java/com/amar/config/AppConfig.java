package com.amar.config;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import org.springframework.web.client.RestTemplate;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class AppConfig {

   @Value("${app.cors.allowed-origins:http://localhost:3000,http://localhost:3001,https://main.d3qx2m8xrsqsoa.amplifyapp.com}")
    private String allowedOrigins;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            CorsConfigurationSource corsConfigurationSource)
            throws Exception {

        http

                // ==============================
                // STATELESS SESSION
                // ==============================
                .sessionManagement(management ->
                        management.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // ==============================
                // AUTHORIZATION
                // ==============================
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers(
                                        "/",

                                        // Customer Authentication
                                        "/auth/**",

                                        // Products public
                                        "/products",
                                        "/products/**",

                                        // Reviews / Product API public
                                        "/api/products/**",

                                        // Seller login
                                        "/sellers/sent/login-otp",
                                        "/sellers/login",
                                        "/sellers/verify/**",

                                        // Razorpay payment verification
                                        "/api/payment/razorpay/verify"
                                )
                                .permitAll()

                                // Everything else requires authentication
                                .anyRequest()
                                .authenticated()
                )

                // ==============================
                // JWT FILTER
                // ==============================
                .addFilterBefore(
                        new JwtTokenValidator(),
                        BasicAuthenticationFilter.class
                )

                // ==============================
                // CSRF
                // ==============================
                .csrf(csrf -> csrf.disable())

                // ==============================
                // CORS
                // ==============================
                .cors(cors ->
                        cors.configurationSource(
                                corsConfigurationSource
                        )
                );

        return http.build();
    }

    // ==============================
    // CORS CONFIGURATION
    // ==============================
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
                Arrays.stream(allowedOrigins.split(","))
                        .map(String::trim)
                        .filter(origin -> !origin.isEmpty())
                        .toList()
        );

        configuration.setAllowedMethods(
                Arrays.asList(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "PATCH",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                Arrays.asList("*")
        );

        configuration.setAllowCredentials(true);

        configuration.setExposedHeaders(
                Arrays.asList("Authorization")
        );

        configuration.setMaxAge(3600L);

        return new CorsConfigurationSource() {

            @Override
            public CorsConfiguration getCorsConfiguration(
                    HttpServletRequest request) {

                return configuration;
            }
        };
    }

    // ==============================
    // PASSWORD ENCODER
    // ==============================
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ==============================
    // REST TEMPLATE
    // ==============================
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}