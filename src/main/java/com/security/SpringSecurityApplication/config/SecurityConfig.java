package com.security.SpringSecurityApplication.config;

import com.security.SpringSecurityApplication.enums.Role;
import com.security.SpringSecurityApplication.filters.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.security.SpringSecurityApplication.enums.Role.*;

@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/user/create", "/api/auth/**").permitAll()
                        //Only for ADMIN
                        .requestMatchers("/api/user/**").hasRole(ADMIN.name())
                        //GET is for USER as well as ADMIN
                        .requestMatchers(HttpMethod.GET, "/api/employee/**")
                        .hasAnyRole(ADMIN.name(), USER.name())
                        //POST, PUT, PATCH, DELETE for only ADMIN
                        .requestMatchers("/api/employee/**").hasRole(Role.ADMIN.name())

                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
//                .formLogin(Customizer.withDefaults())
//                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) {
        return config.getAuthenticationManager();
    }
}
