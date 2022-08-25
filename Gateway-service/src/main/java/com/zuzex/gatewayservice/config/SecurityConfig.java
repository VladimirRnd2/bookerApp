package com.zuzex.gatewayservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .mvcMatcher("/user/book/**")
                .authorizeRequests()
                .mvcMatchers("/user/book/**").access("hasAuthority('SCOPE_openid')")
                .and()
                .oauth2ResourceServer()
                .jwt();
        return http.build();
    }
}
