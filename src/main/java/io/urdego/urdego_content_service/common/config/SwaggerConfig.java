package io.urdego.urdego_content_service.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("어데고 컨텐츠 API")
                        .version("v3")
                        .description("어데고 컨텐츠 서버")
                        .contact(new Contact()
                                .name("📍 어데고 GitHub Link")
                                .url("https://github.com/urdego"))
                        .license(new License()
                                .name("⚖️ Apache License Version 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0")))

                .servers(List.of(
                        new Server().url("https://urdego.site"),
                        new Server().url("http://localhost:8081")
                ));
    }
}