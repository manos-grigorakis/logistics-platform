package com.manosgrigorakis.logisticsplatform.security;

import com.manosgrigorakis.logisticsplatform.security.jwt.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${swagger.enabled:false}")
    private boolean swaggerEnabled;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter, UserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Permissions for each role based on API endpoints
        http.authorizeHttpRequests(configurer -> {

            // Swagger
            if (swaggerEnabled) {
                configurer
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/swagger-resources/**").permitAll()
                        .requestMatchers("/webjars/**").permitAll();
            }

            configurer
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                    // Public endpoints
                    .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/auth/request-reset").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/auth/reset-password/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/auth/reset-password/confirm").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/auth/setup-password").permitAll()

                    // Metadata
                    .requestMatchers(HttpMethod.GET, "/api/metadata/customer-types").authenticated()

                    // ROLES
                    .requestMatchers(HttpMethod.GET, "/api/roles").hasAuthority("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/api/roles/**").hasAuthority("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/api/roles").hasAuthority("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/api/roles/**").hasAuthority("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/roles/**").hasAuthority("ADMIN")

                    // USERS
                    .requestMatchers(HttpMethod.GET, "/api/users").hasAuthority("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/api/users/**").hasAuthority("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/api/users").hasAuthority("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/api/users/**").hasAuthority("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasAuthority("ADMIN")

                    // Customers
                    .requestMatchers(HttpMethod.GET, "/api/customers").hasAnyAuthority("ADMIN", "MANAGER")
                    .requestMatchers(HttpMethod.GET, "/api/customers/**").hasAnyAuthority("ADMIN", "MANAGER")
                    .requestMatchers(HttpMethod.POST, "/api/customers").hasAnyAuthority("ADMIN", "MANAGER")
                    .requestMatchers(HttpMethod.PUT, "/api/customers/**").hasAnyAuthority("ADMIN", "MANAGER")
                    .requestMatchers(HttpMethod.DELETE, "/api/customers/**").hasAnyAuthority("ADMIN", "MANAGER")

                    // Quotes
                    .requestMatchers(HttpMethod.GET, "/api/quotes").hasAnyAuthority("ADMIN", "MANAGER")
                    .requestMatchers(HttpMethod.GET, "/api/quotes/**").hasAnyAuthority("ADMIN", "MANAGER")
                    .requestMatchers(HttpMethod.POST, "/api/quotes").hasAnyAuthority("ADMIN", "MANAGER")
                    .requestMatchers(HttpMethod.PUT, "/api/quotes/**").hasAnyAuthority("ADMIN", "MANAGER")
                    .requestMatchers(HttpMethod.PATCH, "/api/quotes/**").hasAnyAuthority("ADMIN", "MANAGER")

                    // Vehicles
                    // Quotes
                    .requestMatchers(HttpMethod.GET, "/api/vehicles").hasAnyAuthority("ADMIN", "MANAGER")
                    .requestMatchers(HttpMethod.GET, "/api/vehicles/**").hasAnyAuthority("ADMIN", "MANAGER")
                    .requestMatchers(HttpMethod.POST, "/api/vehicles").hasAnyAuthority("ADMIN", "MANAGER")
                    .requestMatchers(HttpMethod.PUT, "/xapi/vehicles/**").hasAnyAuthority("ADMIN", "MANAGER")
                    .requestMatchers(HttpMethod.DELETE, "/api/vehicles/**").hasAnyAuthority("ADMIN", "MANAGER")

                    // All other endpoints require authentication
                    .anyRequest().authenticated();
        });

        // Stateless session (required for JWT)
        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Set custom authentication provider
        http.authenticationProvider(authenticationProvider());

        // Add JWT filter before Spring Security default filter
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        // Enables CORS
        http.cors(Customizer.withDefaults());

        // Disable CSRF
        http.csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /* Custom authentication provider
    Links UserDetailsService and PasswordEncoder */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    // Authentication manager bean
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws
            Exception {
        return config.getAuthenticationManager();
    }

    // Configuration of CORS for the frontend
    @Bean
    UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(frontendUrl));
        configuration.setAllowedMethods(Arrays.asList("GET","POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
