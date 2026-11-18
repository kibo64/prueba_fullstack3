package com.tienda.tienda_online.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth

                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs/**"
                ).permitAll()

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/productos",
                    "/api/productos/**"
                ).hasAnyRole("ADMINISTRADOR", "VENDEDOR", "CLIENTE")

                .requestMatchers(
                    HttpMethod.POST,
                    "/api/productos",
                    "/api/productos/**"
                ).hasRole("ADMINISTRADOR")

                .requestMatchers(
                    HttpMethod.PUT,
                    "/api/productos",
                    "/api/productos/**"
                ).hasRole("ADMINISTRADOR")

                .requestMatchers(
                    HttpMethod.DELETE,
                    "/api/productos",
                    "/api/productos/**"
                ).hasRole("ADMINISTRADOR")
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/categorias",
                    "/api/categorias/**"
                ).hasAnyRole("ADMINISTRADOR", "VENDEDOR", "CLIENTE")

                .requestMatchers("/api/categorias/**")
                    .hasRole("ADMINISTRADOR")
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/pedidos",
                    "/api/pedidos/**"
                ).hasAnyRole("ADMINISTRADOR", "VENDEDOR", "CLIENTE")

                .requestMatchers(
                    HttpMethod.POST,
                    "/api/pedidos"
                ).hasAnyRole("ADMINISTRADOR", "CLIENTE")

                .requestMatchers("/api/pedidos/**")
                    .hasRole("ADMINISTRADOR")
                .requestMatchers("/api/**").authenticated()

                .anyRequest().permitAll()
            )
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
