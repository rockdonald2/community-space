package edu.pdae.cs.gateway.route;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HubManagementRouter implements BaseRouter {

    @Value("${cs.hub-mgmt.address}")
    private String hubManagementAddress;

    @Override
    public RouteLocatorBuilder.Builder buildRoutes(RouteLocatorBuilder.Builder routeLocatorBuilder) {
        return routeLocatorBuilder
                .route(r -> r
                        .path("/api/v1/hubs")
                        .and()
                        .method("POST", "GET")
                        .and()
                        .uri(hubManagementAddress))
                .route(r -> r
                        .path("/api/v1/hubs/**")
                        .and()
                        .method("GET", "DELETE", "PATCH")
                        .and()
                        .uri(hubManagementAddress)
                )
                .route(r -> r
                        .path("/api/v1/hubs/*/members")
                        .and()
                        .method("GET", "POST")
                        .and()
                        .uri(hubManagementAddress))
                .route(r -> r
                        .path("/api/v1/hubs/*/members/**")
                        .and()
                        .method("GET", "DELETE")
                        .and()
                        .uri(hubManagementAddress))
                .route(r -> r
                        .path("/api/v1/hubs/*/waiters")
                        .and()
                        .method("GET", "POST")
                        .and()
                        .uri(hubManagementAddress))
                .route(r -> r
                        .path("/api/v1/hubs/*/waiters/**")
                        .and()
                        .method("GET", "DELETE")
                        .and()
                        .uri(hubManagementAddress));
    }

}
