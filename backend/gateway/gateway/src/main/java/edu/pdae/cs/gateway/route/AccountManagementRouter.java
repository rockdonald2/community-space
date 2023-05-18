package edu.pdae.cs.gateway.route;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AccountManagementRouter implements BaseRouter {

    @Value("${cs.account-mgmt.address}")
    private String accountManagementAddress;

    @Value("${cs.account-mgmt.realtime.address}")
    private String accountManagementRealtimeAddress;

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
                        .path("/api/v1/sessions")
                        .and()
                        .method("POST", "GET", "DELETE")
                        .and()
                        .uri(accountManagementAddress))
                .route(r -> r
                        .path("/ws/account/**")
                        .and()
                        .method("GET")
                        .uri(accountManagementRealtimeAddress));
    }

    @Override
    public Map<String, List<String>> defineOpenEndpoints() {
        return Map.of(
                "^/api/v1/sessions$", List.of("POST", "GET", "DELETE"),
                "^/api/v1/users$", List.of("POST"),
                "^/ws/account/.*$", List.of("GET")
        );
    }

}
