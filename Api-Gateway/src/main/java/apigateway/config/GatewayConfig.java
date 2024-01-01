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
						.filters(f -> f.rewritePath("/auth/(?<remaining>.*)", "/${remaining}"))
						.uri("http://authentication:8080"))
				.route("cart-service", r -> r.path("/cart/**")
						.filters(f -> f.rewritePath("/cart/(?<remaining>.*)", "/${remaining}"))
						.uri("http://cart:8080"))
				.route("gallery-service", r -> r.path("/gallery/**")
						.filters(f -> f.rewritePath("/gallery/(?<remaining>.*)", "/${remaining}"))
						.uri("http://gallery:8080"))
				.route("generation-service", r -> r.path("/generation/**")
						.filters(f -> f.rewritePath("/generation/(?<remaining>.*)", "/${remaining}"))
						.uri("http://generation:8080"))
				.route("transaction-service", r -> r.path("/transaction/**")
						.filters(f -> f.rewritePath("/transaction/(?<remaining>.*)", "/${remaining}"))
						.uri("http://transaction-gateway:8080"))
				.build();
	}

}
