package edu.pdae.cs.gateway.route;

import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;

public interface BaseRouter {

    RouteLocatorBuilder.Builder buildRoutes(RouteLocatorBuilder.Builder routeLocatorBuilder);

}
