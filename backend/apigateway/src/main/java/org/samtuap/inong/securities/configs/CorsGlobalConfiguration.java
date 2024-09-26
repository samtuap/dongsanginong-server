package org.samtuap.inong.securities.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

@Configuration
public class CorsGlobalConfiguration {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.addAllowedOrigin("http://localhost:8081"); // 프론트엔드 주소 허용
        corsConfig.addAllowedMethod("*"); // 모든 HTTP 메소드 허용
        corsConfig.addAllowedHeader("*"); // 모든 헤더 허용
        corsConfig.setAllowCredentials(true); // 크로스 도메인 쿠키 허용
        corsConfig.setMaxAge(3600L); // 사전 요청(preflight request) 캐시 시간

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        source.registerCorsConfiguration("/**", corsConfig); // 모든 경로에 대해 CORS 설정 적용

        return new CorsWebFilter(source);
    }
}