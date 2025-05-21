package org.lineReader.config;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.*;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.web.*;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Not relevant to the exercise but a security mechanism can be added
        http.authorizeHttpRequests(
                auth -> auth.anyRequest().permitAll()) // Allow all request to be executed without additional security
                .csrf(csrf -> csrf.disable()); // Cross Site Request Forgery not required in this case

        return http.build();
    }
}

