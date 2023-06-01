package edu.pdae.cs.gateway.route;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ActivityNotificationsRouter implements BaseRouter {

    @Value("${cs.activity-notifications-mgmt.address}")
    private String activityNotificationsManagementAddress;

    @Value("${cs.activity-notifications-mgmt.realtime.address}")
    private String activityNotificationsManagementRealtimeAddress;

    @Override
    public RouteLocatorBuilder.Builder buildRoutes(RouteLocatorBuilder.Builder routeLocatorBuilder) {
        return routeLocatorBuilder
                .route(r -> r
                        .path("/api/v1/activities")
                        .and()
                        .method("GET", "HEAD")
                        .and()
                        .uri(activityNotificationsManagementAddress))
                .route(r -> r
                        .path("/api/v1/activities/groups")
                        .and()
                        .method("GET", "HEAD")
                        .and()
                        .uri(activityNotificationsManagementAddress)
                )
                .route(r -> r
                        .path("/api/v1/notifications")
                        .and()
                        .method("GET", "HEAD")
                        .and()
                        .uri(activityNotificationsManagementAddress))
                .route(r -> r
                        .path("/api/v1/notifications/**")
                        .and()
                        .method("PATCH")
                        .and()
                        .uri(activityNotificationsManagementAddress))
                .route(r -> r
                        .path("/ws/notifications/**")
                        .and()
                        .method("GET", "HEAD")
                        .uri(activityNotificationsManagementRealtimeAddress));
    }

    @Override
    public Map<String, List<String>> defineOpenEndpoints() {
        return Map.of(
                "^/ws/notifications/.*$", List.of("GET")
        );
    }

}
