package edu.pdae.cs.gateway.route;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.stereotype.Component;

@Component
public class AccountManagementRouter implements BaseRouter {

    @Value("${cs.account-mgmt.address}")
    private String accountManagementAddress;

    @Override
    public RouteLocatorBuilder.Builder buildRoutes(RouteLocatorBuilder.Builder routeLocatorBuilder) {
        return routeLocatorBuilder
                .route(r -> r
                        .path("/api/v1/users")
                        .and()
                        .method("POST", "GET")
                        .and()
                        .uri(accountManagementAddress))
                .route(r -> r
                        .path("/api/v1/users/**")
                        .and()
                        .method("GET", "DELETE")
                        .and()
                        .uri(accountManagementAddress))
                .route(r -> r
                        .path("/api/v1/auth")
                        .and()
                        .method("POST", "DELETE")
                        .and()
                        .uri(accountManagementAddress))
                .route(r -> r
                        .path("/api/v1/auth/**")
                        .and()
                        .method("GET")
                        .and()
                        .uri(accountManagementAddress));
    }

}
