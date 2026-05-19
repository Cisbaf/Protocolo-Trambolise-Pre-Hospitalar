package com.viteprotocolo.auth.config;

import com.viteprotocolo.auth.service.JwtRequestFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz

                        // === ROTAS DE AUTENTICAÇÃO (AUTH) ===
                        .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET,  "/auth/me").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/logout").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/refresh").permitAll()

                        // === ROTAS DE ATENDENTES ===
                        .requestMatchers(HttpMethod.POST, "/atendentes/municipio/{muni}").permitAll()
                        .requestMatchers(HttpMethod.POST, "/atendentes/registeratt").permitAll()
                        .requestMatchers(HttpMethod.GET,  "/atendentes/municipio").permitAll()

                        // === ROTAS DE ATENDIMENTO (SINGULAR) ===
                        .requestMatchers(HttpMethod.POST, "/atendimento").permitAll()
                        .requestMatchers(HttpMethod.GET,  "/atendimento").permitAll()
                        .requestMatchers(HttpMethod.GET,  "/atendimento/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET,  "/atendimento/params").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/atendimento/{id}").permitAll()

                        // === ROTAS DE ATENDIMENTO PRÉ (PLURAL) ===
                        .requestMatchers(HttpMethod.GET,  "/atendimentos/pre/home").permitAll()
                        .requestMatchers(HttpMethod.GET,  "/atendimentos/pre/all").permitAll()
                        .requestMatchers(HttpMethod.POST, "/atendimentos/pre/pre-preenchimento").permitAll()
                        .requestMatchers(HttpMethod.POST, "/atendimentos/pre/update/**").permitAll()

                        // === SWAGGER E DOCS ===
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // Qualquer outra rota não mapeada acima exigirá autenticação
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Set-Cookie"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) {
        return authenticationConfiguration.getAuthenticationManager();
    }
}