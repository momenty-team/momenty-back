package com.momenty.global.config.security;

import com.momenty.global.auth.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF
                .csrf(AbstractHttpConfigurer::disable)
                // CORS
                .cors(AbstractHttpConfigurer::disable)
                // 요청 별 인가 설정
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/auth/apple/callback").permitAll()
                        .requestMatchers("/token/access-token").permitAll()
                        .anyRequest().authenticated()
                )
                // 폼 로그인 비활성화: 기본 formLogin을 사용하지 않음
                .formLogin(AbstractHttpConfigurer::disable)
                // 세션 관리 설정
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        // JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 앞에 추가
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
