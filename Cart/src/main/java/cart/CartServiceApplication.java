package cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.IOException;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "cart.repository")
public class CartServiceApplication {
    public static void main(String[] args) throws IOException {
        SpringApplication.run(CartServiceApplication.class, args);
    }
}
