package com.vapps.expense.config;

import com.vapps.expense.common.util.Endpoints;
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
                .disable().authorizeHttpRequests(http -> http.requestMatchers(HttpMethod.GET, Endpoints.GET_USER)
                        .access(hasAnyScope("ExpenseManager.User.READ", "ExpenseManager.User.ALL"))
                        .requestMatchers(HttpMethod.POST, Endpoints.CREATE_USER)
                        .access(hasAnyScope("ExpenseManager.User.CREATE", "ExpenseManager.User.ALL"))
                        .requestMatchers(HttpMethod.PATCH, Endpoints.UPDATE_USER)
                        .access(hasAnyScope("ExpenseManager.User.UPDATE", "ExpenseManager.User.ALL"))
                        .requestMatchers(HttpMethod.POST, Endpoints.CREATE_FAMILY)
                        .access(hasAnyScope("ExpenseManager.Family.CREATE", "ExpenseManager.Family.ALL"))
                        .requestMatchers(HttpMethod.GET, Endpoints.GET_FAMILY, Endpoints.GET_USER_FAMILY,
                                Endpoints.SEARCH_FAMILY)
                        .access(hasAnyScope("ExpenseManager.Family.READ", "ExpenseManager.Family.ALL"))
                        .requestMatchers(HttpMethod.PATCH, Endpoints.UPDATE_FAMILY)
                        .access(hasAnyScope("ExpenseManager.Family.UPDATE", "ExpenseManager.Family.ALL"))
                        .requestMatchers(HttpMethod.DELETE, Endpoints.DELETE_FAMILY)
                        .access(hasAnyScope("ExpenseManager.Family.DELETE", "ExpenseManager.Family.ALL"))
                        .requestMatchers(HttpMethod.POST, Endpoints.INVITE_MEMBER)
                        .access(hasAnyScope("ExpenseManager.Family.Member.INVITE", "ExpenseManager.Family.ALL"))
                        .requestMatchers(HttpMethod.DELETE, Endpoints.REMOVE_MEMBER_FROM_FAMILY)
                        .access(hasAnyScope("ExpenseManager.Family.Member.REMOVE", "ExpenseManager.Family.ALL"))
                        .requestMatchers(HttpMethod.GET, Endpoints.GET_ALL_INVITATIONS)
                        .access(hasAnyScope("ExpenseManager.Invitation.READ", "ExpenseManager.Invitation.ALL"))
                        .requestMatchers(HttpMethod.POST, Endpoints.ACCEPT_INVITATION)
                        .access(hasAnyScope("ExpenseManager.Invitation.ACCEPT"))
                        .requestMatchers(HttpMethod.POST, Endpoints.CREATE_STATIC_RESOURCE)
                        .access(hasAnyScope("ExpenseManager.StaticResource.CREATE",
                                "ExpenseManager.StaticResource.ALL"))
                        .requestMatchers(HttpMethod.GET, Endpoints.GET_STATIC_RESOURCE)
                        .access(hasAnyScope("ExpenseManager.StaticResource.READ", "ExpenseManager.StaticResource.ALL"))
                        .requestMatchers(HttpMethod.DELETE, Endpoints.DELETE_STATIC_RESOURCE)
                        .access(hasAnyScope("ExpenseManager.StaticResource.DELETE",
                                "ExpenseManager.StaticResource.ALL")).anyRequest().authenticated()).exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler()).and().oauth2ResourceServer(oauth2 -> oauth2.jwt()).build();
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
