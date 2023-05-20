package edu.pdae.cs.gateway.route;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.stereotype.Component;

@Component
public class MemoManagementRouter implements BaseRouter {

    @Value("${cs.memo-mgmt.address}")
    private String memoManagementAddress;

    @Override
    public RouteLocatorBuilder.Builder buildRoutes(RouteLocatorBuilder.Builder routeLocatorBuilder) {
        return routeLocatorBuilder
                .route(r -> r
                        .path("/api/v1/memos")
                        .and()
                        .method("POST", "GET", "HEAD")
                        .and()
                        .uri(memoManagementAddress))
                .route(r -> r
                        .path("/api/v1/memos/**")
                        .and()
                        .method("GET", "DELETE", "PATCH", "HEAD")
                        .and()
                        .uri(memoManagementAddress));
    }

}
