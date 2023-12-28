package apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("auth-service", r -> r.path("/auth/**")
						.uri("http://authentication:8080"))
				.route("cart-service", r -> r.path("/cart/**")
						.uri("http://cart:8080"))
				.route("gallery-service", r -> r.path("/gallery/**")
						.uri("http://gallery:8080"))
				.route("generation-service", r -> r.path("/generation/**")
						.uri("http://generation:8080"))
				.route("transaction-service", r -> r.path("/transaction/**")
						.uri("http://transaction:8080"))
				.build();
	}
}
