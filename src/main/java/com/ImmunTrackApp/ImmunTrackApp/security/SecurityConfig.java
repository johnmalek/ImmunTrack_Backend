package com.ImmunTrackApp.ImmunTrackApp.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static com.ImmunTrackApp.ImmunTrackApp.model.Permissions.*;
import static com.ImmunTrackApp.ImmunTrackApp.model.Role.ADMIN;
import static com.ImmunTrackApp.ImmunTrackApp.model.Role.HEALTHCARE;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final LogoutHandler logoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
                        authorizationManagerRequestMatcherRegistry
                                .requestMatchers("/api/v1/auth/**")
                                .permitAll()
                                .requestMatchers("/api/v1/health_care/**")
                                .permitAll()

                                .requestMatchers("/api/v1/health_care/**").hasAnyRole(ADMIN.name(), HEALTHCARE.name())

                                .requestMatchers(HttpMethod.GET, "/api/v1/health_care/**").hasAnyAuthority(ADMIN_READ.name(), HEALTHCARE_READ.name())
                                .requestMatchers(HttpMethod.POST, "/api/v1/health_care/**").hasAnyAuthority(ADMIN_CREATE.name(), HEALTHCARE_CREATE.name())
                                .requestMatchers(HttpMethod.PUT, "/api/v1/health_care/**").hasAnyAuthority(ADMIN_UPDATE.name(), HEALTHCARE_UPDATE.name())
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/health_care/**").hasAnyAuthority(ADMIN_DELETE.name(), HEALTHCARE_DELETE.name())

                                .requestMatchers("/api/v1/admin/**").hasRole(ADMIN.name())

                                .requestMatchers(HttpMethod.GET, "/api/v1/admin/**").hasAuthority(ADMIN_READ.name())
                                .requestMatchers(HttpMethod.POST, "/api/v1/admin/**").hasAuthority(ADMIN_CREATE.name())
                                .requestMatchers(HttpMethod.PUT, "/api/v1/admin/**").hasAuthority(ADMIN_UPDATE.name())
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/admin/**").hasAuthority(ADMIN_DELETE.name())
                                .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .httpBasic(Customizer.withDefaults())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout ->
                    logout.logoutUrl("/api/v1/auth/logout")
                        .addLogoutHandler(logoutHandler)
                        .logoutSuccessHandler(((request, response, authentication) -> SecurityContextHolder.clearContext()))
        );

        return http.build();
    }

    // Remove CORS error when consuming apis
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
