package ec.edu.ucacue.odontocare.shared.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import ec.edu.ucacue.odontocare.auth.infrastructure.JwtAuthenticationFilter;
import ec.edu.ucacue.odontocare.auth.infrastructure.RestAccessDeniedHandler;
import ec.edu.ucacue.odontocare.auth.infrastructure.RestAuthenticationEntryPoint;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter,
            RestAuthenticationEntryPoint entryPoint, RestAccessDeniedHandler deniedHandler,
            CorsConfigurationSource corsConfigurationSource) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(errors -> errors.authenticationEntryPoint(entryPoint).accessDeniedHandler(deniedHandler))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/error", "/api/health", "/actuator/health", "/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/clinical-records/*/consultations/*/odontogram/events")
                            .hasRole("ODONTOLOGO")
                        .requestMatchers(HttpMethod.PUT, "/api/clinical-records/*/consultations/*/vital-signs")
                            .hasAnyRole("AUXILIAR", "ODONTOLOGO")
                        .requestMatchers(HttpMethod.POST, "/api/clinical-records/*/consultations/*/close")
                            .hasRole("ODONTOLOGO")
                        .requestMatchers(HttpMethod.GET, "/api/clinical-records/*/odontogram/**")
                            .hasRole("ODONTOLOGO")
                        .requestMatchers(HttpMethod.POST, "/api/clinical-records/*/report.pdf")
                            .hasRole("ODONTOLOGO")
                        .requestMatchers("/api/clinical-records/*/procedures", "/api/clinical-records/*/treatment-plans/**",
                                "/api/clinical-records/*/clinical-evolutions", "/api/clinical-records/*/consultations/*/procedures",
                                "/api/clinical-records/*/consultations/*/clinical-evolutions")
                            .hasRole("ODONTOLOGO")
                        .requestMatchers(HttpMethod.POST, "/api/clinical-records/*/consultations/**")
                            .hasAnyRole("AUXILIAR", "ODONTOLOGO")
                        .requestMatchers(HttpMethod.GET, "/api/clinical-records/*/consultations/**")
                            .hasAnyRole("AUXILIAR", "ODONTOLOGO")
                        .requestMatchers(HttpMethod.GET, "/api/v1/patients/**")
                            .hasAnyRole("RECEPCIONISTA", "AUXILIAR", "ODONTOLOGO")
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/**")
                            .hasAnyRole("RECEPCIONISTA", "AUXILIAR", "ODONTOLOGO")
                        .requestMatchers("/api/v1/appointments/**")
                            .hasAnyRole("RECEPCIONISTA", "ODONTOLOGO")
                        .requestMatchers(HttpMethod.POST, "/api/v1/patients/**")
                            .hasAnyRole("RECEPCIONISTA", "ODONTOLOGO")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/patients/**")
                            .hasAnyRole("AUXILIAR", "ODONTOLOGO")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/patients/**")
                            .hasRole("ODONTOLOGO")
                        .requestMatchers(
                                "/api/v1/patients/*/clinical-record/consultations/*/vital-signs/**",
                                "/api/v1/clinical-records/*/consultations/*/vital-signs/**")
                            .hasAnyRole("AUXILIAR", "ODONTOLOGO")
                        .requestMatchers("/api/v1/diagnoses/**", "/api/v1/procedures/**",
                                "/api/v1/odontogram/**", "/api/v1/reports/**")
                            .hasRole("ODONTOLOGO")
                        .requestMatchers(HttpMethod.GET, "/api/v1/audit-logs").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    @SuppressWarnings("deprecation")
    PasswordEncoder passwordEncoder() {
        return org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(
            @Value("${app.security.allowed-origin}") String allowedOrigin) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(allowedOrigin));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(false);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
