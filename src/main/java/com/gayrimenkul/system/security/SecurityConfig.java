package com.gayrimenkul.system.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.gayrimenkul.system.service.CustomUserDetailsService;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); 
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> {})
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // Public endpoints
                .requestMatchers(
                    "/",
                    "/*.html",
                    "/*.js",
                    "/*.css",
                    "/*.png",
                    "/*.jpg",
                    "/*.jpeg",
                    "/*.gif",
                    "/*.svg",
                    "/*.ico",
                    "/uploads/**",
                    "/favicon.ico"
                ).permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                
                // Category - herkes görebilir
                .requestMatchers(HttpMethod.GET, "/api/categories").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/categories").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/categories/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/categories").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/categories/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/categories").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/categories/**").hasRole("ADMIN")
                
                // City - herkes görebilir
                .requestMatchers(HttpMethod.GET, "/api/cities").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/cities/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/cities").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/cities/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/cities").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/cities/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/cities").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/cities/**").hasRole("ADMIN")
                
                // District - herkes görebilir
                .requestMatchers(HttpMethod.GET, "/api/districts").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/districts/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/districts").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/districts/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/districts").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/districts/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/districts").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/districts/**").hasRole("ADMIN")

                // Neighborhood/Floor - herkes gorebilir
                .requestMatchers(HttpMethod.GET, "/api/neighborhoods").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/neighborhoods/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/neighborhoods").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/neighborhoods/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/neighborhoods").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/neighborhoods/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/neighborhoods").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/neighborhoods/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/neighborhoods").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/neighborhoods/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/floors").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/floors/**").permitAll()
                
                // PropertyType - herkes görebilir
                .requestMatchers(HttpMethod.GET, "/api/property-types").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/property-types/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/property-types").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/property-types/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/property-types").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/property-types/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/property-types").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/property-types/**").hasRole("ADMIN")
                
                // Property - herkes görebilir, kendi propertisini oluşturabilir
                .requestMatchers(HttpMethod.GET, "/api/properties").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/properties/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/listings").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/listings/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/properties").hasAnyRole("AGENT", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/properties/**").hasAnyRole("AGENT", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/listings").hasAnyRole("AGENT", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/listings/**").hasAnyRole("AGENT", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/properties").hasAnyRole("AGENT", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/properties/**").hasAnyRole("AGENT", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/listings").hasAnyRole("AGENT", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/listings/**").hasAnyRole("AGENT", "ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/properties").hasAnyRole("AGENT", "ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/properties/**").hasAnyRole("AGENT", "ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/listings").hasAnyRole("AGENT", "ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/listings/**").hasAnyRole("AGENT", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/properties").hasAnyRole("AGENT", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/properties/**").hasAnyRole("AGENT", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/listings").hasAnyRole("AGENT", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/listings/**").hasAnyRole("AGENT", "ADMIN")
                
                // PropertyImage
                .requestMatchers(HttpMethod.POST, "/api/properties/*/images").hasAnyRole("AGENT", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/listings/*/images").hasAnyRole("AGENT", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/properties/*/images/*").hasAnyRole("AGENT", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/listings/*/images/*").hasAnyRole("AGENT", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/property-images/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/property-images/**").hasAnyRole("AGENT", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/property-images/**").hasAnyRole("AGENT", "ADMIN")
                
                // Favorite - authenticated users
                .requestMatchers("/api/favorites/**").authenticated()
                
                // Message - authenticated users
                .requestMatchers("/api/messages/**").authenticated()
                
                // Role - sadece admin
                .requestMatchers("/api/roles").hasRole("ADMIN")
                .requestMatchers("/api/roles/**").hasRole("ADMIN")
                
                // User - sadece admin
                .requestMatchers(HttpMethod.GET, "/api/users/me").authenticated()
                .requestMatchers("/api/users").hasRole("ADMIN")
                .requestMatchers("/api/users/**").hasRole("ADMIN")
                
                // SystemLog - sadece admin
                .requestMatchers("/api/logs").hasRole("ADMIN")
                .requestMatchers("/api/logs/**").hasRole("ADMIN")
                .requestMatchers("/api/admin/logs").hasRole("ADMIN")
                .requestMatchers("/api/admin/logs/**").hasRole("ADMIN")
                
                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization", "Content-Disposition"));
        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
