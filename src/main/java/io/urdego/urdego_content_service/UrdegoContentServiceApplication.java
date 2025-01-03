package io.urdego.urdego_content_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class UrdegoContentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UrdegoContentServiceApplication.class, args);
	}
	
}
