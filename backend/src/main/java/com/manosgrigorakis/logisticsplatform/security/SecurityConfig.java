package com.manosgrigorakis.logisticsplatform.security;

import com.manosgrigorakis.logisticsplatform.security.jwt.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
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

    @Value("${app.api.prefix}")
    private String apiPrefix;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${swagger.enabled:false}")
    private boolean swaggerEnabled;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter, UserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Enables Actuator endpoints
     */
    @Bean
    @Order(1)
    public SecurityFilterChain actuatorSecurityFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher(EndpointRequest.toAnyEndpoint())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(EndpointRequest.to("health", "prometheus")).permitAll()
                        .anyRequest().hasAnyAuthority("ADMIN")
                );

        // Disable CSRF
        http.csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    @Order(2)
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
                    .requestMatchers(HttpMethod.POST, apiPrefix + "/v1/auth/login").permitAll()
                    .requestMatchers(HttpMethod.POST, apiPrefix + "/v1/auth/request-reset").permitAll()
                    .requestMatchers(HttpMethod.GET, apiPrefix + "/v1/auth/reset-password/**").permitAll()
                    .requestMatchers(HttpMethod.POST, apiPrefix + "/v1/auth/reset-password/confirm").permitAll()
                    .requestMatchers(HttpMethod.POST, apiPrefix + "/v1/auth/setup-password").permitAll()

                    // Metadata
                    .requestMatchers(HttpMethod.GET, apiPrefix + "/v1/metadata/**").authenticated()

                    // ROLES
                    .requestMatchers(HttpMethod.GET, apiPrefix + "/v1/roles/**").hasAuthority("ADMIN")
                    .requestMatchers(HttpMethod.POST, apiPrefix + "/v1/roles").hasAuthority("ADMIN")
                    .requestMatchers(HttpMethod.PUT, apiPrefix + "/v1/roles/**").hasAuthority("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, apiPrefix + "/v1/roles/**").hasAuthority("ADMIN")

                    // USERS
                    .requestMatchers(HttpMethod.GET, apiPrefix + "/v1/users/**").hasAuthority("ADMIN")
                    .requestMatchers(HttpMethod.POST, apiPrefix + "/v1/users").hasAuthority("ADMIN")
                    .requestMatchers(HttpMethod.PUT, apiPrefix + "/v1/users/**").hasAuthority("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, apiPrefix + "/v1/users/**").hasAuthority("ADMIN")

                    // Customers
                    .requestMatchers(HttpMethod.GET, apiPrefix + "/v1/customers/**").hasAnyAuthority("ADMIN", "MANAGER")
                    .requestMatchers(HttpMethod.POST, apiPrefix + "/v1/customers").hasAnyAuthority("ADMIN", "MANAGER")
                    .requestMatchers(HttpMethod.PUT, apiPrefix + "/v1/customers/**").hasAnyAuthority("ADMIN", "MANAGER")
                    .requestMatchers(HttpMethod.DELETE, apiPrefix + "/v1/customers/**").hasAnyAuthority("ADMIN", "MANAGER")

                    // Quotes
                    .requestMatchers(HttpMethod.GET, apiPrefix + "/v1/quotes/**").hasAnyAuthority("ADMIN", "MANAGER")
                    .requestMatchers(HttpMethod.POST, apiPrefix + "/v1/quotes").hasAnyAuthority("ADMIN", "MANAGER")
                    .requestMatchers(HttpMethod.PUT, apiPrefix + "/v1/quotes/**").hasAnyAuthority("ADMIN", "MANAGER")
                    .requestMatchers(HttpMethod.PATCH, apiPrefix + "/v1/quotes/**").hasAnyAuthority("ADMIN", "MANAGER")

                    // Vehicles
                    .requestMatchers(HttpMethod.GET, apiPrefix + "/v1/vehicles/**").hasAnyAuthority("ADMIN", "MANAGER")
                    .requestMatchers(HttpMethod.POST, apiPrefix + "/v1/vehicles").hasAnyAuthority("ADMIN", "MANAGER")
                    .requestMatchers(HttpMethod.PUT, apiPrefix + "/v1/vehicles/**").hasAnyAuthority("ADMIN", "MANAGER")
                    .requestMatchers(HttpMethod.DELETE, apiPrefix + "/v1/vehicles/**").hasAnyAuthority("ADMIN", "MANAGER")

                    // Shipments
                    .requestMatchers(HttpMethod.GET, apiPrefix + "/v1/shipments/driver").hasAnyAuthority("ADMIN", "DRIVER")
                    .requestMatchers(HttpMethod.GET, apiPrefix + "/v1/shipments/**").hasAnyAuthority("ADMIN", "MANAGER")
                    .requestMatchers(HttpMethod.POST, apiPrefix + "/v1/shipments").hasAnyAuthority("ADMIN", "MANAGER")
                    .requestMatchers(HttpMethod.PUT, apiPrefix + "/v1/shipments/**").hasAnyAuthority("ADMIN", "MANAGER")

                    // CMR-Documents
                    .requestMatchers(HttpMethod.PATCH, apiPrefix + "/v1/cmr-documents/*/status").hasAuthority("ADMIN")
                    .requestMatchers(HttpMethod.GET, apiPrefix + "/v1/cmr-documents/**").hasAnyAuthority("ADMIN", "MANAGER", "EMPLOYEE")

                    // Analytics
                    .requestMatchers(HttpMethod.GET, apiPrefix + "/v1/analytics/*").hasAnyAuthority("ADMIN", "MANAGER")

                    // Suppliers
                    .requestMatchers(HttpMethod.POST, apiPrefix + "/v1/suppliers").hasAnyAuthority("ADMIN", "MANAGER")
                    .requestMatchers(HttpMethod.PUT, apiPrefix + "/v1/suppliers/*").hasAnyAuthority("ADMIN", "MANAGER")
                    .requestMatchers(HttpMethod.PATCH, apiPrefix + "/v1/suppliers/*/deactivate").hasAnyAuthority("ADMIN", "MANAGER")
                    .requestMatchers(HttpMethod.GET, apiPrefix + "/v1/suppliers/**").hasAnyAuthority("ADMIN", "MANAGER", "EMPLOYEE")

                    // Supplier Payments
                    .requestMatchers(HttpMethod.POST, apiPrefix + "/v1/supplier-payments").hasAnyAuthority("ADMIN", "MANAGER", "EMPLOYEE")
                    .requestMatchers(HttpMethod.PUT, apiPrefix + "/v1/supplier-payments/*").hasAnyAuthority("ADMIN", "MANAGER", "EMPLOYEE")
                    .requestMatchers(HttpMethod.PATCH, apiPrefix + "/v1/supplier-payments/*/status").hasAnyAuthority("ADMIN", "MANAGER", "EMPLOYEE")
                    .requestMatchers(HttpMethod.GET, apiPrefix + "/v1/supplier-payments/**").hasAnyAuthority("ADMIN", "MANAGER", "EMPLOYEE")

                    // Company Profile
                    .requestMatchers(HttpMethod.POST, apiPrefix + "/v1/company-profile").hasAuthority("ADMIN")
                    .requestMatchers(HttpMethod.PUT, apiPrefix + "/v1/company-profile").hasAuthority("ADMIN")
                    .requestMatchers(HttpMethod.GET, apiPrefix + "/v1/company-profile").authenticated()

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
