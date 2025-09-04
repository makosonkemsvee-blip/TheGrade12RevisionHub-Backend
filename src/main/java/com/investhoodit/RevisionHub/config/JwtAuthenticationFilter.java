package com.investhoodit.RevisionHub.config;

import com.investhoodit.RevisionHub.service.CustomerUserDetailsService;
import com.investhoodit.RevisionHub.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtUtil util;
    private final CustomerUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil util, CustomerUserDetailsService userDetailsService) {
        this.util = util;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        // Skip JWT validation for public endpoints
        if (path.startsWith("/api/auth/signup") ||
                path.startsWith("/api/auth/verify-otp") ||
                path.startsWith("/api/auth/resend-otp") ||
                path.startsWith("/ws/")) {
            logger.debug("Skipping JWT validation for path: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");
        logger.debug("Authorization header: {}", header);

        if (header == null || !header.startsWith("Bearer ")) {
            logger.warn("No valid Bearer token found in Authorization header for path: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        String email = util.validateJwtAndGetEmail(token);
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                if (userDetails != null) {
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    logger.info("Authenticated user: {} for path: {}", email, path);
                } else {
                    logger.warn("UserDetails not found for email: {} for path: {}", email, path);
                }
            } catch (Exception e) {
                logger.error("Error loading UserDetails for email: {} for path: {}", email, path, e);
            }
        } else {
            logger.warn("Invalid email or authentication already set: email={} for path: {}", email, path);
        }
        filterChain.doFilter(request, response);
    }
}