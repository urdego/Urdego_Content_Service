package io.urdego.urdego_content_service.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ProjectConfig implements WebMvcConfigurer {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        if (activeProfile.equals("prod")) {
            prodProfileCorsMapping(registry);
        } else {
            devProfileCorsMapping(registry);
        }
    }

    // Cors 모두 오픈 (개발환경)
    public void devProfileCorsMapping(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    // 프로덕션 환경에서는 Cors 설정을 Front 페이지와 허용할 서버만 등록
    private void prodProfileCorsMapping(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("https://urdego.site", "http://localhost:3000", "https://urdego.vercel.app")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}

