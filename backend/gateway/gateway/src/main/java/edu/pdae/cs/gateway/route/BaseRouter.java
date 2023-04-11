package edu.pdae.cs.gateway.route;

import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public interface BaseRouter {

    RouteLocatorBuilder.Builder buildRoutes(RouteLocatorBuilder.Builder routeLocatorBuilder);

    default Map<String, List<String>> defineOpenEndpoints() {
        return Collections.emptyMap();
    }

}
