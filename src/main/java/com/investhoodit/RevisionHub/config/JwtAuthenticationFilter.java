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
import java.security.Key;

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
        String header = request.getHeader("Authorization");
        logger.debug("Authorization header: {}", header);

        if (header == null) {
            logger.warn("Bearer token is null");
            filterChain.doFilter(request, response);
            return;
        }
        if (!header.startsWith("Bearer ")) {
                logger.warn("No Bearer token found in Authorization header");
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
                    logger.info("Authenticated user: {}", email);
                } else {
                    logger.warn("UserDetails not found for email: {}", email);
                }
            } catch (Exception e) {
                logger.error("Error loading UserDetails for email: {}", email, e);
            }
        } else {
            logger.warn("Invalid email or authentication already set: email={}", email);
        }
        filterChain.doFilter(request, response);
    }

}