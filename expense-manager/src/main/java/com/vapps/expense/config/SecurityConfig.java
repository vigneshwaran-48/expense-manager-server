package com.vapps.expense.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import static org.springframework.security.oauth2.core.authorization.OAuth2AuthorizationManagers.hasAnyScope;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuer;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().csrf()
                .disable().authorizeHttpRequests(http -> http.requestMatchers(HttpMethod.GET, "/api/user/**")
                        .access(hasAnyScope("ExpenseManager.User.READ", "ExpenseManager.User.ALL"))
                        .requestMatchers(HttpMethod.POST, "/api/user")
                        .access(hasAnyScope("ExpenseManager.User.CREATE", "ExpenseManager.User.ALL"))
                        .requestMatchers(HttpMethod.PATCH, "/api/user/**")
                        .access(hasAnyScope("ExpenseManager.User.UPDATE", "ExpenseManager.User.ALL"))
                        .requestMatchers(HttpMethod.POST, "/api/family")
                        .access(hasAnyScope("ExpenseManager.Family.CREATE", "ExpenseManager.Family.ALL"))
                        .requestMatchers(HttpMethod.POST, "/api/family/member/{memberId}/invite")
                        .access(hasAnyScope("ExpenseManager.Family.INVITE", "ExpenseManager.Family.ALL"))
                        .requestMatchers(HttpMethod.GET, "/api/invitation")
                        .access(hasAnyScope("ExpenseManager.Invitation.READ", "ExpenseManager.Invitation.ALL"))
                        .requestMatchers(HttpMethod.POST, "/api/invitation/{id}/accept")
                        .access(hasAnyScope("ExpenseManager.Invitation.ACCEPT")).anyRequest().authenticated())
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler()).and()
                .oauth2ResourceServer(oauth2 -> oauth2.jwt()).build();
    }

    @Bean
    JwtDecoder jwtDecoder() {
        return JwtDecoders.fromIssuerLocation(issuer);
    }

    @Bean
    AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

}
