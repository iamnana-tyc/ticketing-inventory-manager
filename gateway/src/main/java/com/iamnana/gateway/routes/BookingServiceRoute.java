package com.iamnana.gateway.routes;

import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.*;

@Configuration
public class BookingServiceRoute {
    @Bean
    public RouterFunction<ServerResponse> bookingRoutes(){
        return GatewayRouterFunctions.route("booking-service")
                .route(
                        RequestPredicates.POST("/api/v1/book"),
                        HandlerFunctions.http()
                )
                .before(BeforeFilterFunctions.uri("http://localhost:8081"))
                .build();
    }

}
