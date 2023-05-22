package edu.pdae.cs.gateway.route;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.stereotype.Component;

@Component
public class ActivityNotificationsRouter implements BaseRouter {

    @Value("${cs.activity-notifications-mgmt.address}")
    private String activityNotificationsManagementAddress;

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
                );
    }

}
