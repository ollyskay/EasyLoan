package com.example.easyloan.security;

import com.example.easyloan.configuration.JwtFilterConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.springframework.http.HttpMethod.*;

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityFilterChainConfig {
    private final AuthenticationProvider authenticationProvider;
    private final JwtFilterConfiguration filterConfiguration;
    private final CorsConfigurationSource corsConfigurationSource;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(configurer->
                        configurer
                                .requestMatchers("/api/v1","/api/v1/auth/**","/password/**", "/api/v1/loan/**",
                                        "/v3/api-docs",
                                        "/v3/api-docs",
                                        "/v3/api-docs/**",
                                        "/swagger-resources",
                                        "/swagger-resources/**",
                                        "/configuration/ui",
                                        "/configuration/security",
                                        "/swagger-ui/**",
                                        "/webjars/**",
                                        "/swagger-ui.html")
                                .permitAll()

                                .requestMatchers(POST,"/api/v1/auth/userReg").permitAll()
                                .requestMatchers(GET,"/api/v1/user/**", "/api/loanDbt/**").permitAll()
                                .requestMatchers("/swagger-ui/index.html").permitAll()
                                .requestMatchers(PUT,"/api/v1/kyc/update/lender").hasRole("LENDER")
                                .requestMatchers(POST,"/api/v1/kyc/update/borrower").hasRole("BORROWER")
                                .requestMatchers(POST,"/api/messages/send").permitAll()
                                .requestMatchers(GET, "/api/messages/all-messages-by-sender").permitAll()
                                .requestMatchers(GET, "/api/v1/kyc/update/**").authenticated()
                                .requestMatchers(POST, "/api/loan-repayment/**").authenticated()


                             
                                .requestMatchers(GET,"/api/v1/borrowed-amount/**").permitAll()


                                .requestMatchers(DELETE, "/api/v1/kyc/update/delete-document").authenticated()

                                .requestMatchers(GET,"/api/v1/loan//loan-offers").hasRole("BORROWER")
                                .requestMatchers(GET,"/api/v1/loan/outstanding-balance/**").permitAll()
                                .requestMatchers(GET, "/api/v1/loan/loan-requests-list").hasRole("LENDER")









                ).sessionManagement((session) ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(filterConfiguration, UsernamePasswordAuthenticationFilter.class);

        http.csrf(csrf->csrf.disable());
        http.cors().configurationSource(corsConfigurationSource);
       // http.cors(corsConfigurationSource-> corsConfigurationSource.disable());
        return http.build();
    }
}


