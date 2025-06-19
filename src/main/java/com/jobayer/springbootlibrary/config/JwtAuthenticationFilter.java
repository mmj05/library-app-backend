package com.jobayer.springbootlibrary.config;

import com.jobayer.springbootlibrary.service.UserDetailsServiceImpl;
import com.jobayer.springbootlibrary.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestPath = request.getRequestURI();

        // Skip JWT processing for public endpoints only
        if (isPublicEndpoint(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                logger.warn("JWT token extraction failed: " + e.getMessage());
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = 
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception e) {
                logger.warn("User authentication failed: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(String requestPath) {
        // Auth endpoints (except /api/auth/me which requires authentication)
        if (requestPath.startsWith("/api/auth/") && !requestPath.equals("/api/auth/me")) {
            return true;
        }

        // Public book endpoints (non-secure)
        if (requestPath.startsWith("/api/books/") && !requestPath.contains("/secure")) {
            return true;
        }

        // Public review endpoints (non-secure)
        if (requestPath.startsWith("/api/reviews/") && !requestPath.contains("/secure")) {
            return true;
        }

        // Public message endpoints (non-secure)
        if (requestPath.startsWith("/api/messages/") && !requestPath.contains("/secure")) {
            return true;
        }

        // CHANGE FOR CONSISTENCY WITH SECURITY CONFIG
        if (requestPath.startsWith("/api/payment/") && !requestPath.contains("/secure")) {
            return true;
        }

        // CHANGE FOR CONSISTENCY WITH SECURITY CONFIG
        if (requestPath.startsWith("/api/histories/") && !requestPath.contains("/secure")) {
            return true;
        }

        // All other endpoints require authentication
        return false;
    }
} 