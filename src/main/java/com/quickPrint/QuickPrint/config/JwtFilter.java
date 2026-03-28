package com.quickPrint.QuickPrint.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtils jwtUtils;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String path = request.getServletPath();

		
		if (path.startsWith("/api/cafes/login") || path.startsWith("/api/cafes/register")) {
			filterChain.doFilter(request, response);
			return;
		}

		String authHeader = request.getHeader("Authorization");
		String token = null;
		String email = null;

		try {
			if (authHeader != null && authHeader.startsWith("Bearer ")) {
				token = authHeader.substring(7);
				email = jwtUtils.getEmailFromToken(token);
			}

			if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				if (jwtUtils.validateToken(token, email)) {
					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, null,
							new ArrayList<>());

					authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authToken);
				}
			}
		} catch (Exception e) {

			System.out.println("JWT Validation failed: " + e.getMessage());
		}

		filterChain.doFilter(request, response);
	}

//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//
//        // 1. fetching Authorization header 
//        String authHeader = request.getHeader("Authorization");
//        String token = null;
//        String email = null;
//
//        // 2. Check if it starts with "Bearer "
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            token = authHeader.substring(7);
//            email = jwtUtils.getEmailFromToken(token);
//        }
//
//        // 3. if email is found and security context is empty
//        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            
//            // here validation for token
//            if (jwtUtils.validateToken(token, email)) {
//                UsernamePasswordAuthenticationToken authToken = 
//                    new UsernamePasswordAuthenticationToken(email, null, new ArrayList<>());
//                
//                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                
//                // updating in Security Context that user is now "Logged In"
//                SecurityContextHolder.getContext().setAuthentication(authToken);
//            }
//        }
//        filterChain.doFilter(request, response);
//    }
}