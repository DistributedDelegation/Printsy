package apigateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
public class LoggingFilter implements GlobalFilter, Ordered {
    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Log the necessary details
        logger.info("Incoming route: {}", request.getURI());
        logger.info("Method: {}", request.getMethod());
        logger.info("Path: {}", request.getPath());
        logger.info("Headers: {}", request.getHeaders());
        logger.info("Body: {}", request.getBody());

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            // Log the outgoing (routed) URL
            URI routeUri = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
            logger.info("Outgoing route: {}", routeUri);
        }));
    }

    @Override
    public int getOrder() {
        // Set the order of the filter
        return -1; // High precedence
    }
}
