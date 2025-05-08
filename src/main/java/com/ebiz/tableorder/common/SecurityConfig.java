package com.ebiz.tableorder.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1) CORS 활성화
                .cors(Customizer.withDefaults())
                // 2) CSRF 비활성화 (API 서버인 경우)
                .csrf(csrf -> csrf.disable())
                // 3) 인증/인가 설정 예시 (열어둘 경로 지정)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/health", "/customer/**").permitAll()
                        .anyRequest().authenticated()
                )
                // 4) 필요 시 JWT, formLogin, httpBasic 등 설정
                .httpBasic(Customizer.withDefaults())
        ;

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOriginPatterns(List.of("*"));      // 또는 List.of("http://localhost:5173") 등 지정
        cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));             // Content-Type, Authorization 포함
        cfg.setAllowCredentials(false);                  // true 로 하면 Access-Control-Allow-Credentials 헤더 추가
        cfg.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}