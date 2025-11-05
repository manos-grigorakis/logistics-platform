package com.manosgrigorakis.logisticsplatform.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
public class SecurityConfig {
    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource) {
        // Map database to Spring Security
        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);

        // Connect table users to Spring Security
        jdbcUserDetailsManager.setUsersByUsernameQuery(
                "SELECT email AS username, password, enabled FROM users WHERE email = ?"
        );

        // Connect table roles to Spring Security
        jdbcUserDetailsManager.setAuthoritiesByUsernameQuery(
                "SELECT u.email AS username, r.name AS authority " +
                        "FROM users u " +
                        "JOIN roles r ON u.role_id = r.id " +
                        "WHERE u.email = ?"
        );

        return jdbcUserDetailsManager;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Permissions for each role based on API endpoints
        http.authorizeHttpRequests(configurer ->
                configurer
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

        );

        // Use HTTP Basic Authentication
        http.httpBasic(Customizer.withDefaults());

        // Disable CSRF (DEV ONLY)
        http.csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
