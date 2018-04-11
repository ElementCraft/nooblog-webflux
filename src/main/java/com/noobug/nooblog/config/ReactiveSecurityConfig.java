package com.noobug.nooblog.config;

import com.noobug.nooblog.security.AuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ReactiveSecurityConfig {
    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, AuthFilter authFilter) {

        http
                .csrf().disable()
                .authorizeExchange()
                .anyExchange()
                .permitAll()
                .and()
                .addFilterAt(authFilter, SecurityWebFiltersOrder.HTTP_BASIC);


        return http.build();
    }

    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        // FIXME
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("user")
                .password("123")
                .roles("USER")
                .build();
        return new MapReactiveUserDetailsService(user);
    }
}
