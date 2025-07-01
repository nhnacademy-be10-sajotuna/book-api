package com.sajotuna.books.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화 (API 서버이므로)
                .authorizeHttpRequests(authorize -> authorize
                        // 1. 모두 허용 (permitAll)
                        .requestMatchers("/api/books/**").permitAll() // 도서 목록 조회, 도서 상세 조회
                        .requestMatchers("/api/tags/**").permitAll() // 태그 목록 조회 (TagController의 모든 GET 요청 포함)
                        .requestMatchers("/api/categories/**").permitAll() // 카테고리 목록 조회 (CategoryController의 모든 GET 요청 포함)
                        .requestMatchers("/h2-console/**").permitAll() // H2 Console 접근 허용
                        .requestMatchers("/actuator/**").permitAll() // Spring Boot Actuator 엔드포인트 접근 허용 (선택 사항, 필요시)

                        // 2. 관리자만 허용 (admin role이 있다고 가정)
                        // 현재 코드에 Spring Security 인증 로직이 없으므로, 일단 "/admin/**" 경로를 허용하지 않는 방식으로
                        // 나중에 인증/인가 로직이 추가되면 .hasRole("ADMIN") 등으로 변경 가능
                        .requestMatchers("/api/admin/**").authenticated() // 관리자 API는 인증된 사용자만 접근 (향후 Role 기반으로 변경 가능)

                        // 3. 회원만 (인증된 사용자만 접근)
                        .requestMatchers("/api/likes/**").authenticated() // 좋아요 추가/취소, 특정 사용자가 좋아요한 책 목록 조회
                        .anyRequest().authenticated() // 그 외 모든 요청은 인증된 사용자만 허용
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 사용 안 함 (JWT 등 토큰 기반 인증 시)
                );

        // H2 Console을 위한 설정 (Spring Security와 함께 사용할 경우)
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        return http.build();
    }
}