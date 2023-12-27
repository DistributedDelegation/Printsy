package cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

@SpringBootApplication
@EnableScheduling
@EnableJpaRepositories(basePackages = "cart.repository")
public class CartServiceApplication {
    public static void main(String[] args) throws IOException {
        SpringApplication.run(CartServiceApplication.class, args);
    }
}
