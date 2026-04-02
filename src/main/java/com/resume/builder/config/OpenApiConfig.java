package com.resume.builder.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI openAPI() {
    String schemeName = "bearerAuth";

    SecurityScheme bearerScheme = new SecurityScheme()
        .name(schemeName)
        .type(SecurityScheme.Type.HTTP)
        .scheme("bearer")
        .bearerFormat("JWT");

    return new OpenAPI()
        .components(new Components().addSecuritySchemes(schemeName, bearerScheme))
        .addSecurityItem(new SecurityRequirement().addList(schemeName))
        .info(new Info()
            .title("Resume Builder API")
            .version("0.1.0")
            .description("Spring Boot + MySQL + JWT (MVC architecture)"));
  }
}

