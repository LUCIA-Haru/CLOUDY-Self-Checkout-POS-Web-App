package com.Cloudy.Cloudy_Self_Checkout_POS.JWT;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class JWTFilter extends OncePerRequestFilter {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private CloudyUserDetailService service;

    Claims claims = null;
    private String userName;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        log.info("JWT Filter is running for : {}", httpServletRequest.getRequestURI());

        // Skip authentication for public endpoints
        List<String> publicEndpoints = Arrays.asList(
                "/user/**",
                "/product/search/**",
                "/product/all",
                "/category/all",
                "/brand/all",
                "/v1/purchase/\\d+/payment$",
                "/uploads/**",
                "/product/discounts",
                "/url/fetch/**"
        );

        String path = httpServletRequest.getServletPath();
        for (String endpoint : publicEndpoints) {
            if (path.matches(endpoint.replace("**", ".*"))) {
                filterChain.doFilter(httpServletRequest, httpServletResponse);
                return; // Exit immediately, do not proceed with token validation
            }
        }

        // Proceed with token validation for non-public endpoints
        String authorizationHeader = httpServletRequest.getHeader("Authorization");
        String token = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(BEARER_PREFIX_LENGTH);
            userName = jwtUtil.extractUsername(token);
            claims = jwtUtil.extractAllClaims(token);

            String role = jwtUtil.extractRole(token);
            log.info("In DoFilterInternal, User Role: {}", role);
        }

        if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = service.loadUserByUsername(userName);
            if (jwtUtil.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(httpServletRequest)
                );
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}