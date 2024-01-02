package generation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication // Marks this class as a Spring Boot application
public class GenerationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GenerationServiceApplication.class, args); // Boots up the Spring context and starts the
                                                                         // application
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}