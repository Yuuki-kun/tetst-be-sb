package com.amigos.authenautho.demo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import static com.amigos.authenautho.demo.entities.user.Permission.*;
import static com.amigos.authenautho.demo.entities.user.Role.ADMIN;
import static com.amigos.authenautho.demo.entities.user.Role.MANAGER;
import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {
    private final JwtAuthenticationFilter jwtAuthFiler;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .authorizeHttpRequests()
                .requestMatchers("/api/v1/auth/**","/api/v1/home/**").permitAll()
                .requestMatchers("/stripe/api/**").permitAll()
                .requestMatchers("/order/**").permitAll()
                .requestMatchers("/account/**").permitAll()
//                .requestMatchers("/admin-service/**").permitAll()


                .requestMatchers("/admin-service/**").hasRole(ADMIN.name())
//
//                .requestMatchers(GET, "/api/v1/admin-service/**").hasAuthority(ADMIN_READ.name())
//                .requestMatchers(POST, "/api/v1/admin-service/**").hasAuthority(ADMIN_CREATE.name())
//                .requestMatchers(PUT, "/api/v1/admin-service/**").hasAuthority(ADMIN_UPDATE.name())
//                .requestMatchers(DELETE, "/api/v1/admin-service/**").hasAuthority(ADMIN_DELETE.name())

                .anyRequest().authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFiler, UsernamePasswordAuthenticationFilter.class)
                .logout()
//                .logoutUrl("/your-logut-url")
                .logoutUrl("/api/v1/auth/logout")
                .addLogoutHandler(logoutHandler)
                .logoutSuccessHandler((request, response, authentication)
                        -> SecurityContextHolder.clearContext());
        return http.build();
    }
}
