package edu.pdae.cs.gateway.config;

import edu.pdae.cs.gateway.route.BaseRouter;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
public class GatewayRouteCategorizer {

    private static final Map<String, List<String>> openEndpoints = new HashMap<>();
    public static final Predicate<ServerHttpRequest> isSecured =
            request -> openEndpoints
                    .keySet().stream()
                    .noneMatch(
                            apiUri ->
                                    request.getURI().getPath().contains(apiUri)
                                            &&
                                            openEndpoints.get(apiUri).stream().anyMatch(apiMethod -> request.getMethod().matches(apiMethod)));

    private final List<BaseRouter> routers;

    @PostConstruct
    private void populateOpenEndpoints() {
        routers.forEach(router -> openEndpoints.putAll(router.defineOpenEndpoints()));
    }

}
