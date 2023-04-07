package edu.pdae.cs.gateway.config;

import edu.pdae.cs.gateway.route.BaseRouter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class GatewayRouteLocator {

    private final List<BaseRouter> routers;

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder routeLocatorBuilder) {
        final RouteLocatorBuilder.Builder builder = routeLocatorBuilder.routes();
        routers.forEach(router -> router.buildRoutes(builder));
        return builder.build();
    }

}
