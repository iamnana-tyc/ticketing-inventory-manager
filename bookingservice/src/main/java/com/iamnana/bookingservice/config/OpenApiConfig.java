package com.iamnana.bookingservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI bookingServiceApi(){
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("Booking Service API Documentation")
                        .description("Booking Service API")
                        .version("1.0"))

                // Routing the API through the Gateway
                .addServersItem(new Server()
                        .url("/")
                        .description("API Gateway Documentation"))

                // Defining the JWT Bearer authentication
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))


                // Apply authentication requirement to API operations
                .addSecurityItem(new SecurityRequirement()
                        .addList("bearerAuth"));
    }
}
