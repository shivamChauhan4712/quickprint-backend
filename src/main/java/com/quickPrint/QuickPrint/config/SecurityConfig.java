package com.quickPrint.QuickPrint.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Autowired
	private JwtFilter jwtFilter;

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	    http.cors(cors -> cors.configurationSource(corsConfigurationSource())).csrf(csrf -> csrf.disable())
	            .headers(headers -> headers.frameOptions(frame -> frame.disable()))
	            .authorizeHttpRequests(auth -> auth
	                    .requestMatchers(
	                        "/api/cafes/login", 
	                        "/api/cafes/register", 
	                        "/api/file/upload/**",
	                        "/api/file/download/**", // 1. Download allow (GET)
	                        "/api/file/delete/**",   // 2. Single Delete allow(DELETE)
	                        "/api/file/delete-bulk", // 3. Bulk Delete allow(DELETE)
	                        "/api/file/list/**",     // 4. File list allow
	                        "/ws-print/**"
	                    ).permitAll()
	                    .requestMatchers(HttpMethod.PATCH, "/api/file/**").permitAll() 
	                    .anyRequest().authenticated())
	            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

	    return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();

		configuration.setAllowedOrigins(java.util.Arrays.asList("http://localhost:5173",
				"https://quickprint-frontend.vercel.app", "https://staci-nitrifiable-ila.ngrok-free.dev"));
		configuration.setAllowedMethods(java.util.Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(
				java.util.Arrays.asList("Authorization", "Content-Type", "ngrok-skip-browser-warning"));
		configuration.setAllowCredentials(true);

		org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}