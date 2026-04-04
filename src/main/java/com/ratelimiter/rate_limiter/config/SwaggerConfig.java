package com.ratelimiter.rate_limiter.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Rate Limiter API")
                        .version("1.0.0")
                        .description(
                                "A distributed rate limiter built with " +
                                        "Spring Boot and Redis using the " +
                                        "Sliding Window Counter algorithm."
                        )
                        .contact(new Contact()
                                .name("Durgesh")
                                .email("dg13974@gmail.com")
                        )
                );
    }
}