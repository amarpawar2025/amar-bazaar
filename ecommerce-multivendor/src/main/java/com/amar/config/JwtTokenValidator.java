package com.amar.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.List;

public class JwtTokenValidator extends OncePerRequestFilter {

    // =====================================================
    // SKIP JWT VALIDATION FOR PUBLIC APIs
    // =====================================================

    @Override
    protected boolean shouldNotFilter(
            HttpServletRequest request) {

        String path = request.getServletPath();

        // -------------------------------------------------
        // CUSTOMER AUTH APIs
        // -------------------------------------------------

        if (path.startsWith("/auth/")) {
            return true;
        }

        // -------------------------------------------------
        // SELLER LOGIN APIs
        // -------------------------------------------------

        if (path.equals("/sellers/sent/login-otp")
                || path.equals("/sellers/login")
                || path.startsWith("/sellers/verify/")) {

            return true;
        }

        // -------------------------------------------------
        // PRODUCT APIs
        // -------------------------------------------------

        if (path.equals("/products")
                || path.startsWith("/products/")) {

            return true;
        }

        // -------------------------------------------------
        // PRODUCT + REVIEW APIs
        // -------------------------------------------------

        if (path.startsWith("/api/products/")) {
            return true;
        }

        // -------------------------------------------------
        // RAZORPAY PAYMENT VERIFICATION
        // -------------------------------------------------

        if (path.equals(
                "/api/payment/razorpay/verify")) {

            return true;
        }

        // -------------------------------------------------
        // ALL OTHER APIs
        // JWT VALIDATION REQUIRED
        // -------------------------------------------------

        return false;
    }

    // =====================================================
    // JWT VALIDATION
    // =====================================================

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String jwt =
                request.getHeader(
                        JWt_CONSTANT.JWT_HEADER
                );

        // =================================================
        // NO JWT
        // =================================================

        if (jwt == null
                || !jwt.startsWith("Bearer ")) {

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        // Remove "Bearer "
        jwt = jwt.substring(7);

        try {

            // =================================================
            // CREATE SECRET KEY
            // =================================================

            SecretKey key =
                    Keys.hmacShaKeyFor(
                            JWt_CONSTANT.SECRET_KEY
                                    .getBytes()
                    );

            // =================================================
            // PARSE JWT
            // =================================================

            Claims claims =
                    Jwts.parserBuilder()
                            .setSigningKey(key)
                            .build()
                            .parseClaimsJws(jwt)
                            .getBody();

            // =================================================
            // GET EMAIL
            // =================================================

            String email =
                    String.valueOf(
                            claims.get("email")
                    );

            // =================================================
            // GET AUTHORITIES
            // =================================================

            String authorities =
                    String.valueOf(
                            claims.get("authorities")
                    );

            // =================================================
            // CONVERT AUTHORITIES
            // =================================================

            List<GrantedAuthority> auth =
                    AuthorityUtils
                            .commaSeparatedStringToAuthorityList(
                                    authorities
                            );

            // =================================================
            // CREATE AUTHENTICATION
            // =================================================

            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            auth
                    );

            // =================================================
            // SET SECURITY CONTEXT
            // =================================================

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(
                            authentication
                    );

        } catch (Exception e) {

            // =================================================
            // INVALID JWT
            // =================================================

            SecurityContextHolder
                    .clearContext();

            System.out.println(
                    "JWT validation failed: "
                            + e.getMessage()
            );
        }

        // =================================================
        // CONTINUE REQUEST
        // =================================================

        filterChain.doFilter(
                request,
                response
        );
    }
}