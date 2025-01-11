package io.urdego.urdego_content_service.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;


@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI(@Value("${UrdegoCoreUrls}") List<String> serverList) {
        // API 정보 설정
        Info info = new Info()
                .title("Urdego Content Service")
                .version("1.0.0")
                .description("어데고 컨텐츠 서버 API 문서입니다.\n\n"
                        + "[User Service <- 이동하기](https://urdego.site/user/swagger-ui/index.html)\n\n"
                        + "[Game Service <- 이동하기](https://urdego.site/game/swagger-ui/index.html)")
                .contact(new Contact()
                        .name("Urdego GitHub Link")
                        .url("https://github.com/urdego/Urdego_Content_Service"))
                .license(new License()
                        .name("Apache License Version 2.0")
                        .url("http://www.apache.org/licenses/LICENSE-2.0"));

        // 서버 목록 설정
        List<Server> servers = serverList.stream()
                .map(url -> new Server().url(url).description("Server URL: " + url))
                .collect(Collectors.toList());

        // JWT 인증 설정
        SecurityScheme securityScheme = new SecurityScheme()
                .name("Bearer Authentication")
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
        SecurityRequirement schemaRequirement = new SecurityRequirement().addList("bearerAuth");

        return new OpenAPI()
                .components(new Components().addSecuritySchemes("bearerAuth", securityScheme))
                .security(List.of(schemaRequirement))
                .info(info)
                .servers(servers);
    }
}
